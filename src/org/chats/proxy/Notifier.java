package org.chats.proxy;

import org.chats.server.Status;

public class Notifier extends Thread{
    
    private Assistant assist;

    public Notifier(Assistant a){
        assist = a;
    }
    @Override
    public void run(){
        System.out.println("Notifier is running");
        while (assist.getConnection().equals(Status.ONLINE)){
            assist.checkForNew();
        }
        System.out.println("Notifier ended");
    }
}
