package org.akhtyamov.netty.handler;

import javafx.scene.control.TreeItem;

import java.io.File;

public class CommandsHandler {
    public static final String LS = "/list-files";
    public static final String UPLOAD = "/upload";
    public static boolean uploadFlag;

    /**
     * Работает с помощью рекурсии
     * @param files
     * @param countTab в зависимости вложенности папки устанавливает кол-во табов
     * @return
     */
    public static StringBuilder listFiles(File[] files, int countTab){
        StringBuilder sb = new StringBuilder();
        StringBuilder sbTabs = new StringBuilder();

        for (int i=0;i<countTab;i++){
            sbTabs.append("\t");
        }

        for (File f : files){
            sb.append(sbTabs + f.getName()+"\n\r");

            if (f.isDirectory()){
                File[] subFiles = f.listFiles();
                sb.append(listFiles(subFiles, countTab+1));
            }
        }

        return sb;
    }
    public static TreeItem<String> getNodesForDirectory(File directory) {
        TreeItem<String> root = new TreeItem<String>(directory.getName());
        for(File f : directory.listFiles()) {
            if(f.isDirectory()) //если каталог идем на рекурсию
                root.getChildren().add(getNodesForDirectory(f));
            else //если просто файл заполняем только имя
                root.getChildren().add(new TreeItem<String>(f.getName()));
        }
        return root;
    }
}
