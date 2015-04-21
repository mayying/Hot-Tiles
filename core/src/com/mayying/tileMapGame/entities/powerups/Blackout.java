package com.mayying.tileMapGame.entities.powerups;

import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.screens.Play;

/**
 * Created by User on 18/3/15.
 */

/**
 * nothing much. Just causes a black rectangle to appear for 3 seconds. Might be unstable.
 */
public class Blackout implements Usable {
    @Override
    public void use() {
        Jukebox.play("blackout");
        Play.broadcastMessage("effect", "blackout");
    }


}
