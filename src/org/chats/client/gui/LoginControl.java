package org.chats.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import org.chats.client.sc.Messenger;
import java.io.IOException;

public class LoginControl {
    
    private Messenger messenger;
    @FXML
    private Button signin;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Label state;

    public LoginControl(Messenger mess){
        messenger = mess;
    }
    @FXML
    public void signIn(){
        
        String user = username.getText();
        String pass = password.getText();

        if (!user.isEmpty() && !pass.isEmpty()){
            state.setText("Verifying...");
            messenger.verify(user, pass);
            messenger.listener();
            if (messenger.isVerified()){
            
                Stage stage = (Stage)signin.getScene().getWindow();
                Scene scene;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/fxml/userMenu.fxml"));
    
                state.setText("Verified!");
                try{
                    loader.setController(new MenuControl(messenger));
                    scene = new Scene(loader.load());
                    scene.getStylesheets().add(getClass().getResource("/res/css/global.css").toExternalForm());
                    scene.getStylesheets().add(getClass().getResource("/res/css/userMenu.css").toExternalForm());
                    stage.setScene(scene);
                    stage.show();
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }else{
                state.setText("Invalid info");
            }
        }else{
            state.setText("Fill the blanks");
        }
    }
}
