package com.mayying.tileMapGame.entities.powerups;

import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.screens.Play;

/**
 * Created by User on 16/4/15.
 */
public class Fire implements Usable {
    @Override
    public void use() {
        GameWorld.getInstance().getDevicePlayer().setOnFire();
        Play.broadcastMessage("effect", "fire", "0");
    }
}
