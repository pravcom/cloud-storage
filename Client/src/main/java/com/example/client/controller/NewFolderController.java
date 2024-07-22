package com.example.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class NewFolderController{
    @FXML
    public TextField folderName;
    @FXML
    private javafx.scene.control.Button closeButton;
    private MainController mainController;

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

    static void newFolderWindow(Stage stage, FXMLLoader fxmlLoader, MainController mainController) throws IOException {
        Scene scene;
        Parent parent = fxmlLoader.load();
        NewFolderController newFolderController = fxmlLoader.getController();
        newFolderController.setMainController(mainController);
        scene = new Scene(parent, 200, 100);
        stage.setTitle("Folder name");
        stage.setScene(scene);
        stage.show();
    }
}
