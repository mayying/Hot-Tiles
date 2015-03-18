package com.mayying.tileMapGame.entities.powerups;

import com.mayying.tileMapGame.GameWorld;

/**
 * Created by User on 18/3/15.
 */
// Inverts controls
public class ControlInverter implements Usable {
    @Override
    public void use() {
        GameWorld.getPlayer().invert(2000l);
    }
}
