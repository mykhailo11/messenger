package org.chats.messenger;

import static java.time.LocalDate.now;

public class Message {

    private String sender;
    private String reciever;
    private String content;
    private String date;
    private String state;

    public Message(String s, String r){
        sender  = s;
        reciever = r;
        content = "";
        date = now().toString();
        state = "queued";
    }
    public String getSender(){
        return sender;
    }
    public String getReciever(){
        return reciever;
    }
    public void setContent(String cont){
        content = cont;
    }
    public String getContent(){
        return content;
    }
    public void setState(String s){
        state = s;
    }
    public String getState(){
        return state;
    }
    public void setDate(String d){
        date = d;
    }
    public String getDate(){
        return date;
    }
    @Override
    public String toString(){
        return ("sender:" + sender + "%n" + "reciever:" + reciever + "%n" + "content:" + content + "%n" + "date:" + date + "%n" + "state:" + state);
    }
}
