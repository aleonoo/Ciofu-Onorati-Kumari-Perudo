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
                String message = inputStream.readUTF();

                if (message.equals("canStart")) {
                    if(this.lobby.canStart()){
                        this.lobby.hasStarted = true;
                    }
                    else{
                        this.player.sendToThis("Not enough players.");
                        this.lobby.hasStarted = false;
                    }
                }
                else if(message.contains("startBet:")){
                    player.setLastInteraction(message.replace("startBet:", ""));
                    lobby.getMatch().stopWait();
                }
                else if(message.contains("takeAction:")){
                    player.setLastInteraction(message.replace("takeAction:", ""));
                    lobby.getMatch().stopWait();
                }
                else if(message.contains("newBet:")){
                    player.setLastInteraction(message.replace("newBet:", ""));
                    lobby.getMatch().stopWait();
                }
                else if(message.contains("newDiceValue:")){
                    player.setLastInteraction(message.replace("newDiceValue:", ""));
                    lobby.getMatch().stopWait();
                }
                else if(message.contains("newDiceNumber:")){
                    player.setLastInteraction(message.replace("newDiceNumber:", ""));
                    lobby.getMatch().stopWait();
                }

            }
            catch (IOException e) {
                lobby.leaveLobby(player);
                return;
            }
        }
    }
}
