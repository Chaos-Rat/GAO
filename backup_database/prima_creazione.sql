CREATE DATABASE gestione_aste_online;

USE gestione_aste_online;

CREATE TABLE Utenti (
	Id_utente INT NOT NULL AUTO_INCREMENT,
	nome VARCHAR(255) NOT NULL,
	cognome VARCHAR(255) NOT NULL,
	data_nascita DATE NOT NULL,
	citta_residenza VARCHAR(255) NOT NULL,
	cap INT UNSIGNED NOT NULL,
	indirizzo VARCHAR(255) NOT NULL,
	email VARCHAR(255) NOT NULL,
	sale_password BINARY(16) NOT NULL,
	hash_password BINARY(64) NOT NULL,
	iban VARCHAR(255) NOT NULL,
	immagine_profilo BOOLEAN NOT NULL DEFAULT 0,
	PRIMARY KEY (Id_utente)
);

CREATE TABLE Lotti (
	Id_lotto INT NOT NULL AUTO_INCREMENT,
	nome VARCHAR(255) NOT NULL,
	PRIMARY KEY (Id_lotto)
);

INSERT INTO Lotti(Id_lotto, nome)
VALUES (0, "Default");

CREATE TABLE Aste (
	Id_asta INT NOT NULL AUTO_INCREMENT,
	prezzo_inizio DECIMAL(10, 2) NOT NULL,
	data_ora_inizio DATETIME NOT NULL,
	durata TIME NOT NULL,
	asta_automatica BOOLEAN NOT NULL,
    ip_multicast BINARY(4),
	descrizione_annullamento VARCHAR(255),
	Rif_lotto INT NOT NULL,
	PRIMARY KEY (Id_asta),
	FOREIGN KEY (Rif_lotto) REFERENCES Lotti(Id_lotto),
	CHECK (durata > 0),
	CHECK (prezzo_inizio > 0)
);

CREATE TABLE Puntate (
	Id_puntata INT NOT NULL AUTO_INCREMENT,
	data_ora_effettuazione DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	valore DECIMAL(10, 2) NOT NULL,
	Rif_asta INT NOT NULL,
	Rif_utente INT NOT NULL,
	PRIMARY KEY (Id_puntata),
	FOREIGN KEY (Rif_asta) REFERENCES Aste(Id_asta),
 	FOREIGN KEY (Rif_utente) REFERENCES Utenti(Id_utente),
	CHECK(valore > 0)
);

CREATE TABLE Articoli (
   	Id_articolo INT NOT NULL AUTO_INCREMENT,
   	nome VARCHAR(255) NOT NULL,
   	condizione VARCHAR(255) NOT NULL,
   	descrizione VARCHAR(255) NOT NULL,
   	Rif_lotto INT NOT NULL DEFAULT 0,
   	Rif_utente INT NOT NULL,
	quantita INT UNSIGNED NOT NULL,
	PRIMARY KEY (Id_articolo),
   	FOREIGN KEY (Rif_lotto) REFERENCES Lotti(Id_lotto),
   	FOREIGN KEY (Rif_utente) REFERENCES Utenti(Id_utente)
);

CREATE TABLE Immagini (
	Id_immagine INT NOT NULL AUTO_INCREMENT,
	Rif_articolo INT NOT NULL,
	PRIMARY KEY (Id_immagine),
	FOREIGN KEY (Rif_articolo) REFERENCES Articoli(Id_articolo)
);

CREATE TABLE Lotti_Immagini (
	Rif_lotto INT NOT NULL,
	Rif_immagine INT NOT NULL,
	PRIMARY KEY (Rif_lotto, Rif_immagine),
	FOREIGN KEY (Rif_lotto) REFERENCES Lotti(Id_lotto),
	FOREIGN KEY (Rif_immagine) REFERENCES Immagini(Id_immagine)
);

CREATE TABLE Categorie (
   	Id_categoria INT NOT NULL AUTO_INCREMENT,
   	nome VARCHAR(255) NOT NULL UNIQUE,
	PRIMARY KEY (Id_categoria)
);

CREATE TABLE Articoli_Categorie (
   	Rif_articolo INT NOT NULL,
   	Rif_categoria INT NOT NULL,
	PRIMARY KEY (Rif_articolo, Rif_categoria),
   	FOREIGN KEY (Rif_articolo) REFERENCES Articoli(Id_articolo),
   	FOREIGN KEY (Rif_categoria) REFERENCES Categorie(Id_categoria)
);

CREATE TABLE Salvataggi (
   	Rif_asta INT NOT NULL,
   	Rif_utente INT NOT NULL,
	PRIMARY KEY (Rif_asta, Rif_utente),
   	FOREIGN KEY (Rif_asta) REFERENCES Aste(Id_asta),
   	FOREIGN KEY (Rif_utente) REFERENCES Utenti(Id_utente)
);

CREATE USER 'server'@'localhost' IDENTIFIED BY 'JUiv[xDc*OCqCg26';
GRANT SELECT, INSERT, UPDATE, DELETE ON `gestione\_aste\_online`.* TO 'server'@'localhost';