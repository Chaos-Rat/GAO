package aste.server;

import java.net.Socket;

import aste.Richiesta;
import aste.Risposta;

public class GestoreClient {
	private Socket socket;
	private	GestoreAste gestoreAste;
	private int idUtente;
	private Richiesta richiestaEntrante;
	private Risposta rispostaUscente;
}