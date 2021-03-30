package org.chats.client;

import org.chats.server.Status;
import org.chats.server.Commands;
import java.net.Socket;
import java.util.Objects;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

public class Messenger{

    private String username;
    private Socket user;
    private BufferedReader in;
    private PrintWriter out;
    private Status state;
    
    public Messenger(String username, String password, String host, int port) throws UnverifiedUserException{
        
        String hand;

        try{
            user = new Socket(host, port);
            System.out.println("Connection initiated");
            in = new BufferedReader(new InputStreamReader(user.getInputStream()));
            out = new PrintWriter(user.getOutputStream());
            System.out.println("Trying to connect");
            out.println(Commands.CONNECT);
            out.flush();
            do{
                hand = in.readLine();
            }while (Objects.isNull(hand));
            if (hand.equals(Status.CONNECTED.toString())){
                state = Status.CONNECTED;
                System.out.println("Connection established");
            }else{
                throw new UnverifiedUserException("Connection failed");
            }
        }catch (IOException e){
            System.out.println("Unable to establish connection");
        }
    }
    public String getState(){
        if (state.equals(Status.CONNECTED)){
            return "Connected";
        }else if(state.equals(Status.DISCONECTED)){
            return "Disconected";
        }else{
            return "Error";
        }
    }
    public String getUserName(){
        return username;
    }
    public void sendMessage(String mess){
        out.println(mess);
        out.flush();
    }
    public String getMessage(){

        String result = "";

        try{
            result = in.readLine();
        }catch (IOException e){
            System.out.println("Unable to get message");
        }
        return result;
    }
    public void end(){
        try{
            user.close();
            System.out.println("Client has been closed successfully");
        }catch (IOException e){
            System.out.println("Client has been already closed(?)");
        }
    }
}
