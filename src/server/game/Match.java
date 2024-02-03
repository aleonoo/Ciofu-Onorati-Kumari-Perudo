package src.server.game;

import java.io.IOException;

public class Match implements Runnable{

    Bet currentBet = null;
    Lobby lobby;
    boolean hasFinished = false;

    public Match(Lobby lobby){
        this.lobby = lobby;
    }

    @Override
    public void run(){
        try{
            int playersAlive = lobby.getPlayers().size();
            while(!hasFinished){
                for(int i = 0; i<lobby.getPlayers().size(); i++){
                    Player player = lobby.getPlayers().get(i);

                    if(player.hasDice()){
                        if(lobby.getPlayers().size() == 1 && playersAlive == 1){
                            lobby.sendToAll(player.getNickname() + " won.");
                        }

                        if(currentBet == null){
                            while(true){
                                player.sendToThis("StartBet");

                                startWait();

                                String interaction = player.getLastInteraction();

                                String[] bet = interaction.split(";");

                                int dieValue = Integer.parseInt(bet[0]);
                                int dieNumber = Integer.parseInt(bet[1]);

                                if(setStartBet(player, dieValue, dieNumber)){
                                    break;
                                }
                            }
                        }
                        else{
                            while (true){
                                player.sendToThis("TakeAction");

                                startWait();

                                String action = player.getLastInteraction();

                                if(action.equals("1")){

                                    boolean doubt = doubt();

                                    if(doubt){

                                        lobby.sendToAll(currentBet.getPlayer().getNickname() + " lost a die.");
                                        currentBet.getPlayer().getDice().remove();

                                        if(!currentBet.getPlayer().hasDice()){
                                            lobby.sendToAll(currentBet.getPlayer().getNickname() + " lost the game.");
                                            playersAlive--;
                                        }
                                    }
                                    else{

                                        lobby.sendToAll(player.getNickname() + " lost a die.");
                                        player.getDice().remove();

                                        if(!player.hasDice()){
                                            lobby.sendToAll(player.getNickname() + " lost the game.");
                                            playersAlive--;
                                        }

                                    }

                                    currentBet = null;
                                }
                                else if(action.equals("2")){
                                    while(true){
                                        player.sendToThis("NewBet");

                                        startWait();

                                        String choice = player.getLastInteraction();

                                        if(choice.equals("1")){
                                            while(true){

                                                player.sendToThis("NewDiceValue");

                                                startWait();

                                                int newDiceValue = Integer.parseInt(player.getLastInteraction());

                                                if(setNewDiceValue(player, newDiceValue)){
                                                    break;
                                                }
                                            }
                                        }
                                        else if(choice.equals("2")){
                                            while(true){
                                                player.sendToThis("NewDiceNumber");

                                                startWait();

                                                int newDiceNumber = Integer.parseInt(player.getLastInteraction());

                                                if(setNewDiceNumber(player, newDiceNumber)){
                                                    break;
                                                }
                                            }
                                        }
                                        else{
                                            player.sendToThis("Not a choice.");
                                        }
                                    }

                                }
                                else{
                                    player.sendToThis("Not a choice.");
                                }
                            }

                        }


                    }

                }
            }
        }
        catch(Exception e){
            System.out.println("Closing match.");
        }
    }

    public void startWait() throws InterruptedException {
        synchronized (this){
            this.wait();
        }
    }
    public void stopWait(){
        synchronized (this){
            this.notify();
        }
    }

    private boolean setStartBet(Player player, int diceValue, int diceNumber) throws IOException {
        if (diceValue >= 2 && diceValue <= 6) {
            if (diceNumber <= 0) {
                player.sendToThis("Not a viable dice number, must be greater than 0.");
                return false;
            }
            else {
                this.currentBet = new Bet(player, diceValue, diceNumber);
                return true;
            }
        }
        else {
            player.sendToThis("Not a viable dice value, must be greater than 2 and lower than 6.");
            return false;
        }
    }

    private boolean setNewDiceValue(Player player, int newDiceValue) throws IOException {
        if (newDiceValue >= 2 && newDiceValue <= 6 || newDiceValue > this.currentBet.getDieValue()) {
            this.currentBet = new Bet(player, newDiceValue, this.currentBet.getDieNumber());
            return true;
        }
        else {
            player.sendToThis("Not a viable value, must be greater than 2 and lower than 6. It also must be bigger than the current bet's dice value (" + this.currentBet.getDieValue() + ").");
            return false;
        }
    }

    private boolean setNewDiceNumber(Player player, int newDiceNumber) throws IOException {
        if (newDiceNumber <= currentBet.getDieNumber()) {
            player.sendToThis("Not a viable value, must be  bigger than the current bet's dice number (" + currentBet.getDieNumber() + ")");
            return false;
        } else {
            this.currentBet = new Bet(player, this.currentBet.getDieValue(), newDiceNumber);
            return true;
        }
    }

    private boolean doubt() throws IOException {
        int count = 0;

        for(Player player : lobby.getPlayers()){
            for(Die die : player.getDice()){
                if(die.getValue() == currentBet.getDieValue()){
                    count++;
                }
            }
        }

        lobby.sendToAll("");
        lobby.sendToAll("Dices with value (" + currentBet.getDieValue() + ") found: " + count);
        lobby.sendToAll("");

        return currentBet.getDieNumber() > count;
    }

}
