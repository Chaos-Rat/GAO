package aste.client.controller;

import aste.Richiesta;
import aste.Risposta;
import aste.client.HelloApplication;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
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
            category.getSelectionModel().select("Tutte le categorie");
            category.getItems().addAll(catmap.keySet());
        }
        Richiesta richiestaArticoli = new Richiesta();
        richiestaArticoli.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_ARTICOLI;
        richiestaArticoli.payload = new Object[5];
        richiestaArticoli.payload[0] = 10;
        richiestaArticoli.payload[1] = 1;
        richiestaArticoli.payload[2] = "";
        richiestaArticoli.payload[3] = 0 ;
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
                    nomeT.setWrappingWidth(150);
                    condT.setWrappingWidth(150);
                    idT.setWrappingWidth(150);
                    Button button = new Button();
                    button.setText("Details");
                    button.setStyle(".button\n" +
                            "{\n" +
                            "    -fx-background-color :  #16f70a ;\n" +
                            "    -fx-background-radius: 15,15,15,15;\n" +
                            "}\n" +
                            "\n" +
                            ".button:hover\n" +
                            "{\n" +
                            "    -fx-background-color :  #1aab13 ;\n" +
                            "    -fx-background-radius: 15,15,15,15;\n" +
                            "}\n" +
                            "\n" +
                            ".button:pressed\n" +
                            "{\n" +
                            "    -fx-background-color :  #096e03 ;\n" +
                            "    -fx-background-radius: 15,15,15,15;\n" +
                            "}");

                    button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                ArticoloDetailsController.idArticolo = id;
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/ArticoloDetails.fxml"));
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
                    VBox vbox = new VBox();
                    VBox vbox2 = new VBox();
                    VBox vbox3 = new VBox();
                    vbox2.setAlignment(Pos.CENTER);
                    vbox.setAlignment(Pos.CENTER);
                    vbox3.setAlignment(Pos.CENTER);
                    vbox.getChildren().add(item);
                    vbox2.getChildren().addAll(nomeT, condT);
                    vbox3.getChildren().addAll(button);
                    box.setSpacing(50);
                    box.setPrefWidth(940);
                    box.setAlignment(Pos.CENTER);
                    box.getChildren().addAll(vbox, vbox2, vbox3);
                    articoliList.getChildren().add(box);
                }
        }
        else if (rispostaArticoli.payload[0] == Risposta.TipoRisposta.ERRORE)
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
    void CategorySelected(ActionEvent event) throws IOException, ClassNotFoundException
    {
        articoliList.getChildren().clear();
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
        Richiesta richiestaArticoli = new Richiesta();
        richiestaArticoli.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_ARTICOLI;
        richiestaArticoli.payload = new Object[5];
        richiestaArticoli.payload[0] = 10;
        richiestaArticoli.payload[1] = 1;
        richiestaArticoli.payload[2] = "";
        if(category.getSelectionModel().isSelected(0))
        {
            richiestaArticoli.payload[3] =0;
        }else
        {
            richiestaArticoli.payload[3] = catmap.get(category.getSelectionModel().getSelectedItem());
        }
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
                nomeT.setWrappingWidth(150);
                condT.setWrappingWidth(150);
                idT.setWrappingWidth(150);
                Button button = new Button();
                button.setText("Details");
                button.setStyle(".button\n" +
                        "{\n" +
                        "    -fx-background-color :  #16f70a ;\n" +
                        "    -fx-background-radius: 15,15,15,15;\n" +
                        "}\n" +
                        "\n" +
                        ".button:hover\n" +
                        "{\n" +
                        "    -fx-background-color :  #1aab13 ;\n" +
                        "    -fx-background-radius: 15,15,15,15;\n" +
                        "}\n" +
                        "\n" +
                        ".button:pressed\n" +
                        "{\n" +
                        "    -fx-background-color :  #096e03 ;\n" +
                        "    -fx-background-radius: 15,15,15,15;\n" +
                        "}");

                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            ArticoloDetailsController.idArticolo = id;
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/ArticoloDetails.fxml"));
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
                VBox vbox = new VBox();
                VBox vbox2 = new VBox();
                VBox vbox3 = new VBox();
                vbox2.setAlignment(Pos.CENTER);
                vbox.setAlignment(Pos.CENTER);
                vbox3.setAlignment(Pos.CENTER);
                vbox.getChildren().add(item);
                vbox2.getChildren().addAll(nomeT, condT);
                vbox3.getChildren().addAll(button);
                box.setSpacing(50);
                box.setPrefWidth(940);
                box.setAlignment(Pos.CENTER);
                box.getChildren().addAll(vbox, vbox2, vbox3);
                articoliList.getChildren().add(box);
            }
        }
        else if (rispostaArticoli.payload[0] == Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaArticoli.payload[0]);
            System.out.println(rispostaArticoli.payload[1]);
        }
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
