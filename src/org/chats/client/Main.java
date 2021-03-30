package org.chats.client;

public class Main{

    private static final String HOST = "localhost";
    private static final int PORT = 50000;

    public static void main(String[] args) {

        Messenger mess;

        try{
            mess = new Messenger("user", "pass", HOST, PORT);
            mess.sendMessage("Hello");
            mess.end();
        }catch (UnverifiedUserException e){
            System.out.println("Try again");
        }
    }
}
