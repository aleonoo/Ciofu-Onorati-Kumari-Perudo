package src.server.game;

import java.util.Random;

public class Die {

    int value;

    public Die(){
        roll();
    }

    public int getValue() {
        return value;
    }
    public void roll()
    {
        value = new Random().nextInt(2, 7);
    }
}
