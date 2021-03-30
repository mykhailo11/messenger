package org.chats.proxy;

import org.chats.server.Status;
import org.chats.server.Commands;
import java.net.Socket;
import java.util.Objects;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

public class Assistant extends Thread{

    private String username;
    private Socket client;
    private Status connection;
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
                connection = Status.CONNECTED;
                System.out.println("Connection established");
            }else{
                connection = Status.DISCONECTED;
            }
            out.println(connection);
            out.flush();
        }catch (IOException e){
            System.out.println("Unable to establish data stream");
            connection = Status.ERROR;
        }
    }
    public Status getConnection(){
        return connection;
    }
    private void process(String intend){
        System.out.println(intend);
    }
    @Override
    public void run(){
        while (connection.equals(Status.CONNECTED)){
            
            String intend;

            try{
                do{
                    intend = in.readLine();
                }while (Objects.isNull(intend));
                System.out.println("Got message");
                process(intend);
            }catch (IOException e){
                System.out.println("Unable to communicate with cient");
                connection = Status.ERROR;
                break;
            }
        }
        System.out.println("Connection ended");
    }

}
