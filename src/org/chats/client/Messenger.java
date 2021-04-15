package org.chats.client;

import org.chats.server.Status;
import org.chats.server.Commands;
import org.chats.server.Responses;

import java.net.Socket;
import java.util.Objects;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import org.bson.Document;

/**
 * Client class. Implements messenger logics
 */
public class Messenger{

    private String username;
    private Socket user;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String state;
    private boolean verified;

    private boolean requested;
    
    /**
     * Basic messenger constructor
     * @param host - server IP address
     * @param port - server port
     * @throws UnverifiedUserException
     */
    public Messenger(String host, int port){
        connectToAccessor(host, port);
        state = Status.ONLINE;
        requested = false;
        verified = false;
    }
    /**
     * Method initializes connection
     */
    private void connectToAccessor(String host, int port){
        try{
            user = new Socket(host, port);
            System.out.println("Connection initiated");
            out = new ObjectOutputStream(user.getOutputStream());
            in = new ObjectInputStream(user.getInputStream());
            //out = new ObjectOutputStream(user.getOutputStream());
            System.out.println("Streams initialized");
        }catch (IOException e){
            System.out.println("Unable to establish data stream");
        }
    }
    /**
     * Method verifies user
     * @param uname - username
     * @param pass - password
     */
    public void verify(String uname, String pass){
        try{
            System.out.println("Sending request for verification");
            username = uname;
            out.writeObject(Commands.VERIFY);
            out.writeObject(username);
            out.writeObject(pass);
            out.flush();
            requested = true;
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    /**
     * Method sends message to the server to be
     * delivered to the desired user
     * @param mess - message (BSON document)
     */
    public void sendMessage(Document mess){
        if (verified && !requested){
            try{
                System.out.println("Sending message");
                out.writeObject(Commands.ADDMESSAGE);
                out.writeObject(mess);
                out.flush();
                requested = true;
            }catch (IOException e){
                System.out.println("Unable to send message");
            }
        }
    }
    /**
     * Method makes a request for all available 
     * messages related with the currentt user
     */
    public void getMessages(){
        if (verified && !requested){
            try{
                System.out.println("Sending request for messages");
                out.writeObject(Commands.GETMESSAGES);
                out.flush();
                requested = true;
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }
    /**
     * Method that handles server responses after
     * requests
     * @param response - server response
     */
    private void process(String response){
        if (response.equals(Responses.CONNECTION)){
            try{
                verified = ((String)in.readObject()).equals(Status.ONLINE);
            }catch (IOException e){
                state = Status.OFFLINE;
                System.out.println(e.getMessage());
            }catch (ClassNotFoundException e){
                System.out.println("Unknown protocol detected");
            }
        }else if (response.equals(Responses.MESSAGEPACK)){
            //Saving array of messages
        }else if (response.equals(Responses.NEWMESS)){
            //Adding message to the existing array
        }else if (response.equals(Responses.ADDED)){
            System.out.println("Message successfuly has been sent");
        }
    }
    /**
     * Server listener. Checks for responses if any request has
     * been sent
     */
    public void listener(){
        if (requested && state.equals(Status.ONLINE)){

            Object response;

            System.out.println("Waiting for response");
            try{
                do{
                    response = in.readObject();
                }while (Objects.isNull(response));
                System.out.println("Got response:" + response);
                process((String)response);
                requested = false;
            }catch (IOException e){
                state = Status.OFFLINE;
                System.out.println(e.getMessage());
            }catch (ClassNotFoundException e){
                System.out.println("Unknown protocol detected");
            }
        }
    }
    /**
     * Method "asks" the server to close connection
     */
    public void end(){
        try{
            out.writeObject(Commands.DISCONNECT);
            out.flush();
            user.close();
            state = Status.OFFLINE;
            System.out.println("Client has been closed successfully");
        }catch (IOException e){
            System.out.println("Client has been already closed(?)");
        }
    }
}
