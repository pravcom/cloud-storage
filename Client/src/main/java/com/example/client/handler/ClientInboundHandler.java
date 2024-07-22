package com.example.client.handler;

import com.example.client.controller.MainController;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import org.akhtyamov.MessageExchange;
import org.akhtyamov.files.PartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ClientInboundHandler extends SimpleChannelInboundHandler<MessageExchange> {
    private final MainController mainController;
    private Channel channel;

    public ClientInboundHandler(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println(channel.remoteAddress() + " Отключился");
        ctx.channel().close();
        mainController.setActive(false);
//        mainController.clearServerNames();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Клиент подключился: " + ctx.channel().remoteAddress());
        channel = ctx.channel();
        // отправляем клиенту список файлов на сервере
        mainController.getServerFileList();
        mainController.getServerDir();
        mainController.setActive(true);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageExchange message) {
        System.out.println("Сообщение пришло: " + message.getType());

        switch (message.getType()) {
            case FILE -> getFileFromServer(message);
            case LIST_FILE -> updateServerFileList(message);
            case GET_SERVER_DIR_NAME -> getServerDirName(message);
            case GET_CURRENT_FILE -> {
                mainController.currentFileOnServer = (File) message.getMessage();
                mainController.doubleClickServer();
            }
        }
    }

    private void getFileFromServer(MessageExchange message) {
        PartFile partFile = (PartFile) message;
        Path path = Path.of(mainController.hostDir + File.separator + partFile.getFilename());

        byte[] fileBytes = (byte[]) partFile.getMessage();
        try {
            Files.write(path, fileBytes, StandardOpenOption.CREATE, StandardOpenOption.SYNC, StandardOpenOption.APPEND);
            mainController.updateHostListView(mainController.hostDir);
        } catch (IOException e) {
            System.out.println("Ошибка на принятие файла: " + e.getMessage());
        }
    }

    /**
     * Передаем контроллеру название директории сервера
     */
    private void getServerDirName(MessageExchange message) {
        mainController.setServerDirName((Paths.get((String) message.getMessage())));
    }

    private void updateServerFileList(MessageExchange message) {
        Platform.runLater(() -> {
            mainController.serverFileList.getItems().clear();
            mainController.serverFileList.getItems().addAll((List<String>) message.getMessage());
        });

    }
}
