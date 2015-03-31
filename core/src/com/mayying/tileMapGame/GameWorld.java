package com.mayying.tileMapGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.entities.DirectionGestureDetector;
import com.mayying.tileMapGame.entities.MyTouchpad;
import com.mayying.tileMapGame.entities.Player;
import com.mayying.tileMapGame.entities.powerups.Bullet;
import com.mayying.tileMapGame.entities.powerups.DelayedThread;
import com.mayying.tileMapGame.entities.powerups.Mine;
import com.mayying.tileMapGame.entities.powerups.SpawnPowerUps;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Luccan on 2/3/2015.
 */
public class GameWorld {
    private static Player player; // static cause i'm lazy. Replace with array of all players in game.
    // Better to separate into bullets and mines for now to decouple so we can do stuff like remove all mines or whatever
    private MyTouchpad myTouchPad;
    private SpawnPowerUps spawnPowerUps;

    // Better to separate into bullets and mines for now to decouple so we can do stuff like remove all mines or whatever
    public static Rectangle screenBound;
    public static Vector<Sprite> bullets = new Vector<Sprite>();
    public static Vector<Mine> mines = new Vector<Mine>();

    private DirectionGestureDetector directionGestureDetector;
    private Rectangle playerBound;
    private TextureAtlas playerAtlas;

    public static float TILE_WIDTH, TILE_HEIGHT, delta;
    private static final ArrayList<Player> players = new ArrayList<Player>();
    private static boolean blackout = false;
    private int countX = 0, countY = 0;

    public GameWorld(TiledMapTileLayer playableLayer) {
        playerAtlas = new TextureAtlas("img/player3.txt");
        player = new Player(playerAtlas, playableLayer, this);
        player.spawn();
        // TODO - create additional threads to manage the other player's interactions, positions etc

        TILE_WIDTH = playableLayer.getTileWidth();
        TILE_HEIGHT = playableLayer.getTileHeight();

        screenBound = new Rectangle(4 * TILE_WIDTH, TILE_HEIGHT, 10 * TILE_WIDTH, 8 * TILE_HEIGHT);
        myTouchPad = new MyTouchpad();
        spawnPowerUps = new SpawnPowerUps(playableLayer, getPlayer());

        setPlayerBound();

        directionGestureDetector = new DirectionGestureDetector(new DirectionGestureDetector.DirectionListener() {
            float screenLeft = screenBound.getX();
            float screenBottom = screenBound.getY();
            float screenTop = screenBottom + screenBound.getHeight();// + (world.getPlayer().getHeight() / 2);
            float screenRight = screenLeft + screenBound.getWidth();

            float newX;
            float newY;
            float x = delta;

            @Override
            public void onLeft() {

                newX = getPlayer().getX();
                newY = getPlayer().getY();
                newX -= TILE_WIDTH * player.getSpeed();
                if (newX >= screenLeft && newX + playerBound.getWidth() <= screenRight) {
                    getPlayer().setX(newX);
                }
            }

            @Override
            public void onRight() {
                newX = getPlayer().getX();
                newY = getPlayer().getY();
                newX += TILE_WIDTH * player.getSpeed();
                player.rightPressed();
                player.animate(delta);
                if (newX >= screenLeft && newX + playerBound.getWidth() <= screenRight) {
                    getPlayer().setX(newX);
                }
            }

            @Override
            public void onUp() {
                newX = getPlayer().getX();
                newY = getPlayer().getY();
                newY += TILE_HEIGHT * player.getSpeed();
                player.upPressed();
                player.animate(delta);
                if (newY >= screenBottom && newY <= screenTop) {
                    getPlayer().setY(newY);
                }
            }

            @Override
            public void onDown() {
                newX = getPlayer().getX();
                newY = getPlayer().getY();
                newY -= TILE_HEIGHT * player.getSpeed();
                player.downPressed();
                player.animate(delta);
                if (newY >= screenBottom && newY <= screenTop) {
                    getPlayer().setY(newY);
                }
            }
        });
    }

    public void drawAndUpdate(Batch batch) {
        spawnPowerUps.draw(batch);
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(batch);
        }

        for (int i = 0; i < players.size(); i++) {
            players.get(i).draw(batch);
        }

        for (int i = 0; i < mines.size(); i++) {
            mines.get(i).draw(batch);
        }
        spawnPowerUps.draw(batch);


        if (blackout) {
            // This causes Player object to disappear for some reason
            ShapeRenderer shapeRenderer = new ShapeRenderer();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(Color.BLACK));
            shapeRenderer.rect(120, 0, 1000, 720);
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
        float screenTop = screenBottom + screenBound.getHeight();// + (world.getPlayer().getHeight() / 2);
        float screenRight = screenLeft + screenBound.getWidth();

        float newX = getPlayer().getX();
        float newY = getPlayer().getY();
        if (velocity.x > 0.5) {
            // add back in leftpressed rightpressed etc for direction, if we are using the bullets and stuff
            newX += TILE_WIDTH * player.getSpeed();
            getPlayer().rightPressed();
        } else if (velocity.x < -0.5) {
            newX -= TILE_WIDTH * player.getSpeed();
            getPlayer().leftPressed();
        } else if (velocity.y > 0.5) {
            newY += TILE_HEIGHT * player.getSpeed();
            getPlayer().upPressed();
        } else if (velocity.y < -0.5) {
            newY -= TILE_HEIGHT * player.getSpeed();
            getPlayer().downPressed();
        }
        // Animate player movement
       // if (velocity.x > 0.5 || velocity.x < -0.5 || velocity.y > 0.5 || velocity.y < -0.5)
            getPlayer().animate(delta);

        countX++;
        countY++;

        if (!Player.isDead && newX >= screenLeft && newX + playerBound.getWidth() <= screenRight) {
            if (myTouchPad.getTouchPad().getKnobPercentX() != 0 && countX > 17) {
                getPlayer().setX(newX);
                countX = 0;
            }
        }
        if (!Player.isDead && newY >= screenBottom && newY <= screenTop) {
            if (myTouchPad.getTouchPad().getKnobPercentY() != 0 && countY > 17) {
                getPlayer().setY(newY);
                countY = 0;
            }
        }

    }

    public TextureAtlas getPlayerAtlas() {
        return playerAtlas;
    }

    public MyTouchpad getMyTouchPad() {
        return myTouchPad;
    }

    /**
     * @return Player of the current device.
     */
    public static Player getPlayer() {
        return player;
    }

    public DirectionGestureDetector getDirectionGestureDetector() {
        return directionGestureDetector;
    }

    private void setPlayerBound() {
        playerBound = getPlayer().getBoundingRectangle();
    }

    /**
     *
     * @param idx player's index
     * @return player of specified index
     */
    public static Player getPlayer(int idx){return players.get(idx);}

    public static int getNumPlayers(){return players.size();}

    // Custom Methods
    // Currently unused to prevent excessive coupling
    public static synchronized void addInstanceToRenderList(Sprite s) {
        bullets.add(s);
    }

    public static synchronized void addBullet(Bullet bullet) {
//        Bullet bullet = new Bullet(new Sprite(new Texture("img/shuriken.png")), 6, world.getPlayer(), 2, (TiledMapTileLayer) map.getLayers().get(0));
        bullets.add(bullet);
    }

    public static synchronized void removeBullet(Bullet bullet) {
        bullet.setAlpha(0);
        bullets.remove(bullet);
        // causes the black box to appear, but probably necessary? not sure how garbage collection works
        bullet.getTexture().dispose();
    }

    public static void setBlackout() {
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

    public static synchronized void addMine(Mine mine) {
        mines.add(mine);
    }

    public static synchronized void removeMine(Mine mine) {
        mine.getTexture().dispose();
        mine.setAlpha(0);
        mines.remove(mine);
    }

    public void removePlayer(Player player) {
        synchronized (players) {
            players.remove(player);
        }
    }

    public void addPlayer(Player player) {
        synchronized (players) {
            players.add(player);
        }
    }

//    public void swipe(){
//        InputMultiplexer inputMultiplexer=new InputMultiplexer();
//        Stage stage=new Stage();
//        stage.addActor(getMyTouchpad().getTouchpad());
//
//        inputMultiplexer.addProcessor(stage);
//        inputMultiplexer.addProcessor(directionGestureDetector);
//        Gdx.input.setInputProcessor(inputMultiplexer);
//    }

    public static void setPlayerPosition(int playerIndex, Vector2 pos) {
        Player p = players.get(playerIndex);
        if(p!=null) {
            p.setPosition(pos.x, pos.y);
        }
    }
}
