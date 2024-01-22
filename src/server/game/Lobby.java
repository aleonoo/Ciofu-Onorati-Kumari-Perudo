package src.server.game;

import src.server.ClientMessageThread;
import src.server.ServerNetwork;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class Lobby implements Runnable{

    LinkedList<Player> players = new LinkedList<>();

    @Override
    public void run() {
        while(true){

        }
    }

    public void joinLobby(Player newPlayer) throws IOException {
        players.add(newPlayer);
        sendToAll(newPlayer.getNickname() + " joined the lobby.");
        new Thread(new ClientMessageThread(newPlayer, this)).start(); //Thread per gestire i messaggi di questo giocatore
    }

    public LinkedList<Player> getPlayers() {
        return players;
    }

    public boolean isFull(){
        return players.size() >= 6;
    }

    //Manda messaggio a tutti i client
    public void sendToAll(String message) throws IOException {
        for(Player p : players){
            DataOutputStream outputStream = new DataOutputStream(p.getSocket().getOutputStream());
            outputStream.writeUTF(message);
        }
    }
}
