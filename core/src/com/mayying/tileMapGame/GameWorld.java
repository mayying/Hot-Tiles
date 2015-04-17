package com.mayying.tileMapGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.entities.MyTouchpad;
import com.mayying.tileMapGame.entities.Player;
import com.mayying.tileMapGame.entities.PlayerMetaData;
import com.mayying.tileMapGame.entities.ScoreBoard;
import com.mayying.tileMapGame.entities.powerups.Bullet;
import com.mayying.tileMapGame.entities.powerups.DelayedThread;
import com.mayying.tileMapGame.entities.powerups.Mine;
import com.mayying.tileMapGame.entities.powerups.SpawnPowerUps;
import com.mayying.tileMapGame.entities.powerups.Thunderbolt;
import com.mayying.tileMapGame.entities.powerups.factory.PowerUp;
import com.mayying.tileMapGame.entities.powerups.factory.PowerUpFactory;
import com.mayying.tileMapGame.multiplayer.MessageParser;
import com.mayying.tileMapGame.screens.Play;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Concurrency issues/ design choices:
 * ????
 */

// MAYBE CHANGE THIS TO A SINGLETON
public class GameWorld {
    private static final String TAG = "GameWorld";
    private MyTouchpad myTouchPad;
    private SpawnPowerUps spawnPowerUps = null;
    private PowerUp powerUp = null;
    private Player devicePlayer;
    private boolean blackout = false;
    private static GameWorld instance;
    private final HashMap<String, Player> players = new HashMap<String, Player>();
    private final HashMap<String, Long> randomSeeds = new HashMap<>();
    private ShapeRenderer shapeRenderer;

    private TiledMapTileLayer playableLayer;
    private Play play;

    public static final long GAME_WAIT_OFFSET = 5000;

    public Rectangle screenBound;
    public static float TILE_WIDTH, TILE_HEIGHT;
    public static final Vector<Sprite> bullets = new Vector<Sprite>();
    public final Vector<Mine> mines = new Vector<Mine>();
    public final Vector<Sprite> thunder = new Vector<>();


    private GameWorld(TiledMapTileLayer playableLayer, String myId, ArrayList<PlayerMetaData> metaData,
                      Play play) {
        this.play = play;
        ScoreBoard.getInstance().reset();
        // Initialize all players
        for (PlayerMetaData data : metaData) {
            Player player = new Player(playableLayer, data);
            player.spawn(); // sync multiplayer spawn positions using message parser and spawn(x,y)
            register(player);
        }
        Gdx.app.log(TAG, "Players: " + players);
        devicePlayer = players.get(myId);
        // TODO - create additional threads to manage the other player's interactions, positions etc (?)

        TILE_WIDTH = playableLayer.getTileWidth();
        TILE_HEIGHT = playableLayer.getTileHeight();

        screenBound = new Rectangle(4 * TILE_WIDTH, TILE_HEIGHT, 10 * TILE_WIDTH, 8 * TILE_HEIGHT);
        myTouchPad = new MyTouchpad();
        //Moved to gameStart();
        this.playableLayer = playableLayer;
        this.play = play;
        shapeRenderer = new ShapeRenderer();
    }

    public static GameWorld getInstance(TiledMapTileLayer playableLayer, String myId, ArrayList<PlayerMetaData> metaData,
                                        Play play) {
        if (instance == null) {
            instance = new GameWorld(playableLayer, myId, metaData, play);
        }
        return instance;
    }

    // For methods that do not want to construct
    public static GameWorld getInstance() {
        return instance;
    }

    // Register a new player onto the scoreboard and add to the world render list
    private void register(Player p) {
        ScoreBoard scoreBoard = ScoreBoard.getInstance();
        scoreBoard.register(p);
        players.put(p.getID(), p);
    }

    public void playerReady(String id, Long randomSeed) {
        this.randomSeeds.put(id, randomSeed);
        if (randomSeeds.size() == players.size()) {
            this.gameStart();
        }
    }

    public void gameStart() {
        spawnPowerUps = new SpawnPowerUps(playableLayer, this, randomSeeds.get(Play.getMultiplayerMessaging().getHostId()));
        play.initializeBurningTiles(randomSeeds.get(Play.getMultiplayerMessaging().getHostId()));
    }

    public HashMap<String, Player> getPlayers() {
        return players;
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public void drawAndUpdate(Batch batch) {
        if (spawnPowerUps != null) {
            spawnPowerUps.draw(batch);
            powerUp = spawnPowerUps.getPowerUp();
        }

        for (String key : players.keySet()) {
            players.get(key).draw(batch);
        }

        for (int i = 0; i < mines.size(); i++) {
            mines.get(i).draw(batch);
        }
        for (int i = 0; i < thunder.size(); i++) {
            thunder.get(i).draw(batch);
        }

        if (blackout) {
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(Color.BLACK));
//            shapeRenderer.setColor(0, 0, 0, 0.7f);
            shapeRenderer.rect(0, 0, Play.camera.viewportWidth, Play.camera.viewportHeight);
            shapeRenderer.end();
        }
    }

    private long lastMovement = -1;
    public final static long MOVEMENT_FREQUENCY = 400;
    public final static long MOVEMENT_FREQUENCY_HASTED = 275;

    // Should separate into collision/bounds logic and update movement so that when we factor in concurrent
    // updates from server we can just update movement via setX / setY
    // Movement logic shouldn't be here. OH WELL
    public void playerMovement(float delta) {
        Vector2 velocity = new Vector2();

        velocity.x = getMyTouchPad().getTouchPad().getKnobPercentX();
        velocity.y = getMyTouchPad().getTouchPad().getKnobPercentY();
        Player player = getDevicePlayer();
        int newX = (int) player.getPlayerPosition().x;
        int newY = (int) player.getPlayerPosition().y;
        boolean pressed = true;

        if (velocity.x > 0.5) {
            newX += player.getSpeed();
            player.rightPressed();
        } else if (velocity.x < -0.5) {
            newX -= player.getSpeed();
            player.leftPressed();
        } else if (velocity.y > 0.5) {
            newY += player.getSpeed();
            player.upPressed();
        } else if (velocity.y < -0.5) {
            newY -= player.getSpeed();
            player.downPressed();
        } else {
            pressed = false;
        }

        long freq = MOVEMENT_FREQUENCY;
        if (getDevicePlayer().isHasted){
            freq = MOVEMENT_FREQUENCY_HASTED;
        }
        if (System.currentTimeMillis() - lastMovement >= freq) {
            if (pressed) {
                lastMovement = System.currentTimeMillis();
                if (!player.isDead)
                    player.setPlayerPosition(newX, newY);
            }
        }

        // Animate player movement
        player.animate(delta);
    }

    public MyTouchpad getMyTouchPad() {
        return myTouchPad;
    }


    public boolean pickUpPowerUp() {
        return spawnPowerUps != null && spawnPowerUps.isPowerUpPickedUp();
    }

    public SpawnPowerUps getSpawnPowerUps() {
        return spawnPowerUps;
    }

    /**
     * @return Player of the current device.
     */
    public Player getDevicePlayer() {
        return devicePlayer;
    }

    public String generateDevicePlayerCoordinatesBroadcastMessage() {
        Vector2 xy = devicePlayer.getPlayerPosition();
        return MessageParser.COMMAND_POSITION + "," + String.valueOf((int) xy.x) + "," + String.valueOf((int) xy.y) + "," + String.valueOf(devicePlayer.getFacing());

    }

    /**
     * @param ID player's ID / key
     * @return player of specified index
     */
    public Player getPlayer(String ID) {
        return players.get(ID);
    }

    // Custom Methods
    // Currently unused to prevent excessive coupling
    public static synchronized void addInstanceToRenderList(Sprite s) {
        bullets.add(s);
    }

    public static synchronized void addBullet(Bullet bullet) {
//        Bullet bullet = new Bullet(new Sprite(new Texture("img/shuriken.png")), 6, world.getDevicePlayer(), 2, (TiledMapTileLayer) map.getLayers().get(0));
        bullets.add(bullet);
    }

    public static synchronized void removeBullet(Bullet bullet) {
        bullet.setAlpha(0);
        bullets.remove(bullet);
        // causes the black box to appear, but probably necessary? not sure how garbage collection works
        bullet.getTexture().dispose();
    }

    public void setBlackout() {
        if (!blackout) {
            blackout = true;
            Jukebox.play("blackout");
            new DelayedThread(3000l) {
                @Override
                public void run() {
                    super.run();
                    blackout = false;
                }
            }.start();
        }
    }

    public synchronized void addMine(Mine mine) {
        mines.add(mine);
    }

    public synchronized void removeMine(Mine mine) {
        mine.getTexture().dispose();
        mine.setAlpha(0);
        mines.remove(mine);
    }

    public synchronized void addThunder(Thunderbolt t) {
        thunder.add(t);
    }

    public synchronized void removeThunder(Thunderbolt t) {
        t.getTexture().dispose();
        t.setAlpha(0);
        thunder.remove(t);
    }

    public void setPlayerPosition(String playerId, Vector2 pos, int facing) {
        Player p = players.get(playerId);
        if (p != null) {
            p.setFacing(facing);
            p.setPlayerPosition((int) pos.x, (int) pos.y);
        } else {
            Gdx.app.log(TAG, "Error while setting player's position. Player is null");
        }
    }

    public void dispose() {
        Gdx.app.log(TAG, "disposing");
        for (String key : players.keySet()) {
            players.get(key).getTexture().dispose();
        }
        players.clear();

        for (int i = 0; i < mines.size(); i++) {
            mines.get(i).getTexture().dispose();
            mines.remove(i);
        }
        PowerUpFactory.getInstance(this).reset();
        instance = null;
    }

    public void lightningAt(final Float x, final Float y, final String senderId) {
//        new DelayedThread(1000, devicePlayer, playableLayer, String.valueOf(x), String.valueOf(y), senderId){
//            @Override
//            public void run(){
//                super.run();
//                new Thunderbolt(Float.valueOf(getMessage()[0]), Float.valueOf(getMessage()[1]), getPlayableLayer());
//                Jukebox.play("lightning");
//                if (getPlayer().getPlayerPosition().equals(new Vector2(Float.valueOf(getMessage()[0]), Float.valueOf(getMessage()[1])))) {
//                    getPlayer().setLastHitBy(getMessage()[2]);
//                    getPlayer().die();
//                }
//            }
//        }.start();
        new Thunderbolt(x, y, playableLayer);
        Jukebox.play("lightning");
        if (devicePlayer.getPlayerPosition().equals(new Vector2(x, y))) {
            devicePlayer.setLastHitBy(senderId);
            devicePlayer.die();
        }
    }
}
