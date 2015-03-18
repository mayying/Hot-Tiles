package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.mayying.tileMapGame.entities.Touchpad;


/**
 * Created by May Ying on 24/2/2015.
 */

public class Play implements Screen {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Touchpad touchpad;
    private Stage stage;
    private int countX = 0, countY = 0;
    private TextButton buttonA, buttonB;//, buttonX, buttonY;
    private TextButton.TextButtonStyle textButtonStyleA, textButtonStyleB;// textButtonStyleX, textButtonStyleY;
    private Skin skin;
    private TextureAtlas buttonAtlas;
    private BitmapFont font;
    private FitViewport viewport;
    private GameWorld world;
    private BurningTiles burningTiles, burningTiles2;
    //    private Bullet bullet;
//    public static Vector<Bullet> bullets;
    private long lastPressed;

    @Override
    public void show() {
        // To load the map into TileMap class
        // map = new TmxMapLoader().load("map/map128x102.tmx");
        map = new TmxMapLoader().load("map/map70x70.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        world = new GameWorld((TiledMapTileLayer) map.getLayers().get("Background"));

        camera = new OrthographicCamera();

        //camera.position.set(world.getPlayer().getX(), world.getPlayer().getY(), 0);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2 /*+ 100 + (world.getPlayer().getHeight() / 2)*/, 0);

        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();

        burningTiles = new BurningTiles(map, world, (TiledMapTileLayer) map.getLayers().get("Foreground"));
        burningTiles.create();

        burningTiles2 = new BurningTiles(map, world, (TiledMapTileLayer) map.getLayers().get("Foreground"));
        burningTiles2.create();
        //camera.setToOrtho(false, 1600, 900);

        setStage();
        // bullets = new Vector<Bullet>();

        // TODO - Merge input processors for joystick and world / Refactor joystick into world so it receives input / Use Stage for game logic
        //  Gdx.input.setInputProcessor(new InputHandler(world.getPlayer()));
    }

    /*
    Refactored WaiYan's Part
     */
    private void setStage() {
        touchpad = new Touchpad(world);
        stage = new Stage();
        stage.addActor(touchpad.getTouchpad());
        Gdx.input.setInputProcessor(stage);

        //creating buttons
//        skin = new Skin();
//        buttonAtlas = new TextureAtlas(Gdx.files.internal("xbox-buttons/out/buttons.pack"));
//        skin.addRegions(buttonAtlas);
//        font = new BitmapFont();
//        textButtonStyleA = new TextButton.TextButtonStyle();
//        textButtonStyleB = new TextButton.TextButtonStyle();
////        textButtonStyleX = new TextButton.TextButtonStyle();
////        textButtonStyleY = new TextButton.TextButtonStyle();
//        textButtonStyleA.font = textButtonStyleB.font = font; //= textButtonStyleX.font = textButtonStyleY.font ;
//        textButtonStyleA.up = skin.getDrawable("xbox-controller-a-button-md");
//        textButtonStyleA.down = skin.getDrawable("xbox-controller-b-button-md");
//        textButtonStyleB.up = skin.getDrawable("xbox-controller-b-button-md");
////        textButtonStyleX.up = skin.getDrawable("xbox-controller-x-button-md");
////        textButtonStyleY.up = skin.getDrawable("xbox-controller-y-button-md");
//        buttonA = new TextButton("", textButtonStyleA);
//        buttonB = new TextButton("", textButtonStyleB);
////        buttonX = new TextButton("", textButtonStyleX);
////        buttonY = new TextButton("", textButtonStyleY);
//        buttonA.setBounds(Gdx.graphics.getWidth() - world.getPlayer().getWidth() * 4 - 40, 5, world.getPlayer().getWidth() * 2, world.getPlayer().getHeight() + 50);
//        buttonB.setBounds(Gdx.graphics.getWidth() - world.getPlayer().getWidth() * 2 - 20, 5, world.getPlayer().getWidth() * 2, world.getPlayer().getHeight() + 50);
////        buttonX.setBounds(490, 80, 70, 70);
////        buttonY.setBounds(550, 140, 70, 70);
//        stage.addActor(buttonA);
//        stage.addActor(buttonB);
////        stage.addActor(buttonX);
////        stage.addActor(buttonY);
//        buttonA.addListener(new InputListener() {
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                return true;
//            }
//
//            @Override
//            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
//                if (System.currentTimeMillis() - lastPressed > 200) {
//                    //  lastPressed = System.currentTimeMillis();
//                    world.getPlayer().spacePressed();
////                    createNewBullet();
//                }
//
//            }
//        });


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);

        renderer.getBatch().begin();
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("Background"));
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("Foreground"));
        // bounding rectangle around the player
        final Rectangle bounds = world.getPlayer().getBoundingRectangle();

        // Get the bounding rectangle that our screen.  If using a camera you would create this based on the camera's
        // position and viewport width/height instead.
        //final Rectangle screenBounds = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        final Rectangle screenBounds = new Rectangle(0, 0, camera.viewportWidth, camera.viewportHeight);

        // Sprite
        // float left = bounds.getX();
        // float bottom = bounds.getY();
        // float top = bottom + bounds.getHeight();
        // float right = left + bounds.getWidth();

        // Screen
        float screenLeft = screenBounds.getX();
        float screenBottom = screenBounds.getY() + 200;
        float screenTop = screenBounds.getHeight();// + (world.getPlayer().getHeight() / 2);
        float screenRight = screenLeft + screenBounds.getWidth();

        float newX = bounds.getX();
        float newY = bounds.getY();

        if (touchpad.getTouchpad().getKnobPercentX() > 0.5) {
            newX += world.getPlayer().getCollisionLayer().getTileWidth();
            world.getPlayer().rightPressed();
        } else if (touchpad.getTouchpad().getKnobPercentX() < -0.5) {
            newX -= world.getPlayer().getCollisionLayer().getTileWidth();
            world.getPlayer().leftPressed();
        }

        if (touchpad.getTouchpad().getKnobPercentY() > 0.5) {
            newY += world.getPlayer().getCollisionLayer().getTileHeight();
            world.getPlayer().upPressed();
        } else if (touchpad.getTouchpad().getKnobPercentY() < -0.5) {
            newY -= world.getPlayer().getCollisionLayer().getTileHeight();
            world.getPlayer().downPressed();
        }

        countX++;
        countY++;

        if (newX >= screenLeft && newX <= screenRight) {
            if (touchpad.getTouchpad().getKnobPercentX() != 0 && countX > 30) {
                world.getPlayer().setX(newX);
                countX = 0;
            }
        }
        if (newY >= screenBottom && newY <= screenTop) {
            if (touchpad.getTouchpad().getKnobPercentY() != 0 && countY > 30) {
                world.getPlayer().setY(newY);
                countY = 0;
            }
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

//        ShapeRenderer shapeRenderer = new ShapeRenderer();
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(new Color(Color.BLACK));
//        shapeRenderer.rect(0, 0, 480, 480);
//        shapeRenderer.end();
        world.drawAndUpdate(renderer.getBatch());


        burningTiles.render(1);
        burningTiles2.render(2);
        renderer.getBatch().end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Luccan's edit
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
        stage.dispose();
    }

//    public void createNewBullet() {
//        Bullet bullet = new Bullet(new Sprite(new Texture("img/shuriken.png")), 6, world.getPlayer(), 2, (TiledMapTileLayer) map.getLayers().get(0));
//        bullets.add(bullet);
//    }
//
//    public static synchronized void removeBullet(Bullet bullet) {
//        bullets.remove(bullet);
//        // causes the black box to appear, but probably necessary? not sure how garbage collection works
//        bullet.getTexture().dispose();
//    }
}
