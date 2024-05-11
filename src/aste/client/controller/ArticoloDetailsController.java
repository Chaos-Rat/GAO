package aste.client.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import aste.Richiesta;
import aste.Risposta;
import aste.client.HelloApplication;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ArticoloDetailsController {

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
    private ImageView articolo;

    @FXML
    private Circle avatar;

    @FXML
    private Text catText;

    @FXML
    private ComboBox<String> category;

    @FXML
    private Text condText;

    @FXML
    private Text descText;

    @FXML
    private Text emailText;

    @FXML
    private Text lottoText;

    @FXML
    private Button modifyB;

    @FXML
    private Text nomeText;

    @FXML
    private Text useridText;

    @FXML
    private Text username;

	public static Integer idArticolo;

	@FXML
	public void initialize() throws IOException, ClassNotFoundException {
//		System.out.println(idArticolo);
//        useridText.setUnderline(true);
//        Richiesta richiestaArticolo = new Richiesta();
//        richiestaArticolo.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_ARTICOLO;
//        richiestaArticolo.payload = new Object[1];
//        richiestaArticolo.payload[0] = idArticolo;
//        HelloApplication.output.writeObject(richiestaArticolo);
//        Risposta rispostaArticolo = (Risposta) HelloApplication.input.readObject();
//        if (rispostaArticolo.tipoRisposta == Risposta.TipoRisposta.OK)
//        {
//            nomeText.setText((String) rispostaArticolo.payload[0]);
//            condText.setText((String) rispostaArticolo.payload[1]);
//            descText.setText((String) rispostaArticolo.payload[2]);
//            lottoText.setText((String) rispostaArticolo.payload[3]);
//            FileOutputStream out = new FileOutputStream("cache/Articolo.png");
//            out.write((byte[]) rispostaArticolo.payload[4]);
//            out.close();
//            FileInputStream in = new FileInputStream("cache/Articolo.png");
//            Image img = new Image(in);
//            articolo.setImage(img);
//            in.close();
//            useridText.setText((String) rispostaArticolo.payload[5]);
//            emailText.setText((String) rispostaArticolo.payload[6]);
//
//        } else if (rispostaArticolo.tipoRisposta == Risposta.TipoRisposta.ERRORE)
//        {
//            System.out.println(rispostaArticolo.payload[0]);
//            System.out.println(rispostaArticolo.payload[1]);
//        }
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
    void idClicked(MouseEvent event)
    {
        useridText.setText("idk sumn");
//        Stage stage1 = (Stage) useridText.getScene().getWindow();
//        stage1.close();
    }

    @FXML
    void AsteClicked(ActionEvent event) throws IOException 
	{
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
    void ModifyClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/ArticoloModify.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("The AuctionHouse");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        Stage stage1 = (Stage) modifyB.getScene().getWindow();
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