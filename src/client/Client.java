package src.client;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        //TODO: startNetwork in ClientNetwork
        new Thread(new ClientNetwork("0", 12345)).start();
    }
}
