package org.chats.proxy;

public class Listener extends Thread{
    
    private Assistant assist;

    public Listener(Assistant a){
        assist = a;
    }
    @Override
    public void run(){
        assist.listen();
    }
}
