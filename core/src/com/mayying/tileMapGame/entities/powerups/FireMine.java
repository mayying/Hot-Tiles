package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Player;
import com.mayying.tileMapGame.screens.Play;

/**
 * Created by User on 31/3/15.
 */
public class FireMine extends Mine {
    public FireMine(Player player, TiledMapTileLayer collisionLayer) {
        super(new Sprite(new Texture(Gdx.files.internal("img/shuriken.png"))), player, collisionLayer);
    }

    @Override
    public void onCollisionDetected(Player hitPlayer) {
        super.onCollisionDetected(hitPlayer);
        String hitPlayerID = hitPlayer.getID();
        hitPlayer.burn(hitPlayerID);
        // Format: "effect","freeze",playerIdx, user
        Play.broadcastMessage("effect", "burn", hitPlayerID, GameWorld.getInstance().getDevicePlayer().getID());
    }

}
