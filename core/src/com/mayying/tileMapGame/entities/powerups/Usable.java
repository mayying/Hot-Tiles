package com.mayying.tileMapGame.entities.powerups;

import com.mayying.tileMapGame.entities.Player;

/**
 * Created by User on 18/3/15.
 */
// Interface for all the usable items/power ups
public interface Usable {
    // Standardize this as the method to use for when the power up button is pressed, using the power up
    public void use(Player[] players);
}
