module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.all;
    requires org.akhtyamov;

    opens com.example.client to javafx.fxml;
    exports com.example.client;
    exports com.example.client.controller;
    opens com.example.client.controller to javafx.fxml;
    exports com.example.client.model;
    opens com.example.client.model to javafx.fxml;


}