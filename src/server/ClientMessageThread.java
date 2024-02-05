package src.server;

import src.server.game.Lobby;
import src.server.game.Player;

import java.io.DataInputStream;
import java.io.IOException;

public class ClientMessageThread implements Runnable{

    private final Player player;
    private final Lobby lobby;

    public ClientMessageThread(Player player, Lobby lobby){
        this.player = player;
        this.lobby = lobby;
    }

    @Override
    public void run() {
        try {
            while(true){

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
                else if(message.contains("newDieValue:")){
                    player.setLastInteraction(message.replace("newDieValue:", ""));
                    lobby.getMatch().stopWait();
                }
                else if(message.contains("newDieNumber:")){
                    player.setLastInteraction(message.replace("newDieNumber:", ""));
                    lobby.getMatch().stopWait();
                }
            }
        }
        catch (IOException e) {
            lobby.leaveLobby(player);
        }
    }
}
