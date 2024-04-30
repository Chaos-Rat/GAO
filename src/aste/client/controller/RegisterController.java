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
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class RegisterController {
    @FXML
    private Button backB2;

    @FXML
    private Button backB;

    @FXML
    private Button regB;

    @FXML
    private TextField capField;

    @FXML
    private Text capText;

    @FXML
    private TextField addressField;

    @FXML
    private Text addressText;

    @FXML
    private TextField cityField;

    @FXML
    private Text cityText;

    @FXML
    private TextField cognField;

    @FXML
    private Text cognText;

    @FXML
    private DatePicker dateP;

    @FXML
    private Text dateText;

    @FXML
    private TextField emailF;

    @FXML
    private Text emailT;

    @FXML
    private TextField ibanField;

    @FXML
    private Text ibanText;

    @FXML
    private TextField nameField;

    @FXML
    private Text nameText;

    @FXML
    private Button nextB;

    @FXML
    private PasswordField passF;

    @FXML
    private Text passT;

    @FXML
    public void initialize()
    {
        emailT.setVisible(false);
        emailF.setVisible(false);
        passT.setVisible(false);
        passF.setVisible(false);
        backB2.setVisible(false);
        regB.setVisible(false);

    }
    @FXML
    void BackStageClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../view/Login.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("The AuctionHouse");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        Stage stage1 = (Stage) backB.getScene().getWindow();
        stage1.close();

    }
    @FXML
    void RegisterClick(ActionEvent event) throws IOException, ClassNotFoundException {
        Richiesta richiesta = new Richiesta();
        richiesta.tipoRichiesta = Richiesta.TipoRichiesta.REGISTRAZIONE;
        richiesta.payload = new Object[9];
        richiesta.payload[0] = nameField.getText();
        richiesta.payload[1] = cognField.getText();
        richiesta.payload[2] = passF.getText();
        richiesta.payload[3] = dateP.getValue();
        richiesta.payload[4] = cityField.getText();
        richiesta.payload[5] = Integer.parseInt(capField.getText());
        richiesta.payload[6] = addressField.getText();
        richiesta.payload[7] = emailF.getText();
        richiesta.payload[8] = ibanField.getText();
        HelloApplication.output.writeObject(richiesta);
        Risposta risposta = (Risposta) HelloApplication.input.readObject();
        if (risposta.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            Parent root = FXMLLoader.load(getClass().getResource("../view/Login.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("The AuctionHouse");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            Stage stage1 = (Stage) backB.getScene().getWindow();
            stage1.close();
        }
        else if (risposta.tipoRisposta == Risposta.TipoRisposta.ERRORE && risposta.payload[0]==Risposta.TipoErrore.CAMPI_INVALIDI)
        {
            System.out.println("Please fill out all the required fields");
            System.out.println(risposta.payload[1]);
        }

    }
    @FXML
    void BackClick(ActionEvent event)
    {
        emailT.setVisible(false);
        emailF.setVisible(false);
        passT.setVisible(false);
        passF.setVisible(false);
        backB2.setVisible(false);
        regB.setVisible(false);
        addressText.setVisible(true);
        addressField.setVisible(true);
        nameText.setVisible(true);
        nameField.setVisible(true);
        cognText.setVisible(true);
        cognField.setVisible(true);
        capText.setVisible(true);
        capField.setVisible(true);
        cityText.setVisible(true);
        cityField.setVisible(true);
        ibanText.setVisible(true);
        ibanField.setVisible(true);
        dateText.setVisible(true);
        dateP.setVisible(true);
        nextB.setVisible(true);
        backB.setVisible(true);

    }

    @FXML
    void NextClick(ActionEvent event)
    {
        emailT.setVisible(true);
        emailF.setVisible(true);
        passT.setVisible(true);
        passF.setVisible(true);
        regB.setVisible(true);
        backB2.setVisible(true);
        addressText.setVisible(false);
        addressField.setVisible(false);
        nameText.setVisible(false);
        nameField.setVisible(false);
        cognText.setVisible(false);
        cognField.setVisible(false);
        capText.setVisible(false);
        capField.setVisible(false);
        cityText.setVisible(false);
        cityField.setVisible(false);
        ibanText.setVisible(false);
        ibanField.setVisible(false);
        dateText.setVisible(false);
        dateP.setVisible(false);
        nextB.setVisible(false);
        backB.setVisible(false);
    }

}
