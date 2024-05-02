package com.example.client;

import com.example.client.controller.MainController;
import com.example.client.handler.ClientInboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.application.Platform;
import org.akhtyamov.Action;
import org.akhtyamov.Commands;
import org.akhtyamov.files.PartFile;

import java.io.*;

public class ClientNetwork {
    private SocketChannel channel;
    private final static String HOST = "localhost";
    private final static int PORT = 1234;
    private Callback onMessageReceivedCallback;
    private String filePath;
    private FileOutputStream fos;
    private final MainController mainController;

    public SocketChannel getChannel() {
        return channel;
    }

    public ClientNetwork(MainController mainController) {
        this.mainController = mainController;
        new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;

//                                channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
//                                channel.pipeline().addLast(new LengthFieldPrepender(4));
//                                channel.pipeline().addLast(new ByteArrayDecoder());
//                                channel.pipeline().addLast(new ByteArrayEncoder());
//                                channel.pipeline().addLast(new ClientInboundHandler(onMessageReceivedCallback));

                                channel.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                                channel.pipeline().addLast(new ObjectEncoder());
                                channel.pipeline().addLast(new ClientInboundHandler(mainController));

                            }
                        });
                ChannelFuture future = bootstrap.connect(HOST, PORT).sync();


                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        }).start();
    }

    public void close() {
        channel.close();
        Platform.exit();
    }

    /**
     * Метод для отправки команды или сообщения на сервер
     * @param command тип команды
     */
    public void sendCommand(String command) {
//        ByteBuf buf = Unpooled.buffer();
//        buf.writeBytes(command.getBytes());
//        channel.writeAndFlush(buf);
        channel.writeAndFlush(new Action("PathFile", Commands.DOWNLOAD));
    }

    /**
     * Метод для отправки файла на сервер
     *
     * @param command тип команды
     * @param filePath  путь файла
     */
    public void sendCommand(String command, String filePath) {
//        File file = new File("client/client/test45.txt");
//        File file = new File("client/client" +File.separator+filePath);

//        if (!file.exists()) return;
//
//        ByteBuf bufFileContent = Unpooled.buffer();
//        ByteBuf bufCommand = Unpooled.buffer();
//        ByteBuf bufFileName = Unpooled.buffer();
//        bufCommand.writeBytes(command.getBytes());
//        bufFileName.writeBytes((file.getName()).getBytes());
//
//        System.out.println("Client send file: " + file.getName());
//
//        try {
//            byte[] fileData = Files.readAllBytes(file.toPath());
//            bufFileContent.writeBytes(fileData);
//            channel.writeAndFlush(bufCommand);
//            channel.writeAndFlush(bufFileName);
//            channel.writeAndFlush(bufFileContent);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        RandomAccessFile randomAccessFile = null;

        byte[] part;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile("client/client" + File.separator + filePath, "r")) {
            part = new byte[(int) randomAccessFile.length()];
            randomAccessFile.read(part);

            PartFile partFile = new PartFile(part, filePath);
            channel.writeAndFlush(partFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
