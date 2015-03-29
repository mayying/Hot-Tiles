package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Player;

/**
 * Created by User on 10/3/15.
 */
public class Mine extends Sprite implements Collidable, Usable {
    Player player;
    TiledMapTileLayer collisionLayer;
    long mineCreated = 0l;
    public Mine(Sprite sprite, Player player, TiledMapTileLayer collisionLayer) {
        super(sprite);
        // Must only draw to this player, specify by player index in main draw method?
        this.player = player;
        this.collisionLayer = collisionLayer;
        // originate from player
        this.setPosition(player.getX(), player.getY());
//        mineCreated = System.currentTimeMillis();
    }

    @Override
    public void draw(Batch batch) {
        update();
        super.draw(batch);
    }
    // Override this method if you want to do more than just checking collisions
    public void update() {
        // Trigger mine if player on top
        collisionCheck();


    }
    // Override this method to desired mine behavior
    @Override
    public void onCollisionDetected(Player hitPlayer) {
        if (!hitPlayer.isInvulnerable) GameWorld.removeMine(this);
        // TODO - KD logic for players, depending on the subclass type of mine
    }

    @Override
    public void collisionCheck() {
        // Mine takes 2 seconds before activating
        if(System.currentTimeMillis() - mineCreated > 2000l) {
            // Check for every player because only this device sees the mine, it has to tell the server if the mine hits.
            for(int i=0; i<GameWorld.getNumPlayers(); i++) {
                Player p = GameWorld.getPlayer(i);
                if (this.getX() == p.getX() && this.getY() == p.getY())
                    onCollisionDetected(p);
            }
        }

    }



    @Override
    public void use(Player[] players) {
        // To Delay the mine before it can explode
        mineCreated = System.currentTimeMillis();

        // Add to render list
        GameWorld.addMine(this);
    }
}
