package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mayying.tileMapGame.GameWorld;

/**
 * Created by User on 13/4/15.
 */
public class Thunderbolt  extends Sprite{
    public Thunderbolt(float x, float y, TiledMapTileLayer playableLayer){
        super(new Texture(Gdx.files.internal("powerups/thunderbolt.png")));
        float _x = GameWorld.TILE_WIDTH / 2 - getWidth() / 2 + GameWorld.TILE_WIDTH * (x + 4);

        float _y = GameWorld.TILE_HEIGHT / 4 + GameWorld.TILE_HEIGHT * (y + 1);
        setPosition(_x, _y);

        // Not caring about spam of uncleaned sprites/threads for now
        GameWorld.getInstance().addThunder(this);
        new DelayedThread(200l){
            @Override
            public void run() {
                super.run();
                GameWorld.getInstance().removeThunder(Thunderbolt.this);
            }
        }.start();
    }

}
