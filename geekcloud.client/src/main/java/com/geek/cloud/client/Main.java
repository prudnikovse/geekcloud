package com.geek.cloud.client;

import com.geek.cloud.common.*;
import com.geek.cloud.common.bl.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Main{ //  extends Application {
    private static boolean isLogin;
    private static ConcurrentHashMap<String, UploadFileManager> uploads =
            new ConcurrentHashMap<>(10);
    private static ConcurrentHashMap<String, DownloadFileManager> downloads =
            new ConcurrentHashMap<>(10);

//    @Override
//    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("/mainForm.fxml"));
//        primaryStage.setTitle("My Geek Cloud");
//        primaryStage.setScene(new Scene(root, 400, 400));
//        primaryStage.show();
//
//        //new Thread(() -> Network.getInstance().start(() -> changeSceneToMain())).start();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }

    public static void main (String[] args) throws InterruptedException {

        new Thread(() -> Network.instance().start()).start();

        System.out.println("Подключение...");
        while (!Network.instance().isConnectionOpen()){
        }
        System.out.println("Связь установлена");

        startServerListen();

        RequestData request = new RequestData();
        request.setAction(Action.AUTH_USER);
        request.setData("user3&pa$$w0rd3");
        Network.instance().sendData(request);
    }


    public static void authorizeEvent(ResponseData res){
        System.out.println(res.getMessage());
        if(isLogin){
            //startUploadFile("./geekcloud.server/upload/JavaFX 2.0. Разработка RIA-приложений.pdf", "");
            //startDownloadFile("JavaFX 2.0. Разработка RIA-приложений.pdf", "");
            //renameFile("New folder", "New folder2");
            //deleteFile("JavaFX 2.pdf");
            //createDirectory("New folder");
            //deleteFile("New folder");
            getFileList("");
        }
    }

    static RequestData getRequest(Action action, Object data){
        RequestData res = new RequestData();
        res.setAction(action);
        res.setData(data);
        return res;
    }

    public static void startUploadFile(String pathFrom, String pathDirectoryTo){
        try{
            UploadFileManager ufm = new UploadFileManager(10 * 1024, pathFrom, pathDirectoryTo);
            uploads.put(ufm.getProcessId(), ufm);

            System.out.println("Начинаем закачку...");
            Network.instance().sendData(getRequest(Action.UPLOAD_FILE, ufm.readFile()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void startDownloadFile(String pathFrom, String pathDirectoryTo){
        try{
            System.out.println("Начинаем скачку...");
            Network.instance().sendData(getRequest(Action.DOWNLOAD_FILE_START,
                     new String[] { pathFrom, pathDirectoryTo }));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void renameFile(String pathFrom, String newFileName) {
        try{
            System.out.println("Переименовываем файл...");
            Network.instance().sendData(getRequest(Action.RENAME_FILE,
                    new String[] { pathFrom, newFileName }));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteFile(String path) {
        try{
            System.out.println("Удаляем файл...");
            Network.instance().sendData(getRequest(Action.DELETE_FILE, path ));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void createDirectory(String path){
        try{
            System.out.println("Создаем папку...");
            Network.instance().sendData(getRequest(Action.CREATE_DIRECTORY, path ));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void getFileList(String path){
        try{
            Network.instance().sendData(getRequest(Action.GET_FILE_LIST, path ));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void startServerListen(){
        new Thread(() -> {
            while (true){
                try {
                    ResponseData res = null;
                    if((res = Network.instance().getResponse()) != null){
                        if(res.isSuccess()){
                            switch (res.getAction()){
                                case REGISTER_USER:
                                case AUTH_USER:{
                                    isLogin = res.isSuccess();
                                    authorizeEvent(res);
                                }
                                case UPLOAD_FILE:{
                                    if(res.getData() != null && res.getData() instanceof String){
                                        String processId = (String)res.getData();
                                        if(uploads.containsKey(processId)) {
                                            if (res.isSuccess()) {
                                                try {
                                                    UploadFileManager ufm = uploads.get(processId);
                                                    FileData fd = ufm.readFile();
                                                    if(fd == null){
                                                        ufm.close();
                                                        uploads.remove(processId);
                                                        System.out.println("Загрузка файла завершена!");
                                                    }else {
                                                        Network.instance().sendData(getRequest(Action.UPLOAD_FILE, fd));
                                                    }
                                                }catch (IOException ex) {
                                                    uploads.get(processId).close();
                                                    uploads.remove(processId);
                                                    System.out.println("Ошибка чтения файла!");
                                                }
                                            }else {
                                                uploads.remove(processId);
                                                System.out.println(res.getMessage());
                                            }
                                        }
                                    }
                                    break;
                                }
                                case DOWNLOAD_FILE_START:
                                case DOWNLOAD_FILE:{
                                    if(res.getData() != null && res.getData() instanceof FileData) {
                                        FileData data = (FileData) res.getData();
                                        if(res.isSuccess()) {
                                            DownloadFileManager dfm = null;
                                            try {
                                                if (downloads.containsKey(data.getProcessId())) {
                                                    dfm = downloads.get(data.getProcessId());
                                                    if (dfm.write(data) == 0)
                                                        downloads.remove(data.getProcessId());
                                                } else {
                                                    dfm = new DownloadFileManager(data, "./geekcloud.server/download/", (int) data.getSize());
                                                    if (dfm.write(data) != 0) {
                                                        downloads.put(data.getProcessId(), dfm);

                                                    }
                                                }
                                            }catch (Exception ex){
                                                if(dfm != null)
                                                    dfm.close();
                                            }
                                            if(dfm.isProcessing()){
                                                Network.instance().sendData(getRequest(Action.DOWNLOAD_FILE,
                                                        new String[] { data.getProcessId() }));
                                            }else{
                                                System.out.println("Файл " + data.getFileName() + " скачан!");
                                            }
                                        }
                                    }
                                    break;
                                }
                                case RENAME_FILE:{
                                    if(res.isSuccess()){
                                        System.out.println("Файл переименован!");
                                    }
                                    break;
                                }
                                case DELETE_FILE:{
                                    if(res.isSuccess()){
                                        System.out.println("Файл удален!");
                                    }
                                    break;
                                }
                                case CREATE_DIRECTORY:{
                                    if(res.isSuccess()){
                                        System.out.println("Папка создана!");
                                    }
                                    break;
                                }
                                case GET_FILE_LIST:{
                                    if(res.getData() != null && res.getData() instanceof FileList) {
                                        FileList data = (FileList) res.getData();
                                        for (FileItem item : data.getItems()) {
                                            System.out.println((item.isDirectory() ? "-- " : " ")
                                                    + item.getName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }catch (InterruptedException ex) {

                }
            }
        }).start();
    }
}
