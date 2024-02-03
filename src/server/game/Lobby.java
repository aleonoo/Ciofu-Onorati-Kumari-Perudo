package src.server.game;

import src.server.ClientMessageThread;
import src.server.ServerNetwork;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class Lobby implements Runnable{

    LinkedList<Player> players = new LinkedList<>();
    Player host = null;
    Match match = null;
    public volatile boolean hasStarted = false;
    public volatile boolean startSent = false;
    public volatile boolean hasFinished = false;

    public Lobby(Player host){
        this.host = host;
    }

    @Override
    public void run() {
        try {
            while(true) {
                if (this.hasStarted && this.match == null) {
                    for(Player player : players){
                        player.removeAllDice();
                        player.setup();
                    }

                    this.match = new Match(this);

                    (new Thread(this.match)).start(); //Start new game.
                }
                else if (this.hasStarted && this.match != null && this.match.hasFinished) {
                    this.hasStarted = false;
                    this.startSent = false;
                    this.match = null;

                    //We ask to restart the game.
                    host.sendToThis("StartGame");
                    this.startSent = true;
                }

                //If there's no players left in the lobby we close it.
                if(this.players.isEmpty()){
                    ServerNetwork.lobbies.remove(this);
                    break;
                }
            }
        }
        catch (Exception e) {
            ServerNetwork.lobbies.remove(this);
        }
    }

    public void joinLobby(Player newPlayer) throws IOException {
        players.add(newPlayer);
        sendToAll(newPlayer.getNickname() + " joined the lobby.");
        new Thread(new ClientMessageThread(newPlayer, this)).start(); //Thread per gestire i messaggi di questo giocatore
    }

    public void leaveLobby(Player player) {
        try{
            player.getSocket().close();
            this.players.remove(player);
            if (player.equals(this.host) && !this.players.isEmpty()) {
                this.host = this.players.getFirst();
                this.startSent = false;
            }

            ServerNetwork.players.remove(player);
            this.sendToAll(player.getNickname() + " left the lobby.");
            if (this.match != null) {
                this.match.stopWait();
            }

            if (!this.isFull() && !hasStarted) {
                this.sendToAll("Waiting for players (" + this.players.size() + "/" + 6 + ")");
            }

            if(startSent && !hasStarted){
                host.sendToThis("Start the game? Y/N");
            }
        }
        catch(IOException ignored){}
    }

    public boolean canStart() {
        return this.players.size() >= 6;
    }

    public LinkedList<Player> getPlayers() {
        return players;
    }
    public boolean isFull(){
        return players.size() >= 6;
    }
    public void sendToAll(String message) throws IOException {
        for(Player p : players){
            DataOutputStream outputStream = new DataOutputStream(p.getSocket().getOutputStream());
            outputStream.writeUTF(message);
        }
    }

    public Match getMatch() {
        return match;
    }
}
