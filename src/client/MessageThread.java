package src.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class MessageThread implements Runnable{

    Socket socket;

    public MessageThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        while(true){

            try {
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                System.out.println(inputStream.readUTF());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
