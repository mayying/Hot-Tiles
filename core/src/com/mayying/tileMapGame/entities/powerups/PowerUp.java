package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Created by HtooWaiYan on 19-Mar-15.
 */
public class PowerUp {

    private Vector2 position;
    private float boundWidth;
    private float boundHeight;
    private boolean showing;
    private Rectangle bound;
    private boolean pickup;
    private Sprite sprite;
    private TiledMapTileLayer playerlayer;
    private int x;
    private int y;
    private boolean draw;
    private long starttime;
    private long endtime;
    private float time;
    private float randomSpawnTime;
    private boolean appear;
    private float counttime;

    public PowerUp(int x, int y,Texture texture,TiledMapTileLayer layer){
        counttime=0;
        time=0;
        draw=false;
        playerlayer=layer;
        this.sprite=new Sprite(texture);
        position=new Vector2();
        this.x=x;
        this.y=y;
        setRandomSpawnTime();

        position.x = playerlayer.getTileWidth() / 2 - sprite.getWidth() / 2 + playerlayer.getTileWidth() * (x + 4);
        position.y = playerlayer.getTileHeight() / 4+ playerlayer.getTileHeight()*(y+1);
        this.pickup=false;

        sprite.setPosition(position.x,position.y);
//        this.bound=new Rectangle(sprite.getX(),sprite.getY(),sprite.getWidth(),sprite.getHeight());
        this.bound=sprite.getBoundingRectangle();
//        setBound(sprite.getWidth(),sprite.getHeight());
    }


    public Rectangle getBound(){return getSprite().getBoundingRectangle();}

    public boolean isPickup(){
        return pickup;
    }

    public void setPickup(boolean x){
        pickup=x;
    }

    public Sprite getSprite(){
        return sprite;
    }

    public void dispose(){
        sprite.getTexture().dispose();
    }

    public void setDraw(boolean x){
        draw=x;
    }

    public boolean getDraw(){return draw;}

    public void setTime(float delta){
        time+=delta;
    }

    public float getTime(){
        return time;
    }

    public void setRandomSpawnTime(){
        randomSpawnTime=(float)new Random().nextInt(20);
    }

    public float getRandomSpawnTime(){
        return randomSpawnTime;
    }

    public void setCounttime(float delta){
        counttime+=delta;
    }

    public float getCounttime(){
        return counttime;
    }

    public void zero(){
        counttime=0;
        time=0;
    }

}
