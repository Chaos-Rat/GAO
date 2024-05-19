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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class LottoDetailsController {

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
    private ImageView Lotto;

    @FXML
    private Button ProfileB;

    @FXML
    private Text articoloNomeText;

    @FXML
    private Circle avatar;

    @FXML
    private Text idLottoText;

    @FXML
    private Text lottoText;

    @FXML
    private Button modifyB;

    @FXML
    private Text nomeText;

    @FXML
    private Text username;

    public  static Integer idLotto;

    @FXML
    public  void initialize() throws IOException, ClassNotFoundException {
        modifyB.setVisible(false);
        Richiesta richiestaLotto = new Richiesta();
        richiestaLotto.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_LOTTO;
        richiestaLotto.payload = new Object[1];
        richiestaLotto.payload[0] = idLotto;
        HelloApplication.output.writeObject(richiestaLotto);
        Risposta rispostaLotto = (Risposta) HelloApplication.input.readObject();
        if (rispostaLotto.tipoRisposta == Risposta.TipoRisposta.OK)
        {
            idLottoText.setText(String.valueOf(rispostaLotto.payload[0]));
            nomeText.setText((String) rispostaLotto.payload[1]);
            Image[] image = new Image[((byte[][])rispostaLotto.payload[2]).length];
            for (int i = 0; i < image.length; i++)
            {
                FileOutputStream out = new FileOutputStream("cache/Articolo.png");
                out.write(((byte[][]) rispostaLotto.payload[2])[i]);
                out.close();
                FileInputStream in = new FileInputStream("cache/Articolo.png");
                image [i] = new Image(in);
                in.close();
            }
            Lotto.setImage(image[0]);
            Object [] articoloID = (Object[]) rispostaLotto.payload[3];
            articoloNomeText.setText(String.valueOf(articoloID[1]));
            idLottoText.setText(String.valueOf(articoloID[0]));
        }else if(rispostaLotto.tipoRisposta == Risposta.TipoRisposta.ERRORE)
        {
            System.out.println(rispostaLotto.payload[0]);
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
    void ModifyClicked(ActionEvent event)
    {

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
    void idClicked(MouseEvent event)
    {

    }

}
