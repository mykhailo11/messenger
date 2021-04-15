package org.chats.proxy;

import org.chats.server.Mongo;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    private static final int PORT = 50000;
    
    public static void main(String[] args) {
        
        Assistant a;
        Mongol m = new Mongol(Mongo.MONGOUSER, Mongo.MONGOPASS, Mongo.MONGOHOST, Mongo.MONGOPORT, Mongo.MONGODB);
        int x = 0;

        try (ServerSocket s = new ServerSocket(PORT)){
            Assistant.setMongol(m);
            a = new Assistant(s.accept());
            a.run();
            while (x < 1000000000){
                x+=1;
            }
            m.end();
            s.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
