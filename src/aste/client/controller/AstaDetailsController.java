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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class AstaDetailsController
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
    private ImageView astaPicture;

    @FXML
    private Circle avatar;

    @FXML
    private Text emailText;

    @FXML
    private Text ipMultiText;

    @FXML
    private Button participateB;

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

    private InetAddress ipaddress;

    private LocalDateTime time;

    private Duration duration;

    @FXML
    public void initialize() throws IOException, ClassNotFoundException
    {
        useridText.setUnderline(true);
        System.out.println(idAsta);
        Richiesta richiestaAsta = new Richiesta();
        richiestaAsta.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_ASTA;
        richiestaAsta.payload = new Object[1];
        richiestaAsta.payload[0] = idAsta;
        HelloApplication.output.writeObject(richiestaAsta);
        Risposta rispostaAsta = (Risposta) HelloApplication.input.readObject();
        if (rispostaAsta.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            time = (LocalDateTime)rispostaAsta.payload[0];
            duration = (Duration)rispostaAsta.payload[1];
            Float sp = (Float)rispostaAsta.payload[2];
            Float hb = (Float)rispostaAsta.payload[3];
            if (rispostaAsta.payload[4] != null)
            {
                ipaddress = (InetAddress) rispostaAsta.payload[4];
                ipMultiText.setText(ipaddress.toString());
            }else
            {
                ipMultiText.setText("null");
            }
            String desc = (String)rispostaAsta.payload[5];
            Integer idLotto = (Integer)rispostaAsta.payload[6];
            String nomeLotto = (String)rispostaAsta.payload[7];
            Integer idUser = (Integer)rispostaAsta.payload[8];
            String email = (String)rispostaAsta.payload[9];
            Image [] image = new Image[((byte[][])rispostaAsta.payload[10]).length];
            for (int i = 0; i < image.length; i++)
            {
                FileOutputStream out = new FileOutputStream("cache/Articolo.png");
                out.write(((byte[][]) rispostaAsta.payload[10])[i]);
                out.close();
                FileInputStream in = new FileInputStream("cache/Articolo.png");
                image[i]= new Image(in);
                in.close();
            }
            astaPicture.setImage(image[0]);
            nomelottoText.setText(nomeLotto);
            prezzoInizioText.setText(sp.toString());
            prezzoAttualeText.setText(hb.toString());
            useridText.setText(idUser.toString());
            emailText.setText(email);
        }
        else if (rispostaAsta.tipoRisposta == Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaAsta.payload[0]);
        }
    }
    @FXML
    void ArticoliClicked(ActionEvent event) throws IOException {
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
    void LogoutClicked(ActionEvent event)
    {
        Stage stage = (Stage) LogoutB.getScene().getWindow();
        stage.close();
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
    void ParticipateClicked(ActionEvent event) throws IOException {
        PuntataController.idAsta = idAsta;
        PuntataController.astaNome = nomelottoText.getText();
        PuntataController.duration = duration;
        PuntataController.start = time;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Puntata.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("The AuctionHouse");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        Stage stage1 = (Stage) participateB.getScene().getWindow();
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

    @FXML
    void idClicked(MouseEvent event) throws IOException, ClassNotFoundException
    {
        OtherUserProfileController.idUser = Integer.parseInt(useridText.getText());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/OtherUserProfile.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("The AuctionHouse");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        Stage stage1 = (Stage) useridText.getScene().getWindow();
        stage1.close();
    }

}
