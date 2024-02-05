package src.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientNetwork implements Runnable {
    public static volatile boolean canStartGame = false;
    public static volatile boolean canStartBet = false;
    public static volatile boolean canTakeAction = false;
    public static volatile boolean canNewBet = false;
    public static volatile boolean canNewDieValue = false;
    public static volatile boolean canNewDieNumber = false;
    private final Socket socket;
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public ClientNetwork(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
    }

    @Override
    public void run() {
        try{
            while(!socket.isClosed()){
                System.out.println("Connected to a Server!");

                System.out.println("Type nickname:");

                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(in.readLine());

                new Thread(new MessageThread(socket)).start();

                //Handle commands and send a response
                while(true){
                    if(canStartGame){ //Asking host if they want to start the game
                        System.out.println("Start the game? Y/N");
                        String choice = in.readLine();

                        if(choice.equals("y") || choice.equals("Y")){
                            outputStream.writeUTF("canStart");
                        }
                        else{
                            outputStream.writeUTF("nope");
                        }

                        canStartGame = false;
                    }
                    else if(canStartBet){ //Initial bet of the match
                        System.out.println("[--START BET--]");
                        System.out.println();
                        System.out.println("Insert Die Value: ");

                        String dieValue = in.readLine();

                        System.out.println("Insert Die Number: ");

                        String dieNumber = in.readLine();

                        outputStream.writeUTF("startBet:" + dieValue + ";" + dieNumber);

                        canStartBet = false;
                    }
                    else if (canTakeAction) { //Ask if the player wants to change the bet or doubt the current one
                        System.out.println("[--BET OR DOUBT--]");
                        System.out.println();
                        System.out.println("1. Doubt");
                        System.out.println("2. Change Bet");
                        String choice = in.readLine();

                        outputStream.writeUTF("takeAction:" + choice);

                        canTakeAction = false;
                    }
                    else if (canNewBet) { //Ask how the player wants to change the bet
                        System.out.println("[--NEW BET--]");
                        System.out.println();
                        System.out.println("1. Change die value");
                        System.out.println("2. Change die number");

                        String choice = in.readLine();

                        outputStream.writeUTF("newBet:" + choice);

                        canNewBet = false;
                    }
                    else if (canNewDieValue) { //Ask the new die value
                        System.out.println("[--NEW DIE VALUE--]");
                        System.out.println();
                        System.out.println("Insert new die value: ");
                        String newDieValue = in.readLine();

                        outputStream.writeUTF("newDieValue:" + newDieValue);

                        canNewDieValue = false;
                    }
                    else if (canNewDieNumber) { //Ask the new die number
                        System.out.println("[--NEW DIE NUMBER--]");
                        System.out.println();
                        System.out.println("Insert new die number: ");
                        String newDieNumber = in.readLine();

                        outputStream.writeUTF("newDieNumber:" + newDieNumber);

                        canNewDieNumber = false;
                    }

                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
