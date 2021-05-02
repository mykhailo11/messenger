package org.chats.proxy;

import org.chats.server.Mongo;

public class Main {

    private static final int PORT = 50000;
    
    public static void main(String[] args) {
        
        Accessor server;
        Mongol mongol = Mongol.init(Mongo.MONGOUSER, Mongo.MONGOPASS, Mongo.MONGOHOST, Mongo.MONGOPORT, Mongo.MONGODB);

        server = new Accessor(PORT);
        Assistant.setMongol(mongol);
        server.start();
        mongol.end();
    }
}
