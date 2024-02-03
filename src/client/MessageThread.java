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
                String packet = inputStream.readUTF();

                if(packet.equals("StartGame")){
                    ClientNetwork.canStartGame = true;
                }
                else if(packet.equals("StartBet")){
                    ClientNetwork.canStartBet = true;
                }
                else if (packet.equals("TakeAction")) {
                    ClientNetwork.canTakeAction = true;
                }
                else if (packet.equals("NewBet")) {
                    ClientNetwork.canNewBet = true;
                }
                else if (packet.equals("NewDiceValue")) {
                    ClientNetwork.canNewDieValue = true;
                }
                else if (packet.equals("NewDiceNumber")) {
                    ClientNetwork.canNewDieNumber = true;
                }
                else{
                    System.out.println(packet);
                }

            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
