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
    private HBox idArticoloBox;

    @FXML
    private Text idLottoText;

    @FXML
    private Button modifyB;

    @FXML
    private Text nomeText;

    @FXML
    private Text username;

    public  static Integer idLotto;

    @FXML
    public  void initialize() throws IOException, ClassNotFoundException
    {
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
            avatar.setFill(new ImagePattern(imageView.getImage()));
            defaultImg.close();
        }
        else
        {
            System.out.println(risposta.tipoRisposta.toString());
            System.out.println(((Risposta.TipoErrore) risposta.payload[0]).toString());
        }
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
            String idArticolo = String.valueOf(articoloID[0]);
            String virgola = ",";
            Text idArticoloText = new Text(idArticolo);
            idArticoloText.setStyle("-fx-fill: #2112EE;");
            idArticoloText.setUnderline(true);
            idArticoloText.setOnMouseClicked(mouseEvent -> {
                try {
                    ArticoloDetailsController.idArticolo= Integer.parseInt(idArticoloText.getText());
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/ArticoloDetails.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setTitle("The AuctionHouse");
                    stage.setScene(scene);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();
                    Stage stage1 = (Stage) idArticoloText.getScene().getWindow();
                    stage1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Text virgolaText = new Text(virgola);
            idArticoloBox.getChildren().addAll(idArticoloText);
            articoloNomeText.setText(String.valueOf(articoloID[1]));
            for(int i =1 ; i < articoloID.length/2;i++)
            {
                Text nextId = new Text(String.valueOf(articoloID[i*2+0]));
                nextId.setStyle("-fx-fill: #2112EE;");
                virgolaText = new Text(virgola);
                nextId.setOnMouseClicked(mouseEvent ->
                {
                    try {
                        ArticoloDetailsController.idArticolo= Integer.parseInt(nextId.getText());
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/ArticoloDetails.fxml"));
                        Parent root = loader.load();
                        Scene scene = new Scene(root);
                        Stage stage = new Stage();
                        stage.setTitle("The AuctionHouse");
                        stage.setScene(scene);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.show();
                        Stage stage1 = (Stage) idArticoloText.getScene().getWindow();
                        stage1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                nextId.setUnderline(true);
                idArticoloBox.getChildren().addAll(virgolaText,nextId);
                articoloNomeText.setText(articoloNomeText.getText()+","+String.valueOf(articoloID[i*2+1]));
            }
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
}
