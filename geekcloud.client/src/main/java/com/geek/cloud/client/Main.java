package com.geek.cloud.client;

import com.geek.cloud.common.Action;
import com.geek.cloud.common.RequestData;

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
        RequestData request = new RequestData();
        request.setAction(Action.UPLOADFILE);


        new Thread(() -> Network.instance().start()).start();

        System.out.println("Подключение...");
        while (!Network.instance().isConnectionOpen()){

        }
        System.out.println("Клиент подключился");

        Network.instance().sendData(request);

        //Network.getInstance().sendData(request);
    }
}
