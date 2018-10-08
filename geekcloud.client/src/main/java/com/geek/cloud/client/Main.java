package com.geek.cloud.client;

import com.geek.cloud.common.*;
import com.geek.cloud.common.bl.*;

import java.util.ArrayList;

public class Main{ //  extends Application {

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

        RequestData request = new RequestData();
        request.setAction(Action.AUTHUSER);
        User user = new User();
        user.setUserName("user3");
        user.setPasswordSalt("fgsddfgdg6sdgd98gd9fasdghasddfggas79yfds");
        user.setPassword("pa$$w0rd3");
        user.setEmail("user3@mail.ru");
        request.setData("user3&pa$$w0rd3");

        Network.instance().sendData(request);

        Thread.sleep(10000 );

        if (Network.instance().isLogin()){
            System.out.println("Начинаем закачку...");

//            FileData fd = new FileData();
//            fd.setTotalPacket(1);
//            fd.setDirectoryPath("./geekcloud.server/files/");
//            fd.setFileName("test.txt");
//            fd.setTotalSize(20000);
//            RequestData req = new RequestData();
//            req.setAction(Action.UPLOADFILE);
//            req.setData(fd);
//            Network.instance().sendData(req);
            FileUploadManager fum = new FileUploadManager(10 * 1024);
            ArrayList<FileData> fileParts = fum.uploadFile("./geekcloud.server/upload/Анкета кандидата САО ВСК 2016.docx", "");

//            fum.uploadFile("./geekcloud.server/upload/Анкета кандидата САО ВСК 2016.docx", "",
//                    Network.instance().getCurrentChannel());
            for(FileData fd : fileParts){
                RequestData req = new RequestData();
                req.setAction(Action.UPLOADFILE);
                req.setData(fd);
                Network.instance().sendData(req);
            }
        }

//        Object response = null;
//        Network.instance().getCurrentChannel().pipeline().
//        if (response != null && response instanceof ResponseData){
//            ResponseData res = (ResponseData)response;
//            System.out.println(res.getMessage());
//        }
    }
}
