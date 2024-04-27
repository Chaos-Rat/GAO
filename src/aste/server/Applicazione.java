package aste.server;

public class Applicazione {
	private GestoreConnessioni gestoreConnessioni;
	private GestoreAste gestoreAste;

	public Applicazione(int threadPoolConnessioni, int ThreadPoolAste) {
		
	}

	public void avvia() {

	}

	public void finalizza() {

	}

	public static void main(String[] args) {
		Applicazione applicazione = new Applicazione(2, 2);
		applicazione.avvia();
		applicazione.finalizza();
	}
}