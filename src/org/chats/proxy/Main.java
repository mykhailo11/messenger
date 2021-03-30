package org.chats.proxy;

public class Main {

    private static final int PORT = 50000;

    public static void main(String[] args) {
        Accessor server = new Accessor(PORT);
        server.start();
    }
}
