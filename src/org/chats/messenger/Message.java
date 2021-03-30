package org.chats.messenger;

import static java.time.LocalDate.now;

public class Message {

    private String sender;
    private String reciever;
    private String content;
    private String date;
    private MessState state;

    public Message(String s, String r){
        sender  = s;
        reciever = r;
        content = "";
        date = now().toString();
        state = MessState.QUEUED;
    }
    public void setContent(String cont){
        content = cont;
    }
    public void setState(MessState s){
        state = s;
    }
    @Override
    public String toString(){
        return ("----------%n" + "Sender:%n" + sender + "%n" + "Reciever:%n" + reciever + "%n" + "Content:%n" + content + "%n" + "Date:%n" + date + "State:%n" + state + "%n" + "----------%n");
    }
}
