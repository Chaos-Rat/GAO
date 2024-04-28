package aste.server;

import java.net.ServerSocket;
import java.util.concurrent.ThreadPoolExecutor;

public class GestoreConnessioni {
	private GestoreDatabase gestoreDatabase;
    private GestoreAste gestoreAste;
    private ServerSocket serverSocket; 
    private ThreadPoolExecutor poolConnessioni; 


    public GestoreConnessioni(int threadPoolConnessioni, int porta, GestoreDatabase gestoreDatabase, GestoreAste gestoreAste) {
		this.gestoreDatabase = gestoreDatabase;
		this.gestoreAste = gestoreAste;
    }

    public void avvia() {
        
    }
}
