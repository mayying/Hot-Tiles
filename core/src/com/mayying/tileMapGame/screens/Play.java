package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.BurningTiles;
import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.entities.powerups.Blackout;
import com.mayying.tileMapGame.multiplayer.MessageParser;
import com.mayying.tileMapGame.multiplayer.MultiplayerMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by May Ying on 24/2/2015.
 */

public class Play implements Screen {
    public static final int V_WIDTH = 1260, V_HEIGHT = 700;
    private static final String TAG = "HT_Play";
    private static final int TILES_PER_INTERVAL = 5;
    private static final int MAX_TILES = 40;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private StretchViewport viewport;
    private GameWorld world;
    private SideBar sideBar;
    private BurningTiles[] burningTiles;
    private int count = 0;
    private float spawnNewTile = 0f;
    private static MultiplayerMessaging multiplayerMessaging;
    private MessageParser messageParser;
    private boolean allPlayersReady = false;
    private Long randomSeed;
    private long initializedTimeStamp;
    private HashMap<String, String> charselect;

    public Play() {
        super();
        multiplayerMessaging = null;
        this.messageParser = null;
        randomSeed = new Random().nextLong();
    }

    public Play(MultiplayerMessaging mmsg, HashMap<String, String> charselect) {
        super();
        multiplayerMessaging = mmsg;
        this.messageParser = null;
        randomSeed = new Random().nextLong();
        this.charselect = charselect;
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
            participants = multiplayerMessaging.getJoinedParticipants();
            Gdx.app.log("No of participants:", String.valueOf(participants.size()));
            myPlayerId = multiplayerMessaging.getMyId();
        }

        world = new GameWorld((TiledMapTileLayer) map.getLayers().get("Background"), participants, myPlayerId, charselect, this);

        sideBar = new SideBar(world);
        sideBar.show();

        Jukebox.load("sounds/fire.mp3", "fire");
        initializedTimeStamp = System.currentTimeMillis();
    }

    public void initializeBurningTiles(Long randomSeed){
        burningTiles = new BurningTiles[MAX_TILES];
        int randomBase = 102312943;
        for (int i = 0; i < burningTiles.length; i++) {
            //randomBase is just to randomize even more.
            burningTiles[i] = new BurningTiles(map, world, (TiledMapTileLayer) map.getLayers().get("Foreground"), randomSeed+i+randomBase);
            burningTiles[i].create();
        }
        //start timer
        sideBar.unfreezeGameTimer();
        allPlayersReady = true;
    }

    boolean leavingGame = false;
    public void leaveGame(){
        leavingGame = true;
    }

    long lastBroadcast = -1;

    @Override
    public void render(float delta) {
        if (leavingGame){
            leavingGame = false;
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(Play.getMultiplayerMessaging()));
        }
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);

        renderer.getBatch().begin();
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("Background"));
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("Foreground"));

        // Screen
        world.playerMovement(delta);
        world.drawAndUpdate(renderer.getBatch());

        if (this.allPlayersReady) {
//            spawnNewTile += delta;
//            if (spawnNewTile >= Math.log10(0.02f * (SideBar.timeLeft + 10000))
//                    && count < burningTiles.length) {
//                spawnNewTile = 0;
//                count++;
//            }
            if (SideBar.timeLeft>0) {
//                Gdx.app.log(TAG, SideBar.timeLeft+"");
//                count = (int) Math.floor((92 - SideBar.timeLeft) / 1.75);
                count = Math.min(5 + (90 - SideBar.timeLeft)/10 * TILES_PER_INTERVAL, MAX_TILES); //testin
                Gdx.app.log(TAG,"Count: " + count);
            }

            for (int i = 0; i < count; i++) {
                burningTiles[i].render(delta);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            new Blackout().use();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            world.getDevicePlayer().shield();
        }

        renderer.getBatch().end();
        sideBar.render(delta);

        // TODO - Might be better to create an additional thread that handles all the incoming messages
        if (multiplayerMessaging != null) {
            List<String> msgs = multiplayerMessaging.getMessageBuffer();
            for (String msg : msgs) {
                MessageParser.parse(msg);
            }
            //Broadcast Player Location
            if (System.currentTimeMillis() - lastBroadcast > 100) {
                lastBroadcast = System.currentTimeMillis();
                multiplayerMessaging.broadcastMessage(world.generateDevicePlayerCoordinatesBroadcastMessage());

                if (!allPlayersReady && iAmReady()){
                    world.playerReady(multiplayerMessaging.getMyId(), randomSeed);
                    multiplayerMessaging.broadcastMessage("ready," + randomSeed.toString());
                }
            }
        }
    }

    public static final long GAME_SETUP_TIME = 5000;
    private boolean iAmReady(){
        if (System.currentTimeMillis()-initializedTimeStamp > GAME_SETUP_TIME){
            return true;
        }
        return false;
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
        world.dispose();
        map.dispose();
        renderer.dispose();
        sideBar.dispose();
    }

    //TODO This is bullshit
    public static MultiplayerMessaging getMultiplayerMessaging() {
        return multiplayerMessaging;
    }
    public static void broadcastMessage(String msg) {
        Gdx.app.log(TAG, "Broadcasting message: " + msg);
        multiplayerMessaging.broadcastMessage(msg);
    }

    public static void broadcastMessage(String... args) {
        String msg = "";
        for (String arg : args) {
            msg += arg + ",";
        }
        Gdx.app.log(TAG, "Broadcasting message: " + msg);
        multiplayerMessaging.broadcastMessage(msg);
    }
}
