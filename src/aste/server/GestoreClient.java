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


	public void login(String username, String password) {
        // Implementazione del login
    }

    public void registrazione(String username, String password) {
        // Implementazione della registrazione
    }

    public void logout() {
        // Implementazione del logout
    }

    public void visualizzaProfilo() {
        // Implementazione della visualizzazione del profilo
    }

    public void visualizzaImmagineProfilo() {
        // Implementazione della visualizzazione dell'immagine del profilo
    }

    public void modificaProfilo() {
        // Implementazione della modifica del profilo
    }

    public void visualizzaAste() {
        // Implementazione della visualizzazione delle aste
    }

    // Altri metodi pubblici

    // Metodi privati

    private void visualizzaAsteConcluse() {
        // Implementazione della visualizzazione delle aste concluse
    }

    private void visualizzaAsteCorrenti() {
        // Implementazione della visualizzazione delle aste correnti
    }

    private void visualizzaAsteProgrammate() {
        // Implementazione della visualizzazione delle aste programmate
    }

    private void visualizzaAsteVinte() {
        // Implementazione della visualizzazione delle aste vinte
    }

    private void visualizzaAsteSalvate() {
        // Implementazione della visualizzazione delle aste salvate
    }

    private void creaAsta() {
        // Implementazione della creazione di un'asta
    }

    private void modificaAsta() {
        // Implementazione della modifica di un'asta
    }

    private void visualizzaAsta() {
        // Implementazione della visualizzazione di un'asta
    }

    private void salvaAsta() {
        // Implementazione del salvataggio di un'asta
    }

    private void annullaAsta() {
        // Implementazione dell'annullamento di un'asta
    }

    private void visualizzaArticoli() {
        // Implementazione della visualizzazione degli articoli
    }

}