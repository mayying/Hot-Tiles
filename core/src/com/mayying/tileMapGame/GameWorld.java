package com.mayying.tileMapGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mayying.tileMapGame.entities.Bullet;
import com.mayying.tileMapGame.entities.Mine;
import com.mayying.tileMapGame.entities.MyTouchpad;
import com.mayying.tileMapGame.entities.Player;
import com.mayying.tileMapGame.screens.GameScreenRightSideBar;

import java.util.Random;
import java.util.Vector;

/**
 * Created by Luccan on 2/3/2015.
 */
public class GameWorld {
    private Player player;
    // Better to separate into bullets and mines for now to decouple so we can do stuff like remove all mines or whatever
    private MyTouchpad myTouchpad;
    private Rectangle playerBound, screenBound;
    private TiledMapTileLayer playableLayer;


    public static Vector<Sprite> bullets = new Vector<Sprite>();
    public static Vector<Mine> mines = new Vector<Mine>();
    static boolean blackout = false;
    static long blackoutTime = 0l;



    public GameWorld(TiledMapTileLayer playableLayer) {
        this.playableLayer = playableLayer;

        player = new Player(new Sprite(new Texture("img/player3_2.png")), playableLayer);
        int xCoordinate = new Random().nextInt(getPlayer().getCollisionLayer().getWidth() - 5);
        int yCoordinate = new Random().nextInt(getPlayer().getCollisionLayer().getHeight());
        player.setPosition(player.getPosition(xCoordinate, yCoordinate).x, player.getPosition(xCoordinate, yCoordinate).y);

        myTouchpad = new MyTouchpad();

        setScreenBound();
        setPlayerBound();
    }

    public void drawAndUpdate(Batch batch) {
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(batch);
        }

        player.draw(batch);

        for (int i = 0; i < mines.size(); i++) {
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

    public void playerMovement() {
        float screenLeft = screenBound.getX() + getPlayer().getCollisionLayer().getTileWidth() * 3;
        float screenBottom = screenBound.getY();
        float screenTop = screenBound.getHeight();// + (world.getPlayer().getHeight() / 2);
        float screenRight = screenBound.getWidth() - getPlayer().getCollisionLayer().getTileWidth() * 2;

        float newX = player.getX();
        float newY = player.getY();
        if (getMyTouchpad().getTouchpad().getKnobPercentX() > 0.5) {
            newX += getPlayer().getCollisionLayer().getTileWidth();
            getPlayer().rightPressed();
        } else if (getMyTouchpad().getTouchpad().getKnobPercentX() < -0.5) {
            newX -= getPlayer().getCollisionLayer().getTileWidth();
            getPlayer().leftPressed();
        }

        if (getMyTouchpad().getTouchpad().getKnobPercentY() > 0.5) {
            newY += getPlayer().getCollisionLayer().getTileHeight();
            getPlayer().upPressed();
        } else if (getMyTouchpad().getTouchpad().getKnobPercentY() < -0.5) {
            newY -= getPlayer().getCollisionLayer().getTileHeight();
            getPlayer().downPressed();
        }

        countX++;
        countY++;

        if (newX >= screenLeft && newX + playerBound.getWidth() <= screenRight) {
            if (getMyTouchpad().getTouchpad().getKnobPercentX() != 0 && countX > 30) {
                getPlayer().setX(newX);
                countX = 0;
            }
        }
        if (newY >= screenBottom && newY <= screenTop) {
            if (getMyTouchpad().getTouchpad().getKnobPercentY() != 0 && countY > 30) {
                getPlayer().setY(newY);
                countY = 0;
            }
        }
    }


    private void setPlayerBound() {
        playerBound = getPlayer().getBoundingRectangle();
    }

    private void setScreenBound() {
        screenBound = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public MyTouchpad getMyTouchpad() {
        return myTouchpad;
    }

    public Player getPlayer() {
        return player;
    }

    // Custom Methods
    // Currently unused to prevent excessive coupling
    public static synchronized void addInstanceToRenderList(Sprite s) {
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
