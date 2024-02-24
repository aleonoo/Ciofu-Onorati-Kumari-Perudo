package src.server.game;

import java.io.IOException;

public class Match implements Runnable{

    private Bet currentBet = null;
    private Lobby lobby;
    private boolean hasFinished = false;

    public Match(Lobby lobby){
        this.lobby = lobby;
    }

    @Override
    public void run(){
        try{
            int playersAlive = lobby.getPlayers().size();

            //Until the match hasn't finished (although we forcefully exit this loop using breaks)
            while(!hasFinished){
                for(int i = 0; i<lobby.getPlayers().size(); i++){
                    Player player = lobby.getPlayers().get(i);

                    //If the player has some dice it means they can still play
                    if(player.hasDice()){

                        lobby.sendToAll("Turn of: " + player.getNickname());
                        player.sendToThis("YOUR DICE: " + player.getDiceString());

                        //Check for end-game condition
                        if(lobby.getPlayers().size() == 1 || playersAlive == 1){
                            lobby.sendToAll(player.getNickname() + " won.");
                            hasFinished = true;
                            break;
                        }

                        if(currentBet == null){
                            while(true){
                                //Ask first bet of the match
                                player.sendToThis("StartBet");

                                startWait();

                                String interaction = player.getLastInteraction();

                                if(interaction == null){
                                    playersAlive--;
                                    break;
                                }

                                //initial string: 5;3 --> 5   3  --> dieValue = 5 and dieNumber = 3

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
                                lobby.sendToAll(currentBet());
                                //Ask player action between doubt or new bet
                                player.sendToThis("TakeAction");

                                startWait();

                                String action = player.getLastInteraction();

                                if(action == null){
                                    playersAlive--;
                                    break;
                                }

                                //Doubt
                                if(action.equals("1")){

                                    lobby.sendToAll(player.getNickname() + " called a Doubt!");
                                    lobby.sendToAll(currentBet());
                                    boolean doubt = doubt();

                                    if(doubt){
                                        lobby.sendToAll("DOUBT WON!");
                                        lobby.sendToAll(currentBet.getPlayer().getNickname() + " lost a die.");
                                        currentBet.getPlayer().getDice().remove();

                                        if(!currentBet.getPlayer().hasDice()){
                                            lobby.sendToAll(currentBet.getPlayer().getNickname() + " lost the game.");
                                            playersAlive--;
                                        }
                                    }
                                    else{
                                        lobby.sendToAll("DOUBT LOST!");
                                        lobby.sendToAll(player.getNickname() + " lost a die.");
                                        player.getDice().remove();

                                        if(!player.hasDice()){
                                            lobby.sendToAll(player.getNickname() + " lost the game.");
                                            playersAlive--;
                                        }

                                    }

                                    for(Player p : lobby.getPlayers()){
                                        for(Die d : p.dice){
                                            d.roll();
                                        }
                                    }

                                    currentBet = null;
                                    break;
                                }
                                //New bet
                                else if(action.equals("2")){
                                    while(true){
                                        player.sendToThis("NewBet");

                                        startWait();

                                        String choice = player.getLastInteraction();

                                        if(choice == null){
                                            playersAlive--;
                                            break;
                                        }

                                        if(choice.equals("1")){
                                            while(true){
                                                player.sendToThis("NewDiceValue");

                                                startWait();

                                                String interaction = player.getLastInteraction();

                                                if(interaction == null){
                                                    playersAlive--;
                                                    break;
                                                }

                                                int newDiceValue = Integer.parseInt(interaction);

                                                if(setNewDiceValue(player, newDiceValue)){
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                        else if(choice.equals("2")){
                                            while(true){
                                                player.sendToThis("NewDiceNumber");

                                                startWait();

                                                String interaction = player.getLastInteraction();

                                                if(interaction == null){
                                                    playersAlive--;
                                                    break;
                                                }

                                                int newDiceNumber = Integer.parseInt(interaction);

                                                if(setNewDiceNumber(player, newDiceNumber)){
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                        else{
                                            player.sendToThis("Not a choice.");
                                        }
                                    }
                                    lobby.sendToAll("Bet changed!");
                                    lobby.sendToAll(currentBet());
                                    lobby.sendToAll("");
                                    break;
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

    public String currentBet(){
        return "Current Bet: Die Value (" + currentBet.getDieValue() + ") and Die Number(" + currentBet.getDieNumber() +")";
    }

    public boolean hasFinished() {
        return hasFinished;
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
