package aste.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class HelloApplication extends Application
{
    private static Socket socket;
    public static ObjectOutputStream output;
    public static ObjectInputStream input;
	public static int idUtenteLoggato;

    static {
        try {
            socket = new Socket("localhost",3000);
            System.out.println("sono connesso :)");
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("view/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Image image = new Image(getClass().getResourceAsStream("view/Screenshot.png"));
        stage.getIcons().add(image);
        stage.setTitle("The AuctionHouse");
        stage.setScene(scene);
        stage.show();
    }

    public static InetAddress getLocalAddress() {
        return socket.getLocalAddress();
    }

    public static void main(String[] args)
    {
        launch();
    }
}