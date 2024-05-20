package aste.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import aste.Offerta;
import aste.Richiesta;
import aste.Risposta;
import aste.Risposta.TipoErrore;
import aste.Risposta.TipoRisposta;

public class GestoreClient implements Runnable {
	private final Socket socket;
	private GestoreDatabase gestoreDatabase;
	private	GestoreAste gestoreAste;
	private int idUtente;
	private boolean admin;
	private Richiesta richiestaEntrante;
	private Risposta rispostaUscente;

	public GestoreClient(Socket socket, GestoreDatabase gestoreDatabase, GestoreAste gestoreAste) {
		this.socket = socket;
		this.gestoreDatabase = gestoreDatabase;
		this.gestoreAste = gestoreAste;

		idUtente = 0;
		admin = false;
		richiestaEntrante = new Richiesta();
		rispostaUscente = new Risposta();
	}

	@Override
	public void run() {
		String clientAddress = socket.getRemoteSocketAddress().toString();
		System.out.println("Il client " + clientAddress + " si è connesso.");

		try (socket;){
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

			while (!socket.isClosed()) {
				// Risposta resettata
				rispostaUscente = new Risposta();

				try {
					richiestaEntrante = (Richiesta)inputStream.readObject();
					gestisciRichiesta();
				} catch (ClassNotFoundException	| InvalidClassException e) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.RICHIESTA_INVALIDA };
				}

				outputStream.writeObject(rispostaUscente);
				outputStream.flush();
			}
		} catch (IOException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: Errore I/O: " + e.getMessage() +
				"."
			);
		} finally {
			System.out.println("Disconnessione dal client " + clientAddress + ".");
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
				eliminaArticolo();
				break;
			case ELIMINA_LOTTO:
				eliminaLotto();
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
			case VERIFICA_ADMIN:
				verificaAdmin();
			default:
				break;
		}
	}

	private void visualizzaAsteConcluse() {
		// controllo se l'utente e connesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		// Funzione per avere il numero delle aste 
		Integer numeroAste;

		try {
			numeroAste = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroAste"};
			return;
		}

		if (numeroAste == null || numeroAste <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroAste"};
			return;
		}

		// Funzione per avere il numero della pagina 
		Integer numeroPagina;

		try {
			numeroPagina = (Integer)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		if (numeroPagina == null || numeroAste <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		// Funzione per la ricerca delle aste 
		String stringaRicerca;

		try {
			stringaRicerca = (String)richiestaEntrante.payload[2];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		if (stringaRicerca == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		// Funzione per avere le categoria 
		Integer idCategoriaInput;

		try {
			idCategoriaInput = (Integer)richiestaEntrante.payload[3];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		if (idCategoriaInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		boolean categoriaSpecificata = idCategoriaInput != 0;

		if (categoriaSpecificata) {
			// controllo se la categoria presa esiste 
			String queryControlloCategoria = "SELECT Id_categoria\n" +
				"FROM Categorie\n" + 
				"WHERE Id_categoria = ?;"
			;

			try (Connection connection = gestoreDatabase.getConnection();) {
				PreparedStatement preparedStatement = connection.prepareStatement(queryControlloCategoria);
				preparedStatement.setInt(1, idCategoriaInput);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (!resultSet.next()) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria" };
					return;
				}
			} catch (SQLException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: C'e' stato un errore nella query di controllo dell'idCategoria nella visualizzazione delle aste concluse. " + e.getMessage()
				);

				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
				return;
			}
		}

		// Impostazione della query finale 
		String queryVisualizzazione = "SELECT DISTINCT Aste.Id_asta, Aste.durata, Aste.data_ora_inizio, Salvataggi.Rif_utente, Aste.prezzo_inizio, MAX(Puntate.valore) AS prezzo_attuale, " +
			"Lotti.nome, Immagini.Id_immagine\n" + 
			"FROM Aste\n" +
			"LEFT JOIN Puntate ON Aste.Id_asta = Puntate.Rif_asta\n" +
			"JOIN Lotti ON Aste.Rif_lotto = Lotti.Id_lotto\n" +
			"JOIN Articoli ON Lotti.Id_lotto = Articoli.Rif_lotto\n" +
			"LEFT JOIN Salvataggi ON Aste.Id_asta = Salvataggi.Rif_asta\n" +
			"LEFT JOIN Immagini ON Immagini.Rif_articolo = Articoli.Id_articolo\n"+
			"WHERE (CURRENT_TIMESTAMP > DATE_ADD(Aste.data_ora_inizio, INTERVAL Aste.durata MINUTE)) AND\n"
		;

		if (categoriaSpecificata) {
			queryVisualizzazione += "Articoli.Rif_categoria = ? AND\n";
		}

		queryVisualizzazione += "Lotti.nome LIKE ? AND\n" +
			"Immagini.principale = 1 AND\n" +
			"Articoli.Rif_utente = ?\n" +
			"GROUP BY Aste.Id_asta\n" +
			"LIMIT ? OFFSET ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(queryVisualizzazione);
			if (categoriaSpecificata) {
				preparedStatement.setInt(1, idCategoriaInput);
				preparedStatement.setString(2, "%"+ stringaRicerca+ "%");
				preparedStatement.setInt(3, idUtente);
				preparedStatement.setInt(4, numeroAste);
				preparedStatement.setInt(5, ((numeroPagina-1)*numeroAste));
			} else {
				preparedStatement.setString(1, "%"+ stringaRicerca+ "%");
				preparedStatement.setInt(2, idUtente);
				preparedStatement.setInt(3, numeroAste);
				preparedStatement.setInt(4, ((numeroPagina-1)*numeroAste));
			}
			
			ResultSet resultSet = preparedStatement.executeQuery();

			// array list per gli oggetti delle aste 
			ArrayList<Object> aste= new ArrayList<>();

			// While per caricare l'array list 
			while (resultSet.next()) {
				aste.add(resultSet.getInt("Id_asta"));
				aste.add(Duration.ofMinutes(resultSet.getLong("durata")));
				
				float prezzo_attuale = resultSet.getFloat("prezzo_attuale");
				aste.add(prezzo_attuale == 0 ? resultSet.getFloat("prezzo_inizio") : prezzo_attuale);

				aste.add(resultSet.getString("nome"));
				
				int idImmagine = resultSet.getInt("Id_immagine");

				String nomeFile = resultSet.wasNull() ? 
					"static_resources\\default_articolo.png" :
					"res\\immagini_articoli\\"+ idImmagine + ".png"
				;

				try (FileInputStream stream = new FileInputStream(nomeFile);) {
					aste.add(stream.readAllBytes());
				}

				aste.add(resultSet.getTimestamp("data_ora_inizio").toLocalDateTime());
				aste.add(resultSet.getInt("Rif_utente") != 0);
			}

			// Transformazione del array list in array e risposta nel payload uscita 
			rispostaUscente.tipoRisposta= TipoRisposta.OK;
			rispostaUscente.payload = aste.toArray();

		} catch (SQLException e) { // questo catch e per gli errori che potrebbe dare la query 
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di visualizzazione delle aste concluse. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) { // questo catch e per gli errori che potrebbe dare il caricamento del immagine del utente
			System.err.println("[" +
				Thread.currentThread().getName() +
				"]: C'e' stato un errore nell'apertura/lettura/chiusura delle immagini nella visualizzazione aste concluse. "
				+ e.getMessage()
			);
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private void eliminaLotto() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'eliminaLotto'");
	}

	private void eliminaArticolo() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'eliminaArticolo'");
	}

	private void verificaAdmin() {
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		rispostaUscente.tipoRisposta = TipoRisposta.OK;
		rispostaUscente.payload = new Object[]{ Boolean.valueOf(admin) };
	}

	private void visualizzaPuntate() {
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		Integer idAstaInput;

		try {
			idAstaInput = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idAsta"};
			return;
		}

		if (idAstaInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idAsta"};
			return;
		}

		String queryControlloAsta = "SELECT 1\n" +
			"FROM Aste\n" +
			"WHERE Id_asta = ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement statement = connection.prepareStatement(queryControlloAsta);
			statement.setInt(1, idAstaInput);
			ResultSet resultSet = statement.executeQuery();

			if (!resultSet.next()) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
				return;
			}
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di controllo sull'idAsta nella visualizzazione puntata. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
			return;
		}

		String queryVisualizzazione = "SELECT Puntate.Id_puntata, Puntate.data_ora_effettuazione, " +
			"Puntate.valore, Puntate.Rif_utente, Utenti.Email\n" +
			"FROM Puntate\n" +
			"JOIN Utenti ON Puntate.Rif_utente = Utenti.Id_utente\n" +
			"WHERE Rif_asta = ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement statement = connection.prepareStatement(queryVisualizzazione);
			statement.setInt(1, idAstaInput);
			ResultSet result = statement.executeQuery();

			ArrayList<Object> puntate = new ArrayList<>();

			while (result.next()) {
				puntate.add(result.getInt("Id_puntata"));
				puntate.add(result.getFloat("valore"));
				puntate.add(result.getTimestamp("data_ora_effettuazione").toLocalDateTime());
				puntate.add(result.getInt("Rif_utente"));
				puntate.add(result.getString("Email"));
			}

			rispostaUscente.tipoRisposta = TipoRisposta.OK;
			rispostaUscente.payload = puntate.toArray();
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di visualizzazione puntate. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
			return;
		}
	}

	// Metodo Visualizza lotto 1.0
	private void visualizzaLotto() {
		// controllo se l'utente e conesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		// Funzione per avere un'asta 
		Integer idLottoInput;

		try {
			idLottoInput = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idLotto"};
			return;
		}

		if (idLottoInput == null || idLottoInput <= 1) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idLotto"};
			return;
		}

		try (Connection connection = gestoreDatabase.getConnection();) {
			String queryLotto = "SELECT nome\n" +
				"FROM Lotti\n" +
				"WHERE Id_lotto = ?;"
			;

			String queryArticoli = "SELECT Id_articolo, nome\n" +
				"FROM Articoli\n" +
				"WHERE Rif_lotto = ?;"
			;

			String queryImmagini = "SELECT Id_immagine\n" +
				"FROM Immagini\n" +
				"WHERE Rif_articolo = ?;\n"
			;

			PreparedStatement preparedStatement = connection.prepareStatement(queryLotto);
			preparedStatement.setInt(1, idLottoInput);
			ResultSet resultSet = preparedStatement.executeQuery();

			// Non esiste lotto con id dato
			if (!resultSet.next()) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idLotto" };
				return;
			}

			rispostaUscente.tipoRisposta = TipoRisposta.OK;
			rispostaUscente.payload = new Object[]{ idLottoInput,
				resultSet.getString("nome"),
				null,
				null,	
			};

			preparedStatement = connection.prepareStatement(queryArticoli);
			preparedStatement.setInt(1, idLottoInput);
			resultSet = preparedStatement.executeQuery();

			ArrayList<Object> articoli = new ArrayList<>();
			ArrayList<byte[]> immaginiArticoli = new ArrayList<>();
			
			while (resultSet.next()) {
				int idArticolo = resultSet.getInt("Id_articolo");
				articoli.add(idArticolo);
				articoli.add(resultSet.getString("nome"));

				PreparedStatement preparedStatement2 = connection.prepareStatement(queryImmagini);
				preparedStatement2.setInt(1, idArticolo);
				ResultSet resultSet2 = preparedStatement2.executeQuery();

				while (resultSet2.next()) {
					int idImmagine = resultSet2.getInt("Id_immagine");

					String nomeFile = "res\\immagini_articoli\\" + idImmagine + ".png";

					try (FileInputStream stream = new FileInputStream(nomeFile);) {
						immaginiArticoli.add(stream.readAllBytes());
					}
				}
			}

			if (immaginiArticoli.isEmpty()) {
				try (FileInputStream stream = new FileInputStream("static_resources\\default_articolo.png");) {
					immaginiArticoli.add(stream.readAllBytes());
				}
			}

			rispostaUscente.payload[2] = immaginiArticoli.toArray(new byte[0][]);
			rispostaUscente.payload[3] = articoli.toArray();
		} catch (SQLException e) { // questo catch e per gli errori che potrebbe dare la query 
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di vissualizazione Lotto. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) { // questo catch e per gli errori che potrebbe dare il caricamento del immagine del utente
			System.err.println("[" +
				Thread.currentThread().getName() +
				"]: C'e' stato un errore nell'apertura/scrittura/chiusura delle immagini del lotto. "
				+ e.getMessage()
			);
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	// Metodo visualizza lotti 
	private void visualizzaLotti() {
		// conmtrollo se l'utente e connesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		// Funzione per avere il numero dei lotti 
		Integer numeroLotti;

		try {
			numeroLotti = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroLotti"};
			return;
		}

		if (numeroLotti == null || numeroLotti <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroLotti"};
			return;
		}

		// Funzione per avere il numero della pagina 
		Integer numeroPagina;

		try {
			numeroPagina = (Integer)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		if (numeroPagina == null || numeroLotti <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		// Funzione per ls ricerca dei lotti 
		String stringaRicerca;

		try {
			stringaRicerca = (String)richiestaEntrante.payload[2];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		if (stringaRicerca == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		// Funzione per avere le categoria 
		Integer idCategoriaInput;

		try {
			idCategoriaInput = (Integer)richiestaEntrante.payload[3];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		if (idCategoriaInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		boolean categoriaSpecificata = idCategoriaInput != 0;

		if (categoriaSpecificata) {
			// controllo se la categoria presa esiste 
			String queryControlloCategoria = "SELECT Id_categoria\n" +
				"FROM Categorie\n" + 
				"WHERE Id_categoria = ?;"
			;

			try (Connection connection = gestoreDatabase.getConnection();) {
				PreparedStatement preparedStatement = connection.prepareStatement(queryControlloCategoria);
				preparedStatement.setInt(1, idCategoriaInput);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (!resultSet.next()) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria" };
					return;
				}
			} catch (SQLException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: C'e' stato un errore nella query di controllo dell'idCategoria nella visualizzazione dei lotti. " + e.getMessage()
				);

				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
				return;
			}
		}

		Boolean assegnabili;

		try {
			assegnabili = (Boolean)richiestaEntrante.payload[4];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "assegnabili"};
			return;
		}

		if (assegnabili == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "assegnabili"};
			return;
		}

		// Impostazione della query finale
		String queryVisualizzazione = "SELECT L.Id_lotto, L.nome, Immagini.Id_immagine\n" + 
			"FROM Lotti AS L";
		
		queryVisualizzazione += "\n" +
			"JOIN Articoli ON Articoli.Rif_lotto = L.Id_lotto\n" +
			"LEFT JOIN Immagini ON Immagini.Rif_articolo = Articoli.Id_articolo\n" +
			"WHERE Articoli.Rif_utente = ? AND\n"
		;

		queryVisualizzazione += "L.nome LIKE ? AND\n" + 
			"Immagini.principale = 1 AND\n" +
			"L.Id_lotto != 1"
		;

		if (assegnabili) {
			queryVisualizzazione += " AND\n" +
				"(NOT EXISTS (\n" +
					"SELECT 1\n" +
					"FROM Aste\n" +
					"WHERE Rif_lotto = L.Id_lotto AND DATE_ADD(data_ora_inizio, INTERVAL durata MINUTE) > CURRENT_TIMESTAMP\n" +
				") AND NOT EXISTS (\n" +
					"SELECT 1\n" +
					"FROM Aste\n" +
					"JOIN Puntate ON Aste.Id_asta = Puntate.Rif_asta\n" +
					"WHERE Rif_lotto = L.Id_lotto\n" +
				"))"
			;
		}

		if (categoriaSpecificata) {
			queryVisualizzazione += " AND\n" +
				"Articoli.Rif_categoria = ?"
			;
		}

		queryVisualizzazione +=	"\n" +
			"GROUP BY L.Id_lotto\n" +
			"LIMIT ? OFFSET ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(queryVisualizzazione);
			preparedStatement.setInt(1, idUtente);
			preparedStatement.setString(2, "%"+ stringaRicerca+ "%");
			
			if (categoriaSpecificata) {
				preparedStatement.setInt(3, idCategoriaInput);
				preparedStatement.setInt(4, numeroLotti);
				preparedStatement.setInt(5, ((numeroPagina-1) * numeroLotti));
			} else {
				preparedStatement.setInt(3, numeroLotti);
				preparedStatement.setInt(4, ((numeroPagina-1) * numeroLotti));
			}

			
			ResultSet resultSet = preparedStatement.executeQuery();

			// array list per gli oggetti dei lotti 
			ArrayList<Object> lotti = new ArrayList<>();

			// While per caricare l'array list 
			while (resultSet.next()) {
				lotti.add(resultSet.getInt("Id_lotto"));
				lotti.add(resultSet.getString("nome"));
				
				int idImmagine= resultSet.getInt("Id_immagine");

				String nomeFile = resultSet.wasNull() ? 
					"static_resources\\default_articolo.png" :
					"res\\immagini_articoli\\"+ idImmagine+ ".png"
				;

				try (FileInputStream stream = new FileInputStream(nomeFile);) {
					lotti.add(stream.readAllBytes());
				}
			}

			// Transformazione del array list in array e risposta nel payload uscita 
			rispostaUscente.tipoRisposta= TipoRisposta.OK;
			rispostaUscente.payload = lotti.toArray();

		} catch (SQLException e) { // questo catch e per gli errori che potrebbe dare la query 
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di visualizzazione lotti. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) { // questo catch e per gli errori che potrebbe dare il caricamento del immagine del utente
			System.err.println("[" +
				Thread.currentThread().getName() +
				"]: C'e' stato un errore nell'apertura/lettura/chiusura delle immagini dei lotti. "
				+ e.getMessage()
			);
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private void visualizzaCategorie() {
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		String queryVisualizzazione = "SELECT Id_categoria, nome\n" +
			"FROM Categorie\n" +
			"WHERE Id_categoria != 1\n" +
			"ORDER BY nome ASC;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(queryVisualizzazione);

			ArrayList<Object> buffer = new ArrayList<>();

			buffer.add(1);
			buffer.add("Altre Categorie");

			while (resultSet.next()) {
				buffer.add(Integer.valueOf(resultSet.getInt("Id_categoria")));
				buffer.add(resultSet.getString("nome"));
			}
			
			rispostaUscente.tipoRisposta = TipoRisposta.OK;
			rispostaUscente.payload = buffer.toArray();
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di visualizzazione delle categorie. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}
	//Metodo visualizza articolo 1.0
	private void visualizzaArticolo() {
		// controllo se l'utente e conesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		// Funzione per avere un'asta 
		Integer idArticoloInput;

		try {
			idArticoloInput = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idArticolo"};
			return;
		}

		if (idArticoloInput == null || idArticoloInput <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idArticolo"};
			return;
		}

		try (Connection connection = gestoreDatabase.getConnection();) {
			String queryArticolo = "SELECT Articoli.nome, Articoli.condizione, Articoli.descrizione,\n" +
					"Articoli.Rif_lotto, Articoli.Rif_utente, Utenti.email\n" +
				"FROM Articoli\n" +
				"JOIN Utenti ON Articoli.Rif_utente = Utenti.Id_utente\n" +
				"WHERE Id_articolo = ?;"
			;

			String queryImmagini = "SELECT Id_immagine\n" +
				"FROM Immagini\n" +
				"WHERE Rif_articolo = ?;\n"
			;

			PreparedStatement preparedStatement = connection.prepareStatement(queryArticolo);
			preparedStatement.setInt(1, idArticoloInput);
			ResultSet resultSet = preparedStatement.executeQuery();

			// Non esiste lotto con id dato
			if (!resultSet.next()) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idArticolo" };
				return;
			}

			rispostaUscente.tipoRisposta = TipoRisposta.OK;
			rispostaUscente.payload = new Object[]{ idArticoloInput,
				resultSet.getString("nome"),
				resultSet.getString("condizione"),
				resultSet.getString("descrizione"),
				resultSet.getInt("Rif_lotto"),
				null,
				resultSet.getInt("Rif_utente"),
				resultSet.getString("email")	
			};

			preparedStatement = connection.prepareStatement(queryImmagini);
			preparedStatement.setInt(1, idArticoloInput);
			resultSet = preparedStatement.executeQuery();

			ArrayList<byte[]> immaginiArticoli = new ArrayList<>();
			
			while (resultSet.next()) {
				int idImmagine = resultSet.getInt("Id_immagine");

				String nomeFile = "res\\immagini_articoli\\" + idImmagine + ".png";

				try (FileInputStream stream = new FileInputStream(nomeFile);) {
					immaginiArticoli.add(stream.readAllBytes());
				}
			}

			if (immaginiArticoli.isEmpty()) {
				try (FileInputStream stream = new FileInputStream("static_resources\\default_articolo.png");) {
					immaginiArticoli.add(stream.readAllBytes());
				}
			}

			rispostaUscente.payload[5] = immaginiArticoli.toArray(new byte[0][]);
		} catch (SQLException e) { // questo catch e per gli errori che potrebbe dare la query 
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di vissualizazione articolo. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) { // questo catch e per gli errori che potrebbe dare il caricamento del immagine del utente
			System.err.println("[" +
				Thread.currentThread().getName() +
				"]: C'e' stato un errore nell'apertura/scrittura/chiusura delle immagini dell'articolo. "
				+ e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
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
		// Controlla se l'utente e' connesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		// Funzione per prendere un'asta 
		Integer idAstaInput;

		try {
			idAstaInput = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idAsta"};
			return;
		}

		if (idAstaInput == null || idAstaInput <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idAsta"};
			return;
		}

		String queryControlloAsta = "SELECT 1\n" +
			"FROM Aste AS A\n" +
			"WHERE Id_asta = ? AND\n" +
				"(CURRENT_TIMESTAMP > data_ora_inizio) AND\n" +
				"(CURRENT_TIMESTAMP < DATE_ADD(data_ora_inizio, INTERVAL durata MINUTE)) AND\n" +
				"descrizione_annullamento IS NULL AND\n" +
				"NOT EXISTS (\n" +
					"SELECT 1\n" +
					"FROM Aste\n" +
					"JOIN Lotti ON Aste.Rif_lotto = Lotti.Id_lotto\n" +
					"JOIN Articoli ON Lotti.Id_lotto = Articoli.Rif_lotto\n" +
					"WHERE Aste.Id_asta = A.Id_asta AND Articoli.Rif_utente = ?\n" +
				")\n" +
			";"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(queryControlloAsta);
			preparedStatement.setInt(1, idAstaInput);
			preparedStatement.setInt(2, idUtente);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
				return;
			}
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di controllo sull'idAsta nell'effetta puntata. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
			return;
		}

		// Funzione per prendere un valore 
		Float valoreInput;

		try {
			valoreInput = (Float)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "valore"};
			return;
		}

		if (valoreInput == null || valoreInput <= 0 || valoreInput >= 1000000000000.0f) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "valore"};
			return;
		}

		String queryControlloValore = "SELECT MAX(Puntate.valore) AS prezzo_attuale\n" +
			"FROM Puntate\n" +  
			"WHERE Rif_asta = ?;"
		;

		String queryControlloPrezzoInizio = "SELECT prezzo_inizio\n" +
			"FROM Aste\n" +
			"WHERE Id_asta = ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();){
			PreparedStatement preparedStatement = connection.prepareStatement(queryControlloValore);
			preparedStatement.setInt(1, idAstaInput);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				if (valoreInput <= resultSet.getFloat("prezzo_attuale")) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
					return;
				}
			} else {
				preparedStatement = connection.prepareStatement(queryControlloPrezzoInizio);
				preparedStatement.setInt(1, idAstaInput);
				resultSet = preparedStatement.executeQuery();
				resultSet.next();

				if (valoreInput <= resultSet.getFloat("prezzo_inizio")) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
					return;
				}
			}
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di controllo di valore nell'effetua puntata. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
			return;
		}

		String queryPuntata = "INSERT INTO Puntate(valore, Rif_asta, Rif_utente)\n" +
			"VALUES (?, ?, ?);"
		;

		String queryAsta = "SELECT ip_multicast\n" + 
			"FROM Aste\n" +
			"WHERE Id_asta = ?;"
		;

		Connection connection = null;

		try {
			String queryDataOra = "SELECT data_ora_effettuazione\n" +
				"FROM Puntate\n" +
				"WHERE Id_puntata = ?;"
			;

			connection = gestoreDatabase.getConnection();
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(queryPuntata, new String[]{ "Id_puntata" });
			statement.setFloat(1, valoreInput);
			statement.setInt(2, idAstaInput);
			statement.setInt(3, idUtente);
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			int idPuntata = resultSet.getInt(1);

			statement = connection.prepareStatement(queryDataOra);
			statement.setInt(1, idPuntata);
			resultSet = statement.executeQuery();
			resultSet.next();
			LocalDateTime dataOraEffettuazione = resultSet.getTimestamp("data_ora_effettuazione").toLocalDateTime();

			statement = connection.prepareStatement(queryAsta);
			statement.setInt(1, idAstaInput);
			resultSet = statement.executeQuery();
			resultSet.next();
			InetAddress indirizzoMulticast = InetAddress.getByAddress(resultSet.getBytes("ip_multicast"));

			gestoreAste.effettuaPuntata(idAstaInput,
				indirizzoMulticast,
				socket.getLocalAddress(),
				new Offerta(idUtente, valoreInput, dataOraEffettuazione)
			);

			connection.commit();
			rispostaUscente.tipoRisposta = TipoRisposta.OK;
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					System.err.println("[" + Thread.currentThread().getName() +
						"]: C'e' stato un errore nel rollback nell'effettuazione puntata. " + e1.getMessage()
					);
				}
			}

			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di effettuazione puntata. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					System.err.println("[" + Thread.currentThread().getName() +
						"]: C'e' stato un errore nel rollback nell'effettuazione puntata. " + e1.getMessage()
					);
				}
			}

			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore interno nella diffusione della puntata. " + e.getMessage()
			);
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: C'e' stato un errore nel reset dell'autocommit nell'effettuazione della puntata. " + e.getMessage()
				);
			}

			try {
				connection.close();
			} catch (SQLException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: C'e' stato un errore nella chiusura della connessione col DB in effettua puntata. " + e.getMessage()
				);
			}
		}
	}

	private void creaLotto() {
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		String nomeInput;

		try {
			nomeInput = (String)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "nome"};
			return;
		}

		if (nomeInput == null || nomeInput.equals("")) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "nome"};
			return;
		}

		int[] idArticoliInput;

		try {
			idArticoliInput = (int[])richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idArticoli"};
			return;
		}

		if (idArticoliInput == null || idArticoliInput.length == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idArticoli"};
			return;
		}

		String queryControlloArticoli = "SELECT Articoli.Id_articolo\n" +
			"FROM Utenti\n" +
			"JOIN Articoli ON Articoli.Rif_utente = Utenti.Id_utente\n" +
			"WHERE Articoli.Rif_lotto = 1 AND Utenti.Id_utente = ?;"
		;

		ArrayList<Integer> articoliUtente = new ArrayList<>();

		try (Connection connection = gestoreDatabase.getConnection();){
			PreparedStatement statement = connection.prepareStatement(queryControlloArticoli);
			statement.setInt(1, idUtente);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				articoliUtente.add(resultSet.getInt("Id_articolo"));
			}
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di creazione del lotto. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
			return;
		}

		for (int i = 0; i < idArticoliInput.length; ++i) {
			if (!articoliUtente.contains(idArticoliInput[i])) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idArticoli" };
				return;
			}
		}

		String queryCreazioneLotto = "INSERT INTO Lotti(nome)\n" +
			"VALUES (?);"
		;

		StringBuilder builder = new StringBuilder("UPDATE Articoli\n" +
			"SET Rif_lotto = ?\n" +
			"WHERE Id_articolo IN (?");
		
		for (int i = 1; i < idArticoliInput.length; ++i) {
			builder.append(", ?");
		}

		builder.append(");");
		
		String queryAssegnazioneArticoli = builder.toString();

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement statement = connection.prepareStatement(queryCreazioneLotto, new String[]{ "Id_lotto" });
			statement.setString(1, nomeInput);
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();

			if (!resultSet.next()) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
				return;
			}

			int idLotto = resultSet.getInt(1);

			statement = connection.prepareStatement(queryAssegnazioneArticoli);
			statement.setInt(1, idLotto);
			
			for (int i = 0; i < idArticoliInput.length; ++i) {
				statement.setInt(i + 2, idArticoliInput[i]);
			}

			statement.executeUpdate();

			rispostaUscente.tipoRisposta = TipoRisposta.OK;
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di creazione lotto. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private void creaCategoria() {
		if (idUtente == 0 || !admin) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		String nomeInput;

		try {
			nomeInput = (String)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "nome"};
			return;
		}

		if (nomeInput == null || nomeInput.equals("")) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "nome"};
		
			return;
		}

		String queryValidazione = "SELECT Id_categoria\n" +
			"FROM Categorie\n" +
			"WHERE nome = ?;"
		;

		String queryCreazione = "INSERT INTO Categorie(nome)\n" +
			"VALUES (?);"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(queryValidazione);
			preparedStatement.setString(1, nomeInput);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
				return;
			}

			preparedStatement = connection.prepareStatement(queryCreazione, new String[]{ "Id_categoria" });
			preparedStatement.setString(1, nomeInput);
			preparedStatement.executeUpdate();
			resultSet = preparedStatement.getGeneratedKeys();

			while (resultSet.next()) {
				rispostaUscente.tipoRisposta = TipoRisposta.OK;
				rispostaUscente.payload = new Object[] { Integer.valueOf(resultSet.getInt(1)) };
				return;
			}

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di creazione categoria. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private void creaArticolo() {
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		String nomeInput;

		try {
			nomeInput = (String)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "nome"};
			return;
		}

		if (nomeInput == null || nomeInput.equals("")) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "nome"};
			return;
		}

		String condizioneInput;

		try {
			condizioneInput = (String)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "condizione"};
			return;
		}

		if (condizioneInput == null || condizioneInput.equals("")) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "condizione"};
			return;
		}

		String descrizioneInput;

		try {
			descrizioneInput = (String)richiestaEntrante.payload[2];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "descizione"};
			return;
		}

		if (descrizioneInput == null || descrizioneInput.equals("")) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "descrizione"};
			return;
		}

		Integer idLottoInput;

		try {
			idLottoInput = (Integer)richiestaEntrante.payload[3];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idLotto"};
			return;
		}

		if (idLottoInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idLotto"};
			return;
		}

		if (idLottoInput != 1) { // default
			String queryControlloLotto = "SELECT DISTINCT Lotti.Id_lotto\n" +
				"FROM Lotti\n" +
				"JOIN Articoli ON Lotti.Id_lotto = Articoli.Rif_lotto\n" +
				"JOIN Utenti ON Articoli.Rif_utente = Utenti.Id_utente\n" +
				"WHERE Utenti.Id_utente = ? AND Lotti.Id_lotto = ?;"
			;

			try (Connection connection = gestoreDatabase.getConnection();){
				PreparedStatement preparedStatement = connection.prepareStatement(queryControlloLotto);
				preparedStatement.setInt(1, idUtente);
				preparedStatement.setInt(2, idLottoInput);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (!resultSet.next()) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idLotto" };
					return;
				}
			} catch (SQLException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: C'e' stato un errore nella query di controllo dell'idLotto nella creazione dell'articolo. " + e.getMessage()
				);

				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
				return;
			}
		}

		byte[][] immaginiArticoloInput;

		try {
			immaginiArticoloInput = (byte[][])richiestaEntrante.payload[4];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "immaginiArticolo"};
			return;
		}

		if (immaginiArticoloInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "immaginiArticolo"};
			return;
		}

		for (int i = 0; i < immaginiArticoloInput.length; ++i) {
			if (immaginiArticoloInput[i] == null) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "immaginiArticolo[" + i + "]" };
				return;
			}
		}

		Integer immaginePrincipaleInput;

		try {
			immaginePrincipaleInput = (Integer)richiestaEntrante.payload[5];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "immaginePrincipale"};
			return;
		}

		if (immaginePrincipaleInput == null || immaginePrincipaleInput < 0 || (immaginePrincipaleInput >= immaginiArticoloInput.length && immaginePrincipaleInput != 0)) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "immaginePrincipale"};
			return;
		}

		Integer idCategoriaInput;

		try {
			idCategoriaInput = (Integer)richiestaEntrante.payload[6];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		if (idCategoriaInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		String queryControlloCategoria = "SELECT Id_categoria\n" +
			"FROM Categorie\n" + 
			"WHERE Id_categoria = ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(queryControlloCategoria);
			preparedStatement.setInt(1, idCategoriaInput);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria" };
				return;
			}
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di controllo dell'idCategoria nella creazione dell'articolo. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}

		Integer quantitaInput;

		try {
			quantitaInput = (Integer)richiestaEntrante.payload[6];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "quantita"};
			return;
		}

		if (quantitaInput == null || quantitaInput <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "quantita"};
			return;
		}

		String queryCreazioneArticolo = "INSERT INTO Articoli(nome, condizione, descrizione, Rif_lotto, Rif_utente, Rif_categoria, quantita)\n" +
				"VALUES (?, ?, ?, ?, ?, ?, ?);"
			;

		String queryCreazioneImmagine = "INSERT INTO Immagini(principale, Rif_articolo)\n" +
			"VALUES (?, ?);"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement statement = connection.prepareStatement(queryCreazioneArticolo, new String[]{ "Id_articolo" });
			statement.setString(1, nomeInput);
			statement.setString(2, condizioneInput);
			statement.setString(3, descrizioneInput);
			statement.setInt(4, idLottoInput);
			statement.setInt(5, idUtente);
			statement.setInt(6, idCategoriaInput);
			statement.setInt(7, quantitaInput);
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();

			if (!resultSet.next()) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: Non è stato possibile ottenere la chiava autogenerata dalla creazione dell'articolo."
				);

				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
				return;
			}

			int chiaveArticolo = resultSet.getInt(1);

			for (int i = 0; i < immaginiArticoloInput.length; ++i) {
				statement = connection.prepareStatement(queryCreazioneImmagine, new String[] { "Id_immagine" });
				statement.setInt(1, immaginePrincipaleInput == i ? 1 : 0);
				statement.setInt(2, chiaveArticolo);
				statement.executeUpdate();
				resultSet = statement.getGeneratedKeys();

				if (!resultSet.next()) {
					System.err.println("[" + Thread.currentThread().getName() +
						"]: Non è stato possibile ottenere la chiava autogenerata dalla creazione dell'immagine."
					);
	
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
					return;
				}

				int chiaveImmagine = resultSet.getInt(1);

				FileOutputStream stream = new FileOutputStream("res\\immagini_articoli\\" + chiaveImmagine + ".png");
				stream.write(immaginiArticoloInput[i]);
				stream.close();
			}

			rispostaUscente.tipoRisposta = TipoRisposta.OK;
		} catch (IOException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella scrittura/chiusura dell'immagine di un articolo. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di creazione dell'articolo. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO};
		}
	}

	private void login() {
		String emailInput;
		
		try {
			emailInput = (String)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "email"};
			return;
		}

		String passwordInput;
		
		try {
			passwordInput = (String)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "password"};
			return;
		}

		String queryUtenti = "SELECT Id_utente, email, sale_password, hash_password, utente_admin\n" +
			"FROM Utenti;"
		;

		try (Connection connection = gestoreDatabase.getConnection();){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(queryUtenti);

			while (resultSet.next()) {
				Integer idUtente = resultSet.getInt("Id_utente");
				String email = resultSet.getString("email");
				byte[] salePassword = resultSet.getBytes("sale_password");
				byte[] hashPassword = resultSet.getBytes("hash_password");

				if (emailInput.equals(email) && verificaPassword(passwordInput, salePassword, hashPassword)) {
					this.idUtente = idUtente;
					admin = resultSet.getInt("utente_admin") == 1;
					rispostaUscente.tipoRisposta = TipoRisposta.OK;
					rispostaUscente.payload = new Object[]{ idUtente };
					return;
				}
			}

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() + "]: C'e' stato un errore nella query di login. " + e.getMessage());
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private boolean verificaPassword(String password, byte[] sale, byte[] hashRisultante) {
		return Arrays.equals(hashRisultante, generaPassword(password, sale));
	}

	private void registrazione() {
		String nomeInput;
		
		try {
			nomeInput = (String)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "nome" };
			return;
		}

		String cognomeInput;
		
		try {
			cognomeInput = (String)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "cognome" };
			return;
		}

		String passwordInput;

		try {
			passwordInput = (String)richiestaEntrante.payload[2];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "password" };
			return;
		}

		/*
		password must contain 1 number (0-9)
		password must contain 1 uppercase letters
		password must contain 1 lowercase letters
		password must contain 1 non-alpha numeric number
		password is 8-16 characters with no space
		*/
		if (!Pattern.matches("^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,16}$", passwordInput)) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "password" };
			return;
		}

		LocalDate dataNascitaInput;
		
		try {
			dataNascitaInput = (LocalDate)richiestaEntrante.payload[3];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "dataNascita" };
			return;
		}

		if (dataNascitaInput.isAfter(LocalDate.now())) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "dataNascita" };
			return;
		}

		String cittaResidenzaInput;
		
		try {
			cittaResidenzaInput = (String)richiestaEntrante.payload[4];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "cittaResidenza" };
			return;
		}

		Integer capInput;
		
		try {
			capInput = (Integer)richiestaEntrante.payload[5];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "cap" };
			return;
		}

		/*
		Il CAP e un codice di 5 cifre. 
		*/
		if (capInput < 0 || capInput > 100000) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "cap" };
			return;
		}

		String indirizzoInput;

		try {
			indirizzoInput = (String)richiestaEntrante.payload[6];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "indirizzo" };
			return;
		}

		String emailInput;

		try {
			emailInput = (String)richiestaEntrante.payload[7];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "email" };
			return;
		}

		/*
		The email couldn't start or finish with a dot
		The email shouldn't contain spaces into the string
		The email shouldn't contain special chars (<:, *,ecc)
		The email could contain dots in the middle of mail address before the @
		The email could contain a double doman ( '.de.org' or similar rarity)
		*/
		if (!Pattern.matches("^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$", emailInput)) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "email" };
			return;
		}

		String ibanInput;
		
		try {
			ibanInput = (String)richiestaEntrante.payload[8];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "iban" };
			return;
		}

		/*
		Alfanumerico tra 2 e 34 lettere (mauiscole).
		*/
		if (!Pattern.matches("^[A-Z0-9]{2,34}$", ibanInput)) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "iban" };
			return;
		}

		DatiPassword password = generaPassword(passwordInput);

		String queryRegistrazione = "INSERT INTO Utenti(nome, cognome, data_nascita, citta_residenza, " + 
			"cap, indirizzo, email, sale_password, " +
			"hash_password, iban)\n" +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement statement = connection.prepareStatement(queryRegistrazione);
			statement.setString(1, nomeInput);
			statement.setString(2, cognomeInput);
			statement.setDate(3, java.sql.Date.valueOf(dataNascitaInput));
			statement.setString(4, cittaResidenzaInput);
			statement.setInt(5, capInput);
			statement.setString(6, indirizzoInput);
			statement.setString(7, emailInput);
			statement.setBytes(8, password.sale);
			statement.setBytes(9, password.hash);
			statement.setString(10, ibanInput);
			statement.executeUpdate();
			rispostaUscente.tipoRisposta = TipoRisposta.OK;
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di registrazione." + e.getMessage()
			);
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private byte[] generaPassword(String password, byte[] sale) {
		KeySpec specification = new PBEKeySpec(password.toCharArray(), sale, 65535, 512);
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

		return hash;
	}

	private DatiPassword generaPassword(String password) {
		SecureRandom random = new SecureRandom();
		byte[] sale = new byte[16];
		random.nextBytes(sale);

		return new DatiPassword(sale, generaPassword(password, sale));
	}

	private static class DatiPassword {
		public byte[] sale;
		public byte[] hash;

		public DatiPassword(byte[] sale, byte[] hash) {
			this.sale = sale;
			this.hash = hash;
		}
	} 

	private void logout() {
		// Implementazione del logout
		if (idUtente == 0)
		{
			rispostaUscente.tipoRisposta = Risposta.TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[] {Risposta.TipoErrore.OPERAZIONE_INVALIDA};
			return;
		}
		
		idUtente = 0;
		rispostaUscente.tipoRisposta = Risposta.TipoRisposta.OK;
	}

	private void visualizzaProfilo() {
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		Integer idUtenteInput;

		try {
			idUtenteInput = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idUtente"};
			return;
		}

		if (idUtenteInput == null || idUtenteInput < 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idUtente"};
			return;
		}

		try (Connection connection = gestoreDatabase.getConnection();) {
			if (idUtenteInput == 0) {
				String queryVisualizzazione = "SELECT nome, cognome, data_nascita, citta_residenza, cap, indirizzo, email, iban\n" +
					"FROM Utenti\n" +
					"WHERE Id_utente = ?"
				;
	
				PreparedStatement preparedStatement = connection.prepareStatement(queryVisualizzazione);
				preparedStatement.setInt(1, idUtente);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (!resultSet.next()) {
					System.err.println("[" + Thread.currentThread().getName() +
						"]: C'e' stato un errore nell'ottenere il profilo di un'utente. "
					);

					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
				}

				rispostaUscente.tipoRisposta = TipoRisposta.OK;
				rispostaUscente.payload = new Object[]{
					resultSet.getString("nome"),
					resultSet.getString("cognome"),
					resultSet.getDate("data_nascita").toLocalDate(),
					resultSet.getString("citta_residenza"),
					resultSet.getInt("cap"),
					resultSet.getString("indirizzo"),
					resultSet.getString("email"),
					resultSet.getString("iban")
				};
			} else {
				String queryVisualizzazione = "SELECT nome, cognome, email\n" +
					"FROM Utenti\n" +
					"WHERE Id_utente = ?;"
				;

				PreparedStatement preparedStatement = connection.prepareStatement(queryVisualizzazione);
				preparedStatement.setInt(1, idUtenteInput);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (!resultSet.next()) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idUtente" };
					return;
				}

				rispostaUscente.tipoRisposta = TipoRisposta.OK;
				rispostaUscente.payload = new Object[]{
					resultSet.getString("nome"),
					resultSet.getString("cognome"),
					resultSet.getString("email")
				};
			}
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di controllo dell'idUtente nella visualizzazione del profilo. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private void visualizzaImmagineProfilo() {
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		Integer idInput;

		try {
			idInput = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idUtente" };
			return;
		}

		if (idInput == 0) {
			idInput = idUtente;
		}

		String queryUtenti = "SELECT immagine_profilo\n" +
			"FROM Utenti\n" +
			"WHERE Id_utente = ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement statement = connection.prepareStatement(queryUtenti);
			statement.setInt(1, idInput);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Integer immagineProfilo = resultSet.getInt("immagine_profilo");

				rispostaUscente.tipoRisposta = TipoRisposta.OK;

				String nomeFile = immagineProfilo == 1 ? 
					"res\\profili\\" + idInput + ".png" :
					"static_resources\\default_user.png"
				;

				try (FileInputStream stream = new FileInputStream(nomeFile);) {
					rispostaUscente.payload = new Object[]{ stream.readAllBytes() };
				}

				return;
			}

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() + "]: C'e' stato un errore nella query di visualizzazione immagine. " + e.getMessage());
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) {
			System.err.println("[" + Thread.currentThread().getName() + "]: C'e' stato un errore nell'apertura/scrittura/chiusura delle immagine profilo. " + e.getMessage());
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	@FunctionalInterface
	private static interface ConsumerSQL<T> {
		void accept(T t) throws SQLException;
	}

	private void modificaProfilo() 
    {
        String nomeInput;
		
		try {
			nomeInput = (String)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "nome" };
			return;
		}

		String cognomeInput;
		
		try {
			cognomeInput = (String)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "cognome" };
			return;
		}

		LocalDate dataNascitaInput;
		
		try {
			dataNascitaInput = (LocalDate)richiestaEntrante.payload[2];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "dataNascita" };
			return;
		}

		String cittaResidenzaInput;
		
		try {
			cittaResidenzaInput = (String)richiestaEntrante.payload[3];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "cittaResidenza" };
			return;
		}

		Integer capInput;
		
		try {
			capInput = (Integer)richiestaEntrante.payload[4];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "cap" };
			return;
		}

		String indirizzoInput;

		try {
			indirizzoInput = (String)richiestaEntrante.payload[5];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "indirizzo" };
			return;
		}

		String emailInput;

		try {
			emailInput = (String)richiestaEntrante.payload[6];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "email" };
			return;
		}

		String ibanInput;
		
		try {
			ibanInput = (String)richiestaEntrante.payload[7];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "iban" };
			return;
		}

		byte[] immagineProfiloInput;

		try {
			immagineProfiloInput = (byte[])richiestaEntrante.payload[8];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "immagineProfilo" };
			return;
		}

		try (Connection connection = gestoreDatabase.getConnection();) {
			ArrayList<ConsumerSQL<PreparedStatement>> modifiche = new ArrayList<>();

			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("UPDATE Utenti\n");
			stringBuilder.append("SET ");

			if (nomeInput != null) {
				stringBuilder.append("nome = ?, ");
				modifiche.add((PreparedStatement preparedStatement) -> {
					preparedStatement.setString(modifiche.size() + 1, nomeInput);
				});
			}

			if (cognomeInput != null) {
				stringBuilder.append("cognome = ?, ");
				modifiche.add((PreparedStatement preparedStatement) -> {
					preparedStatement.setString(modifiche.size() + 1, cognomeInput);
				});
			}

			if (dataNascitaInput != null) {
				if (dataNascitaInput.isAfter(LocalDate.now())) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "dataNascita" };
					return;
				}

				stringBuilder.append("data_nascita = ?, ");
				modifiche.add((PreparedStatement preparedStatement) -> {
					preparedStatement.setDate(modifiche.size() + 1, Date.valueOf(dataNascitaInput));
				});
			}

			if (cittaResidenzaInput != null) {
				stringBuilder.append("citta_residenza = ?, ");
				modifiche.add((PreparedStatement preparedStatement) -> {
					preparedStatement.setString(modifiche.size() + 1, cittaResidenzaInput);
				});
			}

			if (capInput != null) {
				// Il CAP e un codice di 5 cifre. 
				if (capInput < 0 || capInput > 100000) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "cap" };
					return;
				}

				stringBuilder.append("cap = ?, ");
				modifiche.add((PreparedStatement preparedStatement) -> {
					preparedStatement.setInt(modifiche.size() + 1, capInput);
				});
			}

			if (indirizzoInput != null) {
				stringBuilder.append("indirizzo = ?, ");
				modifiche.add((PreparedStatement preparedStatement) -> {
					preparedStatement.setString(modifiche.size() + 1, indirizzoInput);
				});
			}

			if (emailInput != null) {
				/*
				The email couldn't start or finish with a dot
				The email shouldn't contain spaces into the string
				The email shouldn't contain special chars (<:, *,ecc)
				The email could contain dots in the middle of mail address before the @
				The email could contain a double doman ( '.de.org' or similar rarity)
				*/
				if (!Pattern.matches("^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$", emailInput)) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "email" };
					return;
				}

				stringBuilder.append("email = ?, ");
				modifiche.add((PreparedStatement preparedStatement) -> {
					preparedStatement.setString(modifiche.size() + 1, emailInput);
				});
			}

			if (ibanInput != null) {
				// Alfanumerico tra 2 e 34 lettere (mauiscole).
				if (!Pattern.matches("^[A-Z0-9]{2,34}$", ibanInput)) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "iban" };
					return;
				}

				stringBuilder.append("iban = ?, ");
				modifiche.add((PreparedStatement preparedStatement) -> {
					preparedStatement.setString(modifiche.size() + 1, ibanInput);
				});
			}

			if (immagineProfiloInput != null) {
				if (immagineProfiloInput.length == 0) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "immagineProfilo" };
					return;
				}

				stringBuilder.append("immagine_profilo = ?, ");
				modifiche.add((PreparedStatement preparedStatement) -> {
					preparedStatement.setBoolean(modifiche.size() + 1, true);
				});

				try (FileOutputStream stream = new FileOutputStream("res\\profili\\" + idUtente + ".png");){
					stream.write(immagineProfiloInput);
				}
			}

			if (modifiche.size() == 0) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
				return;
			}

			stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "\nWHERE Id_utente = ?;");

			String queryModifica = stringBuilder.toString();
			PreparedStatement preparedStatement = connection.prepareStatement(queryModifica);

			for (ConsumerSQL<PreparedStatement> operazione : modifiche) {
				operazione.accept(preparedStatement);
			}

			preparedStatement.executeUpdate();
			
			rispostaUscente.tipoRisposta = TipoRisposta.OK;
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di modifica profilo." + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nell'apertura/scrittura/chiusura dell'immagine profilo nella modifica del profilo." + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
    }

	private int calcolaNumeroPagine(int elementiPagina, int elementiTotali) {
		return Math.ceilDiv(elementiPagina, elementiTotali);
	}

    // Implementazione della visualizzazione delle aste (pagina home)
    private void visualizzaAste() {
        // conmtrollo se l'utente e conesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		// Funzione per avere il numero delle aste 
		Integer numeroAste;

		try {
			numeroAste = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroAste"};
			return;
		}

		if (numeroAste == null || numeroAste <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroAste"};
			return;
		}

		// Funzione per avere il numero della pagina 
		Integer numeroPagina;

		try {
			numeroPagina = (Integer)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		if (numeroPagina == null || numeroAste <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		// Funzione per la ricerca delle aste 
		String stringaRicerca;

		try {
			stringaRicerca = (String)richiestaEntrante.payload[2];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		if (stringaRicerca == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		// Funzione per avere le categoria 
		Integer idCategoriaInput;

		try {
			idCategoriaInput = (Integer)richiestaEntrante.payload[3];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		if (idCategoriaInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		boolean categoriaSpecificata = idCategoriaInput != 0;

		if (categoriaSpecificata) {
			// controllo se la categoria presa esiste 
			String queryControlloCategoria = "SELECT Id_categoria\n" +
				"FROM Categorie\n" + 
				"WHERE Id_categoria = ?;"
			;

			try (Connection connection = gestoreDatabase.getConnection();) {
				PreparedStatement preparedStatement = connection.prepareStatement(queryControlloCategoria);
				preparedStatement.setInt(1, idCategoriaInput);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (!resultSet.next()) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria" };
					return;
				}
			} catch (SQLException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: C'e' stato un errore nella query di controllo dell'idCategoria nella visualizzazione delle aste. " + e.getMessage()
				);

				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
				return;
			}
		}

		// Impostazione della query finale 
		String queryVisualizzazione = "SELECT DISTINCT Aste.Id_asta, Aste.durata, Aste.data_ora_inizio, Salvataggi.Rif_utente, Aste.prezzo_inizio, MAX(Puntate.valore) AS prezzo_attuale, " +
			"Lotti.nome, Immagini.Id_immagine\n" + 
			"FROM Aste\n" +
			"LEFT JOIN Puntate ON Aste.Id_asta = Puntate.Rif_asta\n" +
			"JOIN Lotti ON Aste.Rif_lotto = Lotti.Id_lotto\n" +
			"JOIN Articoli ON Lotti.Id_lotto = Articoli.Rif_lotto\n" +
			"LEFT JOIN Salvataggi ON Aste.Id_asta = Salvataggi.Rif_asta AND Salvataggi.Rif_utente = ?\n" +
			"LEFT JOIN Immagini ON Immagini.Rif_articolo = Articoli.Id_articolo\n"+
			"WHERE (CURRENT_TIMESTAMP > Aste.data_ora_inizio) AND\n" +
			"(CURRENT_TIMESTAMP < DATE_ADD(Aste.data_ora_inizio, INTERVAL Aste.durata MINUTE)) AND\n"
		;
		
		if (categoriaSpecificata) {
			queryVisualizzazione += "Articoli.Rif_categoria = ? AND\n";
		}

		queryVisualizzazione += "Lotti.nome LIKE ?\n" +
			"AND Immagini.principale = 1\n" +
			"GROUP BY Aste.Id_asta\n" +
			"LIMIT ? OFFSET ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(queryVisualizzazione);
			if (categoriaSpecificata) {
				preparedStatement.setInt(1, idUtente);
				preparedStatement.setInt(2, idCategoriaInput);
				preparedStatement.setString(3, "%"+ stringaRicerca+ "%");
				preparedStatement.setInt(4, numeroAste);
				preparedStatement.setInt(5, ((numeroPagina-1)*numeroAste));
			} else {
				preparedStatement.setInt(1, idUtente);
				preparedStatement.setString(2, "%"+ stringaRicerca+ "%");
				preparedStatement.setInt(3, numeroAste);
				preparedStatement.setInt(4, ((numeroPagina-1)*numeroAste));
			}
			
			ResultSet resultSet = preparedStatement.executeQuery();

			// array list per gli oggetti delle aste 
			ArrayList<Object> aste= new ArrayList<>();

			// While per caricare l'array list 
			while (resultSet.next()) {
				aste.add(resultSet.getInt("Id_asta"));
				aste.add(Duration.ofMinutes(resultSet.getLong("durata")));
				
				float prezzo_attuale = resultSet.getFloat("prezzo_attuale");
				aste.add(prezzo_attuale == 0 ? resultSet.getFloat("prezzo_inizio") : prezzo_attuale);

				aste.add(resultSet.getString("nome"));
				
				int idImmagine = resultSet.getInt("Id_immagine");

				String nomeFile = resultSet.wasNull() ? 
					"static_resources\\default_articolo.png" :
					"res\\immagini_articoli\\"+ idImmagine + ".png"
				;

				try (FileInputStream stream = new FileInputStream(nomeFile);) {
					aste.add(stream.readAllBytes());
				}

				aste.add(resultSet.getTimestamp("data_ora_inizio").toLocalDateTime());
				aste.add(resultSet.getInt("Rif_utente") != 0);
			}

			// Transformazione del array list in array e risposta nel payload uscita 
			rispostaUscente.tipoRisposta= TipoRisposta.OK;
			rispostaUscente.payload = aste.toArray();

		} catch (SQLException e) { // questo catch e per gli errori che potrebbe dare la query 
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di visualizzazione aste. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) { // questo catch e per gli errori che potrebbe dare il caricamento del immagine del utente
			System.err.println("[" +
				Thread.currentThread().getName() +
				"]: C'e' stato un errore nell'apertura/lettura/chiusura delle immagini nella visualizzazione aste. "
				+ e.getMessage()
			);
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private void visualizzaAsteCorrenti()  {
		// controllo se l'utente e connesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		// Funzione per avere il numero delle aste 
		Integer numeroAste;

		try {
			numeroAste = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroAste"};
			return;
		}

		if (numeroAste == null || numeroAste <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroAste"};
			return;
		}

		// Funzione per avere il numero della pagina 
		Integer numeroPagina;

		try {
			numeroPagina = (Integer)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		if (numeroPagina == null || numeroAste <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		// Funzione per la ricerca delle aste 
		String stringaRicerca;

		try {
			stringaRicerca = (String)richiestaEntrante.payload[2];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		if (stringaRicerca == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		// Funzione per avere le categoria 
		Integer idCategoriaInput;

		try {
			idCategoriaInput = (Integer)richiestaEntrante.payload[3];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		if (idCategoriaInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		boolean categoriaSpecificata = idCategoriaInput != 0;

		if (categoriaSpecificata) {
			// controllo se la categoria presa esiste 
			String queryControlloCategoria = "SELECT Id_categoria\n" +
				"FROM Categorie\n" + 
				"WHERE Id_categoria = ?;"
			;

			try (Connection connection = gestoreDatabase.getConnection();) {
				PreparedStatement preparedStatement = connection.prepareStatement(queryControlloCategoria);
				preparedStatement.setInt(1, idCategoriaInput);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (!resultSet.next()) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria" };
					return;
				}
			} catch (SQLException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: C'e' stato un errore nella query di controllo dell'idCategoria nella visualizzazione delle aste correnti. " + e.getMessage()
				);

				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
				return;
			}
		}

		// Impostazione della query finale 
		String queryVisualizzazione = "SELECT DISTINCT Aste.Id_asta, Aste.durata, Aste.data_ora_inizio, Salvataggi.Rif_utente, Aste.prezzo_inizio, MAX(Puntate.valore) AS prezzo_attuale, " +
			"Lotti.nome, Immagini.Id_immagine\n" + 
			"FROM Aste\n" +
			"LEFT JOIN Puntate ON Aste.Id_asta = Puntate.Rif_asta\n" +
			"JOIN Lotti ON Aste.Rif_lotto = Lotti.Id_lotto\n" +
			"JOIN Articoli ON Lotti.Id_lotto = Articoli.Rif_lotto\n" +
			"LEFT JOIN Salvataggi ON Aste.Id_asta = Salvataggi.Rif_asta\n" +
			"LEFT JOIN Immagini ON Immagini.Rif_articolo = Articoli.Id_articolo\n"+
			"WHERE (CURRENT_TIMESTAMP > Aste.data_ora_inizio) AND\n" +
			"(CURRENT_TIMESTAMP < DATE_ADD(Aste.data_ora_inizio, INTERVAL Aste.durata MINUTE)) AND\n"
		;

		if (categoriaSpecificata) {
			queryVisualizzazione += "Articoli.Rif_categoria = ? AND\n";
		}

		queryVisualizzazione += "Lotti.nome LIKE ? AND\n" +
			"Immagini.principale = 1 AND\n" +
			"Articoli.Rif_utente = ?\n" +
			"GROUP BY Aste.Id_asta\n" +
			"LIMIT ? OFFSET ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(queryVisualizzazione);
			if (categoriaSpecificata) {
				preparedStatement.setInt(1, idCategoriaInput);
				preparedStatement.setString(2, "%"+ stringaRicerca+ "%");
				preparedStatement.setInt(3, idUtente);
				preparedStatement.setInt(4, numeroAste);
				preparedStatement.setInt(5, ((numeroPagina-1)*numeroAste));
			} else {
				preparedStatement.setString(1, "%"+ stringaRicerca+ "%");
				preparedStatement.setInt(2, idUtente);
				preparedStatement.setInt(3, numeroAste);
				preparedStatement.setInt(4, ((numeroPagina-1)*numeroAste));
			}
			
			ResultSet resultSet = preparedStatement.executeQuery();

			// array list per gli oggetti delle aste 
			ArrayList<Object> aste= new ArrayList<>();

			// While per caricare l'array list 
			while (resultSet.next()) {
				aste.add(resultSet.getInt("Id_asta"));
				aste.add(Duration.ofMinutes(resultSet.getLong("durata")));
				
				float prezzo_attuale = resultSet.getFloat("prezzo_attuale");
				aste.add(prezzo_attuale == 0 ? resultSet.getFloat("prezzo_inizio") : prezzo_attuale);

				aste.add(resultSet.getString("nome"));
				
				int idImmagine = resultSet.getInt("Id_immagine");

				String nomeFile = resultSet.wasNull() ? 
					"static_resources\\default_articolo.png" :
					"res\\immagini_articoli\\"+ idImmagine + ".png"
				;

				try (FileInputStream stream = new FileInputStream(nomeFile);) {
					aste.add(stream.readAllBytes());
				}

				aste.add(resultSet.getTimestamp("data_ora_inizio").toLocalDateTime());
				aste.add(resultSet.getInt("Rif_utente") != 0);
			}

			// Transformazione del array list in array e risposta nel payload uscita 
			rispostaUscente.tipoRisposta= TipoRisposta.OK;
			rispostaUscente.payload = aste.toArray();

		} catch (SQLException e) { // questo catch e per gli errori che potrebbe dare la query 
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di visualizzazione delle aste correnti. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) { // questo catch e per gli errori che potrebbe dare il caricamento del immagine del utente
			System.err.println("[" +
				Thread.currentThread().getName() +
				"]: C'e' stato un errore nell'apertura/lettura/chiusura delle immagini nella visualizzazione aste correnti. "
				+ e.getMessage()
			);
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private void visualizzaAsteProgrammate()  {
		// controllo se l'utente e connesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		// Funzione per avere il numero delle aste 
		Integer numeroAste;

		try {
			numeroAste = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroAste"};
			return;
		}

		if (numeroAste == null || numeroAste <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroAste"};
			return;
		}

		// Funzione per avere il numero della pagina 
		Integer numeroPagina;

		try {
			numeroPagina = (Integer)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		if (numeroPagina == null || numeroAste <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		// Funzione per la ricerca delle aste 
		String stringaRicerca;

		try {
			stringaRicerca = (String)richiestaEntrante.payload[2];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		if (stringaRicerca == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		// Funzione per avere le categoria 
		Integer idCategoriaInput;

		try {
			idCategoriaInput = (Integer)richiestaEntrante.payload[3];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		if (idCategoriaInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		boolean categoriaSpecificata = idCategoriaInput != 0;

		if (categoriaSpecificata) {
			// controllo se la categoria presa esiste 
			String queryControlloCategoria = "SELECT Id_categoria\n" +
				"FROM Categorie\n" + 
				"WHERE Id_categoria = ?;"
			;

			try (Connection connection = gestoreDatabase.getConnection();) {
				PreparedStatement preparedStatement = connection.prepareStatement(queryControlloCategoria);
				preparedStatement.setInt(1, idCategoriaInput);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (!resultSet.next()) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria" };
					return;
				}
			} catch (SQLException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: C'e' stato un errore nella query di controllo dell'idCategoria nella visualizzazione delle aste programmate. " + e.getMessage()
				);

				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
				return;
			}
		}

		// Impostazione della query finale 
		String queryVisualizzazione = "SELECT DISTINCT Aste.Id_asta, Aste.durata, Aste.data_ora_inizio, Salvataggi.Rif_utente, Aste.prezzo_inizio, MAX(Puntate.valore) AS prezzo_attuale, " +
			"Lotti.nome, Immagini.Id_immagine\n" + 
			"FROM Aste\n" +
			"LEFT JOIN Puntate ON Aste.Id_asta = Puntate.Rif_asta\n" +
			"JOIN Lotti ON Aste.Rif_lotto = Lotti.Id_lotto\n" +
			"JOIN Articoli ON Lotti.Id_lotto = Articoli.Rif_lotto\n" +
			"LEFT JOIN Salvataggi ON Aste.Id_asta = Salvataggi.Rif_asta\n" +
			"LEFT JOIN Immagini ON Immagini.Rif_articolo = Articoli.Id_articolo\n"+
			"WHERE (CURRENT_TIMESTAMP < Aste.data_ora_inizio) AND\n"
		;

		if (categoriaSpecificata) {
			queryVisualizzazione += "Articoli.Rif_categoria = ? AND\n";
		}

		queryVisualizzazione += "Lotti.nome LIKE ? AND\n" +
			"Immagini.principale = 1 AND\n" +
			"Articoli.Rif_utente = ?\n" +
			"GROUP BY Aste.Id_asta\n" +
			"LIMIT ? OFFSET ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(queryVisualizzazione);
			if (categoriaSpecificata) {
				preparedStatement.setInt(1, idCategoriaInput);
				preparedStatement.setString(2, "%"+ stringaRicerca+ "%");
				preparedStatement.setInt(3, idUtente);
				preparedStatement.setInt(4, numeroAste);
				preparedStatement.setInt(5, ((numeroPagina-1)*numeroAste));
			} else {
				preparedStatement.setString(1, "%"+ stringaRicerca+ "%");
				preparedStatement.setInt(2, idUtente);
				preparedStatement.setInt(3, numeroAste);
				preparedStatement.setInt(4, ((numeroPagina-1)*numeroAste));
			}
			
			ResultSet resultSet = preparedStatement.executeQuery();

			// array list per gli oggetti delle aste 
			ArrayList<Object> aste= new ArrayList<>();

			// While per caricare l'array list 
			while (resultSet.next()) {
				aste.add(resultSet.getInt("Id_asta"));
				aste.add(Duration.ofMinutes(resultSet.getLong("durata")));
				
				float prezzo_attuale = resultSet.getFloat("prezzo_attuale");
				aste.add(prezzo_attuale == 0 ? resultSet.getFloat("prezzo_inizio") : prezzo_attuale);

				aste.add(resultSet.getString("nome"));
				
				int idImmagine = resultSet.getInt("Id_immagine");

				String nomeFile = resultSet.wasNull() ? 
					"static_resources\\default_articolo.png" :
					"res\\immagini_articoli\\"+ idImmagine + ".png"
				;

				try (FileInputStream stream = new FileInputStream(nomeFile);) {
					aste.add(stream.readAllBytes());
				}

				aste.add(resultSet.getTimestamp("data_ora_inizio").toLocalDateTime());
				aste.add(resultSet.getInt("Rif_utente") != 0);
			}

			// Transformazione del array list in array e risposta nel payload uscita 
			rispostaUscente.tipoRisposta= TipoRisposta.OK;
			rispostaUscente.payload = aste.toArray();

		} catch (SQLException e) { // questo catch e per gli errori che potrebbe dare la query 
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di visualizzazione delle aste programmate. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) { // questo catch e per gli errori che potrebbe dare il caricamento del immagine del utente
			System.err.println("[" +
				Thread.currentThread().getName() +
				"]: C'e' stato un errore nell'apertura/lettura/chiusura delle immagini nella visualizzazione aste programmate. "
				+ e.getMessage()
			);
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private void visualizzaAsteVinte()  {
		// controllo se l'utente e connesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		// Funzione per avere il numero delle aste 
		Integer numeroAste;

		try {
			numeroAste = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroAste"};
			return;
		}

		if (numeroAste == null || numeroAste <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroAste"};
			return;
		}

		// Funzione per avere il numero della pagina 
		Integer numeroPagina;

		try {
			numeroPagina = (Integer)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		if (numeroPagina == null || numeroAste <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		// Funzione per la ricerca delle aste 
		String stringaRicerca;

		try {
			stringaRicerca = (String)richiestaEntrante.payload[2];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		if (stringaRicerca == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		// Funzione per avere le categoria 
		Integer idCategoriaInput;

		try {
			idCategoriaInput = (Integer)richiestaEntrante.payload[3];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		if (idCategoriaInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		boolean categoriaSpecificata = idCategoriaInput != 0;

		if (categoriaSpecificata) {
			// controllo se la categoria presa esiste 
			String queryControlloCategoria = "SELECT Id_categoria\n" +
				"FROM Categorie\n" + 
				"WHERE Id_categoria = ?;"
			;

			try (Connection connection = gestoreDatabase.getConnection();) {
				PreparedStatement preparedStatement = connection.prepareStatement(queryControlloCategoria);
				preparedStatement.setInt(1, idCategoriaInput);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (!resultSet.next()) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria" };
					return;
				}
			} catch (SQLException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: C'e' stato un errore nella query di controllo dell'idCategoria nella visualizzazione delle aste vinte. " + e.getMessage()
				);

				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
				return;
			}
		}

		// Impostazione della query finale 
		String queryVisualizzazione = "SELECT DISTINCT A.Id_asta, A.durata, A.data_ora_inizio, Salvataggi.Rif_utente, Ultime_Puntate.valore_massimo," +
			"P.valore, Lotti.nome, Immagini.Id_immagine\n" + 
			"FROM Aste AS A\n" +
			"JOIN Puntate AS P ON A.Id_asta = P.Rif_asta\n" +
			"JOIN (\n" + 
				"SELECT Rif_asta, MAX(valore) AS valore_massimo\n" +
				"FROM Puntate\n" +
				"GROUP BY Rif_asta\n" +
			") AS Ultime_Puntate ON P.Rif_asta = Ultime_Puntate.Rif_asta\n" +
			"JOIN Lotti ON A.Rif_lotto = Lotti.Id_lotto\n" +
			"JOIN Articoli ON Lotti.Id_lotto = Articoli.Rif_lotto\n" +
			"LEFT JOIN Salvataggi ON A.Id_asta = Salvataggi.Rif_Asta\n" +
			"LEFT JOIN Immagini ON Immagini.Rif_articolo = Articoli.Id_articolo\n"+
			"WHERE (CURRENT_TIMESTAMP > DATE_ADD(A.data_ora_inizio, INTERVAL A.durata MINUTE)) AND\n" +
			"P.valore = Ultime_Puntate.valore_massimo AND\n"
		;

		if (categoriaSpecificata) {
			queryVisualizzazione += "Articoli.Rif_categoria = ? AND\n";
		}

		queryVisualizzazione += "Lotti.nome LIKE ? AND\n" +
			"Immagini.principale = 1 AND\n" +
			"P.Rif_utente = ?\n" +
			"GROUP BY A.Id_asta\n" +
			"LIMIT ? OFFSET ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(queryVisualizzazione);
			if (categoriaSpecificata) {
				preparedStatement.setInt(1, idCategoriaInput);
				preparedStatement.setString(2, "%"+ stringaRicerca+ "%");
				preparedStatement.setInt(3, idUtente);
				preparedStatement.setInt(4, numeroAste);
				preparedStatement.setInt(5, ((numeroPagina-1)*numeroAste));
			} else {
				preparedStatement.setString(1, "%"+ stringaRicerca+ "%");
				preparedStatement.setInt(2, idUtente);
				preparedStatement.setInt(3, numeroAste);
				preparedStatement.setInt(4, ((numeroPagina-1)*numeroAste));
			}
			
			ResultSet resultSet = preparedStatement.executeQuery();

			// array list per gli oggetti delle aste 
			ArrayList<Object> aste= new ArrayList<>();

			// While per caricare l'array list 
			while (resultSet.next()) {
				aste.add(resultSet.getInt("Id_asta"));
				aste.add(Duration.ofMinutes(resultSet.getLong("durata")));
				aste.add(resultSet.getFloat("valore_massimo"));
				aste.add(resultSet.getString("nome"));
				
				int idImmagine = resultSet.getInt("Id_immagine");

				String nomeFile = resultSet.wasNull() ? 
					"static_resources\\default_articolo.png" :
					"res\\immagini_articoli\\"+ idImmagine + ".png"
				;

				try (FileInputStream stream = new FileInputStream(nomeFile);) {
					aste.add(stream.readAllBytes());
				}

				aste.add(resultSet.getTimestamp("data_ora_inizio").toLocalDateTime());
				aste.add(resultSet.getInt("Rif_utente") != 0);
			}

			// Transformazione del array list in array e risposta nel payload uscita 
			rispostaUscente.tipoRisposta= TipoRisposta.OK;
			rispostaUscente.payload = aste.toArray();

		} catch (SQLException e) { // questo catch e per gli errori che potrebbe dare la query 
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di visualizzazione delle aste vinte. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) { // questo catch e per gli errori che potrebbe dare il caricamento del immagine del utente
			System.err.println("[" +
				Thread.currentThread().getName() +
				"]: C'e' stato un errore nell'apertura/lettura/chiusura delle immagini nella visualizzazione aste vinte. "
				+ e.getMessage()
			);
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private void visualizzaAsteSalvate()  {
		// controllo se l'utente e connesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		// Funzione per avere il numero delle aste 
		Integer numeroAste;

		try {
			numeroAste = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroAste"};
			return;
		}

		if (numeroAste == null || numeroAste <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroAste"};
			return;
		}

		// Funzione per avere il numero della pagina 
		Integer numeroPagina;

		try {
			numeroPagina = (Integer)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		if (numeroPagina == null || numeroAste <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		// Funzione per la ricerca delle aste 
		String stringaRicerca;

		try {
			stringaRicerca = (String)richiestaEntrante.payload[2];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		if (stringaRicerca == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		// Funzione per avere le categoria 
		Integer idCategoriaInput;

		try {
			idCategoriaInput = (Integer)richiestaEntrante.payload[3];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		if (idCategoriaInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		boolean categoriaSpecificata = idCategoriaInput != 0;

		if (categoriaSpecificata) {
			// controllo se la categoria presa esiste 
			String queryControlloCategoria = "SELECT Id_categoria\n" +
				"FROM Categorie\n" + 
				"WHERE Id_categoria = ?;"
			;

			try (Connection connection = gestoreDatabase.getConnection();) {
				PreparedStatement preparedStatement = connection.prepareStatement(queryControlloCategoria);
				preparedStatement.setInt(1, idCategoriaInput);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (!resultSet.next()) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria" };
					return;
				}
			} catch (SQLException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: C'e' stato un errore nella query di controllo dell'idCategoria nella visualizzazione delle aste vinte. " + e.getMessage()
				);

				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
				return;
			}
		}

		// Impostazione della query finale 
		String queryVisualizzazione = "SELECT DISTINCT Aste.Id_asta, Aste.durata, Aste.data_ora_inizio, Aste.prezzo_inizio, MAX(Puntate.valore) AS prezzo_attuale, " +
			"Lotti.nome, Immagini.Id_immagine\n" + 
			"FROM Aste\n" +
			"LEFT JOIN Puntate ON Aste.Id_asta = Puntate.Rif_asta\n" +
			"JOIN Lotti ON Aste.Rif_lotto = Lotti.Id_lotto\n" +
			"JOIN Articoli ON Lotti.Id_lotto = Articoli.Rif_lotto\n" +
			"JOIN Salvataggi ON Aste.Id_asta = Salvataggi.Rif_asta AND Salvataggi.Rif_utente = ?\n" +
			"LEFT JOIN Immagini ON Immagini.Rif_articolo = Articoli.Id_articolo\n"+
			"WHERE "
		;
		
		if (categoriaSpecificata) {
			queryVisualizzazione += "Articoli.Rif_categoria = ? AND\n";
		}

		queryVisualizzazione += "Lotti.nome LIKE ? AND\n" +
			"Immagini.principale = 1\n" +
			"GROUP BY Aste.Id_asta\n" +
			"LIMIT ? OFFSET ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(queryVisualizzazione);
			if (categoriaSpecificata) {
				preparedStatement.setInt(1, idUtente);
				preparedStatement.setInt(2, idCategoriaInput);
				preparedStatement.setString(3, "%"+ stringaRicerca+ "%");
				preparedStatement.setInt(4, numeroAste);
				preparedStatement.setInt(5, ((numeroPagina-1)*numeroAste));
			} else {
				preparedStatement.setInt(1, idUtente);
				preparedStatement.setString(2, "%"+ stringaRicerca+ "%");
				preparedStatement.setInt(3, numeroAste);
				preparedStatement.setInt(4, ((numeroPagina-1)*numeroAste));
			}
			
			ResultSet resultSet = preparedStatement.executeQuery();

			// array list per gli oggetti delle aste 
			ArrayList<Object> aste= new ArrayList<>();

			// While per caricare l'array list 
			while (resultSet.next()) {
				aste.add(resultSet.getInt("Id_asta"));
				aste.add(Duration.ofMinutes(resultSet.getLong("durata")));
				
				float prezzo_attuale = resultSet.getFloat("prezzo_attuale");
				aste.add(prezzo_attuale == 0 ? resultSet.getFloat("prezzo_inizio") : prezzo_attuale);

				aste.add(resultSet.getString("nome"));
				
				int idImmagine = resultSet.getInt("Id_immagine");

				String nomeFile = resultSet.wasNull() ? 
					"static_resources\\default_articolo.png" :
					"res\\immagini_articoli\\"+ idImmagine + ".png"
				;

				try (FileInputStream stream = new FileInputStream(nomeFile);) {
					aste.add(stream.readAllBytes());
				}

				aste.add(resultSet.getTimestamp("data_ora_inizio").toLocalDateTime());
				aste.add(true);
			}

			// Transformazione del array list in array e risposta nel payload uscita 
			rispostaUscente.tipoRisposta= TipoRisposta.OK;
			rispostaUscente.payload = aste.toArray();

		} catch (SQLException e) { // questo catch e per gli errori che potrebbe dare la query 
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di visualizzazione aste salvata. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) { // questo catch e per gli errori che potrebbe dare il caricamento del immagine del utente
			System.err.println("[" +
				Thread.currentThread().getName() +
				"]: C'e' stato un errore nell'apertura/lettura/chiusura delle immagini nella visualizzazione aste salvata. "
				+ e.getMessage()
			);
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private void creaAsta() {
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		LocalDateTime dataOraInizioInput;

		try {
			dataOraInizioInput = (LocalDateTime)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "dataOraInizio" };
			return;
		}

		if (dataOraInizioInput.isBefore(LocalDateTime.now())) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "dataOraInizio" };
			return;
		}

		Duration durataInput;

		try {
			durataInput = (Duration)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "durata" };
			return;
		}

		if (durataInput.isNegative() || durataInput.isZero()) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "durata" };
			return;
		}

		Float prezzoInizioInput;

		try {
			prezzoInizioInput = (Float)richiestaEntrante.payload[2];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "prezzoInizio" };
			return;
		}

		Boolean astaAutomaticaInput;

		try {
			astaAutomaticaInput = (Boolean)richiestaEntrante.payload[3];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "astaAutomatica" };
			return;
		}

		Integer idLottoInput;

		try {
			idLottoInput = (Integer)richiestaEntrante.payload[4];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idLotto" };
			return;
		}

		if (idLottoInput == null || idLottoInput <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idLotto" };
			return;
		}

		String controlloLotto1 = "SELECT 1\n" +
			"FROM Aste\n" +
			"WHERE Rif_lotto = ? AND DATE_ADD(Aste.data_ora_inizio, INTERVAL Aste.durata MINUTE) > CURRENT_TIMESTAMP;"
		;

		String controlloLotto2 = "SELECT 1\n" +
			"FROM Aste\n" +
			"JOIN Puntate ON Aste.Id_asta = Puntate.Rif_asta\n" +
			"WHERE Rif_lotto = ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement statement = connection.prepareStatement(controlloLotto1);
			statement.setInt(1, idLottoInput);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
				return;
			}

			statement = connection.prepareStatement(controlloLotto2);
			statement.setInt(1, idLottoInput);
			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
				return;
			}
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di controllo sull'idLotto nella creazione asta. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
			return;
		}

		String queryCreazione = "INSERT INTO Aste(prezzo_inizio, data_ora_inizio, durata, asta_automatica, Rif_lotto)\n" +
			"VALUES (?, ?, ?, ?, ?);"
		;

		Connection connection = null;
		
		try {
			connection = gestoreDatabase.getConnection();
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(queryCreazione, new String[]{ "Id_asta" });
			statement.setFloat(1, prezzoInizioInput);
			statement.setTimestamp(2, Timestamp.valueOf(dataOraInizioInput));
			long durata = durataInput.toMinutes();
			statement.setLong(3, durata);
			statement.setInt(4, astaAutomaticaInput ? 1 : 0);
			statement.setInt(5, idLottoInput);
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();

			gestoreAste.creaAsta(resultSet.getInt(1), dataOraInizioInput, durata);
			connection.commit();
			
			rispostaUscente.tipoRisposta = TipoRisposta.OK;
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: C'e' stato un errore del server sul rollback nella creazione asta. " + e1.getMessage()
				);
			}

			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di creazione asta. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IllegalStateException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: C'e' stato un errore del server sul rollback nella creazione asta. " + e1.getMessage()
				);
			}

			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore del server per la creazione asta. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (SQLException e) {
					System.err.println("[" + Thread.currentThread().getName() +
						"]: C'e' stato un errore del server per il reset dell'autocommit asta. " + e.getMessage()
					);
				}
			}
		}
	}

	private void modificaAsta() {
		// Implementazione della modifica di un'asta
	}

	// Metodo visualizza asta 
	private void visualizzaAsta() {
		// controllo se l'utente e' connesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		// Funzione per avere un'asta 
		Integer idAstaInput;

		try {
			idAstaInput = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idAsta"};
			return;
		}

		if (idAstaInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idAsta"};
			return;
		}

		// Impostazione della query finale 
		String queryVisualizzazione = "SELECT Aste.data_ora_inizio, Aste.durata, Aste.prezzo_inizio, " +
			"MAX(Puntate.valore) AS prezzo_attuale, Aste.ip_multicast, Aste.descrizione_annullamento, " +
			"Lotti.Id_lotto, Lotti.nome\n"+ 
			"FROM Aste\n"+
			"LEFT JOIN Puntate ON Aste.Id_asta = Puntate.Rif_asta\n" +
			"JOIN Lotti ON Aste.Rif_lotto = Lotti.Id_lotto\n" +
			"WHERE Aste.Id_asta = ?\n" +
			"GROUP BY Aste.Id_asta;"
		;

		String queryUtente = "SELECT Utenti.Id_utente, Utenti.email\n" +
			"FROM Utenti\n" +
			"JOIN Articoli ON Utenti.Id_utente = Articoli.Rif_utente\n" +
			"WHERE Articoli.Rif_lotto = ?\n" +
			"LIMIT 1;"	
		;

		String queryImmagini = "SELECT Immagini.Id_immagine\n" +
			"FROM Immagini\n" +
			"JOIN Articoli ON Immagini.Rif_articolo = Articoli.Id_articolo\n" +
			"WHERE Articoli.Rif_lotto = ?;"
		;

		try (Connection connection = gestoreDatabase.getConnection();) {
			// Ottenendo dati asta/lotto
			PreparedStatement preparedStatement = connection.prepareStatement(queryVisualizzazione);
			preparedStatement.setInt(1, idAstaInput);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
				return;
			}

			byte[] indirizzo = resultSet.getBytes("ip_multicast");

			rispostaUscente.tipoRisposta = TipoRisposta.OK;
			rispostaUscente.payload = new Object[] {
				resultSet.getTimestamp("data_ora_inizio").toLocalDateTime(),
				Duration.ofMinutes(resultSet.getLong("durata")),
				resultSet.getFloat("prezzo_inizio"),
				resultSet.getFloat("prezzo_attuale"),
				indirizzo != null ? InetAddress.getByAddress(indirizzo) : null,
				resultSet.getString("descrizione_annullamento"),
				resultSet.getInt("Id_lotto"),
				resultSet.getString("nome"),
				null,
				null,
				null,
			};

			// Ottenendo dati utente
			preparedStatement = connection.prepareStatement(queryUtente);
			preparedStatement.setInt(1, (Integer)rispostaUscente.payload[6]);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			
			rispostaUscente.payload[8] = resultSet.getInt("Id_utente");
			rispostaUscente.payload[9] = resultSet.getString("email");

			// Ottenendo immagini articoli
			preparedStatement = connection.prepareStatement(queryImmagini);
			preparedStatement.setInt(1, (Integer)rispostaUscente.payload[6]);
			resultSet = preparedStatement.executeQuery();

			ArrayList<Integer> immagini = new ArrayList<>();

			while (resultSet.next()) {
				immagini.add(resultSet.getInt("Id_immagine"));
			}

			int numeroImmagini = immagini.size();

			if (numeroImmagini == 0) { // Inviata quella di default
				try (FileInputStream stream = new FileInputStream("static_resources\\default_articolo.png");) {
					rispostaUscente.payload[10] = new byte[][]{ stream.readAllBytes() };
				}
			} else { // Mandate le immagini di tutti gli articoli
				rispostaUscente.payload[10] = new byte[immagini.size()][];

				for (int i = 0; i < numeroImmagini; ++i) {
					try (FileInputStream stream = new FileInputStream("res\\immagini_articoli\\" + immagini.get(i) + ".png");) {
						((byte[][])rispostaUscente.payload[10])[i] = stream.readAllBytes();
					}
				}
			}
		} catch (SQLException e) { // questo catch e per gli errori che potrebbe dare la query 
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di vissualizazione asta. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) { // questo catch e per gli errori che potrebbe dare il caricamento del immagine del utente
			System.err.println("[" +
				Thread.currentThread().getName() +
				"]: C'e' stato un errore nell'apertura/scrittura/chiusura delle immagini dell'asta. "
				+ e.getMessage()
			);
			
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private void salvaAsta() {
		// controllo se l'utente e' connesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		Integer idAstaInput;

		try {
			idAstaInput = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idAsta"};
			return;
		}

		if (idAstaInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idAsta"};
			return;
		}

		try (Connection connection = gestoreDatabase.getConnection();) {
			String queryControlloAsta = "SELECT 1\n" +
				"FROM Aste\n" +
				"WHERE Id_asta = ?;"
			;

			PreparedStatement statement = connection.prepareStatement(queryControlloAsta);
			statement.setInt(1, idAstaInput);
			ResultSet resultSet = statement.executeQuery();

			if (!resultSet.next()) {
				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
				return;
			}
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di controllo sull'idAsta nel salvataggio asta. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
			return;
		}

		try (Connection connection = gestoreDatabase.getConnection();) {
			String queryControlloSalvataggio = "SELECT 1\n" +
				"FROM Salvataggi\n" +
				"WHERE Rif_asta = ? AND Rif_utente = ?;"	
			;

			PreparedStatement preparedStatement = connection.prepareStatement(queryControlloSalvataggio);
			preparedStatement.setInt(1, idAstaInput);
			preparedStatement.setInt(2, idUtente);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				String queryElimininazione = "DELETE FROM Salvataggi\n" +
					"WHERE Rif_asta = ? AND Rif_utente = ?;"
				;

				preparedStatement = connection.prepareStatement(queryElimininazione);
				preparedStatement.setInt(1, idAstaInput);
				preparedStatement.setInt(2, idUtente);
				preparedStatement.executeUpdate();
			} else {
				String queryCreazione = "INSERT INTO Salvataggi(Rif_asta, Rif_utente)\n" +
					"VALUES (?, ?);"
				;

				preparedStatement = connection.prepareStatement(queryCreazione);
				preparedStatement.setInt(1, idAstaInput);
				preparedStatement.setInt(2, idUtente);
				preparedStatement.executeUpdate();
			}

			rispostaUscente.tipoRisposta = TipoRisposta.OK;
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di salvataggio asta. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}

	private void annullaAsta() {
		// controllo se l'utente e' connesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		// Funzione per avere un'asta 
		Integer idAstaInput;

		try {
			idAstaInput = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idAsta"};
			return;
		}

		if (idAstaInput == null || idAstaInput <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idAsta"};
			return;
		}

		if (!admin) {
			try (Connection connection = gestoreDatabase.getConnection();) {
				String queryControlloAsta = "SELECT 1\n" +
					"FROM Aste\n" +
					"WHERE Id_asta = ? AND Rif_utente = ?;"
				;
	
				PreparedStatement statement = connection.prepareStatement(queryControlloAsta);
				statement.setInt(1, idAstaInput);
				statement.setInt(2, idUtente);
				ResultSet resultSet = statement.executeQuery();

				if (!resultSet.next()) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idAsta"};
					return;
				}
	
			} catch (SQLException e) {

			}
		}
	}

	// Metodo visualliza articoli 
	private void visualizzaArticoli() {
		// controllo se l'utente e conesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		// Funzione per avere il numero degli articoli 
		Integer numeroArticoli;

		try {
			numeroArticoli = (Integer)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroArticoli"};
			return;
		}

		if (numeroArticoli == null || numeroArticoli <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroArticoli"};
			return;
		}

		// Funzione per avere il numero della pagina 
		Integer numeroPagina;

		try {
			numeroPagina = (Integer)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		if (numeroPagina == null || numeroArticoli <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "numeroPagina"};
			return;
		}

		// Funzione per ls ricerca degli articoli 
		String stringaRicerca;

		try {
			stringaRicerca = (String)richiestaEntrante.payload[2];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		if (stringaRicerca == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "stringaRicerca"};
			return;
		}

		// Funzione per avere le categoria 
		Integer idCategoriaInput;

		try {
			idCategoriaInput = (Integer)richiestaEntrante.payload[3];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		if (idCategoriaInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria"};
			return;
		}

		boolean categoriaSpecificata = idCategoriaInput != 0;

		if (categoriaSpecificata) {
			// controllo se la categoria presa esiste 
			String queryControlloCategoria = "SELECT Id_categoria\n" +
				"FROM Categorie\n" + 
				"WHERE Id_categoria = ?;"
			;

			try (Connection connection = gestoreDatabase.getConnection();) {
				PreparedStatement preparedStatement = connection.prepareStatement(queryControlloCategoria);
				preparedStatement.setInt(1, idCategoriaInput);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (!resultSet.next()) {
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idCategoria" };
					return;
				}
			} catch (SQLException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: C'e' stato un errore nella query di controllo dell'idCategoria nella visualizzazione degli articoli. " + e.getMessage()
				);

				rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
				rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
				return;
			}
		}

		Boolean assegnabiliInput;

		try {
			assegnabiliInput = (Boolean)richiestaEntrante.payload[4];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "assegnabili" };
			return;
		}

		if (assegnabiliInput == null) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "assegnabili" };
			return;
		}

		String queryVisualizzazione = "SELECT Articoli.Id_articolo, Articoli.nome, " +
			"Articoli.condizione, Immagini.Id_immagine\n" + 
			"FROM Articoli\n" +
			"LEFT JOIN Immagini ON Immagini.Rif_articolo = Articoli.Id_articolo\n" +
			"WHERE Articoli.Rif_utente = ? AND Articoli.nome LIKE ? AND Immagini.principale = 1"
		;

		if (categoriaSpecificata) {
			queryVisualizzazione += " AND Articoli.Rif_categoria = ?";
		}

		if (assegnabiliInput) {
			queryVisualizzazione += " AND Articoli.Rif_lotto = 1";
		}

		queryVisualizzazione += "\nLIMIT ? OFFSET ?;";

		try (Connection connection = gestoreDatabase.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(queryVisualizzazione);
			preparedStatement.setInt(1, idUtente);
			preparedStatement.setString(2, "%"+ stringaRicerca+ "%");
			
			if (categoriaSpecificata) {
				preparedStatement.setInt(3, idCategoriaInput);
				preparedStatement.setInt(4, numeroArticoli);
				preparedStatement.setInt(5, ((numeroPagina-1)*numeroArticoli));
			} else {
				preparedStatement.setInt(3, numeroArticoli);
				preparedStatement.setInt(4, ((numeroPagina-1)*numeroArticoli));
			}
			
			ResultSet resultSet = preparedStatement.executeQuery();

			// array list per gli oggetti del articolo 
			ArrayList<Object> articoli= new ArrayList<>();

			// While per caricare l'array list 
			while (resultSet.next()) {
				articoli.add(resultSet.getInt("Id_articolo"));
				articoli.add(resultSet.getString("nome"));
				articoli.add(resultSet.getString("condizione"));
				
				int idImmagine= resultSet.getInt("Id_immagine");

				String nomeFile = resultSet.wasNull() ? 
					"static_resources\\default_articolo.png" :
					"res\\immagini_articoli\\"+ idImmagine+ ".png"
				;

				try (FileInputStream stream = new FileInputStream(nomeFile);) {
					articoli.add(stream.readAllBytes());
				}
			}

			// Transformazione del array list in array e risposta nel payload uscita 
			rispostaUscente.tipoRisposta= TipoRisposta.OK;
			rispostaUscente.payload = articoli.toArray();

		} catch (SQLException e) { // questo catch e per gli errori che potrebbe dare la query 
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di vissualizazione articoli. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };

		} catch (IOException e) { // questo catch e per gli errori che potrebbe dare il caricamento del immagine del utente
			System.err.println("[" +
				Thread.currentThread().getName() +
				"]: C'e' stato un errore nell'apertura/scrittura delle immagini degli articoli. "
				+ e.getMessage()
			);
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}
}