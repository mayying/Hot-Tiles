package com.mayying.tileMapGame.entities.powerups;

import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Player;

/**
 * Created by User on 18/3/15.
 */

/**
 *  Inverts all players passed in the parameters.
 */
public class ControlInverter implements Usable {
    @Override
    public void use(Player[] players) {
        for(int i = 0; i < players.length; i++)
            players[i].invert();
    }
}
