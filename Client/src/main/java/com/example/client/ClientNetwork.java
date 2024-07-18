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

    public SocketChannel getChannel() {
        return channel;
    }
    public void start(MainController mainController){
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

    public ClientNetwork() {

    }

    public void close() {
        if(channel!= null) channel.close();
        Platform.exit();
    }
}
