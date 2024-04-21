package com.test.tps;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML
    private Button ArticoliB;

    @FXML
    private Button HomeB;

    @FXML
    private Button LogoutB;

    @FXML
    private Button LottiB;

    @FXML
    private Button AsteB;

    @FXML
    private Button Profile;

    @FXML
    private ImageView avatar;

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


    }
    @FXML
    void AsteClicked(ActionEvent event)
    {
        Title.setText("Nigger");
    }

}



