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
    Vector2 coords = new Vector2();
    long mineCreated = 0l;
    public Mine(Sprite sprite, Player player, TiledMapTileLayer collisionLayer) {
        super(sprite);
        // Must only draw to this player, specify by player index in main draw method?
        this.player = player;
        this.collisionLayer = collisionLayer;
        // originate from player
        coords.x = player.getPlayerPosition().x;
        coords.y = player.getPlayerPosition().y;

        Vector2 position = new Vector2();
        position.x = GameWorld.TILE_WIDTH / 2 - sprite.getWidth() / 2 + GameWorld.TILE_WIDTH * (coords.x + 4);
        position.y = GameWorld.TILE_HEIGHT / 4 + GameWorld.TILE_HEIGHT * (coords.y + 1);

        this.setPosition(position.x, position.y);
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
        if(System.currentTimeMillis() - mineCreated > 2000) {
            // Check for every player because only this device sees the mine, it has to tell the server if the mine hits.
            for(String key : GameWorld.getPlayers().keySet()) {
                Player p = GameWorld.getPlayer(key);
                if (p.getPlayerPosition().equals(coords))
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
