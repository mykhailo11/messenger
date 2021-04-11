package org.chats.client;

import org.chats.server.Status;
import org.chats.server.Commands;
import org.chats.messenger.Login;
import java.net.Socket;
import java.util.Objects;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import org.bson.Document;

public class Messenger{

    private String username;
    private Socket user;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String state;
    
    public Messenger(String username, String password, String host, int port) throws UnverifiedUserException{
        connectToAccessor(host, port);
        verify(username, password);
        if (!state.equals(Status.ONLINE)){
            throw new UnverifiedUserException("Verification error");
        }
    }
    private void connectToAccessor(String host, int port){
        try{
            user = new Socket(host, port);
            System.out.println("Connection initiated");
            in = new ObjectInputStream(user.getInputStream());
            out = new ObjectOutputStream(user.getOutputStream());
        }catch (IOException e){
            System.out.println("Unable to establish connection");
        }
    }
    private void verify(String username, String password){

        Document hand;

        try{
            System.out.println("Trying to verify");
            out.writeObject(Commands.CONNECT);
            out.writeObject(new Document().append(Login.USERNAME, username).append(Login.PASSWORD, password));
            out.flush();
            do{
                hand = (Document)in.readObject();
            }while (Objects.isNull(hand));
            if (hand.get("status").toString().equals(Status.ONLINE)){
                state = Status.ONLINE;
                System.out.println("Connection established: " + hand);
            }else{
                state = Status.OFFLINE;
            }
        }catch (IOException e){
            System.out.println("Unable to establish connection");
        }catch (ClassNotFoundException e){
            System.out.println("Unable to process response");
        }
    }
    public String getState(){
        if (state.equals(Status.ONLINE)){
            return Status.ONLINE;
        }else if(state.equals(Status.OFFLINE)){
            return Status.OFFLINE;
        }else{
            return "error";
        }
    }
    public String getUserName(){
        return username;
    }
    public void sendMessage(Document mess){
        try{
            out.writeObject(Commands.ADDMESSAGE);
            out.writeObject(mess);
            out.flush();
        }catch (IOException e){
            System.out.println("Unable to send message");
        }
    }
    public ArrayList<Document> getMessages(){

        ArrayList<Document> result = null;

        try{
            out.writeObject(Commands.GETMESSAGES);
            out.flush();
            do{
                result = (ArrayList<Document>)in.readObject();
            }while (Objects.isNull(result));
        }catch (IOException e){
            System.out.println("Unable to get message");
        }catch (ClassNotFoundException e){
            System.out.println("Unable to process response");
        }
        return result;
    }
    public void end(){
        try{
            user.close();
            state = Status.OFFLINE;
            System.out.println("Client has been closed successfully");
        }catch (IOException e){
            System.out.println("Client has been already closed(?)");
        }
    }
}
