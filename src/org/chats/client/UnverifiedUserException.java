package org.chats.client;

public class UnverifiedUserException extends Exception{

    private static final long serialVersionUID = 1L;
    
    public UnverifiedUserException(String mess){
        super(mess);
    }
}
