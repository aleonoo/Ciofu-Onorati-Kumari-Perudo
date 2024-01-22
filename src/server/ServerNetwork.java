package src.server;

import src.server.game.Lobby;
import src.server.game.Player;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class ServerNetwork implements Runnable{
    //TODO: method: startNertwork
    //TODO: create ServerSocket -> wait for connection
    //TODO: on connection create Connection

    ServerSocket serverSocket;
    public static LinkedList<Player> players = new LinkedList<>();
    public static LinkedList<Lobby> lobbies = new LinkedList<>();

    public ServerNetwork(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("Nuovo Client");

                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                String nickname = inputStream.readUTF(); //Prendiamo nickname

                Player player = new Player(socket, nickname);

                players.add(player); //Aggiugniamo alla lista il nuovo giocatore
                System.out.println(player.diceValues());

                if(lobbies.isEmpty()){
                    player.sendToThis("Nuova Lobby creata.");

                    Lobby lobby = new Lobby();
                    lobby.joinLobby(player);

                    lobbies.add(lobby);
                }
                else{
                    boolean isInLobby = false;

                    for(Lobby lobby : lobbies){
                        if(lobby.isFull()){
                            player.sendToThis("Lobby is full.");
                        }
                        else{
                            isInLobby = true; //è in una lobby
                            player.sendToThis("Joined a Lobby.");
                            lobby.joinLobby(player);
                            break;
                        }
                    }

                    if(!isInLobby){ //Se non è in una lobby
                        player.sendToThis("Nuova Lobby creata.");

                        Lobby lobby = new Lobby();
                        lobby.joinLobby(player);

                        lobbies.add(lobby);
                    }

                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
