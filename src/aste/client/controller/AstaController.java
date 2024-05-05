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
import javafx.scene.control.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class AstaController
{
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
    private VBox articoliList;

    @FXML
    private Circle avatar;

    @FXML
    private ComboBox<?> category;

    @FXML
    private Button createB;

    @FXML
    private DatePicker dateF;

    @FXML
    private Spinner<LocalTime>spinner;

    @FXML
    private TextField priceF;

    @FXML
    public void initialize() throws IOException, ClassNotFoundException
    {
        category.setVisible(false);

        spinner.setEditable(true);
        SpinnerValueFactory<LocalTime> factory = new SpinnerValueFactory<LocalTime>() {
            {
                setValue(defaultValue());
            }

            private LocalTime defaultValue() {
                return LocalTime.now().truncatedTo(ChronoUnit.HOURS);
            }
            @Override
            public void decrement(int steps) {
                LocalTime value = getValue();
                setValue(value == null ? defaultValue() : value.minusHours(steps));
            }
            @Override
            public void increment(int steps) {
                LocalTime value = getValue();
                setValue(value == null ? defaultValue() : value.plusHours(steps));
            }
        };
        spinner.setValueFactory(factory);

    }
    @FXML
    void CreateClicked(ActionEvent event) throws IOException, ClassNotFoundException {
        LocalDate startDate = dateF.getValue();
        LocalTime time = spinner.getValue();
        String datetimestr = (startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))+" "+time.format(DateTimeFormatter.ofPattern("HH:mm")));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(datetimestr,formatter);
        System.out.println((String) dateTime.format(formatter));
        Richiesta richiestaCrea = new Richiesta();
        richiestaCrea.tipoRichiesta = Richiesta.TipoRichiesta.CREA_ASTA;
        richiestaCrea.payload = new Object[5];
        richiestaCrea.payload[0] = dateTime;
        richiestaCrea.payload[1] = 0;
        richiestaCrea.payload[2] = Float.valueOf(priceF.getText());
//        richiestaCrea.payload[3] = ;
//        richiestaCrea.payload[4] = ;
        HelloApplication.output.writeObject(richiestaCrea);
        Risposta rispostaCrea = (Risposta) HelloApplication.input.readObject();
        if (rispostaCrea.tipoRisposta == Risposta.TipoRisposta.OK) 
        {
            System.out.println("è stato creato l'asta con successo|");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Aste.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("The AuctionHouse");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            Stage stage1 = (Stage) createB.getScene().getWindow();
            stage1.close();
        } else if (rispostaCrea.payload[0] == Risposta.TipoErrore.OPERAZIONE_INVALIDA || rispostaCrea.payload[0] == Risposta.TipoErrore.CAMPI_INVALIDI ||rispostaCrea.payload[0] == Risposta.TipoErrore.GENERICO)
        {
            System.out.println(rispostaCrea.payload[0]);
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
