package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by May Ying on 24/2/2015.
 */
public class Player extends Sprite implements InputProcessor {

    /**
     * the movement velocity
     */
    private Vector2 velocity = new Vector2();

    private float speed = 60 * 2, gravity = 60 * 1.8f;

    private boolean canJump;

    private TiledMapTileLayer collisionLayer;
    private TiledMapTileLayer tiledMapTileLayer;

    public Player(Sprite sprite, TiledMapTileLayer collisionLayer) {
        super(sprite);
        this.collisionLayer = collisionLayer;
    }

    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());
        super.draw(batch);
    }

    public void update(float delta) {
        // apply gravity
        velocity.y -= gravity * delta;

        // clamp velocity
        if (velocity.y > speed)
            velocity.y = speed;
        else if (velocity.y < -speed)
            velocity.y = -speed;

        // save old position
        float oldX = getX(), oldY = getY(), tiledWidth = collisionLayer.getTileWidth(), tiledHeight = collisionLayer.getTileHeight();
        boolean collisionX = false, collisionY = false;

        // move on x
        setX(getX() + velocity.x * delta);
        try {
            if (velocity.x < 0)  // going left
                collisionX = collidesLeft();
            else if (velocity.x > 0)
                collisionX = collidesRight();
        } catch (NullPointerException e) {
            e.getMessage();
        }

        if (collisionX)
            setX(oldX);
        velocity.x = 0;

        // move on y
        setY(getY() + velocity.y * delta);

        try {
            if (velocity.y < 0)  // going down
                canJump = collisionY = collidesBottom();

            else if (velocity.y > 0)  // going up
                collisionY = collidesTop();

        } catch (NullPointerException e) {
            e.getMessage();
        }

        if (collisionY) {
            setY(oldY);
            velocity.y = 0;
        }
    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
        tiledMapTileLayer = this.collisionLayer = collisionLayer;
    }

    public boolean collidesRight() {
        boolean collides = false;

        for (float step = 0; step < getHeight(); step += collisionLayer.getTileHeight() / 2)
            if (collides = isCellBlocked(getX() + getWidth(), getY() + step))
                break;
        return collides;
    }

    public boolean collidesLeft() {
        boolean collides = false;

        for (float step = 0; step < getHeight(); step += collisionLayer.getTileHeight() / 2)
            if (collides = isCellBlocked(getX(), getY() + step))
                break;
        return collides;
    }

    public boolean collidesTop() {
        boolean collides = false;

        for (float step = 0; step < getWidth(); step += collisionLayer.getTileWidth() / 2)
            if (collides = isCellBlocked(getX() + step, getY() + getHeight()))
                break;
        return collides;
    }

    public boolean collidesBottom() {
        boolean collides = false;

        for (float step = 0; step < getWidth(); step += collisionLayer.getTileWidth() / 2) {
            if (collides = isCellBlocked(getX() + step, getY()))
                break;
        }
        return collides;
    }

    private boolean isCellBlocked(float x, float y) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("blocked");
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

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
                if(canJump) {
                    velocity.y = speed;
                    canJump = false;
                }
                break;
            case Input.Keys.A:
                velocity.x = -speed;
                break;
            case Input.Keys.D:
                velocity.x = speed;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.D:
                velocity.x = 0;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
