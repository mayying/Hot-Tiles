package com.mayying.tileMapGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.mayying.tileMapGame.entities.Bullet;
import com.mayying.tileMapGame.entities.Mine;
import com.mayying.tileMapGame.entities.Player;

import java.util.Vector;

/**
 * Created by Luccan on 2/3/2015.
 */
public class GameWorld {
    private Player player;
    //    private Bullet bullet;
    public static Vector<Sprite> bullets = new Vector<Sprite>();
    public static Vector<Mine> mines = new Vector<Mine>();

    private TiledMapTileLayer playableLayer;

    public GameWorld(TiledMapTileLayer playableLayer){
        this.playableLayer = playableLayer;
        player = new Player(new Sprite(new Texture("img/player.png")), playableLayer);

        System.out.println("player.getCollisionLayer().getWidth() " + player.getCollisionLayer().getWidth());

        player.setPosition((player.getCollisionLayer().getWidth() - 5) * player.getCollisionLayer().getTileWidth(),
                (player.getCollisionLayer().getHeight() - 2) * player.getCollisionLayer().getTileHeight());
    }

    public void drawAndUpdate(Batch batch){
        for(int i=0; i<bullets.size(); i++){
            bullets.get(i).draw(batch);
        }

        player.draw(batch);
//        if(player.isHit(240, 240)){
//            Gdx.app.log("asd", "pewpew");
//        }

        for(int i=0; i<mines.size(); i++){
            mines.get(i).draw(batch);
        }
    }

    public void dispose(){
        player.getTexture().dispose();
    }

    public Player getPlayer(){
        return player;
    }

    //Custom Methods
    // Someone please do the necessary changes to use this method
    public static synchronized void addInstanceToRenderList(Sprite s){
        bullets.add(s);
    }
    public static synchronized void removeBullet(Bullet bullet){
        bullet.setAlpha(0);
        bullets.remove(bullet);
        bullet.getTexture().dispose();
    }
}
