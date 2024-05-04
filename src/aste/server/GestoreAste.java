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
	private Hashtable<Integer, ScheduledFuture<?>> mappaFuturi;

    private ScheduledExecutorService executorScheduler;

	private final byte[] INDIRIZZO_BASE;
    
    public GestoreAste(int threadPoolAste, GestoreDatabase gestoreDatabase) throws IllegalArgumentException {
		if (threadPoolAste <= 0) {
			throw new IllegalArgumentException("Il numero di core per la pool deve essere >= 1.");
		}

		this.gestoreDatabase = gestoreDatabase;
		indirizziLiberi = new ArrayList<>();
		mappaFuturi = new Hashtable<>();
		executorScheduler = Executors.newScheduledThreadPool(threadPoolAste);
		INDIRIZZO_BASE = new byte[]{(byte)224, (byte)0, (byte)1, (byte)0};

		for (int i = 0; i < 256; ++i) {
			indirizziLiberi.add((byte)i);
		}
    }

    public synchronized void creaAsta(int prezzoInizio,
		LocalDateTime dataOraInizio,
		Duration durata,
		boolean astaAutomatica,
		int rifLotto
	) throws IllegalStateException {
		if (indirizziLiberi.size() == 0) {
			throw new IllegalStateException("Impossibile creare una nuova asta, limite raggiunto.");
		}

		final int idAsta;

		String queryInserimento = "INSERT INTO Aste (prezzo_inizio, data_ora_inizio, asta_automatica, Rif_lotto)\n" + 
			"VALUES (" + prezzoInizio + ", " + dataOraInizio + ", " +
			durata + ", " + (astaAutomatica ? 1 : 0) + ", " +
			rifLotto + ");";

		try {
			Connection connection = gestoreDatabase.getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate(queryInserimento, new String[]{"Id_asta"});
			ResultSet result = statement.getGeneratedKeys();
			idAsta = result.getInt(1);
		} catch (SQLException e) {
			throw new Error("[" + Thread.currentThread().getName() + "]: " + e.getMessage());
		}

		Runnable schedulerTask = () -> {
			// Preparando indirizzo multicast
			byte[] buffer = INDIRIZZO_BASE;
			buffer[3] = indirizziLiberi.remove(indirizziLiberi.size() - 1);

			StringBuilder builder = new StringBuilder();

			for (int i = 0; i < buffer.length; ++i) {
				builder.append(Integer.toBinaryString((int)buffer[i]));
			}

			// Aggiornando DB
			String queryUpdate = "UPDATE Aste\n" +
				"SET ip_multicast = " + builder.toString() + "\n" +
				"WHERE Id_asta = " + idAsta + ";";

			try {
				Connection connection = gestoreDatabase.getConnection();
				Statement statement = connection.createStatement();
				statement.executeUpdate(queryUpdate);
			} catch (SQLException e) {
				throw new Error("[" + Thread.currentThread().getName() + "]: " + e.getMessage());
			}
		};

		if (astaAutomatica) {
			mappaFuturi.put(idAsta,
				executorScheduler.scheduleWithFixedDelay(schedulerTask,
					ChronoUnit.SECONDS.between(LocalDateTime.now(), dataOraInizio),
					0,
					TimeUnit.SECONDS
				)
			);
		} else {
			mappaFuturi.put(idAsta,
				executorScheduler.schedule(schedulerTask,
					ChronoUnit.SECONDS.between(LocalDateTime.now(), dataOraInizio),
					TimeUnit.SECONDS
				)
			);
		}
    }

    public synchronized void modificaAsta(int idAsta, int prezzoInizio, LocalDateTime dataOraInizio, LocalTime durata, boolean astaAutomatica, int rifLotto) {
		
    }

    public synchronized void annullaAsta(int idAsta, String descrizioneAnnullamento) {
		if (!mappaFuturi.get(idAsta).cancel(false)) {
			// TODO: libera indirizzo
		}
		
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
	) {
		MulticastSocket socket = null;
		
		try {
			InetSocketAddress indirizzoSocket = new InetSocketAddress(indirizzoMulticast, 3000);
			NetworkInterface interfaccia = NetworkInterface.getByInetAddress(indirizzoServer);
			
			socket = new MulticastSocket();
			socket.joinGroup(indirizzoSocket, interfaccia);

			byte[] datiOfferta = Offerta.toByteArray(offerta);
			
			DatagramPacket pacchetto = new DatagramPacket(datiOfferta, datiOfferta.length, indirizzoSocket);
			socket.send(pacchetto);
		} catch (IOException e) {
			System.err.println("[" + 
			Thread.currentThread().getName() +
			"]: C'e' stato un errore nella diffusione della puntata mediante multicast.");
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
    }
}