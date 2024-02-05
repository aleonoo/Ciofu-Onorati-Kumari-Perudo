package src.server;

import src.server.game.Lobby;
import src.server.game.Player;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class ServerNetwork implements Runnable{
    private final ServerSocket serverSocket;
    public static LinkedList<Player> players = new LinkedList<>();
    public static LinkedList<Lobby> lobbies = new LinkedList<>();

    public ServerNetwork(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {

        System.out.println("[--SERVER--]");
        System.out.println("Info logs will be displayed");

        try {
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("A client has connected.");

                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                String nickname = inputStream.readUTF();

                Player player = new Player(socket, nickname);

                players.add(player);
                System.out.println(player.getDiceString());

                if(lobbies.isEmpty()){
                    player.sendToThis("New Lobby created.");

                    Lobby lobby = new Lobby(player);
                    lobby.joinLobby(player);

                    new Thread(lobby).start();

                    lobbies.add(lobby);
                }
                else{
                    boolean isInLobby = false;

                    for(Lobby lobby : lobbies){
                        if(lobby.isFull()){
                            player.sendToThis("Lobby is full.");
                        }
                        else if(lobby.getMatch() != null){
                            player.sendToThis("Match in this lobby already started.");
                        }
                        else{
                            isInLobby = true;
                            player.sendToThis("Joined a Lobby.");
                            lobby.joinLobby(player);
                            break;
                        }
                    }

                    if(!isInLobby){
                        player.sendToThis("New Lobby created.");

                        Lobby lobby = new Lobby(player);
                        lobby.joinLobby(player);

                        new Thread(lobby).start();

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
