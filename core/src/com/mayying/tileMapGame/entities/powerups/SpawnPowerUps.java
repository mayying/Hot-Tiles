package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.entities.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by HtooWaiYan on 25-Mar-15.
 */
public class SpawnPowerUps {
    private Player player;
    private TiledMapTileLayer tileLayer;
    private float Spawntime;        // the time period powerup appear in map(3 s)
    private float counttime;        // count time until random spawn time
    private float randomSpawnTime;  // waiting time until sprite appears after creating sprite
    private List<String> stringID;
    private Rectangle playerbound;
    private Rectangle spritebound;
    private boolean isPickup;       // check whether player pickup powerup or not
    private boolean draw;           // check whether powerup is drawn or not

    private Sprite sprite;          // powerup sprite
    private Texture PowerUpTexture; // powerup texture
    private boolean created;        // powerup is created or not
    private String id;              // powerup string id

    public SpawnPowerUps(TiledMapTileLayer tileLayer,Player player){
        this.tileLayer=tileLayer;
        this.player=player;

        // powerup stringID list
        stringID = Arrays.asList("Mine","FreezeMine","Invulnerability","ControlInverter");
        playerbound=new Rectangle();
        spritebound=new Rectangle();
        draw=false;     // sprite not drawn yet
        created=false;  // sprite not created yet
        sprite=new Sprite();
    }

    public void draw(Batch batch){
        playerbound=player.getBoundingRectangle();
        if(!created) {
            // picking random stringID from list
            id = stringID.get(new Random().nextInt(stringID.size() - 1));

            // Spawn random powerup texture
            if(id.equals("Mine")){
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


            this.sprite=new Sprite(PowerUpTexture);

            // set random spawn time for powerup
            randomSpawnTime=(float)new Random().nextInt(10);

            // sprite position
            Vector2 position=new Vector2();
            int x = new Random().nextInt(tileLayer.getWidth() - 8);
            int y = new Random().nextInt(tileLayer.getHeight()-2);
            position.x = tileLayer.getTileWidth() / 2 - sprite.getWidth() / 2 + tileLayer.getTileWidth() * (x + 4);
            position.y = tileLayer.getTileHeight() / 4+ tileLayer.getTileHeight()*(y+1);
            sprite.setPosition(position.x,position.y);
            spritebound=sprite.getBoundingRectangle();

            this.isPickup=false;
            Spawntime=0;        // count until 3s
            counttime=0;
            setCreated(true);
        }


        counttime += Gdx.graphics.getDeltaTime();
//            Gdx.app.log("counttime",String.valueOf(counttime));
//            Gdx.app.log("randomspawntime",String.valueOf(randomSpawnTime));
//            Gdx.app.log("time",String.valueOf(id));

        // check collision between player and powerup to pick up powerup
        if (Intersector.overlaps(player.getBoundingRectangle(), sprite.getBoundingRectangle())) {

            // player can only pick 2 powerups(can change in player class)
            if (player.canPickPowerUp()) {
                player.addPowerUp(id);
                setPickup(true);        // powerups is picked up
                setDraw(false);         // if picked up, stop drawing
                setCreated(false);      // cuz need to create new sprite
                Gdx.app.log("Pickup","true");
            }
        }


        // not picked up yet
        if(!getPickup()){
            // check whether the sprite is ready to spawn
            if (counttime < randomSpawnTime + 0.1 && counttime > randomSpawnTime - 0.1) {
                setDraw(true);      // to draw sprite
            }

            // draw only if the sprite is ready to spawn
            if(getDraw()) {
                // the powerup will stay for 3 s on screen then disappear
                Spawntime+=(Gdx.graphics.getDeltaTime());
                if (Spawntime <= 3) {
                    sprite.draw(batch);
                } else {

                    sprite.getTexture().dispose();
                    setDraw(false);
                    setCreated(false);


                }
            }



        }

    }

    public void setPickup(boolean pickup){
        isPickup=pickup;
    }
    public boolean getPickup(){
        return isPickup;
    }

    public void setDraw(boolean draw){
        this.draw=draw;
    }

    public boolean getDraw(){
        return  draw;
    }

    public void setCreated(boolean created){
        this.created=created;
    }

}
