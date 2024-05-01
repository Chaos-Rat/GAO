package aste.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

	private void visualizzaLotto() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visualizzaLotto'");
	}

	private void visualizzaLotti() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visualizzaLotti'");
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
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'creaLotto'");
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

		if (idLottoInput == null || idLottoInput <= 0) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "idLotto"};
			return;
		}

		try {
			Connection connection = gestoreDatabase.getConnection();
		} catch (SQLException e) {
			System.err.println("[" + Thread.currentThread().getName() +
				"]: C'e' stato un errore nella query di controllo dell'idLotto nella creazione del pay. " + e.getMessage()
			);

			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.GENERICO };
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

		int[] categorieInput;

		try {
			categorieInput = (int[])richiestaEntrante.payload[6];
		} catch (ClassCastException e) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "categorieInput"};
			return;
		}

		if (categorieInput == null || categorieInput.length < 1) {
			rispostaUscente.tipoRisposta = TipoRisposta.ERRORE;
			rispostaUscente.payload = new Object[]{ TipoErrore.CAMPI_INVALIDI, "categorieInput"};
			return;
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