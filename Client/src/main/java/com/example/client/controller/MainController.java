package com.example.client.controller;

import com.example.client.ClientNetwork;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.akhtyamov.Action;
import org.akhtyamov.Commands;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
public class MainController implements Initializable {
    public static final String sourceRoot = "client/client";
    public File currentFileOnServer;
    public Path hostDir;
    public Path serverDir;
    public ClientNetwork clientNetwork;
    private HostController hostController;
    private ServerController serverController;
    @FXML
    private TextField commandField;
    @FXML
    public ListView<String> hostFileList;
    @FXML
    public TextField hostPath;
    @FXML
    public TextField serverPath;
    @FXML
    public ListView serverFileList;

    public void sendCommand(ActionEvent actionEvent) {
        clientNetwork.sendCommand(commandField.getText());
        commandField.clear();
        commandField.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hostController = new HostController(this);
        serverController = new ServerController(this);
        hostController.initialize();

    }

    public void onExitAction(ActionEvent actionEvent) {
        clientNetwork.close();
    }

    /**
     * Загрузка "Клиент --> Сервер"
     * @param actionEvent
     */
    public void uploadOnServer(ActionEvent actionEvent) {
        hostController.upload();
    }
    /**
     * Событие по щелчку на списке файлов клиента
     *
     * @param mouseEvent
     */
    public void enterToDir(MouseEvent mouseEvent) {
        hostController.enterToDir(mouseEvent);
    }
    /**
     * Кнопка "Назад" хост
     *
     * @param actionEvent
     */
    public void onBackHost(ActionEvent actionEvent) {
        hostController.onBack();
    }

    /**
     * Событие по щелчку на списке файлов сервера
     *
     * @param mouseEvent
     */
    public void enterToDirServer(MouseEvent mouseEvent) {
        serverController.enterToDir(mouseEvent);
    }

    public void doubleClickServer() {
        serverController.doubleClick();
    }

    /**
     * Кнопка "Назад" для сервера
     *
     * @param actionEvent
     */
    public void onBackServer(ActionEvent actionEvent) {
        serverController.onBack();
    }
    /**
     * Инициализирующий поток, который возвращает содержимое корневой папки сервера
     */
    public void getServerFileList() {
        clientNetwork.getChannel().writeAndFlush(new Action("", Commands.GET_FILE_LIST));
    }

    /**
     * Поток на запрос содержимого выделенной папки ( Проваливание в папку на сервере )
     *
     * @param filename название папки
     */
    public void getServerFileList(String filename) {
        clientNetwork.getChannel().writeAndFlush(new Action(filename, Commands.GET_FILE_LIST));
    }

    public void getServerDir() {
        clientNetwork.getChannel().writeAndFlush(new Action("", Commands.GET_SERVER_DIR_NAME));
    }

    /**
     * Подключение к серверу
     *
     * @param actionEvent
     */
    public void connectServer(ActionEvent actionEvent) {
        clientNetwork = new ClientNetwork(this);
    }

    /**
     * Устанавливаем название корневой директории сервера
     *
     * @param serverDirPath
     */
    public void setServerDirName(Path serverDirPath) {
        serverController.setDirName(serverDirPath);
    }


}