package aste.client.controller;

import aste.Offerta;
import aste.Richiesta;
import aste.Risposta;
import aste.client.HelloApplication;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PuntataController
{
    @FXML
    private Button ArticoloB;

    @FXML
    private Button AsteB;

    @FXML
    private Button HomeB;

    @FXML
    private Button LogoutB;

    @FXML
    private Button LottiB;

    @FXML
    private Button ProfileB;

    @FXML
    private Circle avatar;

    @FXML
    private TextField puntataF;

	@FXML
	private Label timerLabel;

	@FXML
	private Label astaName;

    @FXML
    private Button sendB;

	@FXML
	private VBox chatBox;

    @FXML
    private Text username;

    public static  Integer idAsta;

	public static LocalDateTime start ;

	public static Duration duration;

	public static String astaNome;

    public static InetAddress ipAddress;

    private static class ChatClient extends Thread
    {
        private final MulticastSocket socket;
        private PuntataController puntataController;

        public ChatClient(PuntataController puntataController, InetAddress ipMulticast) throws IOException {
            InetSocketAddress indirizzoSocket = new InetSocketAddress(ipMulticast, 3000);
            NetworkInterface interfaccia = NetworkInterface.getByInetAddress(HelloApplication.getLocalAddress());

            socket = new MulticastSocket();
            socket.setSoTimeout(10 * 1000);
            socket.joinGroup(indirizzoSocket, interfaccia);
        }

        @Override
        public void run() {
            try (socket;) {
                byte[] datiOfferta = new byte[Offerta.MAX_SERIALIZED_SIZE];
                DatagramPacket pacchetto = new DatagramPacket(datiOfferta, datiOfferta.length);

                while (!isInterrupted()) {
                    try {
                        socket.receive(pacchetto);
                        Offerta offerta = Offerta.fromByteArray(datiOfferta);
                        // TODO You should stuff here, you've got your offer
                        puntataController.ChatLog(offerta);
                    } catch (SocketTimeoutException e) {
                        continue;
                    } catch (ClassNotFoundException | ClassCastException e) {
                        System.err.println("C'è stato un errore nella conversione della puntata ricevuta: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void initialize() throws IOException, ClassNotFoundException
    {
        Richiesta richiestaProfilen = new Richiesta();
        richiestaProfilen.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_PROFILO;
        richiestaProfilen.payload = new Object[1];
        richiestaProfilen.payload[0] = 0;
        HelloApplication.output.writeObject(richiestaProfilen);
        Risposta rispostaProfilen = (Risposta) HelloApplication.input.readObject();
        if (rispostaProfilen.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            String nome = (String) rispostaProfilen.payload[0];
            String cognome = (String) rispostaProfilen.payload[1];
            LocalDate birthdate = (LocalDate) rispostaProfilen.payload[2];
            String city = (String) rispostaProfilen.payload[3];
            Integer cap = (Integer) rispostaProfilen.payload[4];
            String address = (String) rispostaProfilen.payload[5];
            String email = (String) rispostaProfilen.payload[6];
            String iban = (String) rispostaProfilen.payload[7];
            String s1 = nome.substring(0,1).toUpperCase() + nome.substring(1);
            String s2 = cognome.substring(0,1).toUpperCase() + cognome.substring(1);
            username.setText(s1  + " " + s2);
        } else if (rispostaProfilen.tipoRisposta == Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaProfilen.payload[0]);
        }
        System.out.println(idAsta);
		astaName.setText("Asta :" + astaNome);
                Duration timestamp = Duration.between(start,LocalDateTime.now());
                duration = duration.minus(timestamp);
                LocalDateTime dateTime = LocalDateTime.now().plus(duration);
                AnimationTimer timer = new AnimationTimer()
                {
                    @Override
                    public void handle(long l)
                    {
                        Duration remaining = Duration.between(LocalDateTime.now(),dateTime) ;
                        if (remaining.isPositive()) {
                            timerLabel.setText("Time Remaning: " + format(remaining));
                        } else {
                            timerLabel.setText("Time Remaning: " + format(Duration.ZERO));
                            stop();
                        }
                    }
                    private String format(Duration remaining) {
                        return String.format("%01d days, %02d:%02d:%02d",
								remaining.toDays(),
                                remaining.toHoursPart(),
                                remaining.toMinutesPart(),
                                remaining.toSecondsPart()
                        );
                    }
                };
                timer.start();
//				Richiesta richiestaPuntate = new Richiesta();
//				richiestaPuntate.payload = new Object[1];
//				richiestaPuntate.payload[0] = idAsta;
//				HelloApplication.output.writeObject(richiestaPuntate);
//				Risposta rispostaPuntate = (Risposta) HelloApplication.input.readObject();
//
//				if(rispostaPuntate.tipoRisposta == Risposta.TipoRisposta.OK)
//				{
//					for (int i = 0; i < rispostaPuntate.payload.length/5; i++) 
//					{
//					Integer idPuntata = (Integer) rispostaPuntate.payload[i / 5 + 0];
//					float valore = (Float)rispostaPuntate.payload[i / 5 + 1];
//					LocalDateTime messageDateTime = (LocalDateTime) rispostaPuntate.payload[i / 5 + 2];
//					Integer idUser = (Integer) rispostaPuntate.payload[i / 5 + 3];
//					String Username = (String)rispostaPuntate.payload[i / 5 + 4];
//					}
//				}
//                else if (rispostaPuntate.tipoRisposta == Risposta.TipoRisposta.ERRORE)
//                 {
//                    System.out.println(rispostaPuntate.payload[0]);
//                 }
    }

    void ChatLog (Offerta offerta)
    {

    }
    @FXML
    void SendClicked(ActionEvent event) throws IOException, ClassNotFoundException {
        Richiesta richiestaPunatata = new Richiesta();
        richiestaPunatata.tipoRichiesta = Richiesta.TipoRichiesta.EFFETTUA_PUNTATA;
        richiestaPunatata.payload = new Object[2];
        richiestaPunatata.payload[0] = idAsta;
        richiestaPunatata.payload[1] = Float.parseFloat(puntataF.getText());
        HelloApplication.output.writeObject(richiestaPunatata);
        Risposta rispostaPuntata = (Risposta)HelloApplication.input.readObject();
        if (rispostaPuntata.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            System.out.println("Hai puntato " + puntataF.getText());
        }else if (rispostaPuntata.tipoRisposta == Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaPuntata.payload[0]);
        }
    }

    @FXML
    void ArticoliClicked(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Articoli.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("The AuctionHouse");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        Stage stage1 = (Stage) AsteB.getScene().getWindow();
        stage1.close();
    }

    @FXML
    void AsteClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Aste.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("The AuctionHouse");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        Stage stage1 = (Stage) AsteB.getScene().getWindow();
        stage1.close();
    }

    @FXML
    void HomeClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Home.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("The AuctionHouse");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        Stage stage1 = (Stage) HomeB.getScene().getWindow();
        stage1.close();
    }

    @FXML
    void LogoutClicked(ActionEvent event)
    {
        Stage stage = (Stage) LogoutB.getScene().getWindow();
        stage.close();
    }

    @FXML
    void LottiClicked(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Lotti.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("The AuctionHouse");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        Stage stage1 = (Stage) LottiB.getScene().getWindow();
        stage1.close();
    }

    @FXML
    void ProfileClicked(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Profile.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("The AuctionHouse");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        Stage stage1 = (Stage) ProfileB.getScene().getWindow();
        stage1.close();
    }

}