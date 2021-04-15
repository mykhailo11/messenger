package org.chats.proxy;

import java.util.ArrayList;
import java.util.Objects;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Hostes class that accepts connections.
 * Provides clients with assistants and manages their
 * life cycle
 */
public class Accessor {

    private ArrayList<Assistant> connected;
    private ServerSocket access;
    
    /**
     * Basic constructor for accessor
     * @param port - listener port on the current machine
     */
    public Accessor(int port){
        setOnPort(port);
    }
    /**
     * Method-helper. Handles server initialization via ServerSocket
     * @param port
     */
    private void setOnPort(int port){
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
    /**
     * Method that listens to connections and provides them
     * with assistants
     */
    public void start(){
        System.out.println("Server is running");
        while (!access.isClosed()){
            try{
                //This limit is optional
                if (connected.size() < 10){
                    System.out.println("Checking for clients");
                    connected.add(new Assistant(access.accept()));
                    connected.get(connected.size() - 1).start();  
                }
            }catch (IOException e){
                System.out.println("Unable to get clients (server is possibly closed)");
                end();
                break;
            }
            //Checking for lazy assistants and punishing them
            checkForLazyAssistants(); 
        }
    }
    /**
     * Method removes unused assistants from list
     */
    private void checkForLazyAssistants(){
        for (Assistant assistant : connected) {
            if (!assistant.isAlive()){
                System.out.println("Lazy assistant detected");
                connected.remove(connected.indexOf(assistant));
            }
        }
    }
    /**
     * Method closes server
     */
    public void end(){
        try{
            access.close();
            System.out.println("Server successfully has been closed");
        }catch (IOException e){
            System.out.println("Server has been already closed(?)"); //Not sure
        }
    }
}
