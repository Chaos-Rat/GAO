package aste.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GestoreAste {
    private int numeroAsteCorrenti; 
    private Hashtable<Integer, ScheduledFuture> mappaFuturi;
    private ScheduledExecutorService executorScheduler;
    
    public GestoreAste(int threadPoolAste) throws IllegalArgumentException {
		if (threadPoolAste <= 0) {
			throw new IllegalArgumentException("Il numero di core per la pool deve essere >= 1.");
		}

		numeroAsteCorrenti = 0;
		mappaFuturi = new Hashtable<>();
		executorScheduler = Executors.newScheduledThreadPool(threadPoolAste);
    }

    public synchronized void creaAsta(int prezzoInizio,
		LocalDateTime dataOraInizio,
		LocalTime durata,
		boolean astaAutomatica,
		int rifLotto
	) {
		if (numeroAsteCorrenti >= 256) {
			throw new IllegalStateException("Impossibile creare una nuova asta, limite raggiunto.");
		}

		Runnable schedulerTask = () -> {
			// Preparando indirizzo multicast
			byte[] buffer = new byte[4];

			buffer[0] = (byte)224;
			buffer[1] = buffer[2] = (byte)0;
			buffer[3] = (byte)numeroAsteCorrenti;

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
					ChronoUnit.MINUTES.between(LocalDateTime.now(), dataOraInizio),
					0,
					TimeUnit.MINUTES
				)
			);
		} else {
			mappaFuturi.put(0, // TODO: Rimpiazzare con valore dal DB
				executorScheduler.schedule(schedulerTask,
					ChronoUnit.MINUTES.between(LocalDateTime.now(), dataOraInizio),
					TimeUnit.MINUTES
				)
			);
		}
    }

    // modificaAsta(idAsta : int, prezzoInizio : int, dataOraInizio : LocalDateTime, durata : LocalTime, astaAutomatica : boolean, rifLotto : int)
    public void modificaAsta(int idAsta, int prezzoInizio, LocalDateTime dataOraInizio, LocalTime durata, boolean astaAutomatica, int rifLotto) {

    }

    // annullaAsta(id : int, descrizioneAnnullamento : String)
    public void annullaAsta(int annullaAsta, String descrizioneAnnullamento) {

    }

    // effettuaOfferta(idAsta : int, idUtente : int, valore : int)
    public void effettuaOfferta(int idAsta, int idUtente, int valore) {

    }
}