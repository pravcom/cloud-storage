package com.example.client.handler;

import com.example.client.Callback;
import com.example.client.controller.MainController;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import org.akhtyamov.MessageExchange;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.List;

public class ClientInboundHandler extends SimpleChannelInboundHandler<MessageExchange> {
    private final MainController mainController;
    private Callback onMessageReceivedCallback;
    private Channel channel;
    private FileInputStream fis;

    public ClientInboundHandler(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(channel.remoteAddress() + " Отключился");
        ctx.channel().close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился: " + ctx.channel().remoteAddress());
        channel = ctx.channel();
        // отправляем клиенту список файлов на сервере
        mainController.getServerFileList();
        mainController.getServerDir();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageExchange message) throws Exception {
        System.out.println("Сообщение пришло: " + message.getType());

        switch (message.getType()) {
            case LIST_FILE -> {
                updateServerFileList(message);
            }
            case GET_SERVER_DIR_NAME -> {
                getServerDirName(message);
            }
            case GET_CURRENT_FILE -> {
                mainController.currentFileOnServer = (File) message.getMessage();
                mainController.doubleClickServer();
            }
        }
    }

    /**
     * Передаем контроллеру название директории сервера
     *
     * @param message
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
