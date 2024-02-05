package src.server.game;

import src.server.ClientMessageThread;
import src.server.ServerNetwork;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class Lobby implements Runnable{

    private final LinkedList<Player> players = new LinkedList<>();
    //List of players in the lobby
    private Player host;
    //Player that created the lobby, if they quit the next player in the list will be the host
    private Match match = null;
    //This value is null if the match hasn't started, otherwise it contains the instance of the match
    public volatile boolean hasStarted = false;
    //If the match has started or not
    public volatile boolean startSent = false;
    //If the lobby asked the host to start the game or not

    public Lobby(Player host){
        this.host = host;
    }

    @Override
    public void run() {
        try {
            while(true) {
                //If the match has started but match is null then we create a new instance of the match
                if (this.hasStarted && this.match == null) {
                    for(Player player : players){
                        player.removeAllDice();
                        player.setup();
                    }

                    this.match = new Match(this);

                    (new Thread(this.match)).start();
                }
                //If the match has started, match isn't null and the match has finished we ask to start a new one
                else if (this.hasStarted && this.match != null && this.match.hasFinished()) {
                    this.hasStarted = false;
                    this.startSent = false;
                    this.match = null;

                    host.sendToThis("StartGame");
                    this.startSent = true;
                }

                //If no players are connected to the lobby, we close it
                if(this.players.isEmpty()){
                    System.out.println("A lobby closed.");
                    ServerNetwork.lobbies.remove(this);
                    break;
                }
            }
        }
        catch (Exception e) {
            //If somehow the lobby has an error, we close it
            ServerNetwork.lobbies.remove(this);
        }
    }

    public void joinLobby(Player newPlayer) throws IOException {
        newPlayer.setup();
        players.add(newPlayer); //Add player to lobby player list
        sendToAll(newPlayer.getNickname() + " joined the lobby.");
        //Start the thread that manages the client's responses to the server's requests
        new Thread(new ClientMessageThread(newPlayer, this)).start();

        //If the lobby isn't full and the match hasn't started send the amount of players as an info
        if (!this.isFull() && !hasStarted) {
            this.sendToAll("Waiting for players (" + players.size() + "/" + 6 + ")");
        }

        //We ask to start the game each time a player joins the lobby
        if(this.canStart() && !hasStarted && this.startSent){
            host.sendToThis("Start the game? Y/N");
        }

        //If there are enough players and the lobby never asked to start the match, we ask to
        if (this.canStart() && !this.startSent && !this.hasStarted) {
            this.host.sendToThis("StartGame");
            this.startSent = true;
        }
    }

    public void leaveLobby(Player player) {
        try{
            player.getSocket().close(); //Close the player's socket, safety measure.
            this.players.remove(player); //Remove the player from the lobby
            if (player.equals(this.host) && !this.players.isEmpty()) {
                this.host = this.players.getFirst();
                this.startSent = false;
            }

            //Remove players from the server's player list
            ServerNetwork.players.remove(player);
            this.sendToAll(player.getNickname() + " left the lobby.");

            //In case the server was waiting for input from the player, stop waiting and skip the turn
            if (this.match != null) {
                this.match.stopWait();
            }

            //If the lobby isn't full and the match hasn't started send the amount of players as an info
            if (!this.isFull() && !hasStarted) {
                this.sendToAll("Waiting for players (" + this.players.size() + "/" + 6 + ")");
            }

            //If the lobby asked to start but hasn't started we ask the host again
            if(startSent && !hasStarted){
                host.sendToThis("Start the game? Y/N");
            }
        }
        //Let's completely ignore any error
        catch(IOException ignored){}
    }

    //If there are at least 2 players we can ask to start the game
    public boolean canStart() {
        return this.players.size() >= 2;
    }

    //Returns the list of players of this lobby
    public LinkedList<Player> getPlayers() {
        return players;
    }

    //If the lobby is full
    public boolean isFull(){
        return players.size() >= 6;
    }

    //Send a message to all players
    public void sendToAll(String message) throws IOException {
        for(Player p : players){
            DataOutputStream outputStream = new DataOutputStream(p.getSocket().getOutputStream());
            outputStream.writeUTF(message);
        }
    }

    //This lobby's match instance
    public Match getMatch() {
        return match;
    }
}
