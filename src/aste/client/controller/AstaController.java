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
import javafx.util.StringConverter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;

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
    private Spinner<Duration>spinnerEnd;

    @FXML
    private Text username;



    private Integer idLotto;

    @FXML
    public void initialize() throws IOException, ClassNotFoundException
    {
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
            String city = (String) rispostaProfile.payload[3];
            Integer cap = (Integer) rispostaProfile.payload[4];
            String address = (String) rispostaProfile.payload[5];
            String email = (String) rispostaProfile.payload[6];
            String iban = (String) rispostaProfile.payload[7];
            String s1 = nome.substring(0,1).toUpperCase() + nome.substring(1);
            String s2 = cognome.substring(0,1).toUpperCase() + cognome.substring(1);
            username.setText(s1  + " " + s2);
        } else if (rispostaProfile.tipoRisposta == Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaProfile.payload[0]);
        }
        spinnerEnd.setEditable(true);
        spinner.setEditable(true);
        SpinnerValueFactory<Duration> valueFactory = new SpinnerValueFactory<Duration>() {
            {
                setConverter(new StringConverter<Duration>() {
                    @Override
                    public String toString(Duration duration) {
                        long hours = duration.toHours();
                        long minutes = duration.toMinutes() % 60;
                        return String.format("%02d:%02d", hours, minutes);
                    }

                    @Override
                    public Duration fromString(String string) {
                        String[] parts = string.split(":");
                        long hours = Long.parseLong(parts[0]);
                        long minutes = Long.parseLong(parts[1]);
                        return Duration.ofHours(hours).plusMinutes(minutes);
                    }
                });
                setValue(Duration.ZERO);
            }

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(steps));
            }
        };

        SpinnerValueFactory<LocalTime> factory = new SpinnerValueFactory<LocalTime>() {
            {
                setValue(defaultValue());
            }

            private LocalTime defaultValue()
            {
                return LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
            }
            @Override
            public void decrement(int steps) {
                LocalTime value = getValue();
                setValue(value == null ? defaultValue() : value.minusMinutes(1));
            }
            @Override
            public void increment(int steps) {
                LocalTime value = getValue();
                setValue(value == null ? defaultValue() : value.plusMinutes(1));
            }
        };
        spinnerEnd.setValueFactory(valueFactory);
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
            category.getSelectionModel().select("Tutte le categorie");
            category.getItems().addAll(catmap.keySet());
        }
        Richiesta richiestaLotti = new Richiesta();
        richiestaLotti.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_LOTTI;
        richiestaLotti.payload = new Object[5];
        richiestaLotti.payload[0] = 10 ;
        richiestaLotti.payload[1] = 1;
        richiestaLotti.payload[2] = "";
        richiestaLotti.payload[3] = 0;
        richiestaLotti.payload[4] = true;
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
                   if(check.isSelected())
                   {
                       idLotto = id;
                       System.out.println(idLotto);
                   }
                });
                vbox2.setAlignment(Pos.CENTER);
                vbox.setAlignment(Pos.CENTER);
                vbox.getChildren().add(item);
                vbox3.setAlignment(Pos.CENTER);
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
    void CategorySelected(ActionEvent event) throws IOException, ClassNotFoundException {
        lottiList.getChildren().clear();
        ToggleGroup group = new ToggleGroup();
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
            category.getSelectionModel().getSelectedItem();
        }
        Richiesta richiestaLotti = new Richiesta();
        richiestaLotti.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_LOTTI;
        richiestaLotti.payload = new Object[5];
        richiestaLotti.payload[0] = 10;
        richiestaLotti.payload[1] = 1;
        richiestaLotti.payload[2] = "";
        richiestaLotti.payload[3] = catmap.get(category.getSelectionModel().getSelectedItem());
        richiestaLotti.payload[4] = true;
        HelloApplication.output.writeObject(richiestaLotti);
        Risposta rispostaLotti = (Risposta) HelloApplication.input.readObject();
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
                check.setOnAction(event2 ->
                {
                    if(check.isSelected())
                    {
                        idLotto = id;
                        System.out.println(idLotto);
                    }
                });
                vbox2.setAlignment(Pos.CENTER);
                vbox.setAlignment(Pos.CENTER);
                vbox.getChildren().add(item);
                vbox3.setAlignment(Pos.CENTER);
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
        Integer id ;
        LocalDate startDate = dateF.getValue();
        LocalTime time = spinner.getValue();
        String datetimestr = (startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))+" "+time.format(DateTimeFormatter.ofPattern("HH:mm")));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(datetimestr,formatter);
        Duration end = spinnerEnd.getValue();
        System.out.println((String) dateTime.format(formatter));
        Richiesta richiestaCrea = new Richiesta();
        richiestaCrea.tipoRichiesta = Richiesta.TipoRichiesta.CREA_ASTA;
        richiestaCrea.payload = new Object[5];
        richiestaCrea.payload[0] = dateTime;
        richiestaCrea.payload[1] = end;
        richiestaCrea.payload[2] = Float.valueOf(priceF.getText());
        richiestaCrea.payload[3] = true;
        richiestaCrea.payload[4] = idLotto;
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
