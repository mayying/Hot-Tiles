package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.powerups.Collidable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by May on 14/3/2015.
 */
public class BurningTiles implements Collidable{
    TiledMap fireTile;
    GameWorld world;
    TiledMapTileLayer foreground;
    TiledMapTileSet tileset;
    TiledMapTileLayer.Cell cell;
    Map<String, TiledMapTile> fireTiles;
    float count = 0f;
    int currentAnimationFrame = 0, xCoord, yCoord;
    private Random random;


    public BurningTiles(TiledMap fireTile, GameWorld world, TiledMapTileLayer foreground, long randomSeed) {
        this.fireTile = fireTile;
        this.world = world;
        this.foreground = foreground;
        cell = new TiledMapTileLayer.Cell();
        fireTiles = new HashMap<String, TiledMapTile>();
        this.random = new Random(randomSeed);
    }

    public void create() {
        // tileset = fireTile.getTileSets().getTileSet("fire");
        tileset = fireTile.getTileSets().getTileSet("tiles");
        for (TiledMapTile tile : tileset) {
            Object property = tile.getProperties().get("fireframe");
            if (property != null) {
                fireTiles.put((String) property, tile);
            }
        }
    }

    public void render(float delta) {
        // Gdx.app.log(count + "", "count");
        if (count > 0.2f) {
            currentAnimationFrame++;
            if (currentAnimationFrame == 1) {
                // Gdx.app.log(elapsedSinceAnimation + "", "elapsedSinceANimation");
                xCoord = random.nextInt(world.getDevicePlayer().getCollisionLayer().getWidth() - 8);
                yCoord = random.nextInt(world.getDevicePlayer().getCollisionLayer().getHeight() - 2);
                foreground.setCell(xCoord + 4, yCoord + 1, cell);
                cell = foreground.getCell(xCoord + 4, yCoord + 1);
            }else if(currentAnimationFrame >= 9){
                collisionCheck();
            }


            updateFireAnimation(currentAnimationFrame);
            // Gdx.app.log(world.getPlayer().getCollisionLayer().getHeight() + "", yCoord + 3 + "");

            count = 0.0f;
        }
        count += delta;
    }

    @Override
    public void onCollisionDetected(Player player) {
//        Gdx.app.log("Player", "Player die from fire Q_Q");
        if (!player.isOnFire)
            player.die();
    }

    @Override
    public void collisionCheck() {

        Vector2 pos = new Vector2(xCoord, yCoord);
//        Gdx.app.log("Tile Coords: ",xCoord+", "+yCoord);
        // Only check for this device's player, let the server update us if other players are burning
        Player player = world.getDevicePlayer();

        Vector2 playerPos = player.getPlayerPosition();
//        Gdx.app.log("Player Coords: ", playerPos.toString());
        if (playerPos.equals(pos)) {
            onCollisionDetected(player);
        }

    }



    private void updateFireAnimation(Integer frame) {
        // Gdx.app.log(frame + "", "frame");
        if (frame > fireTiles.size()) {
            cell.setTile(null);
            cell = new TiledMapTileLayer.Cell();
            currentAnimationFrame = 0;
            return;
        } else {
            TiledMapTile newTile = fireTiles.get(frame.toString());
            cell.setTile(newTile);
        }

    }


}



