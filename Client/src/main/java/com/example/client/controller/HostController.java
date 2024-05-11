package com.example.client.controller;

import org.akhtyamov.files.PartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HostController implements ButtonActionFileList{
    private final MainController mainController;

    public HostController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void upload() {
        Path file = Paths.get(mainController.hostPath.getText());
        try (FileInputStream fis = new FileInputStream(file.toFile())) {
            byte[] buffer = new byte[256];
            //считываем буфер
            fis.read(buffer);
            mainController.clientNetwork.getChannel().writeAndFlush(new PartFile(buffer, getSelectedItem()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void getFiles() {

    }

    @Override
    public void enterToDir() {

    }

    @Override
    public void doubleClickClient() {

    }

    @Override
    public void onBack() {

    }

    @Override
    public void getSelectedItem() {
        return hostFileList.getSelectionModel().getSelectedItem();
    }
}
