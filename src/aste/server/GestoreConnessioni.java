package aste.server;

import java.net.ServerSocket;
import java.util.concurrent.ThreadPoolExecutor;

public class GestoreConnessioni {

    private GestoreAste gestoreAste; 
    private ServerSocket serverSocket; 
    private ThreadPoolExecutor poolConnession; 


    public GestoreConnessioni(int threadPoolConnessioni, GestoreAste gestoreAste) {

    }

    public void avvia() {
        
    }
}
