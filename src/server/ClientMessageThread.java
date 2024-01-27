package src.server;

import src.server.game.Lobby;
import src.server.game.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ClientMessageThread implements Runnable{

    Player player;
    Lobby lobby;

    public ClientMessageThread(Player player, Lobby lobby){
        this.player = player;
        this.lobby = lobby;
    }

    @Override
    public void run() {
        while(true){
            try {
                DataInputStream inputStream = new DataInputStream(player.getSocket().getInputStream());
                lobby.sendToAll(player.getNickname() + ": " + inputStream.readUTF());
            }
            catch (IOException e) {
                ServerNetwork.players.remove(player);
                return;
            }
        }
    }
}
