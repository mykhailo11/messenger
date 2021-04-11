package org.chats.proxy;

import org.chats.server.Status;
import org.chats.messenger.Login;
import org.chats.server.Commands;
import java.net.Socket;
import java.util.Objects;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import org.bson.Document;

public class Assistant extends Thread{

    private static Mongol mongo;
    private String username;
    private boolean verified;
    private Socket client;
    private String connection;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public Assistant(Socket c){
        client = c;
        try{

            String hand;
            Document info;

            in = new ObjectInputStream(client.getInputStream());
            out = new ObjectOutputStream(client.getOutputStream());
            System.out.println("Data stream established");
            do{
                hand = in.readObject().toString();
            }while (Objects.isNull(hand));
            if (hand.equals(Commands.CONNECT)){
                info = (Document)in.readObject();
                verified = mongo.verifyUser(info);
                if (verified){
                    username = info.get(Login.USERNAME).toString();
                    connection = Status.ONLINE;
                    out.writeObject(connection);
                    out.flush();
                }else{
                    System.out.println("Uverified user");
                }
            }else{
                connection = Status.OFFLINE;
            }
        }catch (IOException e){
            System.out.println("Unable to establish data stream");
            connection = Status.OFFLINE;
        }catch (ClassNotFoundException e){
            System.out.println("Incorrect data type passed");
            connection = Status.OFFLINE;
        }
    }
    public static void setMongo(Mongol c){
        mongo = c;
    }
    public String getConnection(){
        return connection;
    }
    public String getUsername(){
        return username;
    }
    public boolean isVerified(){
        return verified;
    }
    private void process(String intend){
        if (intend.equals(Commands.ADDMESSAGE)){
            try{
                mongo.addMessage((Document)in.readObject());
            }catch (ClassNotFoundException e){
                System.out.println("Unable to extract message");
            }catch (IOException e){
                System.out.println("Unable to get access to stream");
            }
        }else if (intend.equals(Commands.GETMESSAGES)){
            try{
                out.writeObject(mongo.getMessages(in.readObject().toString()));
                out.flush();
            }catch (ClassNotFoundException e){
                System.out.println("Unable to define username");
            }catch (IOException e){
                System.out.println("Unable to get access to stream");
            }
        }else if (intend.equals(Commands.DISCONNECT)){
            connection = Status.OFFLINE;
        }
        
    }
    @Override
    public void run(){
        while (connection.equals(Status.ONLINE) && verified){
            
            String intend;

            try{
                do{
                    intend = in.readObject().toString();
                }while (Objects.isNull(intend));
                System.out.println("Got message");
                process(intend);
            }catch (IOException e){
                System.out.println("Unable to communicate with cient");
                connection = Status.OFFLINE;
            }catch (ClassNotFoundException e){
                System.out.println("Unable to define command");
                connection = Status.OFFLINE;
            }
        }
        if (!verified){
            System.out.println("Verification error");
        }
        System.out.println("Connection ended");
    }

}
