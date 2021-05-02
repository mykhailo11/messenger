package org.chats.client.gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.chats.client.sc.CloseAttemptException;
import org.chats.client.sc.Messenger;

public class Main extends Application{

    private static final String HOST = "192.168.0.109";
    private static final int PORT = 50000;

    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage stage){

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/fxml/loginScene.fxml"));
        Scene scene;
        Messenger mess = new Messenger(HOST, PORT);

        try{
            loader.setController(new LoginControl(mess));
            scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/res/css/global.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/res/css/loginScene.css").toExternalForm());
            stage.setScene(scene);
            stage.setOnCloseRequest(e -> {
                try{
                    mess.end();
                }catch (CloseAttemptException ex){
                    System.out.println(ex.getMessage());
                }
            });
            stage.show();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
