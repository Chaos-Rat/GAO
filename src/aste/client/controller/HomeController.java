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
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeController {

    @FXML
    private ToolBar PageMenu;

    @FXML
    private VBox asteList;

    @FXML
    private Button ArticoliB;

    @FXML
    private Button HomeB;

    @FXML
    private HBox avatarBox;

    @FXML
    private Button LogoutB;

    @FXML
    private Button LottiB;

    @FXML
    private Button AsteB;

    @FXML
    private Button ProfileB;

    @FXML
    private Circle avatar;

    @FXML
    private Text Title;


    @FXML
    private VBox vbox;

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
    void LogoutClicked(ActionEvent event) throws IOException
    {
        Stage stage = (Stage) LogoutB.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void initialize() throws IOException, ClassNotFoundException {
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
//        int page = 1;
//        Richiesta richiesta = new Richiesta();
//        richiesta.tipoRichiesta = Richiesta.TipoRichiesta.VISUALIZZA_IMMAGINE_PROFILO;
//        for (int i = 0;i < 15 ; i++)
//        {
//            HBox box = new HBox();
//            Image img = new Image(getClass().getResourceAsStream("../view/Screenshot.png"));
//            Text des = new Text();
//            Text date = new Text();
//            Text status = new Text();
//            des.setText("Description: Un vaso nero");
//            status.setText("Status: Ongoing");
//            date.setText("Date: 2-5-2042" );
//            ImageView item = new ImageView();
//            item.setImage(img);
//            item.setFitWidth(100);
//            item.setFitHeight(100);
//            item.setPreserveRatio(true);
//            VBox vbox = new VBox();
//            VBox vbox2 = new VBox();
//            VBox vbox3 = new VBox();
//            vbox2.setAlignment(Pos.CENTER);
//            vbox2.getChildren().addAll(des,date);
//            vbox3.getChildren().addAll(status);
//            vbox3.setAlignment(Pos.CENTER);
//            vbox.setAlignment(Pos.CENTER);
//            vbox.getChildren().add(item);
//            box.setPrefWidth(940);
//            box.setAlignment(Pos.CENTER);
//            box.getChildren().addAll(vbox,vbox2,vbox3);
//            asteList.getChildren().add(box);
//            if (i == 14)
//            {
//                page++;
//            }
//        }
//        Button b = new Button();
//        b.setText(String.valueOf(page));
//        b.getStyleClass().add("PageButton.css");
//        PageMenu.getItems().addAll(b);

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

}



