package org.chats.server;

/**
 * Class represents server response types
 * for easier client perception
 */
public class Responses {

    public static final String ADDED = "|added|";
    public static final String CONNECTION = "|connection|";
    public static final String MESSAGEPACK = "|pack|";
    public static final String MESSAGE = "|message|";
    /**
     * Special response type. Server can send this type of messages
     * without client initialization
     */
    public static final String NEWMESS = "|newmessage|";
    
    private Responses(){}

}