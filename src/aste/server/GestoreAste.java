package aste.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GestoreAste {
	private ArrayList<Byte> indirizziLiberi;
	private Hashtable<Integer, ScheduledFuture> mappaFuturi;
    private ScheduledExecutorService executorScheduler;

	private final byte[] indirizzoBase;

    
    public GestoreAste(int threadPoolAste) throws IllegalArgumentException {
		if (threadPoolAste <= 0) {
			throw new IllegalArgumentException("Il numero di core per la pool deve essere >= 1.");
		}

		indirizziLiberi = new ArrayList<>();
		mappaFuturi = new Hashtable<>();
		executorScheduler = Executors.newScheduledThreadPool(threadPoolAste);
		indirizzoBase = new byte[]{(byte)224, (byte)0, (byte)0, (byte)0};

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
			byte[] buffer = indirizzoBase;
			buffer[3] = indirizziLiberi.getLast();
			indirizziLiberi.removeLast();

			InetAddress indirizzo = null;

			try {
				indirizzo = InetAddress.getByAddress(buffer);
			} catch (UnknownHostException e) {
				throw new Error(e.getMessage());
			}

			// TODO: Aggiornare ip_multicast sul DB
		};

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
    public synchronized void annullaAsta(int annullaAsta, String descrizioneAnnullamento) {

    }

    // effettuaOfferta(idAsta : int, idUtente : int, valore : int)
    public synchronized void effettuaOfferta(int idAsta, int idUtente, int valore) {

    }
}