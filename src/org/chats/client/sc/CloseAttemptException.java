package org.chats.client.sc;

public class CloseAttemptException extends Exception{

    private static final long serialVersionUID = 1L;
    
    public CloseAttemptException(String mess){
        super(mess);
    }
}
