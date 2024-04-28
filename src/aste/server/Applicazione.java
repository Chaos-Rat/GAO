package aste.server;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

		System.out.println(ChronoUnit.SECONDS.between(LocalDateTime.now(), LocalDateTime.parse("2024-04-28T11:15")));
	}
}