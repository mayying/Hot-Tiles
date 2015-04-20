package com.mayying.tileMapGame.entities.powerups;

import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.screens.Play;

/**
 * Created by Luccan on 18/4/2015.
 */
public class Haste implements Usable {
    @Override
    public void use() {
//        GameWorld.getInstance().getDevicePlayer().setHasted();
        Jukebox.play("bloodlust");
        Play.broadcastMessage("effect", "haste");
    }
}
