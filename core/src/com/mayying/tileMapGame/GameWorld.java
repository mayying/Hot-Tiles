package com.mayying.tileMapGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.mayying.tileMapGame.entities.MyTouchpad;
import com.mayying.tileMapGame.entities.Player;
import com.mayying.tileMapGame.entities.powerups.Bullet;
import com.mayying.tileMapGame.entities.powerups.DelayedThread;
import com.mayying.tileMapGame.entities.powerups.Mine;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Luccan on 2/3/2015.
 */
public class GameWorld {
    private static Player player; // static cause i'm lazy. Replace with array of all players in game.
    private final ArrayList<Player> players = new ArrayList<Player>();
    // Better to separate into bullets and mines for now to decouple so we can do stuff like remove all mines or whatever
    private MyTouchpad myTouchpad;
    private Rectangle playerBound;
    public static Rectangle screenBound;
    private int countX = 0, countY = 0;
    public static Vector<Sprite> bullets = new Vector<Sprite>();
    public static Vector<Mine> mines = new Vector<Mine>();
    static boolean blackout = false;
    public static float TILE_WIDTH;
    public static float TILE_HEIGHT;

    public GameWorld(TiledMapTileLayer playableLayer) {


        player = new Player(new Sprite(new Texture("img/player3_2.png")), playableLayer, this);

        player.spawn();
        // TODO - create additional threads to manage the other player's interactions, positions etc

        myTouchpad = new MyTouchpad();
        // Constants
        TILE_WIDTH = playableLayer.getTileWidth();
        TILE_HEIGHT = playableLayer.getTileHeight();

        screenBound = new Rectangle(4 * TILE_WIDTH, TILE_HEIGHT, 10 * TILE_WIDTH, 8 * TILE_HEIGHT);

        setPlayerBound();
    }

    public void drawAndUpdate(Batch batch) {
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(batch);
        }

        for (int i=0; i<players.size(); i++){
            players.get(i).draw(batch);
        }


        for (int i = 0; i < mines.size(); i++) {
            mines.get(i).draw(batch);
        }


        if (blackout) {
            // This causes Player object to disappear for some reason
            ShapeRenderer shapeRenderer = new ShapeRenderer();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(Color.BLACK));
            shapeRenderer.rect(120, 0, 1000, 720);
            shapeRenderer.end();
        }
    }

    // Should separate into collision/bounds logic and update movement so that when we factor in concurrent
    // updates from server we can just update movement via setX / setY
    // Movement logic shouldn't be here. OH WELL
    public void playerMovement() {
        float screenLeft = screenBound.getX();
        float screenBottom = screenBound.getY();
        float screenTop = screenBottom + screenBound.getHeight();// + (world.getPlayer().getHeight() / 2);
        float screenRight = screenLeft + screenBound.getWidth();

        float newX = player.getX();
        float newY = player.getY();
        if (getMyTouchpad().getTouchpad().getKnobPercentX() > 0.5) {
            // add back in leftpressed rightpressed etc for direction, if we are using the bullets and stuff
            newX += TILE_WIDTH * player.getSpeed();

        } else if (getMyTouchpad().getTouchpad().getKnobPercentX() < -0.5) {
            newX -= TILE_WIDTH * player.getSpeed();
        }

        if (getMyTouchpad().getTouchpad().getKnobPercentY() > 0.5) {
            newY += TILE_HEIGHT * player.getSpeed();
        } else if (getMyTouchpad().getTouchpad().getKnobPercentY() < -0.5) {
            newY -= TILE_HEIGHT * player.getSpeed();
        }

        countX++;
        countY++;

        if (newX >= screenLeft && newX + playerBound.getWidth() <= screenRight) {
            if (myTouchpad.getTouchpad().getKnobPercentX() != 0 && countX > 30) {
                getPlayer().setX(newX);
                countX = 0;
            }
        }
        if (newY >= screenBottom && newY <= screenTop) {
            if (myTouchpad.getTouchpad().getKnobPercentY() != 0 && countY > 30) {
                getPlayer().setY(newY);
                countY = 0;
            }
        }
    }


    private void setPlayerBound() {
        playerBound = getPlayer().getBoundingRectangle();
    }

    public MyTouchpad getMyTouchpad() {
        return myTouchpad;
    }

    public static Player getPlayer() {
        return player;
    }

    // Custom Methods
    // Currently unused to prevent excessive coupling
    public static synchronized void addInstanceToRenderList(Sprite s) {
        bullets.add(s);
    }

    public static synchronized void addBullet(Bullet bullet) {
//        Bullet bullet = new Bullet(new Sprite(new Texture("img/shuriken.png")), 6, world.getPlayer(), 2, (TiledMapTileLayer) map.getLayers().get(0));
        bullets.add(bullet);
    }

    public static synchronized void removeBullet(Bullet bullet) {
        bullet.setAlpha(0);
        bullets.remove(bullet);
        // causes the black box to appear, but probably necessary? not sure how garbage collection works
        bullet.getTexture().dispose();
    }

    public static void setBlackout(long millis) {
        if (!blackout) {
            blackout = true;
            new DelayedThread(millis) {
                @Override
                public void run() {
                    super.run();
                    blackout = false;
                }
            }.start();
        }
    }

    public static synchronized void addMine(Mine mine) {
        mines.add(mine);
    }

    public static synchronized void removeMine(Mine mine) {
        mine.getTexture().dispose();
        mine.setAlpha(0);
        mines.remove(mine);
    }

    public void removePlayer(Player player){
        synchronized (players) {
            players.remove(player);
        }
    }

    public void addPlayer(Player player){
        synchronized (players){
            players.add(player);
        }
    }


}
