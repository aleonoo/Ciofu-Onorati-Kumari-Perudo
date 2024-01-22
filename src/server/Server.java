package src.server;

import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {
        //TODO: startNetwork in ServerNetwork
        new Thread(new ServerNetwork(12345)).start();
    }
}
