package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.GameWorld;

import java.util.concurrent.Semaphore;

/**
 * Created by User on 10/3/15.
 */
public class Mine  extends Sprite implements Collidable {
    Player player;
    TiledMapTileLayer collisionLayer;
    long mineCreated = 0l;
    boolean isAlive = true;
    int count = 0;
    private static final Object lock = new Object();
    private static final Semaphore sem = new Semaphore(1);
    public Mine(Sprite sprite, Player player, TiledMapTileLayer collisionLayer) {
        super(sprite);
        // Must only draw to this player, specify by player index in main draw method?
        this.player = player;
        this.collisionLayer = collisionLayer;
        // originate from player
        this.setPosition(player.getX(), player.getY());
        mineCreated = System.currentTimeMillis();
    }

    @Override
    public void draw(Batch batch) {
        update();
        super.draw(batch);
    }

    public void update() {
        // TODO - get device ID to check if this mine is owned by the current player

        //TODO -  show the mine by setting alpha?
        // Trigger mine if player on top
        collisionCheck();


    }

    @Override
    public void onCollisionDetected(Player hitPlayer) {

        Gdx.app.log("Mine", "Cheekababoom");
        GameWorld.removeMine(this);
        // TODO - KD logic for players, depending on the subclass type of mine
    }

    @Override
    public void collisionCheck() {
        // Mine takes 2 seconds before activating
        if(System.currentTimeMillis() - mineCreated > 2000l) {
            // Call this method in update
            Vector2 pos = getCellFromPosition(Math.round(this.getX()), Math.round(this.getY()));
            Gdx.app.log("Mine Position", pos.toString());

            // For all Players...
            if (getCellFromPosition(Math.round(player.getX()), Math.round(player.getY())).equals(pos)) {
                onCollisionDetected(player);
            }
        }

    }

    // Should probably refactor this into a Utility class but whatever
    private Vector2 getCellFromPosition(int x, int y) {
        Vector2 vector2 = new Vector2();
        vector2.x = collisionLayer.getTileWidth() / 2 - getWidth() / 2;
        vector2.y = 200 + getHeight() + collisionLayer.getTileHeight() / 2;

        if (x != 0) {
            vector2.x += collisionLayer.getTileWidth() * x;
        }

        if (y != 0) {
            vector2.y += collisionLayer.getTileHeight() * y;
        }

        return vector2;
    }
}
