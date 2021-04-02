package org.chats.proxy;

import org.chats.server.Status;
import org.chats.server.Commands;
import org.chats.messenger.Message;
import org.chats.messenger.Field;
import java.net.Socket;
import java.util.Objects;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

public class Assistant extends Thread{

    private static Mongol mongo;
    private String username;
    private boolean verified;
    private Socket client;
    private String connection;
    private BufferedReader in;
    private PrintWriter out;
    
    public Assistant(Socket c){
        client = c;
        try{

            String hand;

            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream());
            System.out.println("Data stream established");
            do{
                hand = in.readLine();
            }while (Objects.isNull(hand));
            if (hand.equals(Commands.CONNECT)){
                username = in.readLine();
                verified = mongo.verifyUser(username, in.readLine());
                if (verified){
                    connection = Status.ONLINE;
                    out.println(connection);
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
        }
    }
    public static void setMongo(Mongol c){
        mongo = c;
    }
    public String getConnection(){
        return connection;
    }
    public boolean isVerified(){
        return verified;
    }
    private void process(String intend){
        if (intend.equals(Commands.ADDMESSAGE)){
            //Doing something
        }
    }
    @Override
    public void run(){
        while (connection.equals(Status.ONLINE) && verified){
            
            String intend;

            try{
                do{
                    intend = in.readLine();
                }while (Objects.isNull(intend));
                System.out.println("Got message");
                process(intend);
            }catch (IOException e){
                System.out.println("Unable to communicate with cient");
                connection = Status.OFFLINE;
                break;
            }
        }
        if (!verified){
            System.out.println("Verification error");
        }
        System.out.println("Connection ended");
    }

}
