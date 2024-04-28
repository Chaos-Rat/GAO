package aste.server;

import java.sql.Statement;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.protocol.x.SyncFlushDeflaterOutputStream;

import aste.Risposta;
import aste.Risposta.TipoErrore;

public class GestoreAste {
	private GestoreDatabase gestoreDatabase;
	private ArrayList<Byte> indirizziLiberi;
	@SuppressWarnings("rawtypes")
	private Hashtable<Integer, ScheduledFuture> mappaFuturi;

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
		INDIRIZZO_BASE = new byte[]{(byte)224, (byte)0, (byte)0, (byte)0};

		for (int i = 0; i < 256; ++i) {
			indirizziLiberi.add((byte)i);
		}
    }

    public synchronized void creaAsta(int prezzoInizio,
		LocalDateTime dataOraInizio,
		LocalTime durata,
		boolean astaAutomatica,
		int rifLotto
	) {
		if (indirizziLiberi.size() == 0) {
			throw new IllegalStateException("Impossibile creare una nuova asta, limite raggiunto.");
		}

		Runnable schedulerTask = () -> {
			// Preparando indirizzo multicast
			byte[] buffer = INDIRIZZO_BASE;
			buffer[3] = indirizziLiberi.getLast();
			indirizziLiberi.removeLast();

			InetAddress indirizzo = null;

			try {
				indirizzo = InetAddress.getByAddress(buffer);
			} catch (UnknownHostException e) {
				throw new Error(e.getMessage());
			}

			// Aggiornando DB
			StringBuilder builder = new StringBuilder();

			for (int i = 0; i < buffer.length; ++i) {
				builder.append(Integer.toBinaryString((int)buffer[i]));
			}

			String query = "UPDATE Aste\n" +
				"SET ip_multicast = " + builder.toString() + "\n" +
				"WHERE Id_asta = " + ";";
		};

		Connection connection = null;

		try {
			String query = "INSERT INTO Aste (prezzo_inizio, data_ora_inizio, asta_automatica, Rif_lotto)\n" + 
				"VALUES (" + prezzoInizio + ", " + dataOraInizio + ", " +
				durata + ", " + (astaAutomatica ? 1 : 0) + ", " +
				rifLotto + ");";

			connection = gestoreDatabase.getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate(query, new String[]{"Id_asta"});
			ResultSet result = statement.getGeneratedKeys();

			result.getInt("Id_asta");

			connection.commit();
		} catch (SQLException e) {
			
		}

		if (astaAutomatica) {
			mappaFuturi.put(0, // TODO: Rimpiazzare con valore dal DB
				executorScheduler.scheduleWithFixedDelay(schedulerTask,
					ChronoUnit.SECONDS.between(LocalDateTime.now(), dataOraInizio),
					0,
					TimeUnit.SECONDS
				)
			);
		} else {
			mappaFuturi.put(0, // TODO: Rimpiazzare con valore dal DB
				executorScheduler.schedule(schedulerTask,
					ChronoUnit.SECONDS.between(LocalDateTime.now(), dataOraInizio),
					TimeUnit.SECONDS
				)
			);
		}
    }

    // modificaAsta(idAsta : int, prezzoInizio : int, dataOraInizio : LocalDateTime, durata : LocalTime, astaAutomatica : boolean, rifLotto : int)
    public void modificaAsta(int idAsta, int prezzoInizio, LocalDateTime dataOraInizio, LocalTime durata, boolean astaAutomatica, int rifLotto) {

    }

    // annullaAsta(id : int, descrizioneAnnullamento : String)
    public synchronized void annullaAsta(int idAsta, String descrizioneAnnullamento) {

    }

    // effettuaOfferta(idAsta : int, idUtente : int, valore : int)
    public synchronized void effettuaOfferta(int idAsta, int idUtente, int valore) {

    }
}