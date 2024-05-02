package com.example.client.controller;

import com.example.client.ClientNetwork;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
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
    private static final String sourceRoot = "client/client";
    public File currentFileOnServer;
    private Path hostDir;
    private Path serverDir;
    private ClientNetwork clientNetwork;
    @FXML
    private TextField commandField;
    @FXML
    private TextArea mainArea;
    @FXML
    private ListView<String> hostFileList;
    @FXML
    private TextField hostPath;
    @FXML
    private TextField serverPath;
    @FXML
    public ListView serverFileList;

    public void sendCommand(ActionEvent actionEvent) {
        clientNetwork.sendCommand(commandField.getText());
        commandField.clear();
        commandField.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hostDir = Paths.get(sourceRoot);
        hostFileList.getItems().addAll(getFiles(hostDir));
        hostPath.setText(hostDir.toString());
    }

    public void onExitAction(ActionEvent actionEvent) {
        clientNetwork.close();
    }

    public void uploadOnServer(ActionEvent actionEvent) {
        clientNetwork.sendCommand("/upload", commandField.getText());
    }

    /**
     * Получаем список вложеннных файлов на клиенте
     *
     * @param path
     * @return
     */
    public List<String> getFiles(Path path) {
        try {
            return Files.list(path)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Событие по щелчку на списке файлов клиента
     *
     * @param mouseEvent
     */
    public void enterToDir(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            if (Paths.get(hostPath.getText()).toFile().isDirectory()) {
                doubleClickClient();
            }
        } else if (mouseEvent.getClickCount() == 1) {

            hostPath.setText(hostDir.toString() + File.separator + getSelectedHostItem());

        }
    }

    /**
     * Проваливание в папку
     */
    private void doubleClickClient() {
        hostFileList.getItems().clear();
        hostFileList.getItems().addAll(getFiles(Paths.get(hostPath.getText())));
        hostDir = Path.of(hostPath.getText());
    }

    /**
     * Кнопка "Назад" хост
     *
     * @param actionEvent
     */
    public void onBackHost(ActionEvent actionEvent) {
        if (!hostDir.equals(Paths.get(sourceRoot))) {
            hostFileList.getItems().clear();
            hostFileList.getItems().addAll(getFiles(hostDir.getParent()));
            hostDir = hostDir.getParent();
            hostPath.setText(hostDir.toString());
        }
    }

    /**
     * Событие по щелчку на списке файлов сервера
     *
     * @param mouseEvent
     */
    public void enterToDirServer(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            //Делаем запрос на сервер, чтобы проверить в дальнейшем, что выбранный файл "папка"
            clientNetwork.getChannel().writeAndFlush(new Action(getSelectedServerItem(), Commands.GET_CURRENT_FILE));
        } else if (mouseEvent.getClickCount() == 1) {

            serverPath.setText(serverDir.toString() + File.separator + getSelectedServerItem());


        }
    }
    public void doubleClickServer(){
        if (currentFileOnServer.isDirectory()) {
            clientNetwork.getChannel().writeAndFlush(new Action(serverPath.getText(), Commands.DOUBLE_CLICK_FILE));
            serverDir = Path.of(serverPath.getText());
        }
    }

    private String getSelectedServerItem() {
        return (String) serverFileList.getSelectionModel().getSelectedItem();
    }

    /**
     * Получаем имя выбранного элемента на клиенте
     *
     * @return
     */
    public String getSelectedHostItem() {
        return hostFileList.getSelectionModel().getSelectedItem();
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
        serverDir = serverDirPath;
        Platform.runLater(() -> {
            serverPath.setText(serverDir.toString());
        });
    }
}