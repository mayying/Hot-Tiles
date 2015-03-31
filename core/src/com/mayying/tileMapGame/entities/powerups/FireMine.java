package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mayying.tileMapGame.entities.Player;

/**
 * Created by User on 31/3/15.
 */
public class FireMine extends Mine {
    public FireMine(Sprite sprite, Player player, TiledMapTileLayer collisionLayer){
        super(sprite,player,collisionLayer);
    }

    @Override
    public void onCollisionDetected(Player hitPlayer) {
        super.onCollisionDetected(hitPlayer);
        hitPlayer.die();
    }
}
