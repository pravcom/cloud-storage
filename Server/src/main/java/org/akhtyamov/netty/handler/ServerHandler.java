package org.akhtyamov.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.akhtyamov.*;
import org.akhtyamov.files.FileList;
import org.akhtyamov.files.FileName;
import org.akhtyamov.files.PartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<MessageExchange> {
    public static final String sourceRoot = "server/server";
    private Path userRootDir;
    private File currentDir;
    private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected: " + ctx.channel().remoteAddress());
        userRootDir = Paths.get("server/server");
        channel = ctx.channel();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected: " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Обработка других типов исключений
        // Например, отправка общего сообщения об ошибке или закрытие соединения
        ctx.writeAndFlush("An error occurred: " + cause.getMessage() + "\n");
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageExchange message) throws Exception {
        System.out.println(message.getType());

        switch (message.getType()) {
            case DOWNLOAD -> {

            }
            case FILE -> {
                PartFile partFile = (PartFile) message;
                Path path = Path.of(userRootDir + File.separator + partFile.getFilename());

                byte[] fileBytes = (byte[]) partFile.getMessage();
                try {
                    Files.write(path, fileBytes, StandardOpenOption.CREATE, StandardOpenOption.SYNC, StandardOpenOption.APPEND);
                    sendFileList(ctx, userRootDir, message);
                } catch (IOException e) {
                    System.out.println("Ошибка на принятие файла: " + e.getMessage());
                }

            }
            case GET_FILE_LIST -> {
                System.out.println(Commands.GET_FILE_LIST + "-->" + LocalTime.now());

                sendFileList(ctx, userRootDir, message);
            }
            case GET_SERVER_DIR_NAME -> {
                System.out.println(Commands.GET_SERVER_DIR_NAME + "-->" + LocalTime.now());
                System.out.print(LocalTime.now());

                sendDirName();
            }
            case DOUBLE_CLICK_FILE -> {
                System.out.println(Commands.DOUBLE_CLICK_FILE + "-->" + LocalTime.now());
                System.out.print(LocalTime.now());

                userRootDir = Paths.get((String) message.getMessage());
                sendFileList(ctx, userRootDir, message);
            }
            case GET_CURRENT_FILE -> {
                System.out.println(Commands.GET_CURRENT_FILE + "-->" + LocalTime.now());

                getFile(message);
            }
            case BACK -> {
                System.out.println(Commands.BACK + "-->" + LocalTime.now());
                if (!userRootDir.equals(Paths.get(sourceRoot))) {
                    userRootDir = userRootDir.getParent();
                    sendFileList(ctx, userRootDir, message);
                    sendDirName();
                }
            }
        }

    }

    private void sendDirName() {
        channel.writeAndFlush(new Action(userRootDir.toString(), Commands.GET_SERVER_DIR_NAME));
    }

    /**
     * Отправляем иницилизирующий список файлов сервера клиенту
     *
     * @param ctx
     * @param currentDir
     * @param message
     */
    private void sendFileList(ChannelHandlerContext ctx, Path currentDir, MessageExchange message) {
        try {
//            String finalFileMask = String.valueOf(message.getMessage()).trim();
//            List<String> lists = Files.list(currentDir)
//                    .map(p -> p.getFileName().toString())
//                    .filter(fileName -> fileName.contains(finalFileMask))
//                    .collect(Collectors.toList());

            List<String> lists = Files.list(currentDir)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());
            FileList fileLists = new FileList(lists);
            ctx.writeAndFlush(fileLists);
        } catch (IOException e) {
            log.error("Cant send file list ", e);
        }
    }

    private void getFile(MessageExchange message) {
        File file = Paths.get(userRootDir.toString() + File.separator + message.getMessage()).toFile();
        FileName fileToSend = new FileName(file);
        channel.writeAndFlush(fileToSend);
    }
}
