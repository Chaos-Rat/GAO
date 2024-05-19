package aste.client.controller;

import aste.Offerta;
import aste.Richiesta;
import aste.Risposta;
import aste.Risposta.TipoErrore;
import aste.Risposta.TipoRisposta;
import aste.Richiesta.TipoRichiesta;
import aste.client.HelloApplication;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Task;
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

    public static  Integer idAsta;

	public static LocalDateTime start;

	public static Duration duration;

	public static String astaNome;

    public static InetAddress ipAddress;

    private static ChatClient handlerPuntate;

	private float valoreMassimoLocale;

    private static class ChatClient extends Thread
    {
        private final MulticastSocket socket;
		private InetSocketAddress indirizzoSocket;
		private NetworkInterface interfaccia;
        private PuntataController puntataController;

		public ChatClient(PuntataController puntataController, InetAddress ipMulticast) throws IOException {
			this.puntataController = puntataController;
			this.indirizzoSocket = new InetSocketAddress(ipMulticast, 0);
			interfaccia = NetworkInterface.getByInetAddress(HelloApplication.getLocalAddress());

			// Se l'interfaccia è di loopback non funziona se la si passa direttamente a joinGroup/leaveGroup.
			// Settandola a null, e facendo decidere al sistema operativo, invece si ¯\_(ツ)_/¯
			if (interfaccia.isLoopback()) {
				interfaccia = null;
			}
           
			// Bisogna fare il bind della porta in quanto riceventi
			socket = new MulticastSocket(6000);
            socket.setSoTimeout(10 * 1000);
			socket.joinGroup(indirizzoSocket, interfaccia);
        }

        @Override
		public void run() {
			try {
                byte[] datiOfferta = new byte[Offerta.MAX_SERIALIZED_SIZE];
                DatagramPacket pacchetto = new DatagramPacket(datiOfferta, datiOfferta.length);

                while (!isInterrupted()) {
                    try {
                        socket.receive(pacchetto);
                        Offerta offerta = Offerta.fromByteArray(datiOfferta);
						
						if (offerta.idUtente != HelloApplication.idUtenteLoggato) {
							Platform.runLater(() -> {
								try {
									puntataController.addRemoteUserBox(offerta);
								} catch (ClassNotFoundException | IOException e) {
									e.printStackTrace();
								}
							});

							return;
						}
						
						if (offerta.valore > puntataController.valoreMassimoLocale) {
							Platform.runLater(() -> {
								try {
									puntataController.addLocalUserBox(offerta);
								} catch (ClassNotFoundException | IOException e) {
									e.printStackTrace();
								}
							});
						}
                    } catch (SocketTimeoutException e) {
                        continue;
                    } catch (ClassNotFoundException | ClassCastException e) {
                        System.err.println("C'è stato un errore nella conversione della puntata ricevuta: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
			} finally {
				try {
					socket.leaveGroup(indirizzoSocket, interfaccia);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				socket.close();
			}
		}
    }

    @FXML
    public void initialize() throws IOException, ClassNotFoundException
    {
		valoreMassimoLocale = 0;
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

		Richiesta richiesta = new Richiesta();
		richiesta.tipoRichiesta = TipoRichiesta.VISUALIZZA_PUNTATE;
		richiesta.payload = new Object[]{ idAsta };
		HelloApplication.output.writeObject(richiesta);

		Risposta risposta = (Risposta)HelloApplication.input.readObject();
		
		if (risposta.tipoRisposta == TipoRisposta.OK) {
			for (int i = 0; i < risposta.payload.length / 5; ++i) {
				Offerta offerta = new Offerta((Integer)risposta.payload[i * 5 + 3],
					(Float)risposta.payload[i * 5 + 1],
					(LocalDateTime)risposta.payload[i * 5 + 2]
				);

				if (HelloApplication.idUtenteLoggato != offerta.idUtente) {
					addRemoteUserBox(offerta);
				} else {
					addLocalUserBox(offerta);
				}
			}
		} else {
			System.err.println("C'è stato un errore nella visualizzazione delle puntate: " + (TipoErrore)risposta.payload[0]);
		}

		handlerPuntate = new ChatClient(this, ipAddress);
		handlerPuntate.start();
    }

    void addRemoteUserBox(Offerta offerta) throws ClassNotFoundException, IOException
    {
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER_LEFT);
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

        puntataRicevuta.setText(offerta.dataOra.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" + offerta.idUtente + ". " + nome + " " + cognome +" \n" + String.valueOf(offerta.valore)+"$");
        TextFlow textFlow = new TextFlow(puntataRicevuta);
        textFlow.setStyle("-fx-background-color: rgb(233,233,235);" + "-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5,5,5,10));
        textFlow.setPrefWidth(200);
        hbox.getChildren().add(textFlow);
        chatBox.getChildren().add(hbox);
    }

	void addLocalUserBox(Offerta offerta) throws IOException, ClassNotFoundException {
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
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER_RIGHT);
		hbox.setPadding(new Insets(5,5,5,10));
		Text puntata = new Text();
		puntata.setText(offerta.dataOra.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" + offerta.idUtente + ". " + nome + " " + cognome +" \n" + String.valueOf(offerta.valore)+"$");
		puntata.setFill(Color.color(0.934,0.945,0.996));
		TextFlow textFlow = new TextFlow(puntata);
		textFlow.setStyle("-fx-background-color: rgb(15,125,242);" + 
		" -fx-background-radius: 20px;" +
		"-fx-color: rgb(239,242,255)");
		textFlow.setPadding(new Insets(5, 5,5,10));
		textFlow.setPrefWidth(200);
		hbox.getChildren().add(textFlow);
		chatBox.getChildren().add(hbox);
	}

    @FXML
    void SendClicked(ActionEvent event) throws IOException, ClassNotFoundException {
		Richiesta richiestaPunatata = new Richiesta();
        richiestaPunatata.tipoRichiesta = Richiesta.TipoRichiesta.EFFETTUA_PUNTATA;
		float valorePuntata = Float.parseFloat(puntataF.getText());
		valoreMassimoLocale = valorePuntata;
        richiestaPunatata.payload = new Object[2];
        richiestaPunatata.payload[0] = idAsta;
        richiestaPunatata.payload[1] = valorePuntata;
        HelloApplication.output.writeObject(richiestaPunatata);
        Risposta rispostaPuntata = (Risposta)HelloApplication.input.readObject();
        if (rispostaPuntata.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            System.out.println("Hai puntato " + puntataF.getText());
			addLocalUserBox(new Offerta(HelloApplication.idUtenteLoggato, valorePuntata, LocalDateTime.now()));
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