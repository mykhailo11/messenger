package org.chats.client;

import org.bson.Document;
import org.chats.messenger.Fields;

public class Main{

    private static final String HOST = "192.168.0.103";
    private static final int PORT = 50000;

    public static void main(String[] args) {

        Messenger mess;

        mess = new Messenger(HOST, PORT);
        mess.listener();
        mess.verify("mishania", "11112222");
        mess.listener();
        mess.sendMessage(new Document().append(Fields.SENDER, "mishania").append(Fields.RECIEVER, "anouser").append(Fields.CONTENT, "Hello... It's me...").append(Fields.DATE, "12.01.2001"));
        mess.listener();
        try{
            mess.end();
        }catch (CloseAttemptException e){
            System.out.println(e.getMessage());
        }
    }
}
