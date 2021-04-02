package org.chats.client;

public class Main{

    private static final String HOST = "localhost";
    private static final int PORT = 50000;

    public static void main(String[] args) {

        Messenger mess;

        try{
            mess = new Messenger("mishania", "11112222", HOST, PORT);
            mess.end();
        }catch (UnverifiedUserException e){
            System.out.println("Try again");
        }
    }
}
