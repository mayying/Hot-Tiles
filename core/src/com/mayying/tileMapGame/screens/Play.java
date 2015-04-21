package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.BurningTiles;
import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.entities.PlayerMetaData;
import com.mayying.tileMapGame.multiplayer.MessageParser;
import com.mayying.tileMapGame.multiplayer.MultiplayerMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by May Ying on 24/2/2015.
 */

public class Play implements Screen {
    public static final int V_WIDTH = 1260, V_HEIGHT = 700, TILES_PER_INTERVAL = 3, MAX_TILES = 25;
    private static final String TAG = "HT_Play";
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    public static OrthographicCamera camera;
    private StretchViewport viewport;
    private GameWorld world;
    private SideBar sideBar;
    private BurningTiles[] burningTiles;
    private int count = 0;
    private float spawnNewTile = 0f;
    private static MultiplayerMessaging multiplayerMessaging;
    private boolean allPlayersReady = false;
    private Long randomSeed;
    private long initializedTimeStamp;
    private ArrayList<PlayerMetaData> metaData;
    private TiledMapTileLayer collisionLayer;
    private long lastTouched = 0l;
    long lightningDelta;
    private boolean cooldown;
    public Play() {
        super();
        multiplayerMessaging = null;
        randomSeed = new Random().nextLong();
    }

    public Play(MultiplayerMessaging mmsg, ArrayList<PlayerMetaData> metaData) {
        super();
        multiplayerMessaging = mmsg;
        randomSeed = new Random().nextLong();
        this.metaData = metaData;

    }

    @Override
    public void show() {
        // To load the map into TileMap class
        map = new TmxMapLoader().load("map/gmap70x70.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, V_WIDTH, V_HEIGHT);

        viewport = new StretchViewport(1260, 700, camera);
        viewport.apply();

        List<String> participants = new ArrayList<String>();

        //TODO shouldve removed this line....
        String myPlayerId = "me";
        if (multiplayerMessaging != null) {
            myPlayerId = multiplayerMessaging.getMyId();
        }
        collisionLayer = (TiledMapTileLayer) map.getLayers().get("Background");
        world = GameWorld.getInstance(collisionLayer, myPlayerId, metaData, this);

        sideBar = new SideBar(world, this.metaData.size());
        sideBar.show();

        initializedTimeStamp = System.currentTimeMillis();

        Jukebox.stopMusic("mainMenu");
        Jukebox.playMusic("background");
        multiplayerMessaging.clearMessageBufferExcept('p');
    }

    public void initializeBurningTiles(Long randomSeed) {
        burningTiles = new BurningTiles[MAX_TILES];
        int randomBase = 102312943;
        for (int i = 0; i < burningTiles.length; i++) {
            //randomBase is just to randomize even more.
            burningTiles[i] = new BurningTiles(map, world, (TiledMapTileLayer) map.getLayers().get("Foreground"), randomSeed + i + randomBase);
            burningTiles[i].create();
        }
        //start timer
        sideBar.unfreezeGameTimer();
        allPlayersReady = true;
    }

    boolean leavingGame = false;

    public void leaveGame() {
        leavingGame = true;
    }

    long lastBroadcast = -1;

    @Override
    public void render(float delta) {
        if (leavingGame) {
            leavingGame = false;
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(Play.getMultiplayerMessaging()));
        }
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);

        renderer.getBatch().begin();
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("Background"));
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("Foreground"));

        if (world.getDevicePlayer().isHasted)
            renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("Frenzy"));

        // Screen
        world.playerMovement(delta);
        world.drawAndUpdate(renderer.getBatch());

        if (this.allPlayersReady) {
            if (SideBar.timeLeft > 0)
                count = Math.min(5 + (90 - SideBar.timeLeft) / 10 * TILES_PER_INTERVAL, MAX_TILES); //testing

            for (int i = 0; i < count; i++)
                burningTiles[i].render(delta);
        }

        lightningDelta = System.currentTimeMillis() - lastTouched;
        if(lightningDelta > 3000l && !sideBar.isTimeFrozen()) {
            sideBar.showLightning(true);
            if (Gdx.input.justTouched()) {
                Vector3 v3 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(v3);
                final double x = Math.floor(v3.x / GameWorld.TILE_WIDTH) - 4;
                final double y = Math.floor(v3.y / collisionLayer.getTileHeight() - 1);
                // Check bounds
                if (x >= 0 && x <= 9 && y >= 0 && y <= 7) {
                    Gdx.app.log(TAG, "Sending Thunder strike");
                    lastTouched = System.currentTimeMillis();
                    broadcastMessage(MessageParser.LIGHTNING, String.valueOf(x), String.valueOf(y));
                    // TESTING ONLY
                    GameWorld.getInstance().lightningAt((float) x, (float) y, world.getDevicePlayer().getID());
                }
                sideBar.showLightning(false);
            }
        }else{
            sideBar.showLightning(false);
        }

        renderer.getBatch().end();
        sideBar.render(delta);

        // TODO - Might be better to create an additional thread that handles all the incoming messages
        if (multiplayerMessaging != null) {
            List<String> msgs = multiplayerMessaging.getMessageBuffer('p');
            for (String msg : msgs) {
                MessageParser.parse(msg);
            }
            //Broadcast Player Location
            if (System.currentTimeMillis() - lastBroadcast > 100) {
                lastBroadcast = System.currentTimeMillis();
                broadcastMessage(world.generateDevicePlayerCoordinatesBroadcastMessage());

                if (!allPlayersReady && iAmReady()) {
                    world.playerReady(multiplayerMessaging.getMyId(), randomSeed);
                    broadcastMessage("ready," + randomSeed.toString());
                }
            }
        }
    }

    public static final long GAME_SETUP_TIME = 5000;

    private boolean iAmReady() {
        return System.currentTimeMillis() - initializedTimeStamp > GAME_SETUP_TIME;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        sideBar.dispose();
        world.dispose();
    }



    //TODO This is bullshit
    public static MultiplayerMessaging getMultiplayerMessaging() {
        return multiplayerMessaging;
    }

    public static void broadcastMessage(String msg) {
//        Gdx.app.log(TAG, "Broadcasting message: " + msg);
        multiplayerMessaging.broadcastMessage(msg);
    }

    public static void broadcastMessage(String... args) {
        String msg = "";
        for (String arg : args) {
            msg += arg + ",";
        }
//        Gdx.app.log(TAG, "Broadcasting message: " + msg);
        multiplayerMessaging.broadcastMessage(msg);
    }
}