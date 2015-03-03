package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mayying.tileMapGame.entities.Player;

import javax.swing.text.View;


/**
 * Created by May Ying on 24/2/2015.
 */
public class Play implements Screen {

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    private Player player;
    private Viewport viewport;

    @Override
    public void show() {
        // To load the map into TileMap class
        map = new TmxMapLoader().load("map/map128x102.tmx");

        
        renderer = new OrthogonalTiledMapRenderer(map);

        camera = new OrthographicCamera();

        viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.apply();


        player = new Player(new Sprite(new Texture("img/player.png")), (TiledMapTileLayer) map.getLayers().get(0));

        /*Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.debug("width: ", String.valueOf(Gdx.graphics.getWidth()));
        Gdx.app.debug("height: ", String.valueOf(Gdx.graphics.getHeight()));
        */

        // player.getCollisionLayer().getWidth() returns the number of tiles in x axis
        // player.getCollisionLayer().getHeight() returns the number of tiles in y axis

        // player.getCollisionLayer().getTileWidth() returns the width of tile
        // player.getCollisionLayer().getTileHeight() returns the height of tile

        player.setPosition((player.getCollisionLayer().getWidth() - 5) * player.getCollisionLayer().getTileWidth(),
                (player.getCollisionLayer().getHeight() - 2) * player.getCollisionLayer().getTileHeight());

        Gdx.input.setInputProcessor(player);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        renderer.setView(camera);
        renderer.render();

        renderer.getBatch().begin();
        renderer.getBatch().setProjectionMatrix(camera.combined);
        player.draw(renderer.getBatch());
        renderer.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        viewport.update(width, height);

        TiledMapTileLayer layer0 = player.getCollisionLayer();
        Vector3 center = new Vector3(layer0.getWidth() * layer0.getTileWidth() / 2, layer0.getHeight() * layer0.getTileHeight() / 2, 0);

        camera.position.set(center);
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
        player.getTexture().dispose();
    }
}
