package aste.server;

import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import aste.Richiesta;
import aste.Risposta;

public class GestoreClient implements Runnable {
	private Socket socket;
	private GestoreDatabase gestoreDatabase;
	private	GestoreAste gestoreAste;
	private int idUtente;
	private Richiesta richiestaEntrante;
	private Risposta rispostaUscente;

	public GestoreClient(Socket socket, GestoreDatabase gestoreDatabase, GestoreAste gestoreAste) {
		this.socket = socket;
		this.gestoreDatabase = gestoreDatabase;
		this.gestoreAste = gestoreAste;
	}

	@Override
	public void run() {
		
	}

	private void login(String username, String password) {
        // Implementazione del login
    }

    private void registrazione(String username, String password) {
        // Implementazione della registrazione
    }

    private void logout() {
        // Implementazione del logout
    }

    private void visualizzaProfilo(Connection conn) throws SQLException {
        // Implementazione della visualizzazione del profilo
        // Definiamo la query SQL per selezionare un utente alla volta
        String query = "SELECT * FROM Utenti WHERE Id_utente";

        // Utilizziamo un oggetto Statement per eseguire la query
        try (Statement stmt = conn.createStatement()) {
            // Eseguiamo la query e otteniamo il risultato
            ResultSet rs = stmt.executeQuery(query);

            // Iteriamo attraverso ogni riga del risultato
            while (rs.next()) {
                // Leggiamo i valori di ogni colonna per la riga corrente
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                Timestamp dataNascita = rs.getTimestamp("data_nascita");
                String cittaResidenza = rs.getString("citta_Residenza");
                String email = rs.getString("email");
                String iban = rs.getString("iban");
                float saldo = rs.getFloat("saldo");

                // Stampiamo le informazioni della riga corrente
                System.out.println("Nome: " + nome);
                System.out.println("Cognome: " + cognome);
                System.out.println("Data Nascita: " + dataNascita);
                System.out.println("Citta Residenza: " + cittaResidenza);
                System.out.println("Email: " + email);
                System.out.println("Iban: " + iban);
                System.out.println("Saldo: " + saldo);
                System.out.println("-----------------------------------------");
            }
        }
    }

    private void visualizzaImmagineProfilo() {
        // Implementazione della visualizzazione dell'immagine del profilo
    }

    private void modificaProfilo() {
        // Implementazione della modifica del profilo
    }

    private void visualizzaAste(Connection conn) throws SQLException {
        // Implementazione della visualizzazione delle aste
        // Definiamo la query SQL per selezionare tutte le aste
        String query = "SELECT COUNT(*) AS numero_aste FROM Aste";

        // Utilizziamo un oggetto Statement per eseguire la query
        try (Statement stmt = conn.createStatement()) {
            // Eseguiamo la query e otteniamo il risultato
            ResultSet rs = stmt.executeQuery(query);

            // Iteriamo attraverso ogni riga del risultato
            while (rs.next()) {
                // Leggiamo i valori di ogni colonna per la riga corrente
                Integer numeroAste = rs.getInt("numero_aste");
                Integer numeroPagina = rs.getInt("numero_pagina");
                String ricerca = rs.getString("ricerca");
                int idCategorie = rs.getInt("id_categorie");

                // Stampiamo le informazioni della riga corrente
                System.out.println("Numero Aste: " + numeroAste);
                System.out.println("Numero Pagina: " + numeroPagina);
                System.out.println("Ricerca: " + ricerca);
                System.out.println("Id Categorie: " + idCategorie);
                System.out.println("-----------------------------------------");
            }
        }
    }

    private void visualizzaAsteConcluse(Connection conn) throws SQLException {
        // Implementazione della visualizzazione delle aste concluse
        // Definiamo la query SQL per selezionare tutte le aste conqulse 
        String query = "SELECT COUNT(*) AS numero_aste, CURDATE() FROM Aste WHERE data_ora_inizio + durata < CURDATE()";

        // Utilizziamo un oggetto Statement per eseguire la query
        try (Statement stmt = conn.createStatement()) {
            // Eseguiamo la query e otteniamo il risultato
            ResultSet rs = stmt.executeQuery(query);

            // Iteriamo attraverso ogni riga del risultato
            while (rs.next()) {
                // Leggiamo i valori di ogni colonna per la riga corrente
                Integer numeroAste = rs.getInt("numero_aste");
                Integer numeroPagina = rs.getInt("numero_pagina");
                String ricerca = rs.getString("ricerca");
                int idCategorie = rs.getInt("id_categorie");

                // Stampiamo le informazioni della riga corrente
                System.out.println("Numero Aste: " + numeroAste);
                System.out.println("Numero Pagina: " + numeroPagina);
                System.out.println("Ricerca: " + ricerca);
                System.out.println("Id Categorie: " + idCategorie);
                System.out.println("-----------------------------------------");
            }
        }
    }

    private void visualizzaAsteCorrenti(Connection conn) throws SQLException {
        // Implementazione della visualizzazione delle aste correnti
        // Definiamo la query SQL per selezionare tutte le aste corrnti
        String query = "SELECT COUNT(*) AS numero_aste, CURDATE() FROM Aste WHERE data_ora_inizio + durata = CURDATE()";

        // Utilizziamo un oggetto Statement per eseguire la query
        try (Statement stmt = conn.createStatement()) {
            // Eseguiamo la query e otteniamo il risultato
            ResultSet rs = stmt.executeQuery(query);

            // Iteriamo attraverso ogni riga del risultato
            while (rs.next()) {
                // Leggiamo i valori di ogni colonna per la riga corrente
                Integer numeroAste = rs.getInt("numero_aste");
                Integer numeroPagina = rs.getInt("numero_pagina");
                String ricerca = rs.getString("ricerca");
                int idCategorie = rs.getInt("id_categorie");

                // Stampiamo le informazioni della riga corrente
                System.out.println("Numero Aste: " + numeroAste);
                System.out.println("Numero Pagina: " + numeroPagina);
                System.out.println("Ricerca: " + ricerca);
                System.out.println("Id Categorie: " + idCategorie);
                System.out.println("-----------------------------------------");
            }
        }
    }

    private void visualizzaAsteProgrammate(Connection conn) throws SQLException {
        // Implementazione della visualizzazione delle aste programmate
        // Definiamo la query SQL per selezionare tutte le aste programmate
        String query = "SELECT COUNT(*) AS numero_aste, CURDATE() FROM Aste WHERE data_ora_inizio > CURDATE()";

        // Utilizziamo un oggetto Statement per eseguire la query
        try (Statement stmt = conn.createStatement()) {
            // Eseguiamo la query e otteniamo il risultato
            ResultSet rs = stmt.executeQuery(query);

            // Iteriamo attraverso ogni riga del risultato
            while (rs.next()) {
                // Leggiamo i valori di ogni colonna per la riga corrente
                Integer numeroAste = rs.getInt("numero_aste");
                Integer numeroPagina = rs.getInt("numero_pagina");
                String ricerca = rs.getString("ricerca");
                int idCategorie = rs.getInt("id_categorie");

                // Stampiamo le informazioni della riga corrente
                System.out.println("Numero Aste: " + numeroAste);
                System.out.println("Numero Pagina: " + numeroPagina);
                System.out.println("Ricerca: " + ricerca);
                System.out.println("Id Categorie: " + idCategorie);
                System.out.println("-----------------------------------------");
            }
        }
    }

    private void visualizzaAsteVinte(Connection conn) throws SQLException {
        // Implementazione della visualizzazione delle aste vinte
        // Definiamo la query SQL per selezionare tutte le aste
        String query = "SELECT COUNT(*) AS numero_aste FROM Aste";

        // Utilizziamo un oggetto Statement per eseguire la query
        try (Statement stmt = conn.createStatement()) {
            // Eseguiamo la query e otteniamo il risultato
            ResultSet rs = stmt.executeQuery(query);

            // Iteriamo attraverso ogni riga del risultato
            while (rs.next()) {
                // Leggiamo i valori di ogni colonna per la riga corrente
                Integer numeroAste = rs.getInt("numero_aste");
                Integer numeroPagina = rs.getInt("numero_pagina");
                String ricerca = rs.getString("ricerca");
                int idCategorie = rs.getInt("id_categorie");

                // Stampiamo le informazioni della riga corrente
                System.out.println("Numero Aste: " + numeroAste);
                System.out.println("Numero Pagina: " + numeroPagina);
                System.out.println("Ricerca: " + ricerca);
                System.out.println("Id Categorie: " + idCategorie);
                System.out.println("-----------------------------------------");
            }
        }
    }

    private void visualizzaAsteSalvate(Connection conn) throws SQLException {
        // Implementazione della visualizzazione delle aste salvate
        // Definiamo la query SQL per selezionare tutte le aste
        String query = "SELECT COUNT(*) AS numero_aste FROM Aste";

        // Utilizziamo un oggetto Statement per eseguire la query
        try (Statement stmt = conn.createStatement()) {
            // Eseguiamo la query e otteniamo il risultato
            ResultSet rs = stmt.executeQuery(query);

            // Iteriamo attraverso ogni riga del risultato
            while (rs.next()) {
                // Leggiamo i valori di ogni colonna per la riga corrente
                Integer numeroAste = rs.getInt("numero_aste");
                Integer numeroPagina = rs.getInt("numero_pagina");
                String ricerca = rs.getString("ricerca");
                int idCategorie = rs.getInt("id_categorie");

                // Stampiamo le informazioni della riga corrente
                System.out.println("Numero Aste: " + numeroAste);
                System.out.println("Numero Pagina: " + numeroPagina);
                System.out.println("Ricerca: " + ricerca);
                System.out.println("Id Categorie: " + idCategorie);
                System.out.println("-----------------------------------------");
            }
        }
    }

    private void creaAsta() {
        // Implementazione della creazione di un'asta
    }

    private void modificaAsta() {
        // Implementazione della modifica di un'asta
    }

    private void visualizzaAsta(Connection conn) throws SQLException {
        // Implementazione della visualizzazione di un'asta
        // Definiamo la query SQL per selezionare tutte le aste
        String query = "SELECT * FROM Aste WHERE Id_asta";

        // Utilizziamo un oggetto Statement per eseguire la query
        try (Statement stmt = conn.createStatement()) {
            // Eseguiamo la query e otteniamo il risultato
            ResultSet rs = stmt.executeQuery(query);

            // Iteriamo attraverso ogni riga del risultato
            while (rs.next()) {
                // Leggiamo i valori di ogni colonna per la riga corrente
                double prezzoInizio = rs.getDouble("prezzo_inizio");
                Timestamp dataOraInizio = rs.getTimestamp("data_ora_inizio");
                int durata = rs.getInt("durata");
                boolean astaAutomatica = rs.getBoolean("asta_automatica");
                String ipMulticast = rs.getString("ip_multicast");
                String descrizioneAnnullamento = rs.getString("descrizione_annullamento");
                int rifLotto = rs.getInt("Rif_lotto");

                // Stampiamo le informazioni della riga corrente
                System.out.println("Prezzo Inizio: " + prezzoInizio);
                System.out.println("Data e Ora Inizio: " + dataOraInizio);
                System.out.println("Durata: " + durata);
                System.out.println("Asta Automatica: " + astaAutomatica);
                System.out.println("IP Multicast: " + ipMulticast);
                System.out.println("Descrizione Annullamento: " + descrizioneAnnullamento);
                System.out.println("Riferimento Lotto: " + rifLotto);
                System.out.println("-----------------------------------------");
            }
        }
    }

    private void salvaAsta() {
        // Implementazione del salvataggio di un'asta
    }

    private void annullaAsta() {
        // Implementazione dell'annullamento di un'asta
    }

    private void visualizzaArticoli(Connection conn) throws SQLException {
        // Implementazione della visualizzazione degli articoli
        // Definiamo la query SQL per selezionare tutti gli articoli
        String query = "SELECT * FROM Articoli WHERE Id_articolo";

        // Utilizziamo un oggetto Statement per eseguire la query
        try (Statement stmt = conn.createStatement()) {
            // Eseguiamo la query e otteniamo il risultato
            ResultSet rs = stmt.executeQuery(query);

            // Iteriamo attraverso ogni riga del risultato
            while (rs.next()) {
                // Leggiamo i valori di ogni colonna per la riga corrente
                String nome = rs.getString("nome");
                String condizione = rs.getString("condizione");
                String descrizione = rs.getString("descrizione");
                int rifLotto = rs.getInt("Rif_lotto");
                int rifUtente = rs.getInt("Rif_utente");

                // Stampiamo le informazioni della riga corrente
                System.out.println("Nome: " + nome);
                System.out.println("Condizione: " + condizione);
                System.out.println("Descrizione: " + descrizione);
                System.out.println("Riferimento Lotto: " + rifLotto);
                System.out.println("Riferimento Utente: " + rifUtente);
                System.out.println("-----------------------------------------");
            }
        }
    }
}