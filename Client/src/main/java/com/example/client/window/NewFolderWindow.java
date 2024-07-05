package com.example.client.window;

import com.example.client.Client;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;

public class NewFolderWindow {
    Stage stage;
    public void start(Stage stage) throws IOException {
        URL fxmlLocation = Client.class.getResource("newFolderView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load(), 200, 100);
        this.stage = stage;
        this.stage.setTitle("Folder name");
        this.stage.setScene(scene);
        this.stage.show();
        //stop app after close window
//        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent windowEvent) {
//                Platform.exit();
//                System.exit(0);
//            }
//        });
    }

    public Stage getStage() {
        return stage;
    }
}
