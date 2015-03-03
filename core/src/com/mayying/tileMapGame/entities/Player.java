package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    /**
     * the movement velocity
     */
    private Vector2 velocity = new Vector2();

    private float speed = 60 * 2, gravity = 60 * 1.6f;

    private TiledMapTileLayer collisionLayer;

    private long lastPressed;
    private int facing;

    public Player(Sprite sprite, TiledMapTileLayer collisionLayer) {
        super(sprite);
        this.collisionLayer = collisionLayer;
        lastPressed = System.currentTimeMillis();
        facing = 6;
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

    public void update(float delta) {
        // apply gravity
//        velocity.y -= gravity * delta;

        // clamp velocity
//        if (velocity.y > speed)
//            velocity.y = speed;
//        else if (velocity.y < -speed)
//            velocity.y = -speed;

        // save old position
        float oldX = getX(), oldY = getY(), tiledWidth = collisionLayer.getTileWidth(), tiledHeight = collisionLayer.getTileHeight();
        boolean collisionX = false, collisionY = false;

//        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
//            velocity.x = -speed;
//        }else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
//            velocity.x = speed;
//        }

        // move on x
        setX(getX() + velocity.x * delta);

        try {
            if (velocity.x < 0) {
                // top left
                collisionX = collisionLayer.getCell((int) (getX() / tiledWidth), (int) ((getY() + getHeight()) / tiledHeight))
                        .getTile().getProperties().containsKey("blocked");

                // middle left
                if (!collisionX)
                    collisionX = collisionLayer.getCell((int) (getX() / tiledWidth), (int) ((getY() + getHeight() / 2) / tiledHeight))
                            .getTile().getProperties().containsKey("blocked");

                // bottom left
                if (!collisionX)
                    collisionX = collisionLayer.getCell((int) (getX() / tiledWidth), (int) ((getY() / tiledHeight)))
                            .getTile().getProperties().containsKey("blocked");

            } else if (velocity.x > 0) {
                // top right
                collisionX = collisionLayer.getCell((int) ((getX() + getWidth()) / tiledWidth), (int) ((getY() + getHeight()) / tiledHeight))
                        .getTile().getProperties().containsKey("blocked");

                // middle right
                collisionX = collisionLayer.getCell((int) ((getX() + getWidth()) / tiledWidth), (int) (((getY() + getHeight()) / 2) / tiledHeight))
                        .getTile().getProperties().containsKey("blocked");

                // bottom right
                collisionX = collisionLayer.getCell((int) ((getX() + getWidth()) / tiledWidth), (int) (getY() / tiledHeight))
                        .getTile().getProperties().containsKey("blocked");

            }
        } catch (NullPointerException e) {
            e.getMessage();
        }

        if (collisionX)
            setX(oldX);
//        velocity.x = 0;

        // move on y
        setY(getY() + velocity.y * delta);

        try {
            if (velocity.y < 0) {
                // bottom left
                collisionY = collisionLayer.getCell((int) (getX() / tiledWidth), (int) (getY() / tiledHeight))
                        .getTile().getProperties().containsKey("blocked");

                // bottom middle
                if (!collisionY)
                    collisionY = collisionLayer.getCell((int) ((getX() + getWidth() / 2) / tiledWidth), (int) (getY() / tiledHeight))
                            .getTile().getProperties().containsKey("blocked");

                // bottom right
                if (!collisionY)
                    collisionY = collisionLayer.getCell((int) ((getX() + getWidth()) / tiledWidth), (int) (getY() / tiledHeight))
                            .getTile().getProperties().containsKey("blocked");

            } else if (velocity.y > 0) {
                // top left
                collisionY = collisionLayer.getCell((int) (getX() / tiledWidth), (int) ((getY() + getHeight()) / tiledHeight))
                        .getTile().getProperties().containsKey("blocked");

                // top middle
                if (!collisionY)
                    collisionY = collisionLayer.getCell((int) ((getX() + getWidth() / 2) / tiledWidth), (int) ((getY() + getHeight() / 2) / tiledHeight))
                            .getTile().getProperties().containsKey("blocked");

                // top right
                if (!collisionY)
                    collisionY = collisionLayer.getCell((int) ((getX() + getWidth()) / tiledWidth), (int) ((getY() + getHeight()) / tiledHeight))
                            .getTile().getProperties().containsKey("blocked");
            }
        } catch (NullPointerException e) {
            e.getMessage();
        }

        if (collisionY) {
            setY(oldY);
//            velocity.y = 0;
        }
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

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
}
