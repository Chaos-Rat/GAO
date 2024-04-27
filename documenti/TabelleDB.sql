CREATE TABLE Utente (
	Id_utente INT,
	nome VARCHAR(255),
	cognome VARCHAR(255),
	data_nascita DATE,
	citta_residenza VARCHAR(255),
	cap VARCHAR(10),
	indirizzo VARCHAR(255),
	email VARCHAR(255),
	password VARCHAR(255),
	saldo INT,
	iban VARCHAR(255)
	PRIMARY KEY (Id_utente)
);

CREATE TABLE Puntata (
	Id_puntata INT,
	data_ora_effettuazione DATETIME,
	valore INT,
	Rif_asta INT,
	Rif_utente INT,
	PRIMARY KEY (Id_puntata)
	FOREIGN KEY (Rif_asta) REFERENCES Asta(Id_asta),
 	FOREIGN KEY (Rif_utente) REFERENCES Utente(Id_utente)
);

CREATE TABLE Asta (
	Id_asta INT,
	prezzo_inizio INT,
	data_ora_inizio DATETIME,
	durata TIME,
	asta_automatica BOOLEAN,
    	ip_multicast VARCHAR(255),
 	numero_riproposte INT,
	descrizione_annullamento VARCHAR(255)
	Rif_lotto INT,
	PRIMARY KEY (Id_asta)
	FOREIGN KEY (Rif_lotto) REFERENCES Lotto(Id_lotto),
	CHECK (durata > 0)
   	CHECK (numero_riproposte > 0)
);

CREATE TABLE Lotto (
	Id_lotto INT,
	nome VARCHAR(255),
	PRIMARY KEY (Id_lotto)

);

CREATE TABLE Articoli (
   	Id_articolo INT,
   	nome VARCHAR(255),
   	condizione VARCHAR(255),
   	descrizione VARCHAR(255),
   	Rif_lotto INT,
   	Rif_utente INT,
	quantita INT,
	numero_immagini int,
	PRIMARY KEY (Id_articolo)
   	FOREIGN KEY (Rif_lotto) REFERENCES Lotto(Id_lotto),
   	FOREIGN KEY (Rif_utente) REFERENCES Utente(Id_utente)
);

CREATE TABLE Categoria (
   	Id_categoria INT,
   	nome VARCHAR(255) UNIQUE
	PRIMARY KEY (Id_categoria)
);

CREATE TABLE Articolo_Categoria (
   	Rif_articolo INT,
   	Rif_categoria INT,
   	FOREIGN KEY (Rif_articolo) REFERENCES Articoli(Id_articolo),
   	FOREIGN KEY (Rif_categoria) REFERENCES Categoria(Id_categoria),
);

CREATE TABLE Salvataggio (
   	Rif_asta INT,
   	Rif_utente INT,
   	FOREIGN KEY (Rif_asta) REFERENCES Asta(Id_asta),
   	FOREIGN KEY (Rif_utente) REFERENCES Utente(Id_utente),
);



