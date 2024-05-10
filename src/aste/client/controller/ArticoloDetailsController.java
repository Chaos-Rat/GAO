package aste.client.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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
    private ComboBox<?> category;

    @FXML
    private TextField conF;

    @FXML
    private Button createB;

    @FXML
    private TextField desF;

    @FXML
    private TextField lottoF;

    @FXML
    private TextField nomeF;

    @FXML
    private TextField quantita;

    @FXML
    private Text username;

	public static Integer idArticolo;

	@FXML
	public void initialize()
	{
		System.out.println(idArticolo);
		
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
    void CreateClicked(ActionEvent event) {

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