package org.chats.proxy;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import org.bson.Document;
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
    public synchronized boolean verifyUser(Document info){
        
        ArrayList<Document> res = new ArrayList<>();

        mongo.getDatabase(mongodb).getCollection(collusers).find(info).into(res);
        return (res.size() == 1);
    }
    public synchronized void addUser(Document info){
        mongo.getDatabase(mongodb).getCollection(collusers).insertOne(info);
    }
    public synchronized ArrayList<Document> getMessages(String username){

        ArrayList<Document> res = new ArrayList<>();

        mongo.getDatabase(mongodb).getCollection(collmessages).find(Filters.or(Filters.eq(Field.SENDER, username), Filters.eq(Field.RECIEVER, username))).into(res);
        if (!res.isEmpty()){
            return res;
        }else{
            return new ArrayList<>();
        }
    }
    public void addMessage(Document mess){
        mongo.getDatabase(mongodb).getCollection(collmessages).insertOne(mess);
    }
    public void end(){
        mongo.close();
    }
}
