package com.test.tps;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;


public class ProfileController
{

    @FXML
    private Button ChangeB;

    @FXML
    private Button ConfirmB;

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
    void ChangeAvatar(ActionEvent event)
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
        Image image = new Image(getClass().getResourceAsStream("Avatar.png"));
        avatar.setFill(new ImagePattern(image));
        ChangeB.setVisible(false);
        ConfirmB.setVisible(false);
        nameEdit.setVisible(false);
        surnameEdit.setVisible(false);
        emailEdit.setVisible(false);
    }
}
