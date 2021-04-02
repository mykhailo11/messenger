package org.chats.proxy;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.util.ArrayList;
import org.bson.Document;
import org.chats.messenger.Message;
import org.chats.messenger.Field;

public class Mongol{
    
    protected MongoClient mongo;
    protected String mongouser;
    private String mongopass;
    private String mongohost;
    private String mongoport;
    protected String mongodb;

    private String collusers = "users";
    private String collmessages = "messages";

    public Mongol(String muser, String mpass, String mhost, String mport, String mdb){
        mongouser = muser;
        mongopass = mpass;
        mongohost = mhost;
        mongoport = mport;
        mongodb = mdb;
        mongo = new MongoClient(new MongoClientURI("mongodb://" + mongouser + ":" + mongopass + "@" + mongohost + ":" + mongoport + "/?authSource=" + mongodb));
    }
    public synchronized boolean verifyUser(String user, String pass){
        
        ArrayList<Document> res = new ArrayList<>();

        mongo.getDatabase(mongodb).getCollection(collusers).find(new Document().append("user", user).append("pass", pass)).into(res);
        return (res.size() == 1);
    }
    public synchronized void addUser(String user, String pass){
        mongo.getDatabase(mongodb).getCollection(collusers).insertOne(new Document().append("user", user).append("pass", pass));
    }
    public synchronized ArrayList<Message> getMessages(String username){

        ArrayList<Document> res = new ArrayList<>();

        mongo.getDatabase(mongodb).getCollection(collmessages).find(new Document().append(Field.SENDER, username)).into(res);
        if (!res.isEmpty()){
            return convertMessages(res);
        }else{
            return new ArrayList<>();
        }
    }
    private ArrayList<Message> convertMessages(ArrayList<Document> docs){

        ArrayList<Message> res = new ArrayList<>();

        for (Document doc : docs) {

            Message mess = new Message(doc.get(Field.SENDER).toString(), doc.get(Field.RECIEVER).toString());

            mess.setContent(doc.get(Field.CONTENT).toString());
            mess.setState(doc.get(Field.STATE).toString());
            mess.setDate(doc.get(Field.DATE).toString());
            res.add(mess);
        }
        return res;
    }
    public void addMessage(Message mess){
        mongo.getDatabase(mongodb).getCollection(collmessages).insertOne(new Document().append(Field.SENDER, mess.getSender()).append(Field.RECIEVER, mess.getReciever()).append(Field.CONTENT, mess.getContent()).append(Field.DATE, mess.getDate()).append(Field.STATE, mess.getState()));
    }
    public void end(){
        mongo.close();
    }
}
