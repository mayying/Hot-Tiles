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
import com.mayying.tileMapGame.entities.powerups.factory.PowerUp;
import com.mayying.tileMapGame.screens.Play;

import java.util.Random;

/**
 * Created by May Ying on 24/2/2015.
 */
public class Player extends Sprite {
    private int facing; // index of player
    private TiledMapTileLayer collisionLayer;
    private Animation forward, backward, left, right, burnt;
    private float speed = 1, animationTime = 0f;
    private long lastPressed = 0l, lastHitTime = 0l; // in case of null pointer or whatever;
    private boolean isFrozen = false, isInverted = false;// for freezing animation and stuff?
    private String ID, lastHitBy, characterName;
    private final TextureAtlas playerAtlas;

    public PowerUp[] powerUpList = new PowerUp[2];
    public boolean isInvulnerable = false, isDead = false;

    public Player(TextureAtlas atlas, TiledMapTileLayer collisionLayer, String ID, String characterName) {
        super(new Animation(1 / 2f, atlas.findRegions(characterName + "forward")).getKeyFrame(0));
        this.ID = ID;
        this.collisionLayer = collisionLayer;
        this.playerAtlas = atlas;
        this.characterName = characterName;

        facing = 8;

        forward = new Animation(1 / 2f, playerAtlas.findRegions(characterName + "forward"));
        backward = new Animation(1 / 2f, playerAtlas.findRegions(characterName + "backward"));
        left = new Animation(1 / 2f, playerAtlas.findRegions(characterName + "left"));
        right = new Animation(1 / 2f, playerAtlas.findRegions(characterName + "right"));
        burnt = new Animation(1 / 6f, playerAtlas.findRegions(characterName + "burnt"));

        forward.setPlayMode(Animation.PlayMode.LOOP);
        backward.setPlayMode(Animation.PlayMode.LOOP);
        left.setPlayMode(Animation.PlayMode.LOOP);
        right.setPlayMode(Animation.PlayMode.LOOP);
        burnt.setPlayMode(Animation.PlayMode.LOOP);
    }


    // Given matrix position, set position on map (non-matrix)
    public void setPlayerPosition(int x, int y) {
        float _x = collisionLayer.getTileWidth() / 2 - getWidth() / 2 + collisionLayer.getTileWidth() * (x + 4);
        float _y = collisionLayer.getTileHeight() / 4 + collisionLayer.getTileHeight() * (y + 1);
        this.setPosition(_x, _y);
    }

    // Return matrix position
    public Vector2 getPlayerPosition() {
        Vector2 vector2 = new Vector2();
        vector2.x = (float) (Math.floor(getX() / collisionLayer.getTileWidth()) - 4);
        vector2.y = (float) (Math.floor(getY() / collisionLayer.getTileHeight()) - 1);
        return vector2;
    }

    public void draw(Batch batch) {
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
        facing = 2;
    }

    public void downPressed() {
        facing = 8;
    }

    public void animate(float delta) {
        animationTime += delta + 0.01;
//        forward.
        setRegion(isDead ? burnt.getKeyFrame(animationTime) : facing == 4 ? left.getKeyFrame(animationTime) : facing == 6 ? right.getKeyFrame(animationTime) :
                facing == 2 ? backward.getKeyFrame(animationTime) : forward.getKeyFrame(animationTime));
    }


    // shuriken
    public boolean isHit(float x, float y) {
        //bottom right
        float x_1 = getX() + getWidth() / 2;
        float y_1 = getY() - getHeight() / 2;

        //top left
        float x_2 = getX() - getWidth() / 2;
        float y_2 = getY() + getHeight() / 2;

        return ((x - x_2) <= getWidth() && (x - x_2) >= 0) &&
                ((y - y_1) >= 0 && (y - y_1) <= getHeight());
    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setLastHitBy(String lastHitBy) {
        this.lastHitBy = lastHitBy;
        Gdx.app.log("Player " + getID(), "Last Hit By: " + lastHitBy);
        this.lastHitTime = System.currentTimeMillis();
    }

    public String getLastHitBy() {
        // Setting 4 seconds now, cause it's the duration of most of our power ups
        return (System.currentTimeMillis() - lastHitTime) <= 4000l ? lastHitBy : "null";
    }

    public void burn(String lastHitPlayerID) {
        // For fire mine, mainly to set last hit
        setLastHitBy(lastHitPlayerID);
        die();
    }

    public void freeze(String lastHitPlayerID) {
        setLastHitBy(lastHitPlayerID); //static cause i'm lazy
        this.freeze(3000l);
    }

    public void freeze(long millis) {
        // User can only be inflicted with one speed modifier at any time. Reduce complexity of code
        // and eliminate interaction for when user is frozen and then inverted or something like that.
        // TL;DR GOT LAZY
        if (speed == 1 && !isInvulnerable) {
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
        // Last hit is set when called by message parser
        Gdx.app.log("Player", "player inverted");
        if (speed == 1 && !isInvulnerable) {
            setSpeed(-1);
            new DelayedThread(4000l) {
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

    public String getName() {
        return characterName;
    }

    /**
     * Kills the player. Use only if this is the device's player. Used on player of current device.
     * Update the scoreboard, then sends the Vector2 spawn coordinates and updated score over the server.
     *
     * @return a Vector2 of where the player will respawn
     */
    public Vector2 die() {
        // Commits sudoku
        if (!isInvulnerable) {
            isDead = true;
            final int xCoordinate = new Random().nextInt(getCollisionLayer().getWidth() - 8);
            final int yCoordinate = new Random().nextInt(getCollisionLayer().getHeight() - 2);
            new DelayedThread(1500l, this) {
                @Override
                public void run() {
                    // Commented out unsafe method, dead players should not share the same ArrayList
                    // as players because it will lead to conflict
//                    gameWorld.addPlayer(getPlayer());
                    Jukebox.play("fire");
                    super.run();
                    Jukebox.stop("fire");
//                    gameWorld.removePlayer(getPlayer());
                    //This thread is called after dispose and causes a new player to be added.
                    spawn(xCoordinate, yCoordinate);
                }
            }.start();
            // Remove from render list,
//            gameWorld.removePlayer(this);

            // Set Invulnerable for 4 secs
            isInvulnerable = true;
            new DelayedThread(4000l) {
                @Override
                public void run() {
                    super.run();
                    isInvulnerable = false;
                }
            }.start();

            // Format: "effect","dieAndSpawn", x, y
            Play.broadcastMessage(
                    "effect",
                    "dieAndSpawn",
                    xCoordinate + "", yCoordinate + ""
            );

            // Update score (local + server)
            updateScore();

            return new Vector2(xCoordinate, yCoordinate);
        } else {
            return null;
        }
    }

    private void updateScore() {
        // TODO - Update score in other devices
        String killerID = getLastHitBy();
        Gdx.app.log("Player " + getID(), "Killed by Player " + killerID);
        ScoreBoard.getInstance().incrementKillsAndOrDeath(killerID.equals(getID()) ? "null" : killerID, getID());

        // Format: "score", killerIdx, victimIdx
        Play.broadcastMessage("score", killerID, getID());
    }


    /**
     * Kills player and forces the player to spawn at x,y. Use this in response to server message.
     * Choose the random coordinates locally and broadcast the coordinates, used by other clients to
     * call this method.
     *
     * @param x
     * @param y
     */
    public void dieAndSpawnAt(final int x, final int y) {

        // Remove from render list,
//            gameWorld.removePlayer(this);
        isDead = true;
        // Set Invulnerable for 4 secs
        isInvulnerable = true;
        new DelayedThread(4000l) {
            @Override
            public void run() {
                super.run();
                isInvulnerable = false;
            }
        }.start();

        // wait for 1.5 secs then choose a random location, add to render list
        new DelayedThread(1500l) {
            @Override
            public void run() {
                Jukebox.play("fire");
                super.run();
                Jukebox.stop("fire");
                spawn(x, y);
            }
        }.start();

        // server will send a message to update score

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
     * THIS METHOD NO LONGER ADDS A PLAYER TO THE ARRAYLIST FOR SAFETY.
     */
    public void spawn() {
        int xCoordinate = new Random().nextInt(getCollisionLayer().getWidth() - 8);
        int yCoordinate = new Random().nextInt(getCollisionLayer().getHeight() - 2);
        setPlayerPosition(xCoordinate, yCoordinate);
//        Gdx.app.log(getPlayerPosition().x + "", "getX()");
//        Gdx.app.log(getPlayerPosition().y + "", "getY()");
//        gameWorld.addPlayer(this);
        isDead = false;
    }

    /**
     * Spawns a player at x, y. Specifically for server's use. THIS METHOD NO LONGER ADDS A PLAYER
     * TO THE ARRAYLIST FOR SAFETY.
     *
     * @param xCoordinate the x coordinate
     * @param yCoordinate the y coordinate
     */
    public void spawn(int xCoordinate, int yCoordinate) {
        setPlayerPosition(xCoordinate, yCoordinate);
//        gameWorld.addPlayer(this);
        isDead = false;
    }


    public void addPowerUp(PowerUp powerUp) {
        for (int i = 0; i < powerUpList.length; i++) {
            if (powerUpList[i] == null) {
                powerUpList[i] = powerUp;
                break;
            }
        }

//        Gdx.app.log("Player", "Picked up: "+powerUp.getName() + " powerUp");
//        Gdx.app.log("Player",Arrays.toString(powerUpList) + "PLayer");

    }

    public void removePowerUp(int idx) {
//        Gdx.app.log("Player", "Removed: "+powerUpList[idx].getName());
        powerUpList[idx] = null;
    }


    public PowerUp getPowerUp(int idx) {
        return powerUpList[idx];
    }

    public boolean canPickPowerUp() {
        for (int i = 0; i < powerUpList.length; i++) {
            if (powerUpList[i] == null)
                return true;
        }
        return false;
    }

    public String getID() {
        return ID;
    }
}
