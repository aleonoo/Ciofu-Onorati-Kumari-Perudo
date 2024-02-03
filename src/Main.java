package src;

import src.client.ClientNetwork;
import src.server.ServerNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) {
        while(true){
            try{
                System.out.println("START MENU");
                System.out.println();
                System.out.println("1. Start Server");
                System.out.println("2. Start Client");
                System.out.println("3. Exit");
                String choice = in.readLine();

                if(choice.equals("1")){
                    new Thread(new ServerNetwork(12345)).start();
                }
                else if(choice.equals("2")){
                    System.out.println("Insert IP:");
                    String ip = in.readLine();
                    new Thread(new ClientNetwork(ip, 12345)).start();
                }
                else{
                    break;
                }
            }
            catch(Exception e){
                System.out.println("IO error");
            }
        }
    }
}
