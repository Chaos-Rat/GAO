package aste.client.controller;

import aste.Richiesta;
import aste.Risposta;
import aste.client.HelloApplication;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import  javafx.scene.control.ComboBox;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

public class LottiController
{
    @FXML
    private VBox lottiList;

    @FXML
    private Button addLotto;

    @FXML
    private Button ArticoliB;

    @FXML
    private Circle avatar;

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
    private ComboBox<String> category;

    @FXML
    private Text username;



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
        Richiesta richiestaLotti = new Richiesta();
        richiestaLotti.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_LOTTI;
        richiestaLotti.payload = new Object[5];
        richiestaLotti.payload[0] = 10 ;
        richiestaLotti.payload[1] = 1;
        richiestaLotti.payload[2] = "";
        richiestaLotti.payload[3] = 0;
        richiestaLotti.payload[4] = false;
        HelloApplication.output.writeObject(richiestaLotti);
        Risposta rispostaLotti = (Risposta) HelloApplication.input.readObject();
        if (rispostaLotti.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            for (int i = 0; i < rispostaLotti.payload.length / 3; i++) {
                HBox box = new HBox();
                FileOutputStream out = new FileOutputStream("cache/Articolo.png");
                out.write((byte[]) rispostaLotti.payload[i * 3 + 2]);
                out.close();
                FileInputStream in = new FileInputStream("cache/Articolo.png");
                Image img = new Image(in);
                in.close();
                ImageView item = new ImageView();
                item.setImage(img);
                item.setFitWidth(100);
                item.setFitHeight(100);
                item.setPreserveRatio(true);
                String nome = (String) rispostaLotti.payload[i * 3 + 1];
                Text nomeT = new Text("Nome: " + nome);
                Integer id = (Integer) rispostaLotti.payload[i * 3 + 0];
                Text idT = new Text("Id: +" + id.toString());
                nomeT.setWrappingWidth(150);
                idT.setWrappingWidth(150);
                VBox vbox = new VBox();
                VBox vbox2 = new VBox();
                vbox2.setAlignment(Pos.CENTER);
                vbox.setAlignment(Pos.CENTER);
                vbox.getChildren().add(item);
                vbox2.getChildren().addAll(nomeT);
				box.setSpacing(50);
                box.setPrefWidth(940);
                box.setAlignment(Pos.CENTER);
                box.getChildren().addAll(vbox, vbox2);
                lottiList.getChildren().add(box);
            }
        }else if (rispostaLotti.payload[0] == Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaLotti.payload[0]);
            System.out.println(rispostaLotti.payload[1]);
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
            imageView.setPreserveRatio(true);
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
    void CategorySelected (ActionEvent event) throws IOException, ClassNotFoundException
    {
        lottiList.getChildren().clear();
        Richiesta richiestacat = new Richiesta();
        richiestacat.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_CATEGORIE;
        HelloApplication.output.writeObject(richiestacat);
        Risposta rispostacat = (Risposta) HelloApplication.input.readObject();
        HashMap<String, Integer> catmap = new HashMap<String, Integer>();
        if (rispostacat.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            catmap.put("Tutte le categorie",0);
            for (int i = 1 ; i < rispostacat.payload.length/2 ; i++)
            {
                catmap.put((String) rispostacat.payload[i*2+1], (Integer) rispostacat.payload[i*2]);
            }
            category.getSelectionModel().getSelectedItem();
        }
        System.out.println(catmap.get(category.getSelectionModel().getSelectedItem()));
        Richiesta richiestaLotti = new Richiesta();
        richiestaLotti.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_LOTTI;
        richiestaLotti.payload = new Object[5];
        richiestaLotti.payload[0] = 10;
        richiestaLotti.payload[1] = 1;
        richiestaLotti.payload[2] = "";
        richiestaLotti.payload[3] = catmap.get(category.getSelectionModel().getSelectedItem());
        richiestaLotti.payload[4] = false;
        HelloApplication.output.writeObject(richiestaLotti);
        Risposta rispostaLotti =(Risposta) HelloApplication.input.readObject();
        if (rispostaLotti.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            for (int i = 0; i < rispostaLotti.payload.length / 3; i++) {
                HBox box = new HBox();
                FileOutputStream out = new FileOutputStream("cache/Articolo.png");
                out.write((byte[]) rispostaLotti.payload[i * 3 + 2]);
                out.close();
                FileInputStream in = new FileInputStream("cache/Articolo.png");
                Image img = new Image(in);
                in.close();
                ImageView item = new ImageView();
                item.setImage(img);
                item.setFitWidth(100);
                item.setFitHeight(100);
                item.setPreserveRatio(true);
                String nome = (String) rispostaLotti.payload[i * 3 + 1];
                Text nomeT = new Text("Nome: " + nome);
                Integer id = (Integer) rispostaLotti.payload[i * 3 + 0];
                Text idT = new Text("Id: +" + id.toString());
                nomeT.setWrappingWidth(150);
                idT.setWrappingWidth(150);
                VBox vbox = new VBox();
                VBox vbox2 = new VBox();
                vbox2.setAlignment(Pos.CENTER);
                vbox.setAlignment(Pos.CENTER);
                vbox.getChildren().add(item);
                vbox2.getChildren().addAll(nomeT);
                box.setSpacing(50);
                box.setPrefWidth(940);
                box.setAlignment(Pos.CENTER);
                box.getChildren().addAll(vbox, vbox2);
                lottiList.getChildren().add(box);
            }
        }
        else if (rispostaLotti.tipoRisposta == Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaLotti.payload[0]);
        }
    }

    @FXML
    void AddLottoClicked(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Lotto.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("The AuctionHouse");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        Stage stage1 = (Stage) addLotto.getScene().getWindow();
        stage1.close();
    }

    @FXML
    void HomeClicked(ActionEvent event) throws IOException
    {
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
