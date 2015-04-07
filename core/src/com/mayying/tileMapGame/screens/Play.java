package com.mayying.tileMapGame.screens;

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
import java.util.List;
import java.util.Random;

/**
 * Created by May Ying on 24/2/2015.
 */

public class Play implements Screen {
    public static final int V_WIDTH = 1260, V_HEIGHT = 700;

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

    public Play(){
        super();
        this.multiplayerMessaging = null;
        this.messageParser = null;
        randomSeed = new Random().nextLong();
    }
    public Play(MultiplayerMessaging multiplayerMessaging){
        super();
        this.multiplayerMessaging = multiplayerMessaging;
        this.messageParser = null;
        randomSeed = new Random().nextLong();
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
        String myPlayerId = "me";
        participants.add(myPlayerId);
        if (multiplayerMessaging != null) {
            participants = multiplayerMessaging.getJoinedParticipants();
            myPlayerId = multiplayerMessaging.getMyId();
        }
        world = new GameWorld((TiledMapTileLayer) map.getLayers().get("Background"), participants, myPlayerId, this);
        if (multiplayerMessaging != null){
            this.messageParser = new MessageParser(world);
        }

        sideBar = new SideBar(world);
        sideBar.show();

//        burningTiles = new BurningTiles[80];
//        for (int i = 0; i < burningTiles.length; i++) {
//            burningTiles[i] = new BurningTiles(map, world, (TiledMapTileLayer) map.getLayers().get("Foreground"));
//            burningTiles[i].create();
//        }

        Jukebox.load("sounds/fire.mp3", "fire");
        // burningTiles = new BurningTiles(map, world, (TiledMapTileLayer) map.getLayers().get("Foreground"));
        // burningTiles.create();
        //  Gdx.input.setInputProcessor(new InputHandler(world.getPlayer()));
//        world.swipe();
    }

    public void initializeBurningTiles(Long randomSeed){
        burningTiles = new BurningTiles[80];
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

    long lastBroadcast = -1;

    @Override
    public void render(float delta) {
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
                count = (int) Math.floor((92 - SideBar.timeLeft) / 1.75);
            }

            for (int i = 0; i < count; i++) {
                burningTiles[i].render(delta);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            new Blackout().use(null);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            world.getDevicePlayer().shield();
        }

        renderer.getBatch().end();
        sideBar.render(delta);

        // TODO - Might be better to create an additional thread that handles all the incoming messages
        if (multiplayerMessaging!=null){
            List<String> msgs = multiplayerMessaging.getMessageBuffer();
            for (String msg : msgs){
                messageParser.parse(msg);
            }
            //Broadcast Player Location
            if (System.currentTimeMillis()-lastBroadcast>100) {
                lastBroadcast = System.currentTimeMillis();
                multiplayerMessaging.BroadCastMessage(world.generateDevicePlayerCoordinatesBroadcastMessage());

                if (!allPlayersReady){
                    world.playerReady(multiplayerMessaging.getMyId(), randomSeed);
                    multiplayerMessaging.BroadCastMessage("ready,"+randomSeed.toString());
                }
            }
        }
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
    public static MultiplayerMessaging getMultiplayerMessaging(){
        return multiplayerMessaging;
    }
}
