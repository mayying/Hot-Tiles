package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by HtooWaiYan on 25-Mar-15.
 */
public class SpawnPowerUps implements Collidable{
    private Player player;
    private TiledMapTileLayer tileLayer;
    private float spawnTime;        // the time period powerup appear in map(3 s)
    private float countTime;        // count time until random spawn time
    private float randomSpawnTime;  // waiting time until sprite appears after creating sprite
    private int state = 0; // 0 - !created, 1 - created, 2 - drawn, 3 - clean up
    private List<String> stringID;
    private Sprite sprite;          // powerup sprite
    private Texture PowerUpTexture; // powerup texture
    private String id;              // powerup string id
    Vector2 position = new Vector2();
    Vector2 coords = new Vector2(); // matrix coords

    public SpawnPowerUps(TiledMapTileLayer tileLayer,Player player){
        this.tileLayer=tileLayer;
        this.player=player;

        // powerup stringID list
        stringID = Arrays.asList("Mine","FreezeMine","Invulnerability","ControlInverter","Swap","Blackout");
        sprite=new Sprite();
    }

    public void draw(Batch batch){
        switch(state){
            case 0:
                // !created
                // picking random stringID from list
                id = stringID.get(new Random().nextInt(stringID.size() - 1));

                // Spawn random powerup texture
                if(id.equals("FireMine")){
                    PowerUpTexture=new Texture("img/shuriken.png");
                }
                else if(id.equals("FreezeMine")){
                    PowerUpTexture=new Texture("img/shuriken.png");
                }
                else if(id.equals("Invulnerability")){
                    PowerUpTexture=new Texture("img/shuriken.png");
                }
                else if(id.equals("ControlInverter")){
                    PowerUpTexture=new Texture("img/shuriken.png");
                }
                else if(id.equals("Swap")){
                    PowerUpTexture=new Texture("img/shuriken.png");
                }
                else if(id.equals("Blackout")){
                    PowerUpTexture=new Texture("img/shuriken.png");
                }
                this.sprite=new Sprite(PowerUpTexture);

                // set random spawn time for powerup
                randomSpawnTime=(float)new Random().nextInt(10);

                // sprite position
                coords.x = new Random().nextInt(tileLayer.getWidth() - 8);
                coords.y = new Random().nextInt(tileLayer.getHeight()-2);

                position.x = tileLayer.getTileWidth() / 2 - sprite.getWidth() / 2 + tileLayer.getTileWidth() * (coords.x + 4);
                position.y = tileLayer.getTileHeight() / 4+ tileLayer.getTileHeight()*(coords.y+1);
                Gdx.app.log(id+" position",coords.x+", "+coords.y);
                sprite.setPosition(position.x,position.y);
                spawnTime = 0;        // count until 3s
                countTime = 0;
                state++; //created
                break;

            case 1:
                //created
                // check whether the sprite is ready to spawn/ be drawn
                if (countTime < randomSpawnTime + 0.1 && countTime > randomSpawnTime - 0.1) {
                    state++; //drawn
                }else{
                    countTime += Gdx.graphics.getDeltaTime();
                }
                break;
            case 2:
                // drawn
                spawnTime+=(Gdx.graphics.getDeltaTime()); //getDeltaTime is actually not in millis, consider using System.currentTimeMillis instead
//                Gdx.app.log("spawn time",String.valueOf(spawnTime));
                if (spawnTime <= 6) {
                    sprite.draw(batch);
                } else {
                    sprite.getTexture().dispose();
                    state = 0;
                }
                // check collision between player and powerup to pick up powerup
                collisionCheck();
                break;
        }

    }

    @Override
    public void onCollisionDetected(Player player) {
//        Gdx.app.log("Player", "Player die from fire Q_Q");
        if (player.canPickPowerUp()) {
            player.addPowerUp(id);
            state = 0; // picked up, restart state
            Gdx.app.log("Powerup",id + " picked up.");
        }
    }

    @Override
    public void collisionCheck() {
        // Only check for this device's player -  power ups appear to everyone differently
        Player player = GameWorld.getPlayer();

        Vector2 playerPos = player.getPlayerPosition();
//        Gdx.app.log("Player Coords: ", playerPos.toString());
        if (playerPos.equals(this.coords)) {
            onCollisionDetected(player);
        }

    }
}
