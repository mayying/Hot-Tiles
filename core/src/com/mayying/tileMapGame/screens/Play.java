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

/**
 * Created by May Ying on 24/2/2015.
 */

public class Play implements Screen {
    public static final int V_WIDTH = 1260, V_HEIGHT = 700;
    private static final String TAG = "HT_Play";

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private StretchViewport viewport;
    private GameWorld world;
    private SideBar sideBar;
    private BurningTiles[] burningTiles;
    private int count = 0;
    private float spawnNewTile = 0f;
    private static MultiplayerMessaging multiplayerMessaging = null;

    public Play() {
        super();
    }

    public Play(MultiplayerMessaging mmsg) {
        super();
        multiplayerMessaging = mmsg;
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
        world = new GameWorld((TiledMapTileLayer) map.getLayers().get("Background"), participants, myPlayerId);
        sideBar = new SideBar(world);
        sideBar.show();

        burningTiles = new BurningTiles[80];
        for (int i = 0; i < burningTiles.length; i++) {
            burningTiles[i] = new BurningTiles(map, world, (TiledMapTileLayer) map.getLayers().get("Foreground"));
            burningTiles[i].create();
        }

        Jukebox.load("sounds/fire.mp3", "fire");
        // burningTiles = new BurningTiles(map, world, (TiledMapTileLayer) map.getLayers().get("Foreground"));
        // burningTiles.create();
        //  Gdx.input.setInputProcessor(new InputHandler(world.getPlayer()));
//        world.swipe();
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

        spawnNewTile += delta;
        if (spawnNewTile >= Math.log10(0.02f * (SideBar.timeLeft + 10000))
                && count < burningTiles.length) {
            spawnNewTile = 0;
            count++;
        }

        for (int i = 0; i < count; i++) {
            burningTiles[i].render(delta);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            new Blackout().use();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            world.getDevicePlayer().shield();
        }
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
            }
        }

        renderer.getBatch().end();
        sideBar.render(delta);
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

    // MORE BULLSHIT HERE
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
