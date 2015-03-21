package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Created by HtooWaiYan on 19-Mar-15.
 */
public class LandMine extends PowerUp{


    public LandMine(int x, int y, Texture texture,TiledMapTileLayer layer) {
        super(x, y, texture, layer);
    }
}
