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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class AstaDetailsController {

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
    private ImageView articolo;

    @FXML
    private Circle avatar;

    @FXML
    private Text emailText;

    @FXML
    private Text ipMultiText;

    @FXML
    private Button modifyB;

    @FXML
    private Text nomelottoText;

    @FXML
    private Text prezzoAttualeText;

    @FXML
    private Text prezzoInizioText;

    @FXML
    private Text useridText;

    @FXML
    private Text username;

    public static Integer idAsta;

    @FXML
    public void initialize() throws IOException, ClassNotFoundException
    {
        System.out.println(idAsta);
        Richiesta richiestaAsta = new Richiesta();
        richiestaAsta.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_ASTA;
        richiestaAsta.payload = new Object[1];
        richiestaAsta.payload[0] = idAsta;
        HelloApplication.output.writeObject(richiestaAsta);
        Risposta rispostaAsta = (Risposta) HelloApplication.input.readObject();
        if (rispostaAsta.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            LocalDateTime time = (LocalDateTime)rispostaAsta.payload[0];
            Duration duration = (Duration)rispostaAsta.payload[1];
            Float sp = (Float)rispostaAsta.payload[2];
            Float hb = (Float)rispostaAsta.payload[3];
            InetAddress ipaddress = (InetAddress)rispostaAsta.payload[4];
            String desc = (String)rispostaAsta.payload[5];
            Integer idLotto = (Integer)rispostaAsta.payload[6];
            String nomeLotto = (String)rispostaAsta.payload[7];
            Integer idUser = (Integer)rispostaAsta.payload[8];
            String email = (String)rispostaAsta.payload[9];
        }
        else if (rispostaAsta.tipoRisposta == Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaAsta.payload[0]);
        }
    }
    @FXML
    void ArticoliClicked(ActionEvent event) {

    }

    @FXML
    void AsteClicked(ActionEvent event) {

    }

    @FXML
    void HomeClicked(ActionEvent event) {

    }

    @FXML
    void LogoutClicked(ActionEvent event) {

    }

    @FXML
    void LottiClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Lotti.fxml"));
        Parent root = loader.load() ;
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
    void ModifyClicked(ActionEvent event) {

    }

    @FXML
    void ProfileClicked(ActionEvent event) {

    }

    @FXML
    void idClicked(MouseEvent event) {

    }

}
