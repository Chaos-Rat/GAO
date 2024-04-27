package aste.server;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Hashtable;
import java.util.concurrent.ScheduledExecutorService;

public class GestoreAste {

    private int numeroAsteCorrenti; 
    private Hashtable<Integer, InetAddress> mappaIndirizzi;
    private ScheduledExecutorService executorScheduler;
    
    
    // GestoreAste(threadPoolAste : int)
    public GestoreAste(int threadPoolAste) {

    }

    // creaAsta(prezzoInizio : int, dataOraInizio : LocalDateTime, durata : LocalTime, astaAutomatica : boolean, rifLotto : int)
    public void creaAsta(int prezzoInizio, LocalDateTime dataOraInizio, LocalTime durata, boolean astaAutomatica, int rifLotto) {

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