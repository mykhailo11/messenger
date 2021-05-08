package org.chats.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.chats.client.sc.Messenger;
import java.io.IOException;

public class SignUpControl {
    
    private Messenger messenger;
    @FXML
    private Button apply;
    @FXML
    private Button back;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private TextField phone;
    @FXML
    private Label state;

    public SignUpControl(Messenger mess){
        messenger = mess;
    }
    @FXML
    public void applyForm(){

        String user = username.getText();
        String pass = password.getText();
        String num = phone.getText();

        password.clear();
        phone.clear();
        if (!user.isEmpty() && !pass.isEmpty() && !num.isEmpty()){
            state.setText("Registering...");
            messenger.register(user, pass, num);
            messenger.listener();
            if (messenger.isVerified()){
            
                Stage stage = (Stage)apply.getScene().getWindow();
                Scene scene;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/fxml/userMenu.fxml"));
    
                state.setText("Success! Welcome");
                try{

                    double width = stage.getWidth();
                    double height = stage.getHeight();

                    loader.setController(new MenuControl(messenger));
                    scene = new Scene(loader.load());
                    scene.getStylesheets().add(getClass().getResource("/res/css/global.css").toExternalForm());
                    scene.getStylesheets().add(getClass().getResource("/res/css/userMenu.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setWidth(width);
                    stage.setHeight(height);
                    //stage.show();
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }else{
                state.setText("Something went wrong");
            }
        }
    }
    @FXML
    public void goBack(){
        Stage stage = (Stage)back.getScene().getWindow();
        Scene scene;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/fxml/loginScene.fxml"));

        try{

            double width = stage.getWidth();
            double height = stage.getHeight();

            loader.setController(new LoginControl(messenger));
            scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/res/css/global.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/res/css/loginScene.css").toExternalForm());
            stage.setScene(scene);
            stage.setWidth(width);
            stage.setHeight(height);
            //stage.show();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
