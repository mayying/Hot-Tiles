package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.Gdx;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Player;

/**
 * Created by User on 18/3/15.
 */

/**
 * nothing much. Just causes a black rectangle to appear for 3 seconds. Might be unstable.
 */
public class Blackout implements Usable {
    @Override
    public void use(Player[] players) {
        Gdx.app.log("Disabled", "Using Blackout will send the server a message and does nothing else locally");
        // TODO - Send blackout move
        GameWorld.setBlackout();
    }




}
