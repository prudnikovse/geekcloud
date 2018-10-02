package com.geek.cloud.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.ObjectOutputStream;
import java.net.Socket;
import com.geek.cloud.common.*;

public class Controller {
    @FXML
    TextField textField;

    @FXML
    TextArea textArea;

    public void btnClickMe() {
//        try {
//            Socket socket = new Socket("localhost", 8189);
//            RequestData request = new RequestData();
//            request.action = Action.UPLOADFILE;
//            request.data = new byte[] {1,2,3,4};
//
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//            oos.writeObject(request);
//            socket.close();
//        }catch (Exception ex){
//
//        }

        //new Thread(() -> Network.getInstance().start()).start();

    }
}
