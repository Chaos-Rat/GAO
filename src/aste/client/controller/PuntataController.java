package aste.client.controller;

import aste.Offerta;
import aste.Richiesta;
import aste.Risposta;
import aste.client.HelloApplication;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

	private Integer idPuntata;

	private float valore;

	private  Integer idUser;

	private String Username;

	private LocalDateTime messageDateTime;

    public static  Integer idAsta;

	public static LocalDateTime start;

	public static Duration duration;

	public static String astaNome;

    public static InetAddress ipAddress;

    private static ChatClient handlerPuntate;

    private static class ChatClient extends Thread
    {
        private final MulticastSocket socket;
        private PuntataController puntataController;

        @SuppressWarnings("deprecation")
		public ChatClient(PuntataController puntataController, InetAddress ipMulticast) throws IOException {
			this.puntataController = puntataController;
            socket = new MulticastSocket(3000);
            socket.setSoTimeout(10 * 1000);
            socket.joinGroup(ipMulticast);
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
		chatBox.setSpacing(8);
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
//				richiestaPuntate.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_PUNTATE;
//				richiestaPuntate.payload = new Object[1];
//				richiestaPuntate.payload[0] = idAsta;
//				HelloApplication.output.writeObject(richiestaPuntate);
//				Risposta rispostaPuntate = (Risposta) HelloApplication.input.readObject();
//
//				if(rispostaPuntate.tipoRisposta == Risposta.TipoRisposta.OK)
//				{
//					for (int i = 0; i < rispostaPuntate.payload.length/5; i++)
//					{
//					idPuntata = (Integer) rispostaPuntate.payload[i / 5 + 0];
//					valore = (Float) rispostaPuntate.payload[i / 5 + 1];
//					messageDateTime = (LocalDateTime) rispostaPuntate.payload[i / 5 + 2];
//					idUser = (Integer) rispostaPuntate.payload[i / 5 + 3];
//					Username = (String)rispostaPuntate.payload[i / 5 + 4];
//					HBox hbox = new HBox();
//					hbox.setAlignment(Pos.CENTER_LEFT);
//					hbox.setPadding(new Insets(5,5,5,10));
//					Text email = new Text();
//					email.setText(messageDateTime.toString() + "\n" +"Username \n" + String.valueOf(idUser));
//					TextFlow textFlow = new TextFlow();
//					textFlow.setStyle("-fx-background-color: rgb(233,233,235)" + "-fx-background-radius: 20px");
//					textFlow.setPadding(new Insets(5,5,5,10));
//					chatBox.getChildren().add(textFlow);
//					}
//				}
//                else if (rispostaPuntate.tipoRisposta == Risposta.TipoRisposta.ERRORE)
//                 {
//                    System.out.println(rispostaPuntate.payload[0]);
//                 }
        handlerPuntate = new ChatClient(this, ipAddress);
        handlerPuntate.start();
    }

    void ChatLog (Offerta offerta) throws ClassNotFoundException, IOException
    {
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		hbox.setPadding(new Insets(5,5,5,10));
		Text puntataRicevuta = new Text();
		Richiesta richiestaUser = new Richiesta();
		richiestaUser.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_PROFILO;
		richiestaUser.payload = new Object[]{ offerta.idUtente };
		HelloApplication.output.writeObject(richiestaUser);
		String nome;
		String cognome;
		String email;
		Risposta rispostaUser = (Risposta) HelloApplication.input.readObject();

        if (rispostaUser.tipoRisposta == Risposta.TipoRisposta.ERRORE) {
            System.out.println(rispostaUser.payload[0]);
            return;
        }

        nome = (String)rispostaUser.payload[0];
        cognome = (String)rispostaUser.payload[1];
        email = (String)rispostaUser.payload[2];

        puntataRicevuta.setText(offerta.dataOra.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" + offerta.idUtente + " " + nome + " " + cognome +" \n" + String.valueOf(offerta.valore));
        TextFlow textFlow = new TextFlow(puntataRicevuta);
        textFlow.setStyle("-fx-background-color: rgb(233,233,235)" + "-fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5,5,5,10));
        textFlow.setPrefWidth(200);
        hbox.getChildren().add(textFlow);
        chatBox.getChildren().add(hbox);
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
			HBox hbox = new HBox();
			hbox.setAlignment(Pos.CENTER_RIGHT);
			hbox.setPadding(new Insets(5,5,5,10));
			Text puntata = new Text();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			puntata.setText(LocalDateTime.now().format(formatter) + "\n" +"Username \n" + puntataF.getText()+ "$");
            puntata.setFill(Color.color(0.934,0.945,0.996));
			TextFlow textFlow = new TextFlow(puntata);
			textFlow.setStyle("-fx-background-color: rgb(15,125,242);" + 
			" -fx-background-radius: 20px;" +
			"-fx-color: rgb(239,242,255)");
			textFlow.setPadding(new Insets(5, 5,5,10));
            textFlow.setPrefWidth(200);
			hbox.getChildren().add(textFlow);
			chatBox.getChildren().add(hbox);
        }else if (rispostaPuntata.tipoRisposta == Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaPuntata.payload[0]);
        }
    }

    @FXML
    void ArticoliClicked(ActionEvent event) throws IOException
    {
        handlerPuntate.interrupt();
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
        handlerPuntate.interrupt();
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
        handlerPuntate.interrupt();
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
        handlerPuntate.interrupt();
        Stage stage = (Stage) LogoutB.getScene().getWindow();
        stage.close();
    }

    @FXML
    void LottiClicked(ActionEvent event) throws IOException
    {
        handlerPuntate.interrupt();
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
        handlerPuntate.interrupt();
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