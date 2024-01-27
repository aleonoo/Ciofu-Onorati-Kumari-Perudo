package src.server.game;

public class Bet {
    private Player player;
    private int dieValue;
    private int dieNumber;

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
