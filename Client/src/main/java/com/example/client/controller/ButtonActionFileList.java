package com.example.client.controller;

import javafx.scene.input.MouseEvent;

public interface ButtonActionFileList {
    void upload();
     void enterToDir(MouseEvent mouseEvent);
     void doubleClick();
     void onBack();
     String getSelectedItem();
     void delete();
     void copy();
     void createNewFolder(String name);
     void callNewFolderScreen();



}
