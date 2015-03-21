package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Player;

/**
 * Created by User on 18/3/15.
 */

/**
 * nothing much. Just causes a black rectangle to appear for 3 seconds. Might be unstable.
 */
public class Blackout implements Usable {

    private Sprite sprite;
    private TiledMapTileLayer collisionLayer;
    private Rectangle rectangle;

    public Blackout(){

    }
    @Override
    public void use(Player[] players) {
        GameWorld.setBlackout(3000l);
    }




}
