package org.akhtyamov.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.akhtyamov.Action;
import org.akhtyamov.Commands;
import org.akhtyamov.MessageExchange;
import org.akhtyamov.files.FileList;
import org.akhtyamov.files.FileName;
import org.akhtyamov.files.PartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<MessageExchange> {
    public static final String sourceRoot = "server/server";
    private Path userRootDir;
    private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client connected: " + ctx.channel().remoteAddress());
        userRootDir = Paths.get("server/server");
        channel = ctx.channel();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Client disconnected: " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Обработка других типов исключений
        // Например, отправка общего сообщения об ошибке или закрытие соединения
        ctx.writeAndFlush("An error occurred: " + cause.getMessage() + "\n");
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageExchange message) {
        System.out.println(message.getType());

        switch (message.getType()) {
            case CREATE_NEW_FOLDER -> {
                System.out.println(Commands.CREATE_NEW_FOLDER + "-->" + LocalTime.now());
                createNewFolder(message);
                sendFileList(ctx, userRootDir);
            }
            case COPY_SERVER_FILE -> {
                System.out.println(Commands.COPY_SERVER_FILE + "-->" + LocalTime.now());
                copyFile(message);
                sendFileList(ctx, userRootDir);
            }
            case DOWNLOAD -> {

            }
            case UPLOAD_ON_HOST -> uploadOnHost(message);
            case FILE -> {
                PartFile partFile = (PartFile) message;
                Path path = Path.of(userRootDir + File.separator + partFile.getFilename());

                byte[] fileBytes = (byte[]) partFile.getMessage();
                try {
                    Files.write(path, fileBytes, StandardOpenOption.CREATE, StandardOpenOption.SYNC, StandardOpenOption.APPEND);
                    sendFileList(ctx, userRootDir);
                } catch (IOException e) {
                    System.out.println("Ошибка на принятие файла: " + e.getMessage());
                }

            }
            case GET_FILE_LIST -> {
                System.out.println(Commands.GET_FILE_LIST + "-->" + LocalTime.now());

                sendFileList(ctx, userRootDir);
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
                sendFileList(ctx, userRootDir);
            }
            case GET_CURRENT_FILE -> {
                System.out.println(Commands.GET_CURRENT_FILE + "-->" + LocalTime.now());
                getFile(message);
            }
            case BACK -> {
                System.out.println(Commands.BACK + "-->" + LocalTime.now());
                if (!userRootDir.equals(Paths.get(sourceRoot))) {
                    userRootDir = userRootDir.getParent();
                    sendFileList(ctx, userRootDir);
                    sendDirName();
                }
            }
            case DELETE_SERVER_FILE -> {
                System.out.println(Commands.DELETE_SERVER_FILE + "-->" + LocalTime.now());
                delete(message);
                sendFileList(ctx, userRootDir);
            }
        }

    }

    private void createNewFolder(MessageExchange message) {
        try {
            Path path = Paths.get(userRootDir + File.separator + message.getMessage());
            Files.createDirectory(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyFile(MessageExchange message) {
        Path file = Paths.get((String) message.getMessage());
        try {
            Integer count = 0;
            Path fileName = Paths.get((String) message.getMessage());

            while (Files.exists(fileName)) {
                count++;
                fileName = Paths.get(userRootDir + File.separator +
                        "Copy" +
                        count +
                        "_" +
                        file.getFileName());
            }

            Files.copy(file, fileName);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void uploadOnHost(MessageExchange message) {
        File file = Paths.get((String) message.getMessage()).toFile();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[fis.available()];
            //считываем буфер
            fis.read(buffer);
            channel.writeAndFlush(new PartFile(buffer, file.getName()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void delete(MessageExchange message) {
        Paths.get((String) message.getMessage()).toFile().delete();
    }

    private void sendDirName() {
        channel.writeAndFlush(new Action(userRootDir.toString(), Commands.GET_SERVER_DIR_NAME));
    }

    /**
     * Отправляем иницилизирующий список файлов сервера клиенту
     */
    private void sendFileList(ChannelHandlerContext ctx, Path currentDir) {
        try {
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
