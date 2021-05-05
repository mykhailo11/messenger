package org.chats.proxy;

public class Unit {

    private Listener listen;
    private Notifier note;

    /**
     * Basic ubit constructor. Auto thread starting
     */
    public Unit(Assistant a){
        listen = new Listener(a);
        note = new Notifier(a);
    }
    public void start(){
        listen.start();
        note.start();
    }
    /**
     * Checking whether all the threads are alive
     */
    public boolean isAlive(){
        return listen.isAlive() && note.isAlive();
    }
}
