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
import javafx.scene.control.CheckBox;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class LottoController
{
    @FXML
    private ComboBox<String> category;

    @FXML
    private VBox articoliList;

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
    private Button createB;

    @FXML
    private TextField lottoF;

    @FXML
    public void initialize() throws IOException, ClassNotFoundException
    {
        Richiesta richiestaProfile = new Richiesta();
        richiestaProfile.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_IMMAGINE_PROFILO;
        richiestaProfile.payload = new Object[]{0};
        HelloApplication.output.writeObject(richiestaProfile);
        Risposta rispostaProfile = (Risposta) HelloApplication.input.readObject();
        if (rispostaProfile.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            FileOutputStream picture = new FileOutputStream("imagine.png");
            picture.write((byte[]) rispostaProfile.payload[0]);
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
            System.out.println(rispostaProfile.tipoRisposta.toString());
            System.out.println(((Risposta.TipoErrore) rispostaProfile.payload[0]).toString());
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
        }else if (rispostaProfile.payload[0] == Risposta.TipoErrore.CAMPI_INVALIDI)
        {
            System.out.println(rispostaProfile.payload[0]);
            System.out.println(rispostaProfile.payload[1]);
        }
        Richiesta richiestaArticoli = new Richiesta();
        richiestaArticoli.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_ARTICOLI;
        richiestaArticoli.payload = new Object[4];
        richiestaArticoli.payload[0] = 10;
        richiestaArticoli.payload[1] = 1;
        richiestaArticoli.payload[2] = "";
        richiestaArticoli.payload[3] = catmap.get(category.getSelectionModel().getSelectedItem());
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
                nomeT.setWrappingWidth(150);
                condT.setWrappingWidth(150);
                Integer id = (Integer) rispostaArticoli.payload[i * 4 + 0];
                Text idT = new Text("Id: " + id.toString());
                idT.setWrappingWidth(150);
                CheckBox check = new CheckBox();
                VBox vbox = new VBox();
                VBox vbox2 = new VBox();
                VBox vbox3 = new VBox();
                box.setSpacing(50);
                check.setOnAction(event ->
                {
                      if (check.isSelected())
                      {
//                          Integer idCheck = Integer.parseInt(idT.getText());
                          String nomeCheck = nomeT.getText();
                          System.out.println(  " nome = "+ nomeCheck);
                      }
                });
                vbox2.setAlignment(Pos.CENTER);
                vbox.setAlignment(Pos.CENTER);
                vbox3.setAlignment(Pos.CENTER);
                vbox.getChildren().add(item);
                vbox2.getChildren().addAll(nomeT,condT,idT);
                vbox3.getChildren().add(check);
                box.setPrefWidth(940);
                box.setAlignment(Pos.CENTER);
                box.getChildren().addAll(vbox,vbox2,vbox3);
                articoliList.getChildren().add(box);
            }
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
        Stage stage1 = (Stage) ArticoloB.getScene().getWindow();
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

