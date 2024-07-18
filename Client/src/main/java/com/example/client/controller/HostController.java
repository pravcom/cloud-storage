package com.example.client.controller;

import com.example.client.Client;
import com.example.client.window.NewFolderWindow;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.akhtyamov.Commands;
import org.akhtyamov.files.PartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class HostController implements ButtonActionFileList {
    private final MainController mainController;


    public HostController(MainController mainController) {
        this.mainController = mainController;
    }

    public void initialize() {
        mainController.hostDir = Paths.get(mainController.sourceRoot);
        mainController.updateHostListView(mainController.hostDir);
//        mainController.hostFileList.getItems().addAll(getFiles(mainController.hostDir));
        mainController.hostPath.setText(mainController.hostDir.toString());
    }

    @Override
    public void upload() {
        Path file = Paths.get(mainController.hostPath.getText());
        try (FileInputStream fis = new FileInputStream(file.toFile())) {
            byte[] buffer = new byte[fis.available()];
            //считываем буфер
            fis.read(buffer);
            mainController.clientNetwork.getChannel().writeAndFlush(new PartFile(buffer, getSelectedItem()));
            mainController.commandsList.appendText("Upload: " + getSelectedItem() + "\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete() {
        Paths.get(mainController.hostPath.getText()).toFile().delete();
        mainController.updateHostListView(mainController.hostDir);
        mainController.commandsList.appendText("Delete: " + mainController.hostPath.getText() + "\n");
    }

    @Override
    public void copy() {
        Path file = Paths.get(mainController.hostPath.getText());
        try {
            Integer count = 0;
            Path fileName = Paths.get(mainController.hostPath.getText());

            while (Files.exists(fileName)) {
                count++;
                fileName = Paths.get(mainController.hostDir.toString() + File.separator +
                        "Copy" +
                        count +
                        "_" +
                        getSelectedItem());
            }

            Files.copy(file, fileName);
            mainController.updateHostListView(mainController.hostDir);
            mainController.commandsList.appendText("Copy: " + fileName + "\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createNewFolder(String name) {
        try {
            Path path = Paths.get(mainController.hostDir.toString() + File.separator + name);
            Files.createDirectory(path);
            mainController.updateHostListView(mainController.hostDir);
            mainController.commandsList.appendText("Create new folder: " + name + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void callNewFolderScreen() {
        try {
            Stage stage = new Stage();
            URL fxmlLocation = Client.class.getResource("newFolderView.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
            Scene scene = null;
//        fxmlLoader.setController(new NewFolderController(mainController));
//        fxmlLoader.setController(new NewFolderController());
            Parent parent = fxmlLoader.load();
            NewFolderController newFolderController = fxmlLoader.getController();
            newFolderController.setMainController(mainController);
            scene = new Scene(parent, 200, 100);
            stage.setTitle("Folder name");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void enterToDir(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            if (Paths.get(mainController.hostPath.getText()).toFile().isDirectory()) {
                doubleClick();
            }
        } else if (mouseEvent.getClickCount() == 1) {
            mainController.hostPath.setText(mainController.hostDir.toString() + File.separator + getSelectedItem());
            mainController.serverFileList.getSelectionModel().clearSelection();
        }
    }

    @Override
    public void doubleClick() {
        mainController.updateHostListView(Paths.get(mainController.hostPath.getText()));
        mainController.hostDir = Path.of(mainController.hostPath.getText());
    }

    @Override
    public void onBack() {
        if (!mainController.hostDir.equals(Paths.get(mainController.sourceRoot))) {
            mainController.updateHostListView(mainController.hostDir.getParent());
            mainController.hostDir = mainController.hostDir.getParent();
            mainController.hostPath.setText(mainController.hostDir.toString());
        }
    }

    @Override
    public String getSelectedItem() {
        return mainController.hostFileList.getSelectionModel().getSelectedItem();
    }
}
