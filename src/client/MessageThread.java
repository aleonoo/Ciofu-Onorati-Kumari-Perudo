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

                //Tell ClientNetwork which command we received
                switch (packet) {
                    case "StartGame" -> ClientNetwork.canStartGame = true;
                    case "StartBet" -> ClientNetwork.canStartBet = true;
                    case "TakeAction" -> ClientNetwork.canTakeAction = true;
                    case "NewBet" -> ClientNetwork.canNewBet = true;
                    case "NewDiceValue" -> ClientNetwork.canNewDieValue = true;
                    case "NewDiceNumber" -> ClientNetwork.canNewDieNumber = true;
                    //If no command was found, simply output it as it is a server message
                    default -> System.out.println(packet);
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
