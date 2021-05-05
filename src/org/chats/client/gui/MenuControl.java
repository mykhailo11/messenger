package org.chats.client.gui;

import java.util.ArrayList;
import java.util.Objects;
import org.chats.client.sc.Messenger;
import org.chats.client.sc.Subscriber;
import javafx.fxml.FXML;
import org.chats.client.sc.Listener;
import javafx.scene.layout.HBox;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.scene.text.Text;
import org.bson.Document;
import org.chats.messenger.Fields;
import org.chats.server.MessState;
import javafx.animation.TranslateTransition;

public class MenuControl implements Subscriber{
    
    private Messenger messenger;
    private Listener listen;
    private final TranslateTransition transl = new TranslateTransition();
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
    @FXML
    private ScrollPane notes;

    public MenuControl(Messenger mess){
        transl.setDuration(Duration.millis(300));
        transl.setFromY(200);
        transl.setToY(0);
        messenger = mess;
        messenger.getMessages();
        listen = new Listener(messenger, this);
        listen.start();
    }
    @FXML
    public void sendMess(){
        if (!current.getText().isEmpty() && !msg.getText().isEmpty()){
            messenger.sendMessage(new Document().append(Fields.SENDER, messenger.getUsername()).append(Fields.RECIEVER, current.getText()).append(Fields.CONTENT, msg.getText()).append(Fields.DATE, "0/0/0"));
        }
    }
    @FXML
    public void addCompanion(){
        Platform.runLater(() -> {

            String comp = msg.getText();
            ArrayList<String> comps = messenger.getCompanions();

            if (!comps.contains(comp) && !comp.equals(messenger.getUsername()) && !comp.isEmpty()){
                messenger.getCompanions().add(comp);
                users.getChildren().add(designIcon(comp));
            }
        });
    }
    public void alarm(){
        Platform.runLater(() -> {
            users.getChildren().clear();
            messenger.getCompanions().forEach(comp -> users.getChildren().add(designIcon(comp)));
        });
        showMessages();
    }
    /**
     * Method builds graphical representation of users the client
     * has conversation with
     */
    private Button designIcon(String companion){
        
        Button icon = new Button(companion);

        icon.getStyleClass().add("icon");
        icon.setTextOverrun(OverrunStyle.CLIP);
        icon.setOnAction(e -> {
            current.setText(((Button)e.getSource()).getText());
            showMessages();
        });
        return icon;
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
            if (mess.get(Fields.STATE).equals(MessState.QUEUED)){
                transl.setNode(area);
                mess.put(Fields.STATE, MessState.DELIVERED);
            }
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
            transl.setNode(null);
            designBlocks(getChat(messages)).forEach(area -> msgs.getChildren().add(area));
            Platform.runLater(() -> {
                if (!Objects.isNull(transl.getNode())){
                    notes.setVvalue(notes.getVmax());
                    transl.play();
                }
            });
        });
    }
}
