package com.example.client.controller;

import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.akhtyamov.files.PartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
        mainController.hostFileList.getItems().addAll(getFiles(mainController.hostDir));
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
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
    }

    @Override
    public void delete() {
        Paths.get(mainController.hostPath.getText()).toFile().delete();
        mainController.hostFileList.getItems().clear();
        mainController.hostFileList.getItems().addAll(getFiles(mainController.hostDir));
    }

    public List<String> getFiles(Path path) {
        try {
            return Files.list(path)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());
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
        mainController.hostFileList.getItems().clear();
        mainController.hostFileList.getItems().addAll(getFiles(Paths.get(mainController.hostPath.getText())));
        mainController.hostDir = Path.of(mainController.hostPath.getText());
    }

    @Override
    public void onBack() {
        if (!mainController.hostDir.equals(Paths.get(mainController.sourceRoot))) {
            mainController.hostFileList.getItems().clear();
            mainController.hostFileList.getItems().addAll(getFiles(mainController.hostDir.getParent()));
            mainController.hostDir = mainController.hostDir.getParent();
            mainController.hostPath.setText(mainController.hostDir.toString());
        }
    }

    @Override
    public String getSelectedItem() {
        return mainController.hostFileList.getSelectionModel().getSelectedItem();
    }
}
