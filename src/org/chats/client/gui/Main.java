package org.chats.client.gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

        try{
            loader.setController(new LoginControl(HOST, PORT));
            scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/res/css/global.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/res/css/loginScene.css").toExternalForm());
            stage.setTitle("Chats");
            stage.setScene(scene);
            stage.show();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
