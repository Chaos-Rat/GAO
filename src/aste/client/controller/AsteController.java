package aste.client.controller;

import aste.Richiesta;
import aste.Risposta;
import aste.client.HelloApplication;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;

public class AsteController
{
    @FXML
    private Button addB;

    @FXML
    private Circle avatar;

    @FXML
    private VBox asteList;

    @FXML
    private Button ArticoliB;

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
    private ComboBox<String> category;

    @FXML
    private Label timerLabel;

    @FXML
    public void initialize() throws IOException, ClassNotFoundException
    {
        LocalTime end = LocalTime.now().plusHours(1);
        AnimationTimer timer = new AnimationTimer()
        {
            @Override
            public void handle(long l)
            {
                Duration remaining = Duration.between(LocalTime.now(), end);
                if (remaining.isPositive()) {
                    timerLabel.setText(format(remaining));
                } else {
                    timerLabel.setText(format(Duration.ZERO));
                    stop();
                }
            }
            private String format(Duration remaining) {
                return String.format("%02d:%02d:%02d",
                        remaining.toHoursPart(),
                        remaining.toMinutesPart(),
                        remaining.toSecondsPart()
                );
            }
        };
        timer.start();
        Richiesta richiesta = new Richiesta();
        richiesta.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_IMMAGINE_PROFILO;
        richiesta.payload = new Object[]{0};
        HelloApplication.output.writeObject(richiesta);
        Risposta risposta = (Risposta) HelloApplication.input.readObject();
        if (risposta.tipoRisposta == Risposta.TipoRisposta.OK) {
            FileOutputStream picture = new FileOutputStream("imagine.png");
            picture.write((byte[]) risposta.payload[0]);
            picture.close();
            FileInputStream defaultImg = new FileInputStream("imagine.png");
            Image image = new Image(defaultImg);
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            imageView.setPreserveRatio(true);
            avatar.setFill(new ImagePattern(imageView.getImage()));
            defaultImg.close();
        }
        else
        {
            System.out.println(risposta.tipoRisposta.toString());
            System.out.println(((Risposta.TipoErrore) risposta.payload[0]).toString());
        }
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
    void AddClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Asta.fxml"));
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


}
