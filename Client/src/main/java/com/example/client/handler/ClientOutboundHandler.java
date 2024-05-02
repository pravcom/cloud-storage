package com.example.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultFileRegion;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class ClientOutboundHandler extends ChannelOutboundHandlerAdapter {
    private String filePath;

    public ClientOutboundHandler(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof String){
            if (!msg.toString().equals("/update")){
                return;
            }
        }
        File file = new File(filePath);
        String name = file.getName();
        if (file.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                FileChannel fileChannel = raf.getChannel();
                long length = file.length();
                DefaultFileRegion fileRegion = new DefaultFileRegion(fileChannel, 0, length);
                ctx.writeAndFlush(fileRegion, promise);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File not found");
        }
    }
}
