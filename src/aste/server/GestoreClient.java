package aste.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import aste.Richiesta;
import aste.Risposta;
import aste.Richiesta.TipoRichiesta;
import aste.Risposta.TipoErrore;
import aste.Risposta.TipoRisposta;

public class GestoreClient implements Runnable {
	private Socket socket;
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
		try {
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
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: Impossibile chiudere Socket: " + e.getMessage() +
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
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visualizzaPuntate'");
	}

	// Metodo Visualizza lotto 
	private void visualizzaLotto() {
		// conmtrollo se l'utente e conesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}


	}

	// Metodo visualizza lotti 
	private void visualizzaLotti() {
		// conmtrollo se l'utente e conesso 
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

		// controllo se la categoria presa esiste 
		String queryControlloCategoria = "SELECT Id_categoria\n" +
			"FROM Categorie\n" + 
			"WHERE Id_categoria = ?;"
		;

		try {
			Connection connection = gestoreDatabase.getConnection();
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
		}

		// Impostazione della query finale 
		String queryVisualizzazione = "SELECT DISTINCT Lotti.Id_lotto, Lotti.nome, Immagini.Id_immagine\n" + 
			"FROM Lotti\n" +
			"JOIN Articoli ON Articoli.Rif_lotto = Lotti.Id_lotto\n" +
			"LEFT JOIN Immagini ON Immagini.Rif_articolo = Articoli.Id_articolo\n" +
			"WHERE Articoli.Rif_utente = ? AND Articoli.Rif_categoria = ? AND Lotti.nome LIKE ? AND Immagini.principale = 1\n" +
			"LIMIT ? OFFSET ?;";

		try {
			Connection connection = gestoreDatabase.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(queryVisualizzazione);
			preparedStatement.setInt(1, idUtente);
			preparedStatement.setInt(2, idCategoriaInput);
			preparedStatement.setString(3, "%"+ stringaRicerca+ "%");
			preparedStatement.setInt(4, numeroLotti);
			preparedStatement.setInt(5, ((numeroPagina-1)*numeroLotti));
			ResultSet resultSet = preparedStatement.executeQuery();

			// array list per gli oggetti dei lotti 
			ArrayList<Object> lotti= new ArrayList<>();

			// While per caricare l'array list 
			while (resultSet.next()) {
				lotti.add(resultSet.getInt("Id_lotto"));
				lotti.add(resultSet.getString("nome"));
				
				int idImmagine= resultSet.getInt("Id_immagine");
				FileInputStream stream;
				if (resultSet.wasNull()) {
					stream= new FileInputStream("static_resources\\default_articolo.png");
				} else {
					stream= new FileInputStream("res\\immagini_articoli\\"+ idImmagine+ ".png");
				}

				lotti.add(stream.readAllBytes());

				stream.close();
			}

			// Transformazione del array list in array e risposta nel payload uscita 
			rispostaUscente.tipoRisposta= TipoRisposta.OK;
			rispostaUscente.payload = lotti.toArray();

		} catch (SQLException e) { // questo catch e per gli errori che potrebbe dare la query 
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di vissualizzazione lotti. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) { // questo catch e per gli errori che potrebbe dare il caricamento del immagine del utente
			System.err.println("[" +
				Thread.currentThread().getName() +
				"]: C'e' stato un errore nell'apertura/lettura/chiusura delle immagini degli articoli. "
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
			"ORDER BY nome ASC;"
		;

		try {
			Connection connection = gestoreDatabase.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(queryVisualizzazione);

			ArrayList<Object> buffer = new ArrayList<>();

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
			"WHERE Id_lotto = 1 AND Utenti.Id_utente = ?;"
		;

		ArrayList<Integer> articoliUtente = new ArrayList<>();

		try {
			Connection connection = gestoreDatabase.getConnection();
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

		if (articoliUtente.isEmpty()) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}

		for (int i = 0; i < idArticoliInput.length; ++i) {
			
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

		try {
			Connection connection = gestoreDatabase.getConnection();
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

			try {
				Connection connection = gestoreDatabase.getConnection();
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

		if (immaginePrincipaleInput == null || immaginePrincipaleInput < 0 || immaginePrincipaleInput >= immaginiArticoloInput.length) {
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

		try {
			Connection connection = gestoreDatabase.getConnection();
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

		try {
			Connection connection = gestoreDatabase.getConnection();
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

		try {
			Connection connection = gestoreDatabase.getConnection();
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

		try {
			Connection connection = gestoreDatabase.getConnection();
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
		// TODO: change back to 65535
		KeySpec specification = new PBEKeySpec(password.toCharArray(), sale, 4, 512);
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

		try {
			if (idUtenteInput == 0) { // utente loggato
				String queryVisualizzazione = "SELECT nome, cognome, data_nascita, citta_residenza, cap, indirizzo, email, iban\n" +
					"FROM Utenti\n" +
					"WHERE Id_utente = ?"
				;
	
				Connection connection = gestoreDatabase.getConnection();
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

				Connection connection = gestoreDatabase.getConnection();
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

		FileInputStream stream = null;

		try {
			Connection connection = gestoreDatabase.getConnection();
			PreparedStatement statement = connection.prepareStatement(queryUtenti);
			statement.setInt(1, idInput);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Integer immagineProfilo = resultSet.getInt("immagine_profilo");

				rispostaUscente.tipoRisposta = TipoRisposta.OK;

				if (immagineProfilo == 1) {
					stream = new FileInputStream("res\\profili\\" + idInput + ".png");
				} else {
					stream = new FileInputStream("static_resources\\default_user.png");
				}

				rispostaUscente.payload = new Object[]{ stream.readAllBytes() };
				return;
			}

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() + "]: C'e' stato un errore nella query di visualizzazione immagine. " + e.getMessage());
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} catch (IOException e) {
			System.err.println("[" + Thread.currentThread().getName() + "]: C'e' stato un errore nell'apertura/scrittura delle immagine profilo. " + e.getMessage());
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					System.err.println("[" + Thread.currentThread().getName() + "]: C'e' stato un errore nella chiusura dell'immagine profilo. " + e.getMessage());
					rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
					rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
				}
			}
		}
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
        String query = "SELECT aste.Id_asta, aste.durata, lotti.nome, immagini.Id_immagine,"+ 
        "articoli.nome, categorie.Id_categoria, lotti.nome, articoli.nome, categorie.Id_categoria" +
        "FROM articoli JOIN lotti ON articoli.Rif_lotto=lotti.Id_lotto"+
        "JOIN articoli_categorie ON articoli.Id_articolo=articoli_categorie.Rif_articolo"+ 
        "JOIN categorie "+ 
        "ON articoli_categorie.Rif_categoria=categorie.Id_categoria "+ 
        "WHERE Articolo.nome like '"+ stringaRicerca+ "%' OR Lotto.nome like '"+ stringaRicerca+ "%'"+ 
        "LIMIT 5 OFFSET "+ ((numeroPagina-1)*numeroAste)+ ";";

        

        // Utilizziamo un oggetto Statement per eseguire la query
        rispostaUscente.payload[0] = precaricamentoAste();
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
		LocalDateTime dataOraInizio;

		try {
			dataOraInizio = (LocalDateTime)richiestaEntrante.payload[0];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "dataOraInizio" };
			return;
		}

		if (dataOraInizio.isBefore(LocalDateTime.now())) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "dataOraInizio" };
			return;
		}

		Duration durata;

		try {
			durata = (Duration)richiestaEntrante.payload[1];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "durata" };
			return;
		}

		Float prezzoInizio;

		try {
			prezzoInizio = (Float)richiestaEntrante.payload[2];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "prezzoInizio" };
			return;
		}

		Boolean astaAutomatica;

		try {
			astaAutomatica = (Boolean)richiestaEntrante.payload[3];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "astaAutomatica" };
			return;
		}

		Integer idLotto;

		try {
			idLotto = (Integer)richiestaEntrante.payload[4];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idLotto" };
			return;
		}
	}

	private void modificaAsta() {
		// Implementazione della modifica di un'asta
	}

	// Metodo visualizza asta 
	private void visualizzaAsta() {
		// conmtrollo se l'utente e conesso 
		if (idUtente == 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.OPERAZIONE_INVALIDA };
			return;
		}
	}

	private void salvaAsta() {
		// Implementazione del salvataggio di un'asta
	}

	private void annullaAsta() {
		// Implementazione dell'annullamento di un'asta
	}

	// Metodo visualliza articoli 
	private void visualizzaArticoli() {
		// conmtrollo se l'utente e conesso 
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

		// controllo se la categoria presa esiste 
		String queryControlloCategoria = "SELECT Id_categoria\n" +
			"FROM Categorie\n" + 
			"WHERE Id_categoria = ?;"
		;

		try {
			Connection connection = gestoreDatabase.getConnection();
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

		// Impostazione della query finale 
		String queryVisualizzazione = "SELECT Articoli.Id_articolo, Articoli.nome, Articoli.condizione, Immagini.Id_immagine\n" + 
			"FROM Articoli\n" +
			"LEFT JOIN Immagini ON Immagini.Rif_articolo = Articoli.Id_articolo\n" +
			"WHERE Articoli.Rif_utente = ? AND Articoli.Rif_categoria = ? AND Articoli.nome LIKE ? AND Immagini.principale = 1\n" +
			"LIMIT ? OFFSET ?;";

		try {
			Connection connection = gestoreDatabase.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(queryVisualizzazione);
			preparedStatement.setInt(1, idUtente);
			preparedStatement.setInt(2, idCategoriaInput);
			preparedStatement.setString(3, "%"+ stringaRicerca+ "%");
			preparedStatement.setInt(4, numeroArticoli);
			preparedStatement.setInt(5, ((numeroPagina-1)*numeroArticoli));
			ResultSet resultSet = preparedStatement.executeQuery();

			// array list per gli oggetti del articolo 
			ArrayList<Object> articoli= new ArrayList<>();

			// While per caricare l'array list 
			while (resultSet.next()) {
				articoli.add(resultSet.getInt("Id_articolo"));
				articoli.add(resultSet.getString("nome"));
				articoli.add(resultSet.getString("condizione"));
				
				int idImmagine= resultSet.getInt("Id_immagine");
				
				FileInputStream stream;

				if (resultSet.wasNull()) {
					stream= new FileInputStream("static_resources\\default_articolo.png");
				} else {
					stream= new FileInputStream("res\\immagini_articoli\\"+ idImmagine+ ".png");
				}

				articoli.add(stream.readAllBytes());

				stream.close();
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
				"]: C'e' stato un errore nell'apertura/scrittura/chiusura delle immagini degli articoli. "
				+ e.getMessage()
			);
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
		}
	}
}