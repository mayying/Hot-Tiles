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
import com.mayying.tileMapGame.multiplayer.MessageParser;
import com.mayying.tileMapGame.screens.Play;
import com.mayying.tileMapGame.tween.SpriteAccessor;

import java.util.Random;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

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
    private Sprite sprite; // powerup sprite
    private Random spawnRNG;
    private boolean powerUpIsPickedUp = false;
    Vector2 position = new Vector2(), coords = new Vector2();
    private TweenManager tweenManager;

    public SpawnPowerUps(TiledMapTileLayer tileLayer, GameWorld world, long seed) {
        this.tileLayer = tileLayer;
        this.world = world;
        powerUpFactory = PowerUpFactory.getInstance(world);
        spawnRNG = new Random(seed);
        sprite = new Sprite();

        tweenManager = new TweenManager();
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
    }

    public void draw(Batch batch) {
        switch (state) {
            case 0:
                // !created
                // picking random stringID from list
                powerUpIsPickedUp = false;
                powerUp = powerUpFactory.createPowerUp(spawnRNG.nextInt(5)); //TODO - only 5? no hardcode?
//                powerUp = powerUpFactory.createPowerUp(4); //TODO - revert back to to previous line after done debugging

                sprite = new Sprite(powerUp.getTextureVector());

                // set random spawn time for powerup
                //TODO - only 5? no hardcode?
//                randomSpawnTime = (float) spawnRNG.nextInt(10);
                randomSpawnTime = (float) spawnRNG.nextInt(1);

                // sprite position
                coords.x = spawnRNG.nextInt(tileLayer.getWidth() - 8);
                coords.y = spawnRNG.nextInt(tileLayer.getHeight() - 2);

                position.x = tileLayer.getTileWidth() / 2 - sprite.getWidth() / 2 + tileLayer.getTileWidth() * (coords.x + 4);
                position.y = tileLayer.getTileHeight() / 4 + tileLayer.getTileHeight() * (coords.y + 1);
//                Gdx.app.log(powerUp.getName() + " position", coords.x + ", " + coords.y);
                sprite.setPosition(position.x, position.y);
                Timeline.createSequence().beginSequence()
                        .push(Tween.set(sprite, SpriteAccessor.POSITION).target(position.x, position.y))
                        .push(Tween.to(sprite, SpriteAccessor.POSITION, 0.05f).target(position.x, position.y + 5f).repeatYoyo(30, 0.2f))
                        .end().start(tweenManager);

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
                    tweenManager.update(Gdx.graphics.getDeltaTime());
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
        // README: PowerUp disappear when a player steps over it.
        // If not implemented this way, we will have to broadcast message if powerup is picked up etc.
//        state = 0; // picked up, restart state
        if (player.canPickPowerUp() && player.equals(world.getDevicePlayer())) {
            Play.broadcastMessage(MessageParser.POWERUP_PICKED_UP);
            state = 0; // picked up, restart state
            player.addPowerUp(powerUp);
            powerUpIsPickedUp = true;
            Gdx.app.log("HT_Powerup", powerUp.getName() + " picked up.");
        }
    }

    //Resets the power up when another play picks it up
    public void reset(){
        state = 0;
    }

    @Override
    public void collisionCheck() {
        // Check for every player. Unless we want to do a client-host handshake implementation, this is more concurrent with
        // the rest of our implementation
        Player player = world.getDevicePlayer();
//        for (Player player : world.getPlayers().values()) {
            Vector2 playerPos = player.getPlayerPosition();
//            Gdx.app.log("Player Coords: ", playerPos.toString());
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
