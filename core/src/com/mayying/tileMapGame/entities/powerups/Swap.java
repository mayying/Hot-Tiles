package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.entities.Player;
import com.mayying.tileMapGame.screens.Play;

/**
 * Created by User on 28/3/15.
 */
public class Swap implements Usable {

    @Override
    public void use() {
        Jukebox.play("swap");
        GameWorld world = GameWorld.getInstance();
        Player p = world.getDevicePlayer();

        if (world.getPlayers().size()>1) {
            for (String key : world.getPlayers().keySet()) {
                world.getPlayers().get(key).toggleSwap(true);
            }
        } else {
            //single player swap
            p.toggleSwap(true);
            p.animate(Gdx.graphics.getDeltaTime() * 20);
            new DelayedThread(200l, p) {
                @Override
                public void run() {
                    super.run();
                    getPlayer().toggleSwap(false);
                }
            }.start();
        }

        Vector2 playerPos = p.getPlayerPosition();
        int xCoord = (int) playerPos.x;
        int yCoord = (int) playerPos.y;

        Play.broadcastMessage("effect", "swap", String.valueOf(xCoord), String.valueOf(yCoord), "1");

//        Player p1 = GameWorld.getInstance().getDevicePlayer();
//        HashMap<String, Player> map = GameWorld.getInstance().getPlayers();
//        map.remove(p1.getID());
//
//        Player p2 = players[1];
//
//        float x = p1.getX();
//        p1.setX(p2.getX());
//        p2.setX(x);
//
//        float y = p1.getY();
//        p1.setY(p2.getY());
//        p2.setY(y);

    }
}
