package aste.client.controller;

import aste.Richiesta;
import aste.Risposta;
import aste.client.HelloApplication;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class HomeController {

    @FXML
    private ToolBar PageMenu;

    @FXML
    private VBox asteList;

    @FXML
    private Button ArticoliB;

    @FXML
    private Button HomeB;

    @FXML
    private HBox avatarBox;

    @FXML
    private Button LogoutB;

    @FXML
    private Button LottiB;

    @FXML
    private Button AsteB;

    @FXML
    private Button ProfileB;

    @FXML
    private Circle avatar;

    @FXML
    private Text Title;

    @FXML
    private ComboBox<String>category;

    @FXML
    private VBox vbox;

    @FXML
    private Text username;

    @FXML
    void ProfileClicked(ActionEvent event) throws IOException {
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

    @FXML
    void LogoutClicked(ActionEvent event) throws IOException
    {
        Stage stage = (Stage) LogoutB.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void initialize() throws IOException, ClassNotFoundException
    {
        Richiesta richiestaProfile = new Richiesta();
        richiestaProfile.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_PROFILO;
        richiestaProfile.payload = new Object[1];
        richiestaProfile.payload[0] = 0;
        HelloApplication.output.writeObject(richiestaProfile);
        Risposta rispostaProfile = (Risposta) HelloApplication.input.readObject();
        if (rispostaProfile.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            String nome = (String) rispostaProfile.payload[0];
            String cognome = (String) rispostaProfile.payload[1];
            LocalDate birthdate = (LocalDate) rispostaProfile.payload[2];
            String city = (String) rispostaProfile.payload[3];
            Integer cap = (Integer) rispostaProfile.payload[4];
            String address = (String) rispostaProfile.payload[5];
            String email = (String) rispostaProfile.payload[6];
            String iban = (String) rispostaProfile.payload[7];
            String s1 = nome.substring(0,1).toUpperCase() + nome.substring(1);
            String s2 = cognome.substring(0,1).toUpperCase() + cognome.substring(1);
            username.setText(s1  + " " + s2);
        } else if (rispostaProfile.tipoRisposta == Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaProfile.payload[0]);
        }
        Richiesta richiestacat = new Richiesta();
        richiestacat.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_CATEGORIE;
        HelloApplication.output.writeObject(richiestacat);
        Risposta rispostacat = (Risposta) HelloApplication.input.readObject();
        HashMap<String, Integer> catmap = new HashMap<String, Integer>();
        if (rispostacat.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            for (int i = 0 ; i < rispostacat.payload.length/2 ; i++)
            {
                catmap.put((String) rispostacat.payload[i*2+1], (Integer) rispostacat.payload[i*2]);
            }
            catmap.put("Tutte le categorie",0);
            category.getSelectionModel().select("Tutte le categorie");
            category.getItems().addAll(catmap.keySet());
        }
        Richiesta richiestaAste = new Richiesta();
        richiestaAste.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_ASTE;
        richiestaAste.payload = new Object[4];
        richiestaAste.payload[0] = 10;
        richiestaAste.payload[1] = 1;
        richiestaAste.payload[2] = "";
        richiestaAste.payload[3] = catmap.get(category.getSelectionModel().getSelectedItem());
        HelloApplication.output.writeObject(richiestaAste);
        Risposta rispostaAste = (Risposta) HelloApplication.input.readObject();
        if (rispostaAste.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            for (int i = 0; i < rispostaAste.payload.length/5; i++)
            {
                HBox box = new HBox();
                Integer idAsta = (Integer) rispostaAste.payload[i*5+0];
                Duration duration = (Duration)rispostaAste.payload[i*5+1];
                Float price = (Float)rispostaAste.payload[i*5+2];
                String Lottoname = (String)rispostaAste.payload[i*5+3];
                FileOutputStream out = new FileOutputStream("cache/Articolo.png");
                out.write((byte[]) rispostaAste.payload[i*5+4]);
                out.close();
                FileInputStream in = new FileInputStream("cache/Articolo.png");
                Image img = new Image(in);
                in.close();
                ImageView item = new ImageView();
                item.setImage(img);
                item.setFitWidth(100);
                item.setFitHeight(100);
                item.setPreserveRatio(true);
                Label timerLabel = new Label();
                timerLabel.setVisible(false);
				LocalDateTime endDateTime = LocalDateTime.now().plus(duration);
                AnimationTimer timer = new AnimationTimer()
                {
                    @Override
                    public void handle(long l)
                    {
                        Duration remaining = Duration.between(LocalDateTime.now(), endDateTime);
                        if (remaining.isPositive()) {
                            timerLabel.setText(format(remaining));
                        } else {
                            timerLabel.setText(format(Duration.ZERO));
                            stop();
                        }
                    }
                    private String format(Duration remaining) {
                        return String.format("%02d:%02d:%02d",
                                remaining.toHoursPart(),
                                remaining.toMinutesPart(),
                                remaining.toSecondsPart()
                        );
                    }
                };
                timer.start();
                Text nomeT = new Text("LottoNome: " + Lottoname);
                Text priceT = new Text("StartingPrice : " + price.toString());
                nomeT.setWrappingWidth(150);
                priceT.setWrappingWidth(150);
                VBox vbox = new VBox();
                VBox vbox2 = new VBox();
                VBox vbox3 = new VBox();
                VBox vbox4 = new VBox();
                Button button = new Button();
                button.setText("Details");
                button.setId("#button");
                button.setStyle(".button\n" +
                        "{\n" +
                        "    -fx-background-color :  #6F5CC2 ;\n" +
                        "    -fx-background-radius: 10,10,10,10;\n" +
                        "}\n" +
                        "\n" +
                        ".button:hover\n" +
                        "{\n" +
                        "    -fx-background-color :  #947cfc ;\n" +
                        "    -fx-background-radius: 10,10,10,10;\n" +
                        "}\n" +
                        "\n" +
                        ".button:pressed\n" +
                        "{\n" +
                        "    -fx-background-color :  #6254a1 ;\n" +
                        "    -fx-background-radius: 10,10,10,10;\n" +
                        "}");
//                button.setStyle("-fx-background-color : #16f70a ;");
                button.setOnAction(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        try {
                            AstaDetailsController.idAsta = idAsta;
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/AstaDetails.fxml"));
                            Parent root = loader.load();
                            Scene scene = new Scene(root);
                            Stage stage = new Stage();
                            stage.setTitle("The AuctionHouse");
                            stage.setScene(scene);
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.show();
                            Stage stage1 = (Stage) button.getScene().getWindow();
                            stage1.close();
                        }catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                vbox.setAlignment(Pos.CENTER);
                vbox2.setAlignment(Pos.CENTER);
                vbox3.setAlignment(Pos.CENTER);
                vbox4.setAlignment(Pos.CENTER);
                vbox.getChildren().add(item);
                vbox2.getChildren().addAll(nomeT,priceT);
                vbox3.getChildren().addAll(button);
                vbox4.getChildren().add(timerLabel);
                box.setSpacing(50);
                box.setPrefWidth(940);
                box.setAlignment(Pos.CENTER);
                box.getChildren().addAll(vbox,vbox2,vbox3,vbox4);
                asteList.getChildren().add(box);
            }
        } else if (rispostaAste.payload[0]== Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaAste.payload[0]);
            System.out.println((rispostaAste.payload[1]));
        }
        Richiesta richiesta = new Richiesta();
        richiesta.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_IMMAGINE_PROFILO;
        richiesta.payload = new Object[]{0};
        HelloApplication.output.writeObject(richiesta);
        Risposta risposta = (Risposta) HelloApplication.input.readObject();
        if (risposta.tipoRisposta == Risposta.TipoRisposta.OK) {
            FileOutputStream picture = new FileOutputStream("imagine.png");
            picture.write((byte[]) risposta.payload[0]);
            picture.close();
            FileInputStream defaultImg = new FileInputStream("imagine.png");
            Image image = new Image(defaultImg);
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            avatar.setFill(new ImagePattern(imageView.getImage()));
            defaultImg.close();
        }
        else
        {
            System.out.println(risposta.tipoRisposta.toString());
            System.out.println(((Risposta.TipoErrore) risposta.payload[0]).toString());
        }
    }

    @FXML
    void CategorySelected (ActionEvent event) throws IOException, ClassNotFoundException {
        asteList.getChildren().clear();
        Richiesta richiestacat = new Richiesta();
        richiestacat.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_CATEGORIE;
        HelloApplication.output.writeObject(richiestacat);
        Risposta rispostacat = (Risposta) HelloApplication.input.readObject();
        HashMap<String, Integer> catmap = new HashMap<String, Integer>();
        if (rispostacat.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            for (int i = 0 ; i < rispostacat.payload.length/2 ; i++)
            {
                catmap.put((String) rispostacat.payload[i*2+1], (Integer) rispostacat.payload[i*2]);
            }
            catmap.put("Tutte le categorie",0);
            category.getSelectionModel().getSelectedItem();
        }
        Richiesta richiestaAste = new Richiesta();
        richiestaAste.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_ASTE;
        richiestaAste.payload = new Object[5];
        richiestaAste.payload[0] = 10;
        richiestaAste.payload[1] = 1;
        richiestaAste.payload[2] = "";
        richiestaAste.payload[3] = catmap.get(category.getSelectionModel().getSelectedItem());
        HelloApplication.output.writeObject(richiestaAste);
        Risposta rispostaAste =(Risposta) HelloApplication.input.readObject();
        if (rispostaAste.tipoRisposta == Risposta.TipoRisposta.OK) {
            for (int i = 0; i < rispostaAste.payload.length / 5; i++) {
                HBox box = new HBox();
                Integer idAsta = (Integer) rispostaAste.payload[i * 5 + 0];
                Duration duration = (Duration) rispostaAste.payload[i * 5 + 1];
                Float price = (Float) rispostaAste.payload[i * 5 + 2];
                String Lottoname = (String) rispostaAste.payload[i * 5 + 3];
                FileOutputStream out = new FileOutputStream("cache/Articolo.png");
                out.write((byte[]) rispostaAste.payload[i * 5 + 4]);
                out.close();
                FileInputStream in = new FileInputStream("cache/Articolo.png");
                Image img = new Image(in);
                in.close();
                ImageView item = new ImageView();
                item.setImage(img);
                item.setFitWidth(100);
                item.setFitHeight(100);
                item.setPreserveRatio(true);
                Label timerLabel = new Label();
                timerLabel.setVisible(false);
                LocalDateTime endDateTime = LocalDateTime.now().plus(duration);
                AnimationTimer timer = new AnimationTimer() {
                    @Override
                    public void handle(long l) {
                        Duration remaining = Duration.between(LocalDateTime.now(), endDateTime);
                        if (remaining.isPositive()) {
                            timerLabel.setText(format(remaining));
                        } else {
                            timerLabel.setText(format(Duration.ZERO));
                            stop();
                        }
                    }

                    private String format(Duration remaining) {
                        return String.format("%02d:%02d:%02d",
                                remaining.toHoursPart(),
                                remaining.toMinutesPart(),
                                remaining.toSecondsPart()
                        );
                    }
                };
                timer.start();
                Text nomeT = new Text("LottoNome: " + Lottoname);
                Text priceT = new Text("StartingPrice : " + price.toString());
                nomeT.setWrappingWidth(150);
                priceT.setWrappingWidth(150);
                VBox vbox = new VBox();
                VBox vbox2 = new VBox();
                VBox vbox3 = new VBox();
                VBox vbox4 = new VBox();
                Button button = new Button();
                button.setText("Details");
                button.setId("#button");
                button.setStyle(".button\n" +
                        "{\n" +
                        "    -fx-background-color :  #6F5CC2 ;\n" +
                        "    -fx-background-radius: 10,10,10,10;\n" +
                        "}\n" +
                        "\n" +
                        ".button:hover\n" +
                        "{\n" +
                        "    -fx-background-color :  #947cfc ;\n" +
                        "    -fx-background-radius: 10,10,10,10;\n" +
                        "}\n" +
                        "\n" +
                        ".button:pressed\n" +
                        "{\n" +
                        "    -fx-background-color :  #6254a1 ;\n" +
                        "    -fx-background-radius: 10,10,10,10;\n" +
                        "}");
//                button.setStyle("-fx-background-color : #16f70a ;");
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            AstaDetailsController.idAsta = idAsta;
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/AstaDetails.fxml"));
                            Parent root = loader.load();
                            Scene scene = new Scene(root);
                            Stage stage = new Stage();
                            stage.setTitle("The AuctionHouse");
                            stage.setScene(scene);
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.show();
                            Stage stage1 = (Stage) button.getScene().getWindow();
                            stage1.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                vbox.setAlignment(Pos.CENTER);
                vbox2.setAlignment(Pos.CENTER);
                vbox3.setAlignment(Pos.CENTER);
                vbox4.setAlignment(Pos.CENTER);
                vbox.getChildren().add(item);
                vbox2.getChildren().addAll(nomeT, priceT);
                vbox3.getChildren().addAll(button);
                vbox4.getChildren().add(timerLabel);
                box.setSpacing(50);
                box.setPrefWidth(940);
                box.setAlignment(Pos.CENTER);
                box.getChildren().addAll(vbox, vbox2, vbox3, vbox4);
                asteList.getChildren().add(box);
            }
        }

    }

    @FXML
    void LottiClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Lotti.fxml"));
        Parent root = loader.load() ;
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
    void ArticoliClicked(ActionEvent event) throws IOException {
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

}



