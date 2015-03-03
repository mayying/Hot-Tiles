package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mayying.tileMapGame.entities.Bullet;
import com.mayying.tileMapGame.entities.Player;
import com.mayying.tileMapGame.entities.Touchpad;

import java.util.Vector;


/**
 * Created by May Ying on 24/2/2015.
 */
public class Play implements Screen {

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Batch batch;
    private Touchpad touchpad;
    private Stage stage;
    private int speed =5;
    TextButton buttonA;
    TextButton buttonB;
    TextButton buttonX;
    TextButton buttonY;
    TextButton.TextButtonStyle textButtonStyleA;
    TextButton.TextButtonStyle textButtonStyleB;
    TextButton.TextButtonStyle textButtonStyleX;
    TextButton.TextButtonStyle textButtonStyleY;
    Skin skin;
    TextureAtlas buttonAtlas;
    BitmapFont font;

    private Player player;
    //    private Bullet bullet;
    public static Vector<Bullet> bullets = new Vector<Bullet>();
    private long lastPresed;
    @Override
    public void show() {
        batch=new SpriteBatch();

        // To load the map into TileMap class
        map = new TmxMapLoader().load("map/map120x120_2.tmx");
        float aspectRatio=(float) Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
        renderer = new OrthogonalTiledMapRenderer(map);
        camera = new OrthographicCamera();
        camera.setToOrtho(false,1600,900);
        setStage();


        player = new Player(new Sprite(new Texture("img/player.png")), (TiledMapTileLayer) map.getLayers().get(0));

        //Use Gdx.app.log instead
//        System.out.println("player.getCollisionLayer().getWidth() " + player.getCollisionLayer().getWidth());

        player.setPosition((player.getCollisionLayer().getWidth() - 5) * player.getCollisionLayer().getTileWidth(),
              (player.getCollisionLayer().getHeight() - 2) * player.getCollisionLayer().getTileHeight());
    }

    /*
    Refactored WaiYan's Part
     */
    private void setStage() {
        touchpad=new Touchpad();
        touchpad.getTouchpad().setBounds(15, 15, 200, 200);
        stage=new Stage();
        stage.addActor(touchpad.getTouchpad());
        Gdx.input.setInputProcessor(stage);

        //creating buttons
        skin = new Skin();
        buttonAtlas = new TextureAtlas(Gdx.files.internal("xbox-buttons/out/buttons.pack"));
        skin.addRegions(buttonAtlas);
        font = new BitmapFont();
        textButtonStyleA = new TextButton.TextButtonStyle();
        textButtonStyleB = new TextButton.TextButtonStyle();
        textButtonStyleX = new TextButton.TextButtonStyle();
        textButtonStyleY = new TextButton.TextButtonStyle();
        textButtonStyleA.font = font;
        textButtonStyleB.font = font;
        textButtonStyleX.font = font;
        textButtonStyleY.font = font;
        textButtonStyleA.up = skin.getDrawable("xbox-controller-a-button-md");
        textButtonStyleA.down=skin.getDrawable("xbox-controller-b-button-md");
        textButtonStyleB.up = skin.getDrawable("xbox-controller-b-button-md");
        textButtonStyleX.up = skin.getDrawable("xbox-controller-x-button-md");
        textButtonStyleY.up = skin.getDrawable("xbox-controller-y-button-md");
        buttonA = new TextButton("", textButtonStyleA);
        buttonB = new TextButton("", textButtonStyleB);
        buttonX = new TextButton("", textButtonStyleX);
        buttonY = new TextButton("", textButtonStyleY);
        buttonA.setBounds(550,20,70,70);
        buttonB.setBounds(610,80,70,70);
        buttonX.setBounds(490,80,70,70);
        buttonY.setBounds(550,140,70,70);
        stage.addActor(buttonA);
        stage.addActor(buttonB);
        stage.addActor(buttonX);
        stage.addActor(buttonY);
        buttonA.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(System.currentTimeMillis() - lastPresed > 200) {
                    lastPresed = System.currentTimeMillis();
                    createNewBullet();
                }

            }
        });


    }

    @Override
    public void render(float delta) {
//        Gdx.app.log("Game Screen","Width: " +Gdx.graphics.getWidth()+" and "+camera.viewportWidth+" and Height: "+Gdx.graphics.getHeight()+" and "+camera.viewportHeight);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);
        renderer.render();

        // bounding rectangle around the player
        final Rectangle bounds = player.getBoundingRectangle();

        // Get the bounding rectangle that our screen.  If using a camera you would create this based on the camera's
        // position and viewport width/height instead.
        final Rectangle screenBounds = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//        final Rectangle screenBounds = new Rectangle(0, 0, camera.viewportWidth,camera.viewportHeight);
        // Sprite
        float left = bounds.getX();
        float bottom = bounds.getY();
        float top = bottom + bounds.getHeight();
        float right = left + bounds.getWidth();

        // Screen
        float screenLeft = screenBounds.getX();
        float screenBottom = screenBounds.getY();
        float screenTop = screenBottom + screenBounds.getHeight();
        float screenRight = screenLeft + screenBounds.getWidth();

        float newX=bounds.getX()+ touchpad.getTouchpad().getKnobPercentX()*speed;
        float newY=bounds.getY()+ touchpad.getTouchpad().getKnobPercentY() * speed;

        if(newX>screenLeft && newX+bounds.getWidth()<screenRight){
            player.setX(newX);
        }
        if(newY>screenBottom && newY+bounds.getHeight()<screenTop){
            player.setY(newY);
        }

//        player.setX(player.getX() + touchpad.getTouchpad().getKnobPercentX()*speed);
//        player.setY(player.getY() + touchpad.getTouchpad().getKnobPercentY()*speed);

        renderer.getBatch().begin();

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && System.currentTimeMillis() - lastPresed > 200){
            lastPresed = System.currentTimeMillis();
            createNewBullet();
        }
        for(int i=0; i<bullets.size(); i++){
            bullets.get(i).draw(renderer.getBatch());
        }
        // Player should be here so that the projectiles will be rendered with a lower z-index
        player.draw(renderer.getBatch());
//        if(bullet!=null){
//            bullet.draw(renderer.getBatch());
//        }
        renderer.getBatch().end();
        stage.draw();
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
        stage.dispose();
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
