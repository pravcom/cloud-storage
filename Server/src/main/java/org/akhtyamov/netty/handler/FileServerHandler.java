package org.akhtyamov.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

public class FileServerHandler extends ChannelInboundHandlerAdapter{
    private File file;
    private String filename;
    private FileOutputStream fos;
    public static final String sourceRoot = "server/server";
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Файл пришел на сервер: "+msg);
        ByteBuf buf = Unpooled.buffer();
        byte[] bytesMsg = (byte[]) msg;
        buf.writeBytes(bytesMsg);
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        String message = new String(data);

        if (message.equals(CommandsHandler.UPLOAD) || CommandsHandler.uploadFlag == true){
            if (CommandsHandler.uploadFlag == true && filename==null){
                filename = "server"+File.separator+"server"+File.separator + message;
                file = new File(filename);
                return;
            }
            if (file!=null){
                fos = new FileOutputStream(file);
                fos.write(data);
                System.out.println("File created successfully at path: " + file.getPath() );
                CommandsHandler.uploadFlag = false;

                fos.close();
                file=null;
                filename=null;
                return;
            }

            CommandsHandler.uploadFlag = true;

        } else if (message.equals(CommandsHandler.LS)) {
//            File[] listFiles = new File(sourceRoot).listFiles();
//            StringBuilder sb = new StringBuilder();
//            sb.append(CommandsHandler.listFiles(listFiles, 0));
//            ByteBuf byteBuf = Unpooled.buffer();
//            byteBuf.writeBytes(sb.toString().getBytes());
//
//            ctx.writeAndFlush(byteBuf);

            TreeItem<String> root = CommandsHandler.getNodesForDirectory(new File(sourceRoot));

            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes(root.toString().getBytes());

            ctx.writeAndFlush(root);

        }else {
            System.out.println("BufByte is: "+ message);
            ctx.writeAndFlush(bytesMsg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Пиздец ошибка " + cause.getMessage());
    }
}
