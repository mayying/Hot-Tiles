package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.BurningTiles;
import com.mayying.tileMapGame.entities.powerups.Blackout;
import com.mayying.tileMapGame.entities.powerups.FreezeMine;

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
    private GameScreenRightSideBar rSideBar;

    private BurningTiles[] burningTiles;
    private int count = 0;
    private float spawnNewTile = 0f;

    @Override
    public void show() {
        // To load the map into TileMap class
        map = new TmxMapLoader().load("map/gmap70x70.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        camera = new OrthographicCamera();
        //camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.setToOrtho(false, V_WIDTH, V_HEIGHT);

        // camera.setToOrtho(false, 1280, 720);
        viewport = new StretchViewport(1260, 700, camera);
        viewport.apply();

        world = new GameWorld((TiledMapTileLayer) map.getLayers().get("Background"));
        rSideBar = new GameScreenRightSideBar(world);
        rSideBar.create();

        burningTiles = new BurningTiles[80];
        for (int i = 0; i < burningTiles.length; i++) {
            burningTiles[i] = new BurningTiles(map, world, (TiledMapTileLayer) map.getLayers().get("Foreground"));
            burningTiles[i].create();
        }
        // burningTiles = new BurningTiles(map, world, (TiledMapTileLayer) map.getLayers().get("Foreground"));
        // burningTiles.create();
        //  Gdx.input.setInputProcessor(new InputHandler(world.getPlayer()));
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);

        renderer.getBatch().begin();
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("Background"));
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("Foreground"));

        // Screen
        world.playerMovement();
        world.drawAndUpdate(renderer.getBatch());

        spawnNewTile += delta;
//        Gdx.app.log("i", i + "");
//        Gdx.app.log(GameScreenRightSideBar.timeLeft / 100.0f + "", " GameScreenRightSideBar.timeLeft / 100.0f");
        if (spawnNewTile >= Math.log10(0.02f * (GameScreenRightSideBar.timeLeft + 10000))
                && count < burningTiles.length) {
            spawnNewTile = 0;
            count++;
        }

        for (int i = 0; i < count; i++) {
            //Gdx.app.log("count", count + "");
            burningTiles[i].render(delta, 1);
        }
//        Gdx.input.isKeyJustPressed()
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) new Blackout().use(null);
        // Must make sure this is discrete
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            new FreezeMine(new Sprite(new Texture("img/shuriken.png")),
                    world.getPlayer(), (TiledMapTileLayer) map.getLayers().get(0)
            ).use(null);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            GameWorld.getPlayer().die();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            GameWorld.getPlayer().shield();
        }

        renderer.getBatch().end();
        rSideBar.render(delta);
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
        world.getPlayer().getTexture().dispose();
        rSideBar.dispose();
    }
}
