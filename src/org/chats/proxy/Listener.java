package org.chats.proxy;

import org.chats.server.Status;

public class Listener extends Thread{
    
    private Assistant assist;

    public Listener(Assistant a){
        assist = a;
    }
    @Override
    public void run(){
        System.out.println("Listener is running");
        while (assist.getConnection().equals(Status.ONLINE)){
            assist.listen();
        }
        if (!assist.isVerified()){
            System.out.println("Verification error");
        }
        System.out.println("Connection ended");
    }
}
