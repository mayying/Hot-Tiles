package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Player;

/**
 * Created by User on 28/2/15.
 */
// Simple powerup in the form of shooting projectiles
// TODO-  landmine powerups
public class Bullet extends Sprite {

    private static final String TAG = "Bullet";
    private Vector2 velocity = new Vector2();
    private float speed = 180 * 2, gravity = 60 * 1.6f;
    private TiledMapTileLayer collisionLayer;
    private Player player;
    private int direction;
    private float distanceX;
    private float distanceY;
    private float travelledX;
    private float travelledY;
//    private

    /**
     * Creates a general bullet object with specified sprite, direction, and Player
     * (to get position and get bullet owner for killcount?)
     * Abstract away other stuff as necessary depending on future powerup ideas
     *
     * @param sprite    TEH SPRITE
     * @param direction Direction indicated by 8426 int (see numpad)
     * @param player    the player shooting this. X and Y coords are obtained from player
     * @param numTiles  number of tiles this bullet should travel. Multiply this with tile width/height
     *                  to get appropriate distance
     */
    public Bullet(Sprite sprite, int direction, Player player, int numTiles, TiledMapTileLayer collisionLayer) {
        super(sprite);
        this.collisionLayer = collisionLayer;
        this.direction = direction;
        this.player = player;
        distanceX = collisionLayer.getTileWidth() * numTiles;
        distanceY = collisionLayer.getTileHeight() * numTiles;
        // originate from player
        // this.setPosition(player.getX()+player.getWidth(), player.getY()-player.getHeight()/2);
        // this.setPosition(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 4);
        this.setPosition(player.getX(), player.getY() + player.getHeight() / 4);


        switch (direction) {
            case 8:
                velocity.y = -speed;
                break;
            case 2:
                velocity.y = speed;
                break;
            case 4:
                velocity.x = -speed;
                break;
            case 6:
                velocity.x = speed;
                break;
        }
    }

    public void draw(Batch batch) {

        update(Gdx.graphics.getDeltaTime());
        super.draw(batch);
    }

    public void update(float delta) {
        // while bullet life not ended and no collision with players

        if (isAlive()) {
//            Gdx.app.log(TAG, "moving bullet");
            // move on x
            setX(getX() + velocity.x * delta);
            travelledX += velocity.x * delta;
            setY(getY() + velocity.y * delta);
            travelledY += velocity.y * delta;
        } else {
            // dispose of this thing?
//            this.getTexture().dispose();
            // Don't think there's a difference between these 2
//            Play.bullets.remove(this);


            GameWorld.removeBullet(this);
        }


    }

    private boolean isAlive() {
//        Gdx.app.log(TAG, "X: " + distanceX +" vs "+travelledX + "Y: "+distanceY + " vs "+ travelledY);
        return distanceX > Math.abs(travelledX) && distanceY > Math.abs(travelledY);
    }


    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }


}
