package org.chats.proxy;

import org.chats.server.Mongo;

public class Main {

    private static final int PORT = 50000;
    

    public static void main(String[] args) {
        Accessor server = new Accessor(PORT);
        Assistant.setMongo(new Mongol(Mongo.MONGOUSER, Mongo.MONGOPASS, Mongo.MONGOHOST, Mongo.MONGOPORT, Mongo.MONGODB));
        server.start();
    }
}
