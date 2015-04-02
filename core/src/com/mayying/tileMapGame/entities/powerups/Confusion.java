package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.Gdx;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Player;

/**
 * Created by User on 18/3/15.
 */

/**
 *  Inverts all players passed in the parameters.
 */
public class Confusion implements Usable {
    @Override
    public void use(Player[] players) {
        Gdx.app.log("Disabled", "Confusion does nothing locally.");
//        for(int i = 0; i < players.length; i++)
        // TODO  - send Invert move
//        GameWorld.getCurrentPlayer().invert();
        GameWorld.getPlayer(0).invert();
    }
}
