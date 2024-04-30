package aste.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import aste.Richiesta;
import aste.Risposta;
import aste.Risposta.TipoErrore;
import aste.Risposta.TipoRisposta;

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

		idUtente = 0;
		richiestaEntrante = new Richiesta();
		rispostaUscente = new Risposta();
	}

	@Override
	public void run() {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

			while (!socket.isClosed()) {
				// Risposta resettata
				rispostaUscente = new Risposta();

				richiestaEntrante = (Richiesta)inputStream.readObject();
				gestisciRichiesta();

				outputStream.writeObject(rispostaUscente);
			}

		} catch (IOException e) {

		} catch (ClassNotFoundException e) {
			
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: Impossibile chiudere ServerSocket: " + e.getMessage() +
					"."
				);
			}
		}
	}

	private void gestisciRichiesta() {
		switch (richiestaEntrante.tipoRichiesta) {
			case ANNULLA_ASTA:
				annullaAsta();
				break;
			case CREA_ARTICOLO:
				creaArticolo();
				break;
			case CREA_ASTA:
				creaAsta();
				break;
			case CREA_CATEGORIA:
				creaCategoria();
				break;
			case CREA_LOTTO:
				creaLotto();
				break;
			case EFFETTUA_PUNTATA:
				effettuaPuntata();
				break;
			case ELIMINA_ARTICOLO:
				creaArticolo();
				break;
			case ELIMINA_LOTTO:
				creaLotto();
				break;
			case LOGIN:
				login();
				break;
			case LOGOUT:
				logout();
				break;
			case MODIFICA_ARTICOLO:
				modificaArticolo();
				break;
			case MODIFICA_ASTA:
				modificaAsta();
				break;
			case MODIFICA_LOTTO:
				modificaLotto();
				break;
			case MODIFICA_PROFILO:
				modificaProfilo();
				break;
			case REGISTRAZIONE:
				registrazione();
				break;
			case SALVA_ASTA:
				salvaAsta();
				break;
			case VISUALIZZA_ARTICOLI:
				visualizzaArticoli();
				break;
			case VISUALIZZA_ARTICOLO:
				visualizzaArticolo();
				break;
			case VISUALIZZA_ASTA:
				visualizzaAsta();
				break;
			case VISUALIZZA_ASTE:
				visualizzaAste();
				break;
			case VISUALIZZA_ASTE_CONCLUSE:
				visualizzaAsteConcluse();
				break;
			case VISUALIZZA_ASTE_CORRENTI:
				visualizzaAsteCorrenti();
				break;
			case VISUALIZZA_ASTE_PROGRAMMATE:
				visualizzaAsteProgrammate();
				break;
			case VISUALIZZA_ASTE_SALVATE:
				visualizzaAsteSalvate();
				break;
			case VISUALIZZA_ASTE_VINTE:
				visualizzaAsteVinte();
				break;
			case VISUALIZZA_CATEGORIE:
				visualizzaCategorie();
				break;
			case VISUALIZZA_IMMAGINE_PROFILO:
				visualizzaImmagineProfilo();
				break;
			case VISUALIZZA_LOTTI:
				visualizzaLotti();
				break;
			case VISUALIZZA_LOTTO:
				visualizzaLotto();
				break;
			case VISUALIZZA_PROFILO:
				visualizzaProfilo();
				break;
			case VISUALIZZA_PUNTATE:
				visualizzaPuntate();
				break;
			default:
				break;
		}
	}

	private void visualizzaPuntate() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visualizzaPuntate'");
	}

	private void visualizzaLotto() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visualizzaLotto'");
	}

	private void visualizzaLotti() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visualizzaLotti'");
	}

	private void visualizzaCategorie() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visualizzaCategorie'");
	}

	private void visualizzaArticolo() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visualizzaArticolo'");
	}

	private void modificaLotto() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'modificaLotto'");
	}

	private void modificaArticolo() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'modificaArticolo'");
	}

	private void effettuaPuntata() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'effettuaPuntata'");
	}

	private void creaLotto() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'creaLotto'");
	}

	private void creaCategoria() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'creaCategoria'");
	}

	private void creaArticolo() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'creaArticolo'");
	}

	private void login() {
		String emailInput = (String)richiestaEntrante.payload[0];
		String passwordInput = (String)richiestaEntrante.payload[1];

		String queryUtenti = "SELECT Id_utente, email, sale_password, hash_password\n" +
			"FROM Utenti;"
		;

		try {
			Connection connection = gestoreDatabase.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(queryUtenti);

			while (resultSet.next()) {
				Integer idUtente = resultSet.getInt("Id_utente");
				String email = resultSet.getString("email");
				byte[] salePassword = resultSet.getBytes("sale_password");
				byte[] hashPassword = resultSet.getBytes("hash_password");

				if (emailInput.equals(email) || verificaPassword(passwordInput, salePassword, hashPassword)) {
					this.idUtente = idUtente;
					rispostaUscente.tipoRisposta = TipoRisposta.OK;
					return;
				}
			}

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI };
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() + "]: C'e' stato un errore nella query di login.");
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
    }

	private boolean verificaPassword(String password, byte[] salt, byte[] hashRisultante) {
		KeySpec specification = new PBEKeySpec(password.toCharArray(), salt, 65536, 512);
		SecretKeyFactory factory;

		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new Error("[" + Thread.currentThread().getName() + "]: C'e' stato un errore nella verifica della password.");
		}

		byte[] hash;

		try {
			hash = factory.generateSecret(specification).getEncoded();
		} catch (InvalidKeySpecException e) {
			throw new Error("[" + Thread.currentThread().getName() + "]: C'e' stato un errore nella generazione dell'hash.");
		}

		if (Arrays.equals(hashRisultante, hash)) {
			return true;
		}

		return false;
	}

    private void registrazione() {
		String nomeInput = (String)richiestaEntrante.payload[0];
		String cognomeInput = (String)richiestaEntrante.payload[1];
		String passwordInput = (String)richiestaEntrante.payload[2];
		LocalDate dataNascitaInput = (LocalDate)richiestaEntrante.payload[2];
		String cittaResidenzaInput = (String)richiestaEntrante.payload[4];
		Integer capInput = (Integer)richiestaEntrante.payload[5];
		String indirizzoInput = (String)richiestaEntrante.payload[6];
		String emailInput = (String)richiestaEntrante.payload[7];
		String ibanInput = (String)richiestaEntrante.payload[8];

		if (!Pattern.matches("[a-zA-Z ]+", nomeInput)) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI };
			return;
		}

		if (!Pattern.matches("[a-zA-Z]+", cognomeInput)) {
			
		}

		Pattern patternEmail = Pattern.compile("/^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$");
		Matcher matcherEmail = patternEmail.matcher(emailInput);
    }

    private void logout() {
        // Implementazione del logout
        if (idUtente==0)
        {
            rispostaUscente.tipoRisposta = Risposta.TipoRisposta.ERRORE;
            rispostaUscente.payload = new Object[] {Risposta.TipoErrore.OPERAZIONE_INVALIDA};
            return;
        }
        idUtente = 0;
        rispostaUscente.tipoRisposta = Risposta.TipoRisposta.OK;
    }

    private void visualizzaProfilo() {
        // Implementazione della visualizzazione del profilo
        // Definiamo la query SQL per selezionare un utente alla volta
        Integer id = (Integer)richiestaEntrante.payload[0];
        String query = "SELECT * FROM Utenti WHERE Id_utente";

        // Utilizziamo un oggetto Statement per eseguire la query
        try (Statement stmt = gestoreDatabase.getConnection().createStatement()) {
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
            }
        } catch (SQLException e) {
			throw new Error(e.getMessage());
		}
    }

    private void visualizzaImmagineProfilo() 
    {
        // Implementazione della visualizzazione dell'immagine del profilo

    }

    private void modificaProfilo() 
    {
        // Implementazione della modifica del profilo

    }

    // Metodo per caricare tutte le aste in un array list 
    private ArrayList<Object> precaricamentoAste() {
        // Definiamo la query SQL per selezionare tutte le aste
        String query = "SELECT aste.Id_asta, aste.durata, lotti.nome, immagini.Id_immagine"+ 
        "FROM aste, lotti, immagini;";
        
        try {
            Statement stmt = gestoreDatabase.getConnection().createStatement();
            // Cariciamo il payload richiestaEntrante per sapere quante pagine e aste faciamo vedere nella pagina
            ResultSet rs = stmt.executeQuery(query);
            
            // Iteriamo attraverso ogni riga del risultato
            while (rs.next()) {
                // Creamo un array di object per contenere pià aste in una pagina 
                Object[] aste = new Object[4];

                // I dati di una singola asta 
                Integer idAsta = rs.getInt("id_asta");
                Timestamp durata = rs.getTimestamp("data_ora_inizio");
                Float prezzoAttuale = rs.getFloat("data_ora_inizio");
                String nomeLotto = rs.getString("data_ora_inizio");
                Byte ImmaginePrincipale = rs.getByte("data_ora_inizio"); 

                // cariciamo i dati di una singola asta nel array 
                aste[0]= idAsta;
                aste[1]= durata;
                aste[2]= prezzoAttuale;
                aste[3]= nomeLotto;
                aste[4]= ImmaginePrincipale;

                // cariciamo l'array nel payload 
                precaricamentoAste().add(aste);
            }
        } catch (SQLException e) {
            
        }

        return precaricamentoAste();
    }

    // Implementazione della visualizzazione delle aste
    private void visualizzaAste() {
        rispostaUscente = new Risposta();
        Integer numeroAste = (Integer) richiestaEntrante.payload[0];
        Integer numeroPagina = (Integer) richiestaEntrante.payload[1];
        String stringaRicerca = (String) richiestaEntrante.payload[2];
        int[] idCategorie = (int[]) richiestaEntrante.payload[3];

        
        // Definiamo la query SQL per selezionare tutte le aste
        /*
        SELECT aste.Id_asta, aste.durata, Lotto.nome, Immagine.Id_immagine, articoli.nome, categorie.Id_categoria, lotti.nome, articoli.nome, categorie.Id_categoria
        FROM articoli 
        JOIN lotti 
        ON articoli.Rif_lotto=lotti.Id_lotto 
        JOIN articoli_categorie 
        ON articoli.Id_articolo=articoli_categorie.Rif_articolo 
        JOIN categorie 
    O   N articoli_categorie.Rif_categoria=categorie.Id_categoria
        */
        
        String query = "SELECT Asta.Id_asta, Asta.durata, Lotto.nome, Immagine.Id_immagine, articoli.nome, categorie.Id_categoria" +
        "FROM Aste, Lotto, Immagine LIMIT 5 OFFSET "+ 
        ((numeroPagina-1)*numeroAste)+ " Articoli JOIN Lotto ON Articolo.nome=Lotto.nome WHERE Articolo.nome like '"
        + stringaRicerca+ "%' OR Lotto.nome like '"+ stringaRicerca+ "%';";

        

        // Utilizziamo un oggetto Statement per eseguire la query
        rispostaUscente.payload[0] = precaricamentoAste();
    }public void setGestoreDatabase(GestoreDatabase gestoreDatabase) {
        this.gestoreDatabase = gestoreDatabase;
    }

    private void visualizzaAsteConcluse() {
        // Implementazione della visualizzazione delle aste concluse
        // Definiamo la query SQL per selezionare tutte le aste conqulse 
        String query = "SELECT COUNT(*) AS numero_aste, CURDATE() FROM Aste WHERE data_ora_inizio + durata < CURDATE()";

        // Utilizziamo un oggetto Statement per eseguire la query
        try {
			Statement stmt = gestoreDatabase.getConnection().createStatement();
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
        } catch (SQLException e) {

		}
    }

    private void visualizzaAsteCorrenti()  {
        // Implementazione della visualizzazione delle aste correnti
        // Definiamo la query SQL per selezionare tutte le aste corrnti
        String query = "SELECT COUNT(*) AS numero_aste, CURDATE() FROM Aste WHERE data_ora_inizio + durata = CURDATE()";

        // Utilizziamo un oggetto Statement per eseguire la query
        try {
			Statement stmt = gestoreDatabase.getConnection().createStatement();
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
        } catch (SQLException e) {
			
		}
    }

    private void visualizzaAsteProgrammate()  {
        // Implementazione della visualizzazione delle aste programmate
        // Definiamo la query SQL per selezionare tutte le aste programmate
        String query = "SELECT COUNT(*) AS numero_aste, CURDATE() FROM Aste WHERE data_ora_inizio > CURDATE()";

        // Utilizziamo un oggetto Statement per eseguire la query
        try {
			Statement stmt = gestoreDatabase.getConnection().createStatement();
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
        } catch (SQLException e) {
			
		}
    }

    private void visualizzaAsteVinte()  {
        // Implementazione della visualizzazione delle aste vinte
        // Definiamo la query SQL per selezionare tutte le aste
        String query = "SELECT COUNT(*) AS numero_aste FROM Aste";

        // Utilizziamo un oggetto Statement per eseguire la query
        try {
			Statement stmt = gestoreDatabase.getConnection().createStatement();
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
        } catch (SQLException e) {

		}
    }

    private void visualizzaAsteSalvate()  {
        // Implementazione della visualizzazione delle aste salvate
        // Definiamo la query SQL per selezionare tutte le aste
        String query = "SELECT COUNT(*) AS numero_aste FROM Aste";

        // Utilizziamo un oggetto Statement per eseguire la query
        try {
			Statement stmt = gestoreDatabase.getConnection().createStatement();
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
        } catch (SQLException e) {

		}
    }

    private void creaAsta() {
        // if (prezzoInizio < 0) {
		// 	throw new IllegalArgumentException("Impossibile avere un prezzo di inizio < 0.");
		// }

		// if (dataOraInizio.isBefore(LocalDateTime.now())) {
		// 	throw new IllegalArgumentException("La data deve essere dopo quella corrente < 0.");
		// }

		// if (durata.isNegative() || durata.isZero()) {
		// 	throw new IllegalArgumentException("La durata deve essere >= 0.");
		// }

		// if (rifLotto <= 0) {
		// 	try {
		// 		String queryControlloLotto = "SELECT \n" +
		// 			"FROM " +
		// 			"" +
		// 			"" +
		// 		;

		// 		Connection connection = gestoreDatabase.getConnection();
		// 		Statement statement = connection.createStatement();
		// 		ResultSet result = statement.executeQuery(null);
		// 		// TODO: Vedere se il lotto e' dell'utente.
		// 	} catch (SQLException e) {

		// 	}
		// }
    }

    private void modificaAsta() {
        // Implementazione della modifica di un'asta
    }

    private void visualizzaAsta() {
        // // Implementazione della visualizzazione di un'asta
        // // Definiamo la query SQL per selezionare tutte le aste
        // String query = "SELECT * FROM Aste WHERE Id_asta";

        // // Utilizziamo un oggetto Statement per eseguire la query
        // try (Statement stmt = gestoreDatabase.getConnection().createStatement()) {
        //     // Eseguiamo la query e otteniamo il risultato
        //     ResultSet rs = stmt.executeQuery(query);
        //     Integer numeroAste = (Integer) richiestaEntrante.payload[0];
        //     Integer numeroPagine = (Integer) richiestaEntrante.payload[1];
        //     String stringaRicerca = (String) richiestaEntrante.payload[2];
        //     int idCategorie [] = (int[]) richiestaEntrante.payload[3];
            
        //     if()
        //     // Iteriamo attraverso ogni riga del risultato
        //     while (rs.next()) {
        //         // Leggiamo i valori di ogni colonna per la riga corrente
        //         Risposta risposta = new Risposta();
        //         risposta.payload = new Object[];
        //         risposta.payload[0] = 
                
        //         double prezzoInizio = rs.getDouble("prezzo_inizio");
        //         Timestamp dataOraInizio = rs.getTimestamp("data_ora_inizio");
        //         int durata = rs.getInt("durata");
        //         boolean astaAutomatica = rs.getBoolean("asta_automatica");
        //         String ipMulticast = rs.getString("ip_multicast");
        //         String descrizioneAnnullamento = rs.getString("descrizione_annullamento");
        //         int rifLotto = rs.getInt("Rif_lotto");

        //         // Stampiamo le informazioni della riga corrente
         
        //     }
        // }
    }

    private void salvaAsta() {
        // Implementazione del salvataggio di un'asta
    }

    private void annullaAsta() {
        // Implementazione dell'annullamento di un'asta
    }

    private void visualizzaArticoli() {
        // Implementazione della visualizzazione degli articoli
        // Definiamo la query SQL per selezionare tutti gli articoli
        String query = "SELECT * FROM Articoli WHERE Id_articolo";

        // Utilizziamo un oggetto Statement per eseguire la query
        try {
			Statement stmt = gestoreDatabase.getConnection().createStatement();
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
        } catch (SQLException e) {

		}
    }
}