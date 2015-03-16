package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.mayying.tileMapGame.GameWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by May on 14/3/2015.
 */
public class BurningTiles {
    TiledMap fireTile;
    GameWorld world;
    TiledMapTileLayer foreground;
    TiledMapTileSet tileset;
    TiledMapTileLayer.Cell cell;
    Map<String, TiledMapTile> fireTiles;
    int count = 0, currentAnimationFrame = 0, xCoord, yCoord;


    public BurningTiles(TiledMap fireTile, GameWorld world, TiledMapTileLayer foreground) {
        this.fireTile = fireTile;
        this.world = world;
        this.foreground = foreground;
        cell = new TiledMapTileLayer.Cell();
        fireTiles = new HashMap<String, TiledMapTile>();
    }

    public void create() {
       // tileset = fireTile.getTileSets().getTileSet("fire");
        tileset = fireTile.getTileSets().getTileSet("tile");
        for (TiledMapTile tile : tileset) {
            Object property = tile.getProperties().get("fireframe");
            if (property != null) {
                fireTiles.put((String) property, tile);
            }
        }
    }

    public void render(int delay) {
        if (count > 30 * delay) {
            currentAnimationFrame++;
            if (currentAnimationFrame == 1) {
                // Gdx.app.log(elapsedSinceAnimation + "", "elapsedSinceANimation");
                xCoord = new Random().nextInt(world.getPlayer().getCollisionLayer().getWidth());
                yCoord = new Random().nextInt(world.getPlayer().getCollisionLayer().getHeight() - 2);
                foreground.setCell(xCoord, yCoord + 2, cell);
                cell = foreground.getCell(xCoord, yCoord + 2);
            }
            updateFireAnimation(currentAnimationFrame);
            // Gdx.app.log(world.getPlayer().getCollisionLayer().getHeight() + "", yCoord + 3 + "");

            count = 0;
        }
        count++;
    }

    private void updateFireAnimation(Integer frame) {
        // Gdx.app.log(frame + "", "frame");
        if (frame > fireTiles.size()) {
            currentAnimationFrame = 0;
            cell.setTile(null);
            cell = new TiledMapTileLayer.Cell();
            return;
        } else{
            TiledMapTile newTile = fireTiles.get(frame.toString());
            // Gdx.app.log(frame + "", "frame");
            // Gdx.app.log(newTile + "", "newTile");
            cell.setTile(newTile);
        }

    }

}



