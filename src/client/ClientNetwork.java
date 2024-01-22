package src.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientNetwork implements Runnable {
    //TODO: method: startNertwork
    //TODO: create ClientSocket
    //TODO: connect to server
    //TODO: do what server asks

    Socket socket;
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public ClientNetwork(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
    }

    @Override
    public void run() {
        try{
            while(!socket.isClosed()){
                System.out.println("Connesso");

                System.out.println("Inserisci nickname:");

                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(in.readLine());

                new Thread(new MessageThread(socket)).start();

                while(true){
                    System.out.println("Invia Messaggio:");
                    outputStream.writeUTF(in.readLine());
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
