package org.chats.client.gui;

import java.util.ArrayList;
import org.chats.client.sc.Messenger;
import org.chats.client.sc.Subscriber;
import javafx.fxml.FXML;
import org.chats.client.sc.Listener;
import javafx.scene.layout.HBox;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import org.bson.Document;
import org.chats.messenger.Fields;

public class MenuControl implements Subscriber{
    
    private Messenger messenger;
    private Listener listen;
    @FXML
    private Label current;
    @FXML
    private HBox users;
    @FXML
    private VBox msgs;
    @FXML
    private Button send;
    @FXML
    private TextField msg;

    public MenuControl(Messenger mess){
        messenger = mess;
        messenger.getMessages();
        listen = new Listener(messenger, this);
        listen.start();
    }
    @FXML
    public void sendMess(){
        if (!current.getText().isEmpty() && !msg.getText().isEmpty()){
            messenger.sendMessage(new Document().append(Fields.SENDER, messenger.getUsername()).append(Fields.RECIEVER, current.getText()).append(Fields.CONTENT, msg.getText()).append(Fields.DATE, "0/0/0"));
            showMessages();
        }
    }
    public void alarm(){
        
        ArrayList<Document> messages = messenger.getPack();

        Platform.runLater(() -> {
            users.getChildren().clear();
            designIcons(getCompanions(messages)).forEach(but -> users.getChildren().add(but));
        });
        showMessages();
    }
    /**
     * Methods returns list of users the client has conversation with
     */
    private ArrayList<String> getCompanions(ArrayList<Document> messages){
        
        ArrayList<String> companions = new ArrayList<>();

        messages.forEach(mess -> {

            String sender = (String)mess.get(Fields.SENDER);
            String reciever = (String)mess.get(Fields.RECIEVER);

            if (!companions.contains(sender) && !sender.equals(messenger.getUsername())){
                companions.add(sender);
            }else if (!companions.contains(reciever) && !reciever.equals(messenger.getUsername())){
                companions.add(reciever);
            }
        });
        return companions;
    }
    /**
     * Method builds graphical representation of users the client
     * has conversation with
     */
    private ArrayList<Button> designIcons(ArrayList<String> companions){
        
        ArrayList<Button> icons = new ArrayList<>();

        companions.forEach(comp -> {
            
            Button icon = new Button(comp);

            icon.getStyleClass().add("icon");
            icon.setTextOverrun(OverrunStyle.CLIP);
            icon.setOnAction(e -> {
                current.setText(((Button)e.getSource()).getText());
                showMessages();
            });
            icons.add(icon);
        });
        return icons;
    }
    private ArrayList<TextFlow> designBlocks(ArrayList<Document> messages){
        
        ArrayList<TextFlow> texts = new ArrayList<>();

        messages.forEach(mess -> {
            
            Text content = new Text((String)mess.get(Fields.CONTENT));
            TextFlow area = new TextFlow();
            
            if (content.getText().length() <= 4){
                content.getStyleClass().add("short");
            }
            area.getStyleClass().add("msg");
            if (mess.get(Fields.SENDER).equals(current.getText())){
                area.getStyleClass().add("s");
            }else if (mess.get(Fields.RECIEVER).equals(current.getText())){
                area.getStyleClass().add("r");
            }
            area.getChildren().add(content);
            texts.add(area);
        });
        return texts;
    }
    /**
     * Method returns messages related with the current companion
     */
    private ArrayList<Document> getChat(ArrayList<Document> messages){

        ArrayList<Document> related = new ArrayList<>();

        messages.forEach(mess -> {
            if (mess.get(Fields.SENDER).equals(current.getText()) || mess.get(Fields.RECIEVER).equals(current.getText())){
                related.add(mess);
            }
        });
        return related;
    }
    private void showMessages(){

        ArrayList<Document> messages = messenger.getPack();

        Platform.runLater(() -> {
            msgs.getChildren().clear();
            designBlocks(getChat(messages)).forEach(area -> msgs.getChildren().add(area));
        });
    }
}
