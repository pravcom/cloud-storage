package com.example.client.controller;

import com.example.client.Client;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.akhtyamov.Action;
import org.akhtyamov.Commands;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class ServerController implements ButtonActionFileList {
    private final MainController mainController;

    public ServerController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void upload() {
        mainController.clientNetwork.getChannel().writeAndFlush(new Action(mainController.serverPath.getText(), Commands.UPLOAD_ON_HOST));
        mainController.commandsList.appendText("Upload: " + mainController.serverPath.getText() + "\n");
    }

    @Override
    public void delete() {
        mainController.clientNetwork.getChannel()
                .writeAndFlush(new Action(mainController.serverPath.getText(), Commands.DELETE_SERVER_FILE));
        mainController.commandsList.appendText("Delete: "+mainController.serverPath.getText()+"\n");
    }

    @Override
    public void copy() {
        mainController.clientNetwork.getChannel().writeAndFlush(new Action(mainController.serverPath.getText(), Commands.COPY_SERVER_FILE));
        mainController.commandsList.appendText("Copy: " + mainController.serverPath.getText() + "\n");
    }

    @Override
    public void createNewFolder(String name) {
        mainController.clientNetwork.getChannel().writeAndFlush(new Action(name, Commands.CREATE_NEW_FOLDER));
        mainController.commandsList.appendText("Create new folder: " + name + "\n");
    }

    @Override
    public void callNewFolderScreen() {
        try {
            Stage stage = new Stage();
            URL fxmlLocation = Client.class.getResource("newFolderView.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
            NewFolderController.newFolderWindow(stage, fxmlLoader, mainController);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void enterToDir(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            //Делаем запрос на сервер, чтобы проверить в дальнейшем, что выбранный файл "папка"
            mainController.clientNetwork.getChannel().writeAndFlush(new Action(getSelectedItem(), Commands.GET_CURRENT_FILE));
        } else if (mouseEvent.getClickCount() == 1) {
            mainController.serverPath.setText(mainController.serverDir.toString() + File.separator + getSelectedItem());
            mainController.hostFileList.getSelectionModel().clearSelection();
        }
    }

    @Override
    public void doubleClick() {
        if (mainController.currentFileOnServer.isDirectory()) {
            mainController.clientNetwork.getChannel().writeAndFlush(new Action(mainController.serverPath.getText(), Commands.DOUBLE_CLICK_FILE));
            mainController.serverDir = Path.of(mainController.serverPath.getText());
        }
    }

    @Override
    public void onBack() {
        mainController.clientNetwork.getChannel().writeAndFlush(new Action("", Commands.BACK));
    }

    @Override
    public String getSelectedItem() {
        return (String) mainController.serverFileList.getSelectionModel().getSelectedItem();
    }

    public void setDirName(Path serverDirPath) {
        mainController.serverDir = serverDirPath;
        Platform.runLater(() -> mainController.serverPath.setText(mainController.serverDir.toString()));
    }
}
