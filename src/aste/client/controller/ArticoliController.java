package aste.client.controller;

import aste.Richiesta;
import aste.Risposta;
import aste.client.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jdk.jfr.Category;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ArticoliController
{
    @FXML
    private VBox articoliList;

    @FXML
    private ComboBox <String> category;

    @FXML
    private TextField categoryF;

    @FXML
    private Button confirmB;

    @FXML
    private Circle avatar;

    @FXML
    private Button addB;

    @FXML
    private Button AddCategory;

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
    public void initialize() throws IOException, ClassNotFoundException
    {
        AddCategory.setVisible(false);
        Richiesta richiest2 = new Richiesta();
        richiest2.tipoRichiesta = Richiesta.TipoRichiesta.VERIFICA_ADMIN;
        HelloApplication.output.writeObject(richiest2);
        Risposta risposta2 = (Risposta) HelloApplication.input.readObject();
        if ((risposta2.tipoRisposta == Risposta.TipoRisposta.OK) && (Boolean)risposta2.payload[0])
        {
            AddCategory.setVisible(true);
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
            category.getSelectionModel().select("Altre categorie");
            category.getItems().addAll(catmap.keySet());
        }
        Richiesta richiestaArticoli = new Richiesta();
        richiestaArticoli.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_ARTICOLI;
        richiestaArticoli.payload = new Object[5];
        richiestaArticoli.payload[0] = 10;
        richiestaArticoli.payload[1] = 1;
        richiestaArticoli.payload[2] = "";
        richiestaArticoli.payload[3] = catmap.get(category.getSelectionModel().getSelectedItem());
		richiestaArticoli.payload[4] = false;
        HelloApplication.output.writeObject(richiestaArticoli);
        Risposta rispostaArticoli = new Risposta();
        rispostaArticoli = (Risposta) HelloApplication.input.readObject();
        if (rispostaArticoli.tipoRisposta == Risposta.TipoRisposta.OK) {
            for (int i = 0; i < rispostaArticoli.payload.length / 4; i++) {
                HBox box = new HBox();
                FileOutputStream out = new FileOutputStream("cache/Articolo.png");
                out.write((byte[]) rispostaArticoli.payload[i * 4 + 3]);
                out.close();
                FileInputStream in = new FileInputStream("cache/Articolo.png");
                Image img = new Image(in);
                in.close();
                ImageView item = new ImageView();
                item.setImage(img);
                item.setFitWidth(100);
                item.setFitHeight(100);
                item.setPreserveRatio(true);
                String nome = (String) rispostaArticoli.payload[i * 4 + 1];
                String cond = (String) rispostaArticoli.payload[i * 4 + 2];
                Text nomeT = new Text("Nome: " + nome);
                Text condT = new Text("Condition: " + cond);
                Integer id = (Integer) rispostaArticoli.payload[i * 4 + 0];
                Text idT = new Text("Id: +" + id.toString());
                VBox vbox = new VBox();
                VBox vbox2 = new VBox();
                vbox2.setAlignment(Pos.CENTER);
                vbox.setAlignment(Pos.CENTER);
                vbox.getChildren().add(item);
                vbox2.getChildren().addAll(nomeT, condT);
                box.setPrefWidth(940);
                box.setAlignment(Pos.CENTER);
                box.getChildren().addAll(vbox, vbox2);
                articoliList.getChildren().add(box);
            }
        }
        else if (rispostaArticoli.payload[0] == Risposta.TipoErrore.CAMPI_INVALIDI)
        {
            System.out.println(rispostaArticoli.payload[0]);
            System.out.println(rispostaArticoli.payload[1]);
        }

        confirmB.setVisible(false);
        categoryF.setVisible(false);
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
    void CategorySelected(ActionEvent event)
    {

    }
    @FXML
    void AddClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Articolo.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("The AuctionHouse");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        Stage stage1 = (Stage) addB.getScene().getWindow();
        stage1.close();
    }

    @FXML
    void AddCategoryClicked (ActionEvent event)
    {
        confirmB.setVisible(true);
        categoryF.setVisible(true);
    }
    @FXML
    void ConfirmCategory(ActionEvent event) throws IOException, ClassNotFoundException {
        Richiesta richiesta = new Richiesta();
        richiesta.tipoRichiesta = Richiesta.TipoRichiesta.CREA_CATEGORIA;
        richiesta.payload = new Object[1];
        richiesta.payload[0] = categoryF.getText();
        HelloApplication.output.writeObject(richiesta);
        Risposta risposta = (Risposta) HelloApplication.input.readObject();
        if (risposta.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            confirmB.setVisible(false);
            categoryF.setVisible(false);
        }
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
    void LottiClicked(ActionEvent event) throws IOException {
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
}
