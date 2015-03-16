package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.BurningTiles;


/**
 * Created by May Ying on 24/2/2015.
 */

public class Play implements Screen {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    private FitViewport viewport;
    private GameWorld world;
    private BurningTiles burningTiles, burningTiles2;

    @Override
    public void show() {
        // To load the map into TileMap class
        map = new TmxMapLoader().load("map/map70x70.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);


        camera = new OrthographicCamera();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2 /*+ 100 + (world.getPlayer().getHeight() / 2)*/, 0);
        //camera.setToOrtho(false, 1600, 900);

        world = new GameWorld((TiledMapTileLayer) map.getLayers().get("Background"), camera);

        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();

        burningTiles = new BurningTiles(map, world, (TiledMapTileLayer) map.getLayers().get("Foreground"));
        burningTiles.create();

        burningTiles2 = new BurningTiles(map, world, (TiledMapTileLayer) map.getLayers().get("Foreground"));
        burningTiles2.create();

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

        burningTiles.render(delta, 1);
        burningTiles2.render(delta, 2);
        renderer.getBatch().end();
        world.getMyStage().draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        //camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2 + (world.getPlayer().getHeight() / 2), 0);
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
        world.getMyStage().dispose();
    }
}
