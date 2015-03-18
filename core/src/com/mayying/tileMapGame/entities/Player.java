package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.GameWorld;

/**
 * Created by May Ying on 24/2/2015.
 */
public class Player extends Sprite {

    private Vector2 velocity = new Vector2();
    private float speed = 60 * 2, gravity = 60 * 1.6f;
    private long lastPressed = 0l, lastHitTime = 0l; // in case of null pointer or whatever;
    private int facing;

    private TiledMapTileLayer collisionLayer;
    private Player lastHitBy;


    public Player(Sprite sprite, TiledMapTileLayer collisionLayer) {
        super(sprite);
        this.collisionLayer = collisionLayer;
        facing = 6;
    }


    public Vector2 getPosition(int x, int y) {
        Vector2 vector2 = new Vector2();
        vector2.x = collisionLayer.getTileWidth() / 2 - getWidth() / 2 + collisionLayer.getTileWidth() * (x + 3);
        vector2.y = getHeight() + collisionLayer.getTileHeight() / 2 + collisionLayer.getTileHeight() * y;

        return vector2;
    }


    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());
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

    public void rightPressed() {
       velocity.x = speed;
        facing = 6;
    }

    public void leftPressed() {
        velocity.x = -speed;
        facing = 4;
    }

    public void upPressed() {
       velocity.y = speed;
        facing = 8;
    }

    public void downPressed() {
       velocity.y = -speed;
        facing = 2;
    }

    public void leftRightReleased() {
        velocity.x = 0;
    }

    public void upDownReleased() {
        velocity.y = 0;
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
        //TODO - Check for every bullet, laser beam, mine etc whether it was a hit

        // TODO - Do bullets after it is confirmed that synchronization wont pose an issue


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
}
