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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.BurningTiles;
import com.mayying.tileMapGame.entities.Mine;


import java.util.ArrayList;

/**
 * Created by May Ying on 24/2/2015.
 */

public class Play implements Screen {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private GameWorld world;
    private GameScreenRightSideBar rSideBar;
    private long lastPressed;
    private ArrayList<BurningTiles> burningTiles;
    private int count = 0, currentAnimationFrame = 1, animatingFrame = 0;
    private float spawnNewTile = 0f, animationFrameTime = 0f;
    private boolean cont;

    @Override
    public void show() {
        // To load the map into TileMap class
        map = new TmxMapLoader().load("map/map70x70.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        camera = new OrthographicCamera();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2 /*+ 100 + (world.getPlayer().getHeight() / 2)*/, 0);
        //camera.setToOrtho(false, 1600, 900);

        world = new GameWorld((TiledMapTileLayer) map.getLayers().get("Background"));
        rSideBar = new GameScreenRightSideBar(world);
        rSideBar.create();

        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();

        burningTiles = new ArrayList<BurningTiles>();

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
        animationFrameTime += delta;
//        Gdx.app.log("i", i + "");
//        Gdx.app.log(GameScreenRightSideBar.timeLeft / 100.0f + "", " GameScreenRightSideBar.timeLeft / 100.0f");
        if (spawnNewTile >= GameScreenRightSideBar.timeLeft / 100.0f) {
            Gdx.app.log("count", count + "");
            burningTiles.add(new BurningTiles(map, world, (TiledMapTileLayer) map.getLayers().get("Foreground")));
            burningTiles.get(count).create();
            spawnNewTile = 0;
            count++;
        }

        for (int i = 0; i < burningTiles.size(); i++){
            burningTiles.get(i).render(delta, 1);
        }
      
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) GameWorld.setBlackout();
        // Must make sure this is discrete
        if(Gdx.input.isKeyPressed(Input.Keys.X)) {
            if(System.currentTimeMillis() - lastPressed > 1000l) {
                GameWorld.addMine(
                        new Mine(new Sprite(new Texture("img/shuriken.png")),
                                world.getPlayer(),
                                (TiledMapTileLayer) map.getLayers().get(0)
                        ));
                lastPressed = System.currentTimeMillis();
            }
        }

        renderer.getBatch().end();
        rSideBar.render(delta);
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
        rSideBar.dispose();
    }
}
