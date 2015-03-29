package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.powerups.Bullet;
import com.mayying.tileMapGame.entities.powerups.DelayedThread;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by May Ying on 24/2/2015.
 */
public class Player extends Sprite {

    private final TextureAtlas playerAtlas;
    private float speed = 1, animationTime = 0;
    private long lastPressed = 0l, lastHitTime = 0l; // in case of null pointer or whatever;
    private int facing;
    private TiledMapTileLayer collisionLayer;
    private Player lastHitBy;
    private int kills;
    private int deaths;
    private GameWorld gameWorld;
    private boolean isFrozen = false,isInverted = false; // for freezing animation and stuff?
    public boolean isInvulnerable = false;
    private Animation forward, backward,left,right;
    public ArrayList<String> powerUpList;


    public Player(TextureAtlas atlas,TiledMapTileLayer collisionLayer, GameWorld gameWorld) {
        super(new Animation(1 / 2f, atlas.findRegions("player_2_forward")).getKeyFrame(0));
        this.collisionLayer = collisionLayer;
        this.gameWorld = gameWorld;
        facing = 8;
        powerUpList = new ArrayList<String>();

        // Movement Animations
        this.playerAtlas = atlas;
        forward = new Animation(1 / 2f, playerAtlas.findRegions("player_2_forward"));
        backward = new Animation(1 / 2f, playerAtlas.findRegions("player_2_backward"));
        left = new Animation(1 / 2f, playerAtlas.findRegions("player_2_left"));
        right = new Animation(1 / 2f, playerAtlas.findRegions("player_2_right"));
        forward.setPlayMode(Animation.PlayMode.LOOP);
        backward.setPlayMode(Animation.PlayMode.LOOP);
        left.setPlayMode(Animation.PlayMode.LOOP);
        right.setPlayMode(Animation.PlayMode.LOOP);

    }


    public Vector2 setPlayerPosition(int x, int y) {
        Vector2 vector2 = new Vector2();
        vector2.x = collisionLayer.getTileWidth() / 2 - getWidth() / 2 + collisionLayer.getTileWidth() * (x + 4);
        vector2.y = collisionLayer.getTileHeight() / 4 + collisionLayer.getTileHeight() * (y + 1);

        return vector2;
    }

    public Vector2 getPlayerPosition() {
        Vector2 vector2 = new Vector2();
        vector2.x = (float) (Math.floor(getX() / 70f) - 4);
        vector2.y = (float) (Math.floor(getY() / 70f) - 1);
        return vector2;
    }

    public void draw(Batch batch) {
        // Gdx.app.log(isInvulnerable + "", "isVulnerable");
        if (isInvulnerable) {
            this.setAlpha(0.7f);
        } else
            this.setAlpha(1);
        super.draw(batch);

    }

    private void fireBullet() {
        if (System.currentTimeMillis() - lastPressed > 200) {
            lastPressed = System.currentTimeMillis();
            Bullet bullet = new Bullet(new Sprite(new Texture("img/shuriken.png")), facing, this, 2, collisionLayer);
            GameWorld.addInstanceToRenderList(bullet);
        }
    }

    public void spacePressed() {
        fireBullet();
    }

    public int getFacing() {
        return facing;
    }

    public void rightPressed() {
        facing = 6;
    }

    public void leftPressed() {
        facing = 4;
    }

    public void upPressed() {
        facing = 8;
    }

    public void downPressed() {
        facing = 2;
    }
    public void animate(float delta){
        animationTime += delta;
        switch (facing){
            case 2:
                this.setRegion(forward.getKeyFrame(animationTime));
                break;
            case 4:
                this.setRegion(left.getKeyFrame(animationTime));
                break;
            case 6:
                this.setRegion(right.getKeyFrame(animationTime));
                break;
            case 8:
                this.setRegion(backward.getKeyFrame(animationTime));
                break;
        }
    }

    public boolean isHit(float x, float y) {
        //bottom right
        float x_1 = getX() + getWidth() / 2;
        float y_1 = getY() - getHeight() / 2;

        //top left
        float x_2 = getX() - getWidth() / 2;
        float y_2 = getY() + getHeight() / 2;
//        Gdx.app.log("Player","X-Bounds: "+ x_2 + " - "+ x_1 + ((x - x_2)<= getWidth() && (x - x_2) >= 0));
//        Gdx.app.log("Player","Y-Bounds: "+ y_1 + " - "+ y_2 + ((y - y_1)>= 0 && (y-y_1)<=getHeight()));

        return ((x - x_2) <= getWidth() && (x - x_2) >= 0) &&
                ((y - y_1) >= 0 && (y - y_1) <= getHeight());

    }

    public void update(float delta) {


    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setLastHitBy(Player lastHitBy) {
        this.lastHitBy = lastHitBy;
        this.lastHitTime = System.currentTimeMillis();
    }

    public Player getLastHitBy() {
        // Setting 3 seconds now
        return (lastHitTime - System.currentTimeMillis()) <= 3000l ? lastHitBy : null;
    }

    public void freeze(){
        this.freeze(2000l);
    }

    public void freeze(long millis) {
        // User can only be inflicted with one speed modifier at any time. Reduce complexity of code
        // and eliminate interaction for when user is frozen and then inverted or something like that.
        // TL;DR GOT LAZY
        if (speed == 1 && !isInvulnerable) {
            //TODO - last hit logic
//        setLastHitBy();
            // other freezing animations?
            setSpeed(0);
            new DelayedThread(2000l) {
                @Override
                public void run() {
                    super.run();
                    setSpeed(1);
                }
            }.start();

        }
    }

    public void invert() {
        if (speed == 1 && !isInvulnerable) {
            //TODO - last hit logic
//        setLastHitBy();
            // other freezing animations?
            setSpeed(-1);
            new DelayedThread(2000l) {
                @Override
                public void run() {
                    super.run();
                    setSpeed(1);
                }
            }.start();
        }
    }

    // 1 for normal, 0 to stop, -1 to invert, 0.5 to slow??
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    /**
     * Kills the player. Use only if this is the device's player. Probably only use this for testing.
     * @return a Vector2 of where the player will respawn
     */
    public Vector2 die() {
        // Commits sudoku
        if (!isInvulnerable) {
            // Remove from render list,
            gameWorld.removePlayer(this);

            // Set Invulnerable for 4 secs
            isInvulnerable = true;
            new DelayedThread(4000l) {
                @Override
                public void run() {
                    super.run();
                    isInvulnerable = false;
                }
            }.start();
            final int xCoordinate = new Random().nextInt(getCollisionLayer().getWidth() - 8);
            final int yCoordinate = new Random().nextInt(getCollisionLayer().getHeight() - 2);
            // wait for 3 secs then choose a random location, add to render list
            new DelayedThread(1000l) {
                @Override
                public void run() {
                    super.run();
                    spawn(xCoordinate, yCoordinate);
                }
            }.start();

            // TODO -update score based on last hit by field
            deaths++;
//            Gdx.app.log("Player", "Player death count: " + deaths);
            return new Vector2(xCoordinate, yCoordinate);
        }else{
            return null;
        }
    }

    /**
     * Kills player and forces the player to spawn at x,y. Use this in response to server message.
     * Choose the random coordinates locally and broadcast the coordinates, used by other clients to
     * call this method.
     * @param x
     * @param y
     */
    public void dieAndSpawnAt(final int x, final int y){
        if (!isInvulnerable) {
            // Remove from render list,
            gameWorld.removePlayer(this);

            // Set Invulnerable for 4 secs
            isInvulnerable = true;
            new DelayedThread(4000l) {
                @Override
                public void run() {
                    super.run();
                    isInvulnerable = false;
                }
            }.start();

            // wait for 3 secs then choose a random location, add to render list
            new DelayedThread(1000l) {
                @Override
                public void run() {
                    super.run();
                    spawn(x, y);
                }
            }.start();

            // TODO -update score based on last hit by field
            deaths++;
//            Gdx.app.log("Player", "Player death count: " + deaths);
        }else{

        }
    }

    public void shield() {
        // Bubble sprite or something?
        Gdx.app.log("Player", "player shielded");
        if (!isInvulnerable) {
            isInvulnerable = true;
            new DelayedThread(5000l) {
                @Override
                public void run() {
                    super.run();
                    isInvulnerable = false;
                }
            }.start();
        }
    }

    /**
     * Spawns player at a random spot. Use if this player is the user's character. Probably only use for testing.
     */
    public void spawn() {
        int xCoordinate = new Random().nextInt(getCollisionLayer().getWidth() - 8);
        int yCoordinate = new Random().nextInt(getCollisionLayer().getHeight() - 2);
        Vector2 worldCoords = setPlayerPosition(xCoordinate, yCoordinate);
        setPosition(worldCoords.x, worldCoords.y);
//        Gdx.app.log(getPlayerPosition().x + "", "getX()");
//        Gdx.app.log(getPlayerPosition().y + "", "getY()");
        gameWorld.addPlayer(this);
    }

    /**
     * Spawns a player at x, y. Specifically for server's use.
     * @param xCoordinate the x coordinate
     * @param yCoordinate the y coordinate
     */
    public void spawn(int xCoordinate, int yCoordinate){
        Vector2 worldCoords = setPlayerPosition(xCoordinate, yCoordinate);
        setPosition(worldCoords.x, worldCoords.y);
        gameWorld.addPlayer(this);
    }

    public void addPowerUp(String powerUp){
        powerUpList.add(powerUp);
    }

    public int getPowerUpList() {
        return powerUpList.size();
    }

    public boolean canPickPowerUp() {
        if (getPowerUpList() < 2) {
            return true;
        }
        return false;
    }

    public ArrayList<String> getArraylist(){
        return powerUpList;
    }

}
