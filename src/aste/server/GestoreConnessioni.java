package aste.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GestoreConnessioni {
	private GestoreDatabase gestoreDatabase;
    private GestoreAste gestoreAste;
    private ServerSocket serverSocket; 
    private ThreadPoolExecutor poolConnessioni; 

    public GestoreConnessioni(int threadPoolConnessioni,
		int porta,
		GestoreDatabase gestoreDatabase,
		GestoreAste gestoreAste
	) {
		this.gestoreDatabase = gestoreDatabase;
		this.gestoreAste = gestoreAste;
		poolConnessioni = (ThreadPoolExecutor)Executors.newFixedThreadPool(threadPoolConnessioni);
		System.out.println("Avviando server sulla porta " + porta + ".");
		
		try {
			serverSocket = new ServerSocket(porta);
			System.out.println("Server avviato sulla porta " + porta + ".");		
		} catch (IOException e) {
			throw new Error("[" + Thread.currentThread().getName() +
				"]: Impossibile creare una ServerSocket sulla porta " + porta +
				"."
			);
		}
    }

    public void avvia() {
        while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Accettata connessione da un client.");
				poolConnessioni.execute(new GestoreClient(clientSocket, gestoreDatabase, gestoreAste));
			} catch (IOException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: Connessione impossibile: " + e.getMessage() +
					"."
				);
			}
		}
    }

	public void finalizza() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.err.println("[" + Thread.currentThread().getName() +
					"]: Impossibile chiudere ServerSocket: " + e.getMessage() +
					"."
				);
			}
		}
	}
}
