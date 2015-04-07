package com.mayying.tileMapGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.entities.MyTouchpad;
import com.mayying.tileMapGame.entities.Player;
import com.mayying.tileMapGame.entities.ScoreBoard;
import com.mayying.tileMapGame.entities.powerups.Bullet;
import com.mayying.tileMapGame.entities.powerups.DelayedThread;
import com.mayying.tileMapGame.entities.powerups.Mine;
import com.mayying.tileMapGame.entities.powerups.SpawnPowerUps;
import com.mayying.tileMapGame.entities.powerups.factory.PowerUp;
import com.mayying.tileMapGame.entities.powerups.factory.PowerUpFactory;
import com.mayying.tileMapGame.multiplayer.MessageParser;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by Luccan on 2/3/2015.
 */
// MAYBE CHANGE THIS TO A SINGLETON
public class GameWorld {
    private static final String TAG = "GameWorld";
    private MyTouchpad myTouchPad;
    private SpawnPowerUps spawnPowerUps;
    private PowerUp powerUp;
    private Rectangle playerBound;
    private TextureAtlas playerAtlas;
    private Player devicePlayer;
    private int countX = 0, countY = 0;
    private boolean blackout = false;

    private static GameWorld instance;
    public static float TILE_WIDTH, TILE_HEIGHT, delta;

    public Rectangle screenBound;
    public static final Vector<Sprite> bullets = new Vector<Sprite>();
    public final Vector<Mine> mines = new Vector<Mine>();
    private final HashMap<String, Player> players = new HashMap<String, Player>();

    public GameWorld(TiledMapTileLayer playableLayer, List<String> participants, String myId) {
        playerAtlas = new TextureAtlas("img/player2.txt");
        // Initialize all players
        for (int id = 0; id < participants.size(); id++) {
            Player player = new Player(playerAtlas, playableLayer, participants.get(id));
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
        spawnPowerUps = new SpawnPowerUps(playableLayer, this);

        setPlayerBound();
        instance = this;
    }

    public static GameWorld getInstance() {
        return instance;
    }

    // Register a new player onto the scoreboard and add to the world render list
    private void register(Player p) {
        ScoreBoard scoreBoard = ScoreBoard.getInstance();
        scoreBoard.register(p);
        players.put(p.getID(), p);
    }


    public HashMap<String, Player> getPlayers() {
        return players;
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public void drawAndUpdate(Batch batch) {
        spawnPowerUps.draw(batch);
//        for (int i = 0; i < bullets.size(); i++) {
//            bullets.get(i).draw(batch);
//        }

        for (String key : players.keySet()) {
            players.get(key).draw(batch);
        }

        for (int i = 0; i < mines.size(); i++) {
            mines.get(i).draw(batch);
        }
        powerUp = spawnPowerUps.getPowerUp();


        if (blackout) {
            // This causes Player object to disappear for some reason
            ShapeRenderer shapeRenderer = new ShapeRenderer();
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(Color.BLACK));
            shapeRenderer.rect(TILE_WIDTH * 4, 0, TILE_WIDTH * 10, Gdx.graphics.getHeight());

            shapeRenderer.end();
        }
    }

    // Should separate into collision/bounds logic and update movement so that when we factor in concurrent
    // updates from server we can just update movement via setX / setY
    // Movement logic shouldn't be here. OH WELL
    public void playerMovement(float delta) {
        Vector2 velocity = new Vector2();

        velocity.x = getMyTouchPad().getTouchPad().getKnobPercentX();
        velocity.y = getMyTouchPad().getTouchPad().getKnobPercentY();

        float screenLeft = screenBound.getX();
        float screenBottom = screenBound.getY();
        float screenTop = screenBottom + screenBound.getHeight();// + (world.getDevicePlayer().getHeight() / 2);
        float screenRight = screenLeft + screenBound.getWidth();

        float newX = getDevicePlayer().getX();
        float newY = getDevicePlayer().getY();
        if (velocity.x > 0.5) {
            // add back in leftpressed rightpressed etc for direction, if we are using the bullets and stuff
            newX += TILE_WIDTH * getDevicePlayer().getSpeed();
            getDevicePlayer().rightPressed();
        } else if (velocity.x < -0.5) {
            newX -= TILE_WIDTH * getDevicePlayer().getSpeed();
            getDevicePlayer().leftPressed();
        } else if (velocity.y > 0.5) {
            newY += TILE_HEIGHT * getDevicePlayer().getSpeed();
            getDevicePlayer().upPressed();
        } else if (velocity.y < -0.5) {
            newY -= TILE_HEIGHT * getDevicePlayer().getSpeed();
            getDevicePlayer().downPressed();
        }
        // Animate player movement
        // if (velocity.x > 0.5 || velocity.x < -0.5 || velocity.y > 0.5 || velocity.y < -0.5)
        getDevicePlayer().animate(delta);

        countX++;
        countY++;

        if (!getDevicePlayer().isDead && newX >= screenLeft && newX + playerBound.getWidth() <= screenRight) {
            if (myTouchPad.getTouchPad().getKnobPercentX() != 0 && countX > 17) {
                getDevicePlayer().setX(newX);
                countX = 0;
            }
        }
        if (!getDevicePlayer().isDead && newY >= screenBottom && newY <= screenTop) {
            if (myTouchPad.getTouchPad().getKnobPercentY() != 0 && countY > 17) {
                getDevicePlayer().setY(newY);
                countY = 0;
            }
        }

    }

    public MyTouchpad getMyTouchPad() {
        return myTouchPad;
    }


    public boolean pickUpPowerUp() {
        return spawnPowerUps.isPowerUpPickedUp();
    }

    private void setPlayerBound() {
        playerBound = getDevicePlayer().getBoundingRectangle();
    }

    /**
     * @return Player of the current device.
     */
    public Player getDevicePlayer() {
        return devicePlayer;
    }

    public String generateDevicePlayerCoordinatesBroadcastMessage() {
        Vector2 xy = devicePlayer.getPlayerPosition();
        //TODO: safe conversion?
        String broadcastMessage = MessageParser.COMMAND_POSITION + "," + String.valueOf((int) xy.x) + "," + String.valueOf((int) xy.y);
        return broadcastMessage;
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

    public void setPlayerPosition(String playerId, Vector2 pos) {
        Player p = players.get(playerId);
        if (p != null) {
            //TODO: Unsafe conversion?
            p.setPlayerPosition((int) pos.x, (int) pos.y);
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
        ScoreBoard.getInstance().reset();
        PowerUpFactory.getInstance(this).reset();
        instance = null;
    }
}
