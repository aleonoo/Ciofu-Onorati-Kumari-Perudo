package src.server.game;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class Player {
    Socket socket;
    String nickname;
    LinkedList<Die> dice = new LinkedList<>();
    String lastInteraction = "";

    public Player(Socket socket, String nickaname){
        this.socket = socket;
        this.nickname = nickaname;
        setup();
    }

    public void setup(){
        for(int i = 0; i<5; i++){
            dice.add(new Die());
        }
    }
    public void removeAllDice() {
        this.dice = new LinkedList<>();
    }

    public void setLastInteraction(String lastInteraction) {
        this.lastInteraction = lastInteraction;
    }
    public String getLastInteraction() {
        return lastInteraction;
    }

    public LinkedList<Die> getDice(){
        return dice;
    }

    public String getDiceString(){
        StringBuilder temp = new StringBuilder();

        for(Die die : dice){
            temp.append(die.getValue()).append(" ");
        }

        return temp.toString();
    }
    public boolean hasDice(){
        return !dice.isEmpty();
    }

    public Socket getSocket() {
        return socket;
    }
    public String getNickname() {
        return nickname;
    }
    public void sendToThis(String message) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeUTF(message);
    }
}
