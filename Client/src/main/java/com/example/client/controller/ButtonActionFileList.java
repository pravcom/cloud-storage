package com.example.client.controller;

import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.nio.file.Path;
import java.util.List;

public interface ButtonActionFileList {
    public void upload();
    public void enterToDir(MouseEvent mouseEvent);
    public void doubleClick();
    public void onBack();
    public String getSelectedItem();
    public void delete();



}
