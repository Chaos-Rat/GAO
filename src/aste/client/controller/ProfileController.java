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
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ProfileController
{
    private List<File> selectedFiles;

    @FXML
    private Button ArticoliB;

    @FXML
    private Button AsteB;

    @FXML
    private Button ChangeB;

    @FXML
    private Button ConfirmB;

    @FXML
    private Button HomeB;

    @FXML
    private Button LogoutB;

    @FXML
    private Button LottiB;

    @FXML
    private Button ModifyB;

    @FXML
    private TextField addressEdit;

    @FXML
    private Text addressText;

    @FXML
    private Circle avatar;

    @FXML
    private Button backB;

    @FXML
    private TextField capEdit;

    @FXML
    private Text capText;

    @FXML
    private TextField cityEdit;

    @FXML
    private Text cityText;

    @FXML
    private DatePicker dateEdit;

    @FXML
    private Text dateText;

    @FXML
    private TextField emailEdit;

    @FXML
    private Text emailText;

    @FXML
    private TextField ibanEdit;

    @FXML
    private Text ibanText;

    @FXML
    private TextField nameEdit;

    @FXML
    private Text nameText;

    @FXML
    private TextField surnameEdit;

    @FXML
    private Text surnameText;

    private Image [] img;

    @FXML
    void ModifyInfo(ActionEvent event)
    {
        ModifyB.setVisible(false);
        backB.setVisible(true);
        ConfirmB.setVisible(true);
        nameEdit.setVisible(true);
        surnameEdit.setVisible(true);
        emailEdit.setVisible(true);
        ChangeB.setVisible(true);
        dateEdit.setVisible(true);
        cityEdit.setVisible(true);
        capEdit.setVisible(true);
        addressEdit.setVisible(true);
        ibanEdit.setVisible(true);
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
        selectedFiles = fileChooser.showOpenMultipleDialog(stage);
        if (selectedFiles != null) {
            File selectedFile = selectedFiles.get(0);
            Image image = new Image(selectedFile.toURI().toString());
            img = new Image[]{image};
            avatar.setFill(new ImagePattern(image));
        }
        else {
            selectedFiles = new ArrayList<>();
        }

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
        dateEdit.setVisible(false);
        cityEdit.setVisible(false);
        capEdit.setVisible(false);
        addressEdit.setVisible(false);
        ibanEdit.setVisible(false);
        Image image2 = new Image(getClass().getResourceAsStream("../view/Avatar.png"));
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
    void ConfirmClicked(ActionEvent event) throws IOException, ClassNotFoundException {
        nameText.setText("Name: " + nameEdit.getText());
        surnameText.setText("Surname: " + surnameEdit.getText());
        emailText.setText("Email: " + emailEdit.getText());
        ModifyB.setVisible(true);
        backB.setVisible(false);
        ConfirmB.setVisible(false);
        nameEdit.setVisible(false);
        surnameEdit.setVisible(false);
        emailEdit.setVisible(false);
        ChangeB.setVisible(false);
        dateEdit.setVisible(false);
        cityEdit.setVisible(false);
        capEdit.setVisible(false);
        addressEdit.setVisible(false);
        ibanEdit.setVisible(false);
        Richiesta richiestaProfile = new Richiesta();
        richiestaProfile.tipoRichiesta = Richiesta.TipoRichiesta.MODIFICA_PROFILO;
        richiestaProfile.payload = new Object[9];
        if (nameEdit.getText() == "")
        {
            richiestaProfile.payload[0] = null;
        }else{
            richiestaProfile.payload[0] = nameEdit.getText();
        }
        if (surnameEdit.getText() == "")
        {
            richiestaProfile.payload[1] = null;
        }else{
            richiestaProfile.payload[1] = surnameEdit.getText();
        }
        richiestaProfile.payload [2] = dateEdit.getValue();
        if (cityEdit.getText() == "")
        {
            richiestaProfile.payload[3] = null;
        }else{
            richiestaProfile.payload[3] = cityEdit.getText();
        }
        if (capEdit.getText() == "")
        {
            richiestaProfile.payload[4] = null;

        }else{
            richiestaProfile.payload[4] = Integer.parseInt(capEdit.getText());
        }
        if (addressEdit.getText() == "")
        {
            richiestaProfile.payload[5] = null;
        }else{
            richiestaProfile.payload[5] = addressEdit.getText();
        }
        if (emailEdit.getText() == "")
        {
            richiestaProfile.payload[6] = null;
        }else{
            richiestaProfile.payload[6] = emailEdit.getText();
        }
        if (ibanEdit.getText() == "")
        {
            richiestaProfile.payload[7] = null;
        }else{
            richiestaProfile.payload[7] = ibanEdit.getText();
        }
        richiestaProfile.payload[8] = new byte[selectedFiles.size()][];
        for (int i = 0 ; i<selectedFiles.size();i++)
        {
            FileInputStream stream = new FileInputStream(selectedFiles.get(i));
            richiestaProfile.payload[8] =  stream.readAllBytes();
            stream.close();
        }
        HelloApplication.output.writeObject(richiestaProfile);
        Risposta rispostaProfile = (Risposta) HelloApplication.input.readObject();
        if (rispostaProfile.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            System.out.println("The data modification proccess finished with success!");
        } else if (rispostaProfile.tipoRisposta == Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaProfile.payload[0]);
        }

    }

    public void initialize() throws IOException, ClassNotFoundException {
        Image image = new Image(getClass().getResourceAsStream("../view/Avatar.png"));
        avatar.setFill(new ImagePattern(image));
        backB.setVisible(false);
        ChangeB.setVisible(false);
        ConfirmB.setVisible(false);
        nameEdit.setVisible(false);
        surnameEdit.setVisible(false);
        emailEdit.setVisible(false);
        dateEdit.setVisible(false);
        cityEdit.setVisible(false);
        capEdit.setVisible(false);
        addressEdit.setVisible(false);
        ibanEdit.setVisible(false);
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String birthdateText = birthdate.format(formatter);
            String city = (String) rispostaProfile.payload[3];
            Integer cap = (Integer) rispostaProfile.payload[4];
            String address = (String) rispostaProfile.payload[5];
            String email = (String) rispostaProfile.payload[6];
            String iban = (String) rispostaProfile.payload[7];
            nameText.setText("Name : " + nome);
            surnameText.setText("Surname : " + cognome);
            dateText.setText("Date : " + birthdateText);
            cityText.setText("City : " + city);
            capText.setText("Cap : " + cap);
            addressText.setText("Address : " + address);
            emailText.setText("Email : " + email);
            ibanText.setText("Iban : " + iban);
        } else if (rispostaProfile.tipoRisposta == Risposta.TipoRisposta.ERRORE)
        {
                System.out.println(rispostaProfile.payload[0]);
        }

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
