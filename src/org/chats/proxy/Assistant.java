package org.chats.proxy;

import org.chats.server.Status;
import org.chats.server.Commands;
import org.chats.server.Mongo;
import org.chats.server.Responses;
import org.chats.server.MessState;

import java.net.Socket;
import java.util.Objects;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import org.bson.Document;

/**
 * Class that handles client requests. Works in background.
 * Implements messenger server logics
 */
public class Assistant extends Thread{

    private static Mongol mongo;
    private String username;
    private boolean verified;
    private String connection;
    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    /**
     * basic constructor
     * @param c - socket-client
     */
    public Assistant(Socket c){
        client = c;
        streamInit();
        verified = false;
    }
    /**
     * Stream initialization
     */
    private void streamInit(){
        if (!Objects.isNull(client)){
            try{
                out = new ObjectOutputStream(client.getOutputStream());
                in = new ObjectInputStream(client.getInputStream());
                connection = Status.ONLINE;
            }catch (IOException e){
                connection = Status.OFFLINE;
                System.out.println(e.getMessage());
            }
        }
    }
    /**
     * Method sets MongoDB linker for all assistant instances
     * @param mongol - MongoDB linker to be used
     */
    public static void setMongol(Mongol mongol){
        mongo = mongol;
    }
    /**
     * Method that processes client request and defines
     * custom protocol
     * @param intend - initial command
     */
    private void process(String intend){
            if (intend.equals(Commands.VERIFY)){
                verify();
            }else if (intend.equals(Commands.ADDUSER)){
                //Not available yet
            }else if (intend.equals(Commands.ADDMESSAGE) && verified){
                addMessage();
            }else if (intend.equals(Commands.GETMESSAGES) && verified){
                sendMessages();
            }else if (intend.equals(Commands.DISCONNECT)){
                mongo.end();
                mongo.updateData(Mongo.userQuery(username), Mongo.userUpdateState(Status.OFFLINE), Mongo.USERSCOLL);
                connection = Status.OFFLINE;
            }else{
                System.out.println("Incorrect request");
            }
    }
    /**
     * Method verifies client as a messenger user
     */
    private void verify(){
        try{
            username = (String)in.readObject();
            verified = mongo.getData(Mongo.userQuery(username, (String)in.readObject()), Mongo.USERSCOLL).size() == 1;
            if (verified){
                mongo.updateData(Mongo.userQuery(username), Mongo.userUpdateState(Status.ONLINE), Mongo.USERSCOLL);
                out.writeObject(Responses.CONNECTION);
                out.writeObject(connection);
                out.flush();
            }else{
                mongo.updateData(Mongo.userQuery(username), Mongo.userUpdateState(Status.OFFLINE), Mongo.USERSCOLL);
            }
        }catch (ClassNotFoundException e){
            verified = false;
            System.out.println("Unknown protocol detected");
        }catch (IOException e){
            connection = Status.OFFLINE;
            System.out.println(e.getMessage());
        }
    }
    /**
     * Method adds message to the database
     */
    private void addMessage(){
        try{

            Object mess = in.readObject();
            ArrayList<Document> res;

            mongo.addData((Document)mess, Mongo.MESSCOLL);
            mongo.updateData((Document)mess, Mongo.messUpdateState(MessState.QUEUED), Mongo.MESSCOLL);
            res = mongo.getData((Document)mess, Mongo.MESSCOLL);
            if (res.size() == 1){
                out.writeObject(Responses.ADDED);
                out.writeObject(res.get(0).get("_id"));
                out.flush();
            }
        }catch (ClassNotFoundException e){
            System.out.println("Unknown protocol detected");
        }catch (IOException e){
            connection = Status.OFFLINE;
            System.out.println(e.getMessage());
        }
    }
    /**
     * Retrieving all messages related with user and sending them to the client
     */
    private void sendMessages(){

        ArrayList<Document> mess = mongo.getData(Mongo.messRelatedWithUser(username), Mongo.MESSCOLL);

        try{
            out.writeObject(Responses.MESSAGEPACK);
            mess.forEach(m -> {
                try{
                    out.writeObject(Responses.MESSAGE);
                    out.writeObject(m);
                    out.flush();
                    mongo.updateData(m, Mongo.messUpdateState(MessState.DELIVERED), Mongo.MESSCOLL);
                }catch (IOException e){
                    connection = Status.OFFLINE;
                    System.out.println(e.getMessage());
                }
            } );
        }catch (IOException e){
            connection = Status.OFFLINE;
            System.out.println(e.getMessage());
        }
    }
    /**
     * Thread main method (loop). Request listener
     */
    @Override
    public void run(){
        while (connection.equals(Status.ONLINE)){
            
            Object intend;

            try{
                do{
                    intend = in.readObject();
                }while (Objects.isNull(intend));
                System.out.println("Got message: " + intend);
                process((String)intend);
            }catch (IOException e){
                connection = Status.OFFLINE;
                System.out.println("Unable to communicate with cient");
            }catch (ClassNotFoundException e){
                System.out.println("Unable to define command");
            }
        }
        if (!verified){
            System.out.println("Verification error");
        }
        System.out.println("Connection ended");
    }
}
