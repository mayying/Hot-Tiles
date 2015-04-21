package com.mayying.tileMapGame.entities.powerups;

import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.screens.Play;

/**
 * Created by User on 18/3/15.
 */

/**
 * Inverts all players passed in the parameters.
 */
public class Confusion implements Usable {
    @Override
    public void use() {
        Jukebox.play("confused");
        Play.broadcastMessage("effect", "invert");
    }
}
