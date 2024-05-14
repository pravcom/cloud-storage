package com.example.client.controller;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.akhtyamov.Action;
import org.akhtyamov.Commands;

import java.io.File;
import java.nio.file.Path;

public class ServerController implements ButtonActionFileList{
    private final MainController mainController;

    public ServerController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void upload() {

    }

    @Override
    public void delete() {
        mainController.clientNetwork.getChannel()
                .writeAndFlush(new Action(mainController.serverPath.getText(),Commands.DELETE_SERVER_FILE));
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
        Platform.runLater(() -> {
            mainController.serverPath.setText(mainController.serverDir.toString());
        });
    }
}
