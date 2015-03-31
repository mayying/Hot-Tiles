package com.mayying.tileMapGame.entities.powerups;

import com.mayying.tileMapGame.entities.Player;

/**
 * Created by User on 16/3/15.
 */
public interface Collidable {
    void onCollisionDetected(Player player);
    void collisionCheck();
}
