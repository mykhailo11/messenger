package org.chats.proxy;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.util.ArrayList;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 * Class intended for simple communication with MongoDB server
 */
public class Mongol{
    
    protected MongoClient mongo;
    protected String mongouser;
    private String mongopass;
    private String mongohost;
    private String mongoport;
    protected String mongodb;

    /**
     * Basic constructor
     * @param muser - database user
     * @param mpass - password
     * @param mhost - MongoDB instance IP address
     * @param mport - MongoDB instance port
     * @param mdb - database name
     */
    public Mongol(String muser, String mpass, String mhost, String mport, String mdb){
        mongouser = muser;
        mongopass = mpass;
        mongohost = mhost;
        mongoport = mport;
        mongodb = mdb;
        mongo = new MongoClient(new MongoClientURI("mongodb://" + mongouser + ":" + mongopass + "@" + mongohost + ":" + mongoport + "/?authSource=" + mongodb));
        System.out.println("Mongol is active");
    }
    /**
     * Method that adds document to the MongoDB database
     * @param doc - document to be added
     * @param coll - collection the document to be added
     */
    public synchronized void addData(Document doc, String coll){
        mongo.getDatabase(mongodb).getCollection(coll).insertOne(doc);
    }
    /**
     * Method retrieves data from the database
     * @param query - result matches this specific filters
     * @param coll - collection the data to be retrieved from
     * @return Returns list of documents that match query
     */
    public synchronized ArrayList<Document> getData(Bson query, String coll){
        
        ArrayList<Document> docs = new ArrayList<>();

        mongo.getDatabase(mongodb).getCollection(coll).find(query).into(docs);
        return docs;
    }
    /**
     * Method updates data in the specified collection
     * @param query - all the updating documents must match this filters
     * @param doc - update statements
     * @param coll - collection the documents to be updated
     */
    public synchronized void updateData(Bson query, Bson doc, String coll){
        mongo.getDatabase(mongodb).getCollection(coll).updateMany(query, doc);
    }
    /**
     * Method closes connection
     */
    public void end(){
        mongo.close();
    }
}
