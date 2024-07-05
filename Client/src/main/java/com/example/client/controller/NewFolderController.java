package com.example.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NewFolderController{
    @FXML
    public TextField folderName;
    @FXML
    private javafx.scene.control.Button closeButton;
    private MainController mainController;

//    public NewFolderController(MainController mainController) {
//        this.mainController = mainController;
//    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void sendName() {
        Stage stage = (Stage) folderName.getScene().getWindow();
        stage.close();

        mainController.createNewFolder(folderName.getText());
    }

    public void closeWindow() {
        folderName.clear();
        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();
        // do what you have to do
        stage.close();
    }
}
