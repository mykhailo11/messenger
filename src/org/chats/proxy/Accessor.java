package org.chats.proxy;

import java.util.ArrayList;
import java.util.Objects;
import java.io.IOException;
import java.net.ServerSocket;

public class Accessor {

    private ArrayList<Assistant> connected;
    private ServerSocket access;
    
    public Accessor(int port){
        try{
            access = new ServerSocket(port);
            System.out.println("Server has been set on port " + port);
        }catch (IOException e){
            System.out.println("Unable to start a server");
        }
        if (!Objects.isNull(access)){
            connected = new ArrayList<>();
        }
    }
    public void start(){
        System.out.println("Server is running");
        while (!access.isClosed()){
            try{
                if (connected.size() < 10){
                    connected.add(new Assistant(access.accept()));
                    connected.get(connected.size() - 1).start();
                }
            }catch (IOException e){
                System.out.println("Unable to get clients (server may be closed)");
                end();
                break;
            }
        }
    }
    public void end(){
        try{
            access.close();
            System.out.println("Server successfully has been closed");
        }catch (IOException e){
            System.out.println("Server has been already closed(?)"); //Not sure
        }
    }
}
