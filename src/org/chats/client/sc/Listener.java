package org.chats.client.sc;

import org.chats.server.Status;

public class Listener extends Thread{
    
    private Messenger messenger;
    private Subscriber subscriber;

    public Listener(Messenger mess, Subscriber sub){
        messenger = mess;
        subscriber = sub;
    }
    @Override
    public void run(){
        while (messenger.getConnection().equals(Status.ONLINE)){
            messenger.listener();
            subscriber.alarm();
        }
        try{
            messenger.end();
        }catch (CloseAttemptException e){
            System.out.println(e.getMessage());
        }
    }
}
