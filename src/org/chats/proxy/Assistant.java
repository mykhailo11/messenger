package org.chats.proxy;

import org.chats.server.Status;
import org.chats.messenger.Fields;
import org.chats.messenger.User;
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
public class Assistant{

    private static Mongol mongo;
    private String username;
    private boolean verified;
    private String connection;
    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean requested;
    
    /**
     * basic constructor
     * @param c - socket-client
     */
    public Assistant(Socket c){
        client = c;
        streamInit();
        verified = false;
        requested = false;
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
                out.writeObject(Responses.CONNECTION);
                out.writeObject(connection);
                out.flush();
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
    public String getConnection(){
        return connection;
    }
    public String getUsername(){
        return username;
    }
    public boolean isVerified(){
        return verified;
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
                register();
            }else if (intend.equals(Commands.ADDMESSAGE) && verified){
                addMessage();
            }else if (intend.equals(Commands.GETMESSAGES) && verified){
                sendMessages();
            }else if (intend.equals(Commands.DISCONNECT)){
                mongo.updateData(Mongo.userQuery(username), Mongo.userUpdateState(Status.OFFLINE), Mongo.USERSCOLL);
                connection = Status.OFFLINE;
                try{
                    out.writeObject(Responses.CONNECTION);
                    out.writeObject(connection);
                    out.flush();
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }else{
                System.out.println("Incorrect request");
            }
            requested = false;
    }
    /**
     * Method adds user to the system
     */
    //THE METHOD IS EXPERIMENTAL
    private void register(){
        try{
            username = (String)in.readObject();
            if (mongo.getData(Mongo.userQuery(username), Mongo.USERSCOLL).size() == 0){
                mongo.addData(new Document().append(User.USERNAME, username).append(User.PASSWORD, (String)in.readObject()).append(User.PHONENUM, (String)in.readObject()), Mongo.USERSCOLL);
                verified = mongo.getData(Mongo.userQuery(username), Mongo.USERSCOLL).size() == 1;
                if (verified){
                    mongo.updateData(Mongo.userQuery(username), Mongo.userUpdateState(Status.ONLINE), Mongo.USERSCOLL);
                }
            }
            System.out.println(verified);
            out.writeObject(Responses.REGISTRATION);
            out.writeObject(verified);
            out.flush();
        }catch (ClassNotFoundException e){
            System.out.println("Invalid info");
        }catch (IOException e){
            connection = Status.OFFLINE;
            System.out.println(e.getMessage());
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
            }else{
                mongo.updateData(Mongo.userQuery(username), Mongo.userUpdateState(Status.OFFLINE), Mongo.USERSCOLL);
            }
            System.out.println(verified);
            out.writeObject(Responses.VERIFICATION);
            out.writeObject(verified);
            out.flush();
            if (verified){
                sendMessages();
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
            }else{
                out.writeObject(Responses.ERROR);
            }
            out.flush();
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
                    if (!m.get(Fields.SENDER).equals(username)){
                        mongo.updateData(m, Mongo.messUpdateState(MessState.DELIVERED), Mongo.MESSCOLL);
                    }
                }catch (IOException e){
                    connection = Status.OFFLINE;
                    System.out.println(e.getMessage());
                }
            } );
            out.writeObject(Responses.PACKEND);
            out.flush();
        }catch (IOException e){
            connection = Status.OFFLINE;
            System.out.println(e.getMessage());
        }
    }
    /**
     * Additional method for notifier that sends new messages
     */
    private void sendNew(){

        ArrayList<Document> newmess = mongo.getData(Mongo.undeliveredMessages(username), Mongo.MESSCOLL);

        if (!newmess.isEmpty()){
            try{
                System.out.println("Extra");
                out.writeObject(Responses.NEWMESSPACK);
                newmess.forEach(m -> {
                    try{
                        out.writeObject(Responses.MESSAGE);
                        out.writeObject(m);
                        mongo.updateData(m, Mongo.messUpdateState(MessState.DELIVERED), Mongo.MESSCOLL);
                    }catch (IOException e){
                        System.out.println(e.getMessage());
                        connection = Status.OFFLINE;
                    }
                });
                out.writeObject(Responses.PACKEND);
                out.flush();
            }catch (IOException e){
                System.out.println(e.getMessage());
                connection = Status.OFFLINE;
            }
        }            
    }
    /**
     * Thread main method (loop). Request listener
     */
    public void listen(){
            
        Object intend;

        try{
            do{
                intend = in.readObject();
            }while (Objects.isNull(intend));
            System.out.println("Got message: " + intend);
            requested = true;
            process((String)intend);
        }catch (IOException e){
            connection = Status.OFFLINE;
            System.out.println("Unable to communicate with cient");
        }catch (ClassNotFoundException e){
            System.out.println("Unable to define command");
        }
    }
    /**
     * The method allows to send extra messages without
     * client request. Should be implemented as a thread
     */
    public void checkForNew(){
        if (verified && !requested){
            sendNew();
        }
        try{
            Thread.sleep(100);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}
