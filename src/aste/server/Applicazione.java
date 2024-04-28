package aste.server;

public class Applicazione {
	private GestoreAste gestoreAste;
	private GestoreConnessioni gestoreConnessioni;
	private GestoreDatabase gestoreDatabase;

	public Applicazione(int threadPoolAste, int porta, int threadPoolConnessioni, GestoreDatabase.Opzioni opzioniDatabase) {
		gestoreDatabase = new GestoreDatabase(opzioniDatabase);
		gestoreAste = new GestoreAste(threadPoolAste, gestoreDatabase);
		gestoreConnessioni = new GestoreConnessioni(threadPoolConnessioni, porta, gestoreDatabase, gestoreAste);
	}

	public void avvia() {
		gestoreConnessioni.avvia();
	}

	public void finalizza() {
		
	}

	public static void main(String[] args) {
		GestoreDatabase.Opzioni opzioniDB = new GestoreDatabase.Opzioni("com.mysql.cj.jdbc.Driver",
			"jdbc:mysql://localhost:3306/gestione_aste_online",
			"server",
			"JUiv[xDc*OCqCg26",
			5,
			10,
			100
		);

		Applicazione applicazione = new Applicazione(2, 3000, 2, opzioniDB);
		applicazione.avvia();
		applicazione.finalizza();
	}
}