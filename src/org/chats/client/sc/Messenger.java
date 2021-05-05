package org.chats.client.sc;

import org.chats.server.Status;
import org.chats.messenger.Fields;
import org.chats.server.Commands;
import org.chats.server.MessState;
import org.chats.server.Responses;

import java.net.Socket;
import java.util.Objects;
import java.util.ArrayList;
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
    private ArrayList<Document> messages;
    private ArrayList<String> companions;

    private boolean requested;
    
    /**
     * Basic messenger constructor
     * @param host - server IP address
     * @param port - server port
     * @throws UnverifiedUserException
     */
    public Messenger(String host, int port){
        connectToAccessor(host, port);
        requested = true;
        verified = false;
        listener();
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
    public String getUsername(){
        return username;
    }
    public String getConnection(){
        return state;
    }
    public boolean isVerified(){
        return verified;
    }
    public ArrayList<String> getCompanions(){
        return companions;
    }
    public ArrayList<Document> getPack(){
        return messages;
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
                if (Objects.isNull(messages)){
                    messages = new ArrayList<>();
                }
                System.out.println("Sending message");
                mess.put(Fields.STATE, MessState.QUEUED);
                out.writeObject(Commands.ADDMESSAGE);
                out.writeObject(mess);
                out.flush();
                messages.add(mess);
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
     * Method rewrites message pack, recieved from server
     */
    private void rewritePack(boolean hard){
        if (Objects.isNull(messages) || hard){
            messages = new ArrayList<>();
        }
        try{
            while (!((String)in.readObject()).equals(Responses.PACKEND)){
                Document mess = (Document)in.readObject();
                if (mess.get(Fields.SENDER).equals(username) || hard){
                    mess.put(Fields.STATE, MessState.DELIVERED);
                }
                messages.add(mess);
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }catch (ClassNotFoundException e){
            System.out.println("Cannot define intend");
        }
        getCompanions(hard);
    }
    /**
     * Method defines usernames the current user has conversations with
     */
    private void getCompanions(boolean hard){
        if (Objects.isNull(companions) || hard){
            companions = new ArrayList<>();
        }
        messages.forEach(mess -> {

            String sender = (String)mess.get(Fields.SENDER);
            String reciever = (String)mess.get(Fields.RECIEVER);

            if (!companions.contains(sender) && !sender.equals(username)){
                companions.add(sender);
            }else if (!companions.contains(reciever) && !reciever.equals(username)){
                companions.add(reciever);
            }
        });
    }
    /**
     * Method that handles server responses after
     * requests
     * @param response - server response
     */
    private void process(String response){
        if (response.equals(Responses.CONNECTION)){
            setConnection();
        }else if (verified && (response.equals(Responses.MESSAGEPACK) || response.equals(Responses.NEWMESSPACK))){
            rewritePack(response.equals(Responses.MESSAGEPACK));
        }else if (verified && response.equals(Responses.ADDED)){
            try{
                System.out.println("Message " + in.readObject() + " successfuly has been sent");
            }catch (IOException e){
                System.out.println(e.getMessage());
            }catch (ClassNotFoundException e){
                System.out.println("Cannot get message index");
            }
        }else if (response.equals(Responses.VERIFICATION)){
            setVerified();
        }
    }
    private void setConnection(){
        try{
            state = (String)in.readObject();
            System.out.println("Connection:" + state);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }catch (ClassNotFoundException e){
            state = Status.OFFLINE;
            System.out.println("Undefined type");
        }
    }
    private void setVerified(){
        try{
            verified = (Boolean)in.readObject();
            System.out.println(verified);
        }catch (IOException e){
            state = Status.OFFLINE;
            System.out.println(e.getMessage());
        }catch (ClassNotFoundException e){
            System.out.println("Unknown protocol detected");
        }
    }
    /**
     * Server listener. Checks for responses if any request has
     * been sent. Can be implemented as a thread
     */
    public void listener(){
        //if (requested){

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
        //}
    }
    /**
     * Method "asks" the server to close connection
     */
    public void end() throws CloseAttemptException{
        try{
            out.writeObject(Commands.DISCONNECT);
            out.flush();
            requested = true;
            listener();
            if (state.equals(Status.OFFLINE)){
                user.close();
                System.out.println("Client has been closed successfully");
            }else{
                throw new CloseAttemptException("Unable to close socket: server doesn't respond");
            }
        }catch (IOException e){
            System.out.println("Client has been already closed(?)");
        }
    }
}
