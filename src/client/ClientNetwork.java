package src.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientNetwork implements Runnable {
    //TODO: method: startNertwork
    //TODO: create ClientSocket
    //TODO: connect to server
    //TODO: do what server asks

    public static volatile boolean canStartGame = false;
    public static volatile boolean canStartBet = false;
    public static volatile boolean canTakeAction = false;
    public static volatile boolean canNewBet = false;
    public static volatile boolean canNewDieValue = false;
    public static volatile boolean canNewDieNumber = false;

    Socket socket;
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public ClientNetwork(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
    }

    @Override
    public void run() {
        try{
            while(!socket.isClosed()){
                System.out.println("Connesso");

                System.out.println("Inserisci nickname:");

                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(in.readLine());

                new Thread(new MessageThread(socket)).start();

                while(true){

                    if(canStartGame){
                        System.out.println("Start the game? Y/N");
                        System.out.println();
                        String choice = in.readLine();

                        if(choice.equals("y") || choice.equals("Y")){
                            outputStream.writeUTF("canStart");
                        }
                        else{
                            outputStream.writeUTF("nope");
                        }
                    }
                    else if(canStartBet){
                        System.out.println("[--START BET--]");
                        System.out.println();
                        System.out.println("Insert Die Value: ");

                        String dieValue = in.readLine();

                        System.out.println("Insert Die Number: ");

                        String dieNumber = in.readLine();

                        outputStream.writeUTF("startBet:" + dieValue + ";" + dieNumber);
                    }
                    else if (canTakeAction) {
                        System.out.println("[--BET OR DOUBT--]");
                        System.out.println();
                        System.out.println("1. Change bet");
                        System.out.println("2. Doubt");
                        String choice = in.readLine();

                        outputStream.writeUTF("takeAction:" + choice);
                    }
                    else if (canNewBet) {
                        System.out.println("[--NEW BET--]");
                        System.out.println();
                        System.out.println("1. Change die value");
                        System.out.println("2. Change die number");

                        String choice = in.readLine();

                        outputStream.writeUTF("newBet:" + choice);
                    }
                    else if (canNewDieValue) {
                        System.out.println("[--NEW DIE VALUE--]");
                        System.out.println();
                        System.out.println("Insert new die value: ");
                        String newDieValue = in.readLine();

                        outputStream.writeUTF("newDieValue:" + newDieValue);
                    }
                    else if (canNewDieNumber) {
                        System.out.println("[--NEW DIE NUMBER--]");
                        System.out.println();
                        System.out.println("Insert new die number: ");
                        String newDieNumber = in.readLine();

                        outputStream.writeUTF("newDieNumber:" + newDieNumber);
                    }

                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
