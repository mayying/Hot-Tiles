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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mayying.tileMapGame.entities.Bullet;
import com.mayying.tileMapGame.entities.Player;

import java.util.Vector;


/**
 * Created by May Ying on 24/2/2015.
 */
public class Play implements Screen {

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    private Player player;
//    private Bullet bullet;
    public static Vector<Bullet> bullets = new Vector<Bullet>();
    private long lastPresed;
    @Override
    public void show() {
        // To load the map into TileMap class
        map = new TmxMapLoader().load("map/map120x120_2.tmx");

        renderer = new OrthogonalTiledMapRenderer(map);

        camera = new OrthographicCamera();

        player = new Player(new Sprite(new Texture("img/player.png")), (TiledMapTileLayer) map.getLayers().get(0));

        System.out.println("player.getCollisionLayer().getWidth() " + player.getCollisionLayer().getWidth());

        player.setPosition((player.getCollisionLayer().getWidth() - 5) * player.getCollisionLayer().getTileWidth(),
              (player.getCollisionLayer().getHeight() - 2) * player.getCollisionLayer().getTileHeight());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);
        renderer.render();

        renderer.getBatch().begin();
        player.draw(renderer.getBatch());
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && System.currentTimeMillis() - lastPresed > 200){
            lastPresed = System.currentTimeMillis();
            createNewBullet();
        }
        for(int i=0; i<bullets.size(); i++){
            bullets.get(i).draw(renderer.getBatch());
        }
//        if(bullet!=null){
//            bullet.draw(renderer.getBatch());
//        }
        renderer.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;

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
    public void createNewBullet(){
        Bullet bullet = new Bullet(new Sprite(new Texture("img/shuriken.png")), 6, player ,2 ,(TiledMapTileLayer) map.getLayers().get(0));
        bullets.add(bullet);
    }
    public static synchronized void removeBullet(Bullet bullet){
        bullets.remove(bullet);
        // causes the black box to appear, but probably necessary? not sure how garbage collection works
        bullet.getTexture().dispose();
    }
}
