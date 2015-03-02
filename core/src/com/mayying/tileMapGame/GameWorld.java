package com.mayying.tileMapGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.mayying.tileMapGame.entities.Bullet;
import com.mayying.tileMapGame.entities.Player;

import java.util.Vector;

/**
 * Created by Luccan on 2/3/2015.
 */
public class GameWorld {
    private Player player;
    //    private Bullet bullet;
    public static Vector<Bullet> bullets = new Vector<Bullet>();
    private long lastPresed;

    private TiledMapTileLayer playableLayer;

    public GameWorld(TiledMapTileLayer playableLayer){
        this.playableLayer = playableLayer;
        player = new Player(new Sprite(new Texture("img/player.png")), playableLayer);

        System.out.println("player.getCollisionLayer().getWidth() " + player.getCollisionLayer().getWidth());

        player.setPosition((player.getCollisionLayer().getWidth() - 5) * player.getCollisionLayer().getTileWidth(),
                (player.getCollisionLayer().getHeight() - 2) * player.getCollisionLayer().getTileHeight());
    }

    public void drawAndUpdate(Batch batch){
        player.draw(batch);
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && System.currentTimeMillis() - lastPresed > 200){
            lastPresed = System.currentTimeMillis();
            createNewBullet();
        }
        for(int i=0; i<bullets.size(); i++){
            bullets.get(i).draw(batch);
        }
//        if(bullet!=null){
//            bullet.draw(renderer.getBatch());
//        }
    }

    public void dispose(){
        player.getTexture().dispose();
    }


    //Custom Methods
    public void createNewBullet(){
        Bullet bullet = new Bullet(new Sprite(new Texture("img/shuriken.png")), 6, player ,2 ,playableLayer);
        bullets.add(bullet);
    }
    public static synchronized void removeBullet(Bullet bullet){
        bullet.setAlpha(0);
        bullets.remove(bullet);
        // causes the black box to appear, but probably necessary? not sure how garbage collection works
        bullet.getTexture().dispose();
    }
}
