package com.test.tps;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import  java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LoginController {

    ArrayList<String> Usernames = new ArrayList<>();

    @FXML
    private Button EnterB;

    @FXML
    private PasswordField PassField;

    @FXML
    private Text PassText;

    @FXML
    private Button RegisterB;

    @FXML
    private Text Title;

    @FXML
    private TextField UserField;

    @FXML
    private Text UserText;

    public LoginController() throws IOException {
    }

    @FXML
    void Access(MouseEvent event) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("The AuctionHouse");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        Stage thirdstage = (Stage) EnterB.getScene().getWindow();
        thirdstage.close();
        
        
    }
    @FXML
    void Register(MouseEvent event) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Registration Page");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        Stage stage1 = (Stage) RegisterB.getScene().getWindow();
        stage1.close();
    }


    @FXML
    public void initialize() throws IOException
    {
            Title.setText("JiggerBOO AuctionHouse");
            File UsersFile = new File("Users.txt");
            FileReader fr = new FileReader(UsersFile);
            BufferedReader br = new BufferedReader(fr);
            while ((br.ready()))
                    {
                        Usernames.add(br.readLine());
                    }

    }



}
