package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.entities.Player;
import com.mayying.tileMapGame.screens.Play;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by User on 28/3/15.
 */
public class Swap implements Usable {

    @Override
    public void use() {
        Jukebox.play("swap");
        GameWorld world = GameWorld.getInstance();
        Player p = world.getDevicePlayer();
        String deviceID = p.getID();
        if (world.getPlayers().size() > 1) {
            // Add all other IDs into one LinkedList
            LinkedList<String> targets = new LinkedList<>();
            for (String key : world.getPlayers().keySet()) {
                if (!key.equals(deviceID)) {
                    targets.add(key);
                }
            }

            // Select a random player
            int idx = new Random().nextInt(world.getPlayers().size() - 1);
            Vector2 playerPos = p.getPlayerPosition();
            int xCoord = (int) playerPos.x;
            int yCoord = (int) playerPos.y;
            Play.broadcastMessage("effect", "swap", String.valueOf(xCoord), String.valueOf(yCoord), "1", targets.get(idx));
        }

        // Swap animation
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
}
