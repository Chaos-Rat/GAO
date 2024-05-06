package aste.server;

import java.sql.Statement;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import aste.Offerta;

public class GestoreAste {
	private GestoreDatabase gestoreDatabase;
	private ArrayList<Byte> indirizziLiberi;
	private Hashtable<Integer, AstaFutura> mappaFuturi;
    private ScheduledExecutorService executorScheduler;

	private static class AstaFutura {
		public Runnable taskCreazione;
		public Runnable taskDistruzione;
		public ScheduledFuture<?> futuraCreazione;
		public ScheduledFuture<?> futuraDistruzione;

		public AstaFutura(Runnable taskCreazione, Runnable taskDistruzione, ScheduledFuture<?> futuraCrezione, ScheduledFuture<?> futuraDistruzione) {
			this.taskCreazione = taskCreazione;
			this.taskDistruzione = taskDistruzione;
			this.futuraCreazione = futuraCrezione;
			this.futuraDistruzione = futuraDistruzione;
		}
	}

	private static final byte[] INDIRIZZO_BASE = new byte[]{(byte)224, (byte)0, (byte)1, (byte)0};
    
    public GestoreAste(int threadPoolAste, GestoreDatabase gestoreDatabase) throws IllegalArgumentException {
		if (threadPoolAste <= 0) {
			throw new IllegalArgumentException("Il numero di core per la pool deve essere >= 1.");
		}

		this.gestoreDatabase = gestoreDatabase;
		indirizziLiberi = new ArrayList<>();
		mappaFuturi = new Hashtable<>();
		executorScheduler = Executors.newScheduledThreadPool(threadPoolAste);

		for (int i = 0; i < 256; ++i) {
			indirizziLiberi.add((byte)i);
		}
    }

    public synchronized void creaAsta(int idAsta,
		LocalDateTime dataOraInizio,
		Duration durata
	) throws IllegalStateException {
		if (indirizziLiberi.size() == 0) {
			throw new IllegalStateException("Impossibile creare una nuova asta, limite raggiunto.");
		}

		Runnable taskCrezione = () -> {
			// Preparando indirizzo multicast
			byte[] buffer = INDIRIZZO_BASE;
			buffer[3] = indirizziLiberi.remove(indirizziLiberi.size() - 1);

			// Aggiornando DB
			String queryUpdate = "UPDATE Aste\n" +
				"SET ip_multicast = ?\n" +
				"WHERE Id_asta = ?;"
			;

			try {
				Connection connection = gestoreDatabase.getConnection();
				PreparedStatement statement = connection.prepareStatement(queryUpdate);
				statement.setBytes(1, buffer);
				statement.setInt(2, idAsta);
				statement.executeUpdate();
			} catch (SQLException e) {
				throw new Error("[" + Thread.currentThread().getName() + "]: " + e.getMessage());
			}
		};

		Runnable taskDistruzione = () -> {
			String queryControlloVincita = "SELECT Aste.ip_multicast\n" +
				"FROM Aste\n" +
				"JOIN Puntate ON Aste.Id_asta = Puntate.Rif_asta\n" +
				"WHERE Id_asta = ?;"
			;
			
			String queryControlloAutomatica = "SELECT asta_automatica, durata\n" +
				"FROM Aste\n" +
				"WHERE Id_asta = ?;"
			;

			String queryAggiornamento = "UPDATE Aste\n" +
				"SET data_ora_inizio = CURRENT_TIMESTAMP\n" +
				"WHERE Id_asta = ?;"
			;

			try {
				Connection connection = gestoreDatabase.getConnection();
				PreparedStatement statement = connection.prepareStatement(queryControlloVincita);
				statement.setInt(1, idAsta);
				ResultSet resultSet = statement.executeQuery();

				byte indirizzoLibero = resultSet.getBytes("ip_multicast")[3];

				// Asta vinta
				if (resultSet.next()) {
					liberaIndirizzo(idAsta, indirizzoLibero);
					return;
				}

				statement = connection.prepareStatement(queryControlloAutomatica);
				statement.setInt(1, idAsta);
				resultSet = statement.executeQuery();

				if (!resultSet.next()) {
					System.err.println("[" + Thread.currentThread().getName() + "]: L'idLotto fornito per la creazione dell'asta non esiste.");
					return;
				}

				if (resultSet.getInt("asta_automatica") == 1) {
					AstaFutura astaFutura = mappaFuturi.get(idAsta);
					LocalTime durataDatabase = resultSet.getTime("durata").toLocalTime();
					astaFutura.futuraDistruzione = executorScheduler.schedule(astaFutura.taskDistruzione,
						Duration.between(LocalTime.of(0, 0), durataDatabase).toMinutes(),
						TimeUnit.MINUTES
					);
					return;
				}
				
				statement = connection.prepareStatement(queryAggiornamento);
				statement.setInt(1, idAsta);
				statement.executeUpdate();

				liberaIndirizzo(idAsta, indirizzoLibero);
			} catch (SQLException e) {
				throw new Error("[" + Thread.currentThread().getName() + "]: " + e.getMessage());
			}
		};

		mappaFuturi.put(idAsta,
			new AstaFutura(taskCrezione,
				taskDistruzione,
				executorScheduler.schedule(taskCrezione,
					durata.toMinutes(),
					TimeUnit.SECONDS
				),
				executorScheduler.schedule(taskDistruzione,
					durata.toMinutes(),
					TimeUnit.MINUTES
				)
			)
		);
	}

	private void liberaIndirizzo(int idAsta, byte indirizzoLibero) {
		indirizziLiberi.add(indirizzoLibero);
		mappaFuturi.remove(idAsta);
	}

    public synchronized void modificaAsta(int idAsta, int prezzoInizio, LocalDateTime dataOraInizio, LocalTime durata, boolean astaAutomatica, int rifLotto) {
		
    }

    public synchronized void annullaAsta(int idAsta, String descrizioneAnnullamento) {
		// if (!mappaFuturi.get(idAsta).(false)) {
		// 	// TODO: libera indirizzo
		// }
		
		String queryAnnullamento = "UPDATE Aste\n" +
			"SET descrizione_annullamento = ?\n" +
			"WHERE Id_asta = ?;"
		;
		
		try {
			Connection connection = gestoreDatabase.getConnection();
			PreparedStatement statement = connection.prepareStatement(queryAnnullamento);
			statement.setString(1, descrizioneAnnullamento);
			statement.setInt(2, idAsta);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new Error("[" + Thread.currentThread().getName() + "]: " + e.getMessage());
		}
    }

    public synchronized void effettuaOfferta(int idAsta,
		InetAddress indirizzoMulticast,
		InetAddress indirizzoServer,
		Offerta offerta
	) throws IOException {
		MulticastSocket socket = null;
		
		try {
			InetSocketAddress indirizzoSocket = new InetSocketAddress(indirizzoMulticast, 3000);
			NetworkInterface interfaccia = NetworkInterface.getByInetAddress(indirizzoServer);
			
			socket = new MulticastSocket();
			socket.joinGroup(indirizzoSocket, interfaccia);

			byte[] datiOfferta = Offerta.toByteArray(offerta);
			
			DatagramPacket pacchetto = new DatagramPacket(datiOfferta, datiOfferta.length, indirizzoSocket);
			socket.send(pacchetto);
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
    }
}