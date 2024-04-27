package aste.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class ProfileController
{
    @FXML
    private Button ArticoliB;

    @FXML
    private Button AsteB;

    @FXML
    private Button LottiB;

    @FXML
    private Button ChangeB;

    @FXML
    private Button backB;

    @FXML
    private Button LogoutB;

    @FXML
    private Button ConfirmB;

    @FXML
    private Button HomeB;

    @FXML
    private Button ModifyB;

    @FXML
    private Circle avatar;

    @FXML
    private TextField emailEdit;

    @FXML
    private Text emailText;

    @FXML
    private TextField nameEdit;

    @FXML
    private Text nameText;

    @FXML
    private TextField surnameEdit;

    @FXML
    private Text surnameText;

    @FXML
    void ModifyInfo(ActionEvent event)
    {
        ModifyB.setVisible(false);
        ConfirmB.setVisible(true);
        nameEdit.setVisible(true);
        surnameEdit.setVisible(true);
        emailEdit.setVisible(true);
        ChangeB.setVisible(true);
    }

    @FXML
    void ChangeAvatar (ActionEvent event)throws InvocationTargetException
    {
        FileChooser.ExtensionFilter ex1 = new FileChooser.ExtensionFilter("ImageFiles","*.png");
        FileChooser.ExtensionFilter ex2 = new FileChooser.ExtensionFilter("ImageFiles","*.jpg");
        FileChooser.ExtensionFilter ex3 = new FileChooser.ExtensionFilter("ImageFiles","*.gif");
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(ex1,ex2,ex3);
        fileChooser.setTitle("Select your image");
        File selectedFile = fileChooser.showOpenDialog(stage);
        Image image = new Image(selectedFile.toURI().toString());
        avatar.setFill(new ImagePattern(image));
    }

    @FXML
    void LogoutClicked(ActionEvent event) throws IOException
    {
        Stage stage = (Stage) LogoutB.getScene().getWindow();
        stage.close();

    }

    @FXML
    void BackClicked(ActionEvent event) throws IOException
    {
        ModifyB.setVisible(true);
        backB.setVisible(false);
        ConfirmB.setVisible(false);
        nameEdit.setVisible(false);
        surnameEdit.setVisible(false);
        emailEdit.setVisible(false);
        ChangeB.setVisible(false);
        Image image2 = new Image(getClass().getResourceAsStream("Avatar.png"));
        ImageView imageView = new ImageView();
        imageView.setImage(image2);
        imageView.setPreserveRatio(true);
        avatar.setFill(new ImagePattern(imageView.getImage()));

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
    void ConfirmClicked(ActionEvent event)
    {
        nameText.setText("Name: " + nameEdit.getText());
        surnameText.setText("Surname: " + surnameEdit.getText());
        emailText.setText("Email: " + emailEdit.getText());
        ModifyB.setVisible(true);
        ConfirmB.setVisible(false);
        nameEdit.setVisible(false);
        surnameEdit.setVisible(false);
        emailEdit.setVisible(false);
        ChangeB.setVisible(false);
    }

    public void initialize()
    {
        Image image = new Image(getClass().getResourceAsStream("../view/Avatar.png"));
        avatar.setFill(new ImagePattern(image));
        backB.setVisible(false);
        ChangeB.setVisible(false);
        ConfirmB.setVisible(false);
        nameEdit.setVisible(false);
        surnameEdit.setVisible(false);
        emailEdit.setVisible(false);
    }

    @FXML
    void LottiCLicked(ActionEvent event) throws IOException
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

}
