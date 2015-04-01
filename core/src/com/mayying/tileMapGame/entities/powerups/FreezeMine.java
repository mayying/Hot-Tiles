package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mayying.tileMapGame.entities.Player;

/**
 * Created by User on 18/3/15.
 */

/**
 * Mine that freezes people in place for a few seconds
 */
public class FreezeMine extends Mine {
    public FreezeMine(Sprite sprite, Player player, TiledMapTileLayer collisionLayer){
        super(sprite,player,collisionLayer);
    }

    @Override
    public void onCollisionDetected(Player hitPlayer) {
        super.onCollisionDetected(hitPlayer);
//        Gdx.app.log("Freeze Mine","BLINGLINGLING");
        // TODO - Send server move
        hitPlayer.freeze(0);
    }
}
