package org.chats.proxy;

public class Notifier extends Thread{
    
    private Assistant assist;

    public Notifier(Assistant a){
        assist = a;
    }
    @Override
    public void run(){
        assist.checkForNew();
    }
}
