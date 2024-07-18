package com.example.client.controller;

import com.example.client.ClientNetwork;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.akhtyamov.Action;
import org.akhtyamov.Commands;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
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
    public ListView<String> hostFileList;
    @FXML
    public TextField hostPath;
    @FXML
    public TextField serverPath;
    @FXML
    public ListView serverFileList;
    @FXML
    private Label statusText;
    @FXML
    private ImageView ImageStatus;
    @FXML
    private Button BtnServerBack;
    private final Image activeStatus = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/client/status-active.png")));
    private final Image inactiveStatus = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/client/status-inactive.png")));
    private boolean active;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientNetwork = new ClientNetwork();
        hostController = new HostController(this);
        serverController = new ServerController(this);
        hostController.initialize();

    }

    public void onExitAction() {
        clientNetwork.close();
    }

    /**
     * Загрузка "Клиент --> Сервер"
     */
    public void uploadOnServer() {
        if (isSelected(hostFileList)) {
            hostController.upload();
        }

    }

    /**
     * Выгрузка "Сервер --> Клиент"
     */
    public void uploadOnHost() {
        if (isSelected(serverFileList))
            serverController.upload();
    }

    public void delete() {
        if (isSelected(hostFileList)) hostController.delete();
        if (isSelected(serverFileList)) serverController.delete();
    }

    public void copy(ActionEvent actionEvent) {
        if (isSelected(hostFileList)) hostController.copy();
        if (isSelected(serverFileList)) serverController.copy();
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
        if (active) {
            clientNetwork.getChannel().disconnect();
        } else{
            clientNetwork.start(this);
        }
    }
    private boolean checkConnection(){
        boolean active = false;
        if (clientNetwork.getChannel() == null) {
            active = false;
        } else if (clientNetwork.getChannel().isActive()) {
            active = true;
        }else if (clientNetwork.getChannel().isActive() == false){
            active = false;
        }
        return active;
    }
    public void changeStatus(boolean active) {
        Platform.runLater(()->{
            if (active) {
                statusText.setText("Подключено");
                ImageStatus.setImage(activeStatus);
                serverFileList.setDisable(false);
                serverPath.setDisable(false);
                BtnServerBack.setDisable(false);
            } else {
                statusText.setText("Не подключено");
                ImageStatus.setImage(inactiveStatus);
                serverFileList.setDisable(true);
                serverPath.setDisable(true);
                BtnServerBack.setDisable(true);
            }
        });
    }

    /**
     * Устанавливаем название корневой директории сервера
     *
     * @param serverDirPath
     */
    public void setServerDirName(Path serverDirPath) {
        serverController.setDirName(serverDirPath);
    }

    static boolean isSelected(ListView listView) {
        int size = listView.getItems().size();
        boolean isSelected = false;
        for (int i = 0; i < size; i++) {
            if (listView.getSelectionModel().isSelected(i)) {
                isSelected = true;
                break;
            }
        }
        return isSelected;
    }

    public void updateHostListView(Path path) {
        Platform.runLater(()->{
            hostFileList.getItems().clear();
            hostFileList.getItems().addAll(getFiles(path));
        });
    }

    static List<String> getFiles(Path path) {
        try {
            return Files.list(path)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void callFolderScreen() {
        if (isSelected(hostFileList)) hostController.callNewFolderScreen();
        if (isSelected(serverFileList)) serverController.callNewFolderScreen();
    }

    public void createNewFolder(String name) {
        if (isSelected(hostFileList)) hostController.createNewFolder(name);
        if (isSelected(serverFileList)) serverController.createNewFolder(name);
    }

    public void setActive(boolean active) {
        this.active = active;

        changeStatus(active);
    }
    public void clearServerNames(){
        serverDir = null;
        serverPath = null;
        serverFileList = null;
    }
}