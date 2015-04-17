package com.mayying.tileMapGame.entities.powerups;

import com.mayying.tileMapGame.GameWorld;

/**
 * Created by Luccan on 18/4/2015.
 */
public class Haste implements Usable {
    @Override
    public void use() {
        GameWorld.getInstance().getDevicePlayer().setHasted();
//        Play.broadcastMessage("effect", "haste");
    }
}
