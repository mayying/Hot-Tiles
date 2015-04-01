package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Player;
import com.mayying.tileMapGame.entities.powerups.factory.PowerUp;
import com.mayying.tileMapGame.entities.powerups.factory.PowerUpFactory;

import java.util.Random;

/**
 * Created by HtooWaiYan on 25-Mar-15.
 */
public class SpawnPowerUps implements Collidable {
    private PowerUpFactory powerUpFactory;
    private PowerUp powerUp;
    private GameWorld world;
    private TiledMapTileLayer tileLayer;
    private float spawnTime, countTime, randomSpawnTime;
    private int state = 0; // 0 - !created, 1 - created, 2 - drawn, 3 - clean up
    private Sprite sprite;          // powerup sprite
    private boolean powerUpIsPickedUp = false;
    Vector2 position = new Vector2(), coords = new Vector2();

    public SpawnPowerUps(TiledMapTileLayer tileLayer, GameWorld world) {
        this.tileLayer = tileLayer;
        this.world = world;
        powerUpFactory = PowerUpFactory.getInstance();
        sprite = new Sprite();
    }

    public void draw(Batch batch) {
        switch (state) {
            case 0:
                // !created
                // picking random stringID from list
                powerUpIsPickedUp = false;
                powerUp = powerUpFactory.createPowerUp(new Random().nextInt(5));
                this.sprite = new Sprite(powerUp.getTextureVector());

                // set random spawn time for powerup
                randomSpawnTime = (float) new Random().nextInt(10);

                // sprite position
                coords.x = new Random().nextInt(tileLayer.getWidth() - 8);
                coords.y = new Random().nextInt(tileLayer.getHeight() - 2);

                position.x = tileLayer.getTileWidth() / 2 - sprite.getWidth() / 2 + tileLayer.getTileWidth() * (coords.x + 4);
                position.y = tileLayer.getTileHeight() / 4 + tileLayer.getTileHeight() * (coords.y + 1);
//                Gdx.app.log(powerUp.getName() + " position", coords.x + ", " + coords.y);
                sprite.setPosition(position.x, position.y);
                spawnTime = 0;        // count until 3s
                countTime = 0;
                state++; //created
                break;

            case 1:
                // created
                // check whether the sprite is ready to spawn/ be drawn
                if (countTime > randomSpawnTime) {
                    state++; //drawn
                } else {
                    countTime += Gdx.graphics.getDeltaTime();
                }
                break;
            case 2:
                // drawn
                spawnTime += (Gdx.graphics.getDeltaTime()); //getDeltaTime is actually not in millis, consider using System.currentTimeMillis instead
//                Gdx.app.log("spawn time",String.valueOf(spawnTime));
                if (spawnTime <= 6) {
                    sprite.draw(batch);
                } else {
                    sprite.getTexture().dispose();
                    state = 0;
                }
                // check collision between player and powerup to pick up powerup
                collisionCheck();
                break;
        }
    }

    @Override
    public void onCollisionDetected(Player player) {
//        Gdx.app.log("Player", "Player die from fire Q_Q");
        if (player.canPickPowerUp()) {
            powerUpIsPickedUp = true;
            player.addPowerUp(powerUp.getName());
            state = 0; // picked up, restart state
            Gdx.app.log("Powerup", powerUp.getName() + " picked up.");
        }
    }

    @Override
    public void collisionCheck() {
        // Only check for this device's player -  power ups appear to everyone differently
        Player player = world.getPlayer();

        Vector2 playerPos = player.getPlayerPosition();
//        Gdx.app.log("Player Coords: ", playerPos.toString());
        if (playerPos.equals(this.coords)) {
            onCollisionDetected(player);
        }

    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public boolean isPowerUpPickedUp() {
        return powerUpIsPickedUp;
    }
}
