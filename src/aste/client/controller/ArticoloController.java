package aste.client.controller;

import aste.Richiesta;
import aste.Risposta;
import aste.client.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArticoloController
{
    private  List <File> selectedFiles;
    @FXML
    private Button createB;

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
    private Button addimg;

    @FXML
    private ImageView articolo;

    @FXML
    private Circle avatar;

    @FXML
    private ComboBox<String> category;

    private  HashMap<String, Integer> catmap = new HashMap<String, Integer>();

    @FXML
    private TextField conF;

    @FXML
    private TextField desF;

    @FXML
    private TextField lottoF;

    @FXML
    private Button nextB;

    @FXML
    private TextField nomeF;

    @FXML
    private Button previousB;

    @FXML
    private TextField quantita;

    @FXML
    private Text username;

    @FXML
    public void initialize() throws IOException, ClassNotFoundException
    {
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
		selectedFiles = new ArrayList<>();
        Richiesta richiestacat = new Richiesta();
        richiestacat.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_CATEGORIE;
        HelloApplication.output.writeObject(richiestacat);
        Risposta rispostacat = (Risposta) HelloApplication.input.readObject();
        if (rispostacat.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            for (int i = 0 ; i < rispostacat.payload.length/2 ; i++)
            {
                catmap.put((String) rispostacat.payload[i*2+1], (Integer) rispostacat.payload[i*2]);
            }
            category.getItems().addAll(catmap.keySet());
        }
        articolo.setFitWidth(295);
        articolo.setFitHeight(360);
        Image defaultimage = new Image(getClass().getResourceAsStream("../view/NoImage.png"));
        articolo.setPreserveRatio(true);
        articolo.setImage(defaultimage);
    }
    @FXML
    void AddImage(ActionEvent event)
    {
        FileChooser.ExtensionFilter ex1 = new FileChooser.ExtensionFilter("ImageFiles","*.png");
        Stage stageFile = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(ex1);
        fileChooser.setTitle("Select your image");
        selectedFiles = fileChooser.showOpenMultipleDialog(stageFile);
		if (selectedFiles != null) {
			File selectedFile = selectedFiles.get(0);
			Image image = new Image(selectedFile.toURI().toString());
			articolo.setFitWidth(295);
			articolo.setFitHeight(360);
			articolo.setPreserveRatio(true);
			articolo.setImage(image);
		}
        else {
			selectedFiles = new ArrayList<>();
		}
    }

    @FXML
    void CreateClicked(ActionEvent event) throws IOException, ClassNotFoundException {
        Richiesta richiesta = new Richiesta();
        richiesta.tipoRichiesta = Richiesta.TipoRichiesta.CREA_ARTICOLO;
        richiesta.payload = new Object[8];
        richiesta.payload[0] = nomeF.getText();
        richiesta.payload[1] = conF.getText();
        richiesta.payload[2] = desF.getText();
        richiesta.payload[3] = 1;
        richiesta.payload[4] = new byte[selectedFiles.size()][];
        for (int i = 0 ; i<selectedFiles.size();i++)
        {
            FileInputStream stream = new FileInputStream(selectedFiles.get(i));
            ((byte[][])richiesta.payload[4])[i] =  stream.readAllBytes();
            stream.close();
        }
        richiesta.payload[5] = Integer.valueOf(0);
        richiesta.payload[6] = catmap.get(category.getSelectionModel().getSelectedItem());
        richiesta.payload[7] = Integer.parseInt(quantita.getText());
        HelloApplication.output.writeObject(richiesta);
        Risposta risposta = (Risposta) HelloApplication.input.readObject();
        if (risposta.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            System.out.println("Articolo creato");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Articoli.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("The AuctionHouse");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            Stage stage1 = (Stage) createB.getScene().getWindow();
            stage1.close();
        }
        else if (risposta.payload[0] == Risposta.TipoErrore.CAMPI_INVALIDI)
        {
            System.out.println("Articolo non creato");
            System.out.println(risposta.payload[0]);
			System.out.println(risposta.payload[1]);
        } else {
			System.out.println("Articolo non creato");
            System.out.println(risposta.payload[0]);
		}
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
