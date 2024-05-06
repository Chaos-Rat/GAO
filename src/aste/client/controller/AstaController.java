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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    private VBox lottiList;

    @FXML
    private Circle avatar;

    @FXML
    private ComboBox<String> category;

    @FXML
    private Button createB;

    @FXML
    private DatePicker dateF;

    @FXML
    private Spinner<LocalTime>spinner;

    @FXML
    private TextField priceF;

    @FXML
    private Spinner<LocalTime>spinnerEnd;

    private HashMap<Integer, Boolean> lottiSelezionati;

    @FXML
    public void initialize() throws IOException, ClassNotFoundException
    {
        lottiSelezionati = new HashMap<>();
        category.setVisible(false);
        spinnerEnd.setEditable(true);
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
        SpinnerValueFactory<LocalTime> factory2 = new SpinnerValueFactory<LocalTime>() {
            {
                setValue(defaultValue());
            }

            private LocalTime defaultValue() {
                return LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
            }
            @Override
            public void decrement(int steps) {
                LocalTime value = getValue();
                setValue(value == null ? defaultValue() : value.minusMinutes(steps));
            }
            @Override
            public void increment(int steps) {
                LocalTime value = getValue();
                setValue(value == null ? defaultValue() : value.plusMinutes(steps));
            }
        };
        spinnerEnd.setValueFactory(factory2);
        spinner.setValueFactory(factory);
        Richiesta richiestacat = new Richiesta();
        richiestacat.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_CATEGORIE;
        HelloApplication.output.writeObject(richiestacat);
        Risposta rispostacat = (Risposta) HelloApplication.input.readObject();
        HashMap<String, Integer> catmap = new HashMap<String, Integer>();
        if (rispostacat.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            for (int i = 0 ; i < rispostacat.payload.length/2 ; i++)
            {
                catmap.put((String) rispostacat.payload[i*2+1], (Integer) rispostacat.payload[i*2]);
            }
            catmap.put("Tutte le categorie",0);
            category.getSelectionModel().select("Altre categorie");
            category.getItems().addAll(catmap.keySet());
        }
        Richiesta richiestaLotti = new Richiesta();
        richiestaLotti.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_LOTTI;
        richiestaLotti.payload = new Object[5];
        richiestaLotti.payload[0] = 10 ;
        richiestaLotti.payload[1] = 1;
        richiestaLotti.payload[2] = "";
        richiestaLotti.payload[3] = catmap.get(category.getSelectionModel().getSelectedItem());
        richiestaLotti.payload[4] = false;
        HelloApplication.output.writeObject(richiestaLotti);
        Risposta rispostaLotti = (Risposta) HelloApplication.input.readObject();
        ToggleGroup group = new ToggleGroup();
        if (rispostaLotti.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            for (int i = 0; i < rispostaLotti.payload.length / 3; i++) {
                RadioButton check = new RadioButton();
                HBox box = new HBox();
                FileOutputStream out = new FileOutputStream("cache/Articolo.png");
                out.write((byte[]) rispostaLotti.payload[i * 3 + 2]);
                out.close();
                FileInputStream in = new FileInputStream("cache/Articolo.png");
                Image img = new Image(in);
                in.close();
                ImageView item = new ImageView();
                item.setImage(img);
                item.setFitWidth(100);
                item.setFitHeight(100);
                item.setPreserveRatio(true);
                String nome = (String) rispostaLotti.payload[i * 3 + 1];
                Text nomeT = new Text("Nome: " + nome);
                Integer id = (Integer) rispostaLotti.payload[i * 3 + 0];
                Text idT = new Text("Id: +" + id.toString());
                nomeT.setWrappingWidth(150);
                idT.setWrappingWidth(150);
                VBox vbox = new VBox();
                VBox vbox2 = new VBox();
                VBox vbox3 = new VBox();
                box.setSpacing(50);
                check.setToggleGroup(group);
                check.setOnAction(event ->
                {
                    Boolean selezionato = lottiSelezionati.get(id);
                    if (selezionato == null || !selezionato) {
                        lottiSelezionati.put(id, true);
                        return;
                    }
                    lottiSelezionati.put(id, false);
                });
                vbox2.setAlignment(Pos.CENTER);
                vbox.setAlignment(Pos.CENTER);
                vbox.getChildren().add(item);
                vbox2.getChildren().addAll(nomeT);
                vbox3.getChildren().addAll(check);
                box.setPrefWidth(940);
                box.setAlignment(Pos.CENTER);
                box.getChildren().addAll(vbox, vbox2,vbox3);
                lottiList.getChildren().add(box);
            }
        }else if (rispostaLotti.payload[0] == Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaLotti.payload[0]);
            System.out.println(rispostaLotti.payload[1]);
        }

    }
    @FXML
    void CreateClicked(ActionEvent event) throws IOException, ClassNotFoundException
    {

        Integer id;
        LocalDate startDate = dateF.getValue();
        LocalTime time = spinner.getValue();
        String datetimestr = (startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))+" "+time.format(DateTimeFormatter.ofPattern("HH:mm")));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(datetimestr,formatter);
        LocalTime end = spinnerEnd.getValue();
        Duration timeduration = Duration.between(time,end);
        System.out.println((String) dateTime.format(formatter));
        Richiesta richiestacat = new Richiesta();
        richiestacat.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_CATEGORIE;
        HelloApplication.output.writeObject(richiestacat);
        Risposta rispostacat = (Risposta) HelloApplication.input.readObject();
        HashMap<String, Integer> catmap = new HashMap<String, Integer>();
        if (rispostacat.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            for (int i = 0 ; i < rispostacat.payload.length/2 ; i++)
            {
                catmap.put((String) rispostacat.payload[i*2+1], (Integer) rispostacat.payload[i*2]);
            }
            catmap.put("Tutte le categorie",0);
            category.getSelectionModel().select("Altre categorie");
            category.getItems().addAll(catmap.keySet());
        }
        Richiesta richiestaArticoli = new Richiesta();
        richiestaArticoli.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_ARTICOLI;
        richiestaArticoli.payload = new Object[5];
        richiestaArticoli.payload[0] = 10;
        richiestaArticoli.payload[1] = 1;
        richiestaArticoli.payload[2] = "";
        richiestaArticoli.payload[3] = catmap.get(category.getSelectionModel().getSelectedItem());
        richiestaArticoli.payload[4] = true;
        HelloApplication.output.writeObject(richiestaArticoli);
        Risposta rispostaArticoli = new Risposta();
        rispostaArticoli = (Risposta) HelloApplication.input.readObject();
        if (rispostaArticoli.tipoRisposta == Risposta.TipoRisposta.OK) {
            for (int i = 0; i < rispostaArticoli.payload.length / 4; i++) {
                HBox box = new HBox();
                FileOutputStream out = new FileOutputStream("cache/Articolo.png");
                out.write((byte[]) rispostaArticoli.payload[i * 4 + 3]);
                out.close();
                String nome = (String) rispostaArticoli.payload[i * 4 + 1];
                String cond = (String) rispostaArticoli.payload[i * 4 + 2];
                id = (Integer) rispostaArticoli.payload[i * 4 + 0];
            }
        }
        Richiesta richiestaCrea = new Richiesta();
        richiestaCrea.tipoRichiesta = Richiesta.TipoRichiesta.CREA_ASTA;
        richiestaCrea.payload = new Object[5];
        richiestaCrea.payload[0] = dateTime;
        richiestaCrea.payload[1] = timeduration;
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
