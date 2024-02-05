package src.server.game;

public class Bet {
    private Player player; //The player that made the bet
    private int dieValue; //The die's face
    private int dieNumber; //The quantity of dice with the same value

    public Bet(Player player, int dieValue, int dieNumber){
        this.player = player;
        this.dieValue = dieValue;
        this.dieNumber = dieNumber;
    }

    public Player getPlayer() {
        return player;
    }
    public int getDieValue() {
        return dieValue;
    }
    public int getDieNumber() {
        return dieNumber;
    }


}
