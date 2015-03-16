package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mayying.tileMapGame.GameWorld;

/**
 * Created by User on 10/3/15.
 */
public class Mine  extends Sprite {
    Player player;

    public Mine(Sprite sprite, Player player) {
        super(sprite);
        // Must only draw to this player, specify by player index in main draw method?
        this.player = player;

        // originate from player
        this.setPosition(player.getX(), player.getY());
    }

    @Override
    public void draw(Batch batch) {
        update();
        super.draw(batch);
    }

    public void update() {
        // TODO - get device ID to check if this mine is owned by the current player
        // TODO - Do multiplayer first
        if(true){
            // show the mine

            // Trigger mine if player on top

        }



    }
}
