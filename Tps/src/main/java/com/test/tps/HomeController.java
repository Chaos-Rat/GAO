package com.test.tps;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

import java.io.IOException;

public class HomeController {

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
    private VBox vbox;

    @FXML
    void ProfileClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Profile.fxml"));
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
    public void initialize()
    {
        Image image = new Image(getClass().getResourceAsStream("Avatar.png"));
        avatar.setFill(new ImagePattern(image));
    }
    @FXML
    void AsteClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Aste.fxml"));
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



