package com.mayying.tileMapGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.mayying.tileMapGame.entities.Bullet;
import com.mayying.tileMapGame.entities.Mine;
import com.mayying.tileMapGame.entities.Player;

import java.util.Random;
import java.util.Vector;

/**
 * Created by Luccan on 2/3/2015.
 */
public class GameWorld {
    private Player player;
    //    private Bullet bullet;
    // Better to separate into bullets and mines for now to decouple so we can do stuff like remove all mines or whatever
    public static Vector<Sprite> bullets = new Vector<Sprite>();
    public static Vector<Mine> mines = new Vector<Mine>();
    static boolean blackout = false;
    static long blackoutTime = 0l;

    public TiledMapTileLayer playableLayer;

    public GameWorld(TiledMapTileLayer playableLayer) {
        this.playableLayer = playableLayer;
        player = new Player(new Sprite(new Texture("img/player2_2.png")), playableLayer);

        int xCoordinate = new Random().nextInt(getPlayer().getCollisionLayer().getWidth() - 1);
        int yCoordinate = new Random().nextInt(getPlayer().getCollisionLayer().getHeight() - 3);

        player.setPosition(player.getPosition(xCoordinate, yCoordinate).x, player.getPosition(xCoordinate, yCoordinate).y);
    }

    public void drawAndUpdate(Batch batch) {
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(batch);
        }

        player.draw(batch);
//        if(player.isHit(240, 240)){
//            Gdx.app.log("asd", "pewpew");
//        }

        for(int i=0; i<mines.size(); i++){
            mines.get(i).draw(batch);
        }


        if(blackout) {
            // This causes Player object to disappear for some reason
            ShapeRenderer shapeRenderer = new ShapeRenderer();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(Color.BLACK));
            shapeRenderer.rect(120, 0, 1000, 720);
            shapeRenderer.end();
            if(System.currentTimeMillis() - blackoutTime > 2000l) blackout = false;
        }
    }

    public Player getPlayer() {
        return player;
    }

    //Custom Methods
    // Someone please do the necessary changes to use this method
    public static synchronized void addInstanceToRenderList(Sprite s){
        bullets.add(s);
    }

    public static synchronized void addBullet(Bullet bullet){
//        Bullet bullet = new Bullet(new Sprite(new Texture("img/shuriken.png")), 6, world.getPlayer(), 2, (TiledMapTileLayer) map.getLayers().get(0));
        bullets.add(bullet);
    }
    public static synchronized void removeBullet(Bullet bullet){
        bullet.setAlpha(0);
        bullets.remove(bullet);
        // causes the black box to appear, but probably necessary? not sure how garbage collection works
        bullet.getTexture().dispose();
    }

    public static void setBlackout(){
        blackoutTime = System.currentTimeMillis();
        blackout = true;
    }

    public static synchronized void addMine(Mine mine){mines.add(mine);}

    public static synchronized void removeMine(Mine mine){
        mine.getTexture().dispose();
        mine.setAlpha(0);
        mines.remove(mine);
    }
}
