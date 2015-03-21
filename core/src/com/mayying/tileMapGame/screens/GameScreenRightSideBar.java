package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mayying.tileMapGame.GameWorld;


/**
 * Created by May on 17/3/2015.
 */


public class GameScreenRightSideBar {

    private Stage stage;
    private Label heading;
    private GameWorld world;
    private TextButton buttonA, buttonB;
    private OrthographicCamera hudCamera;
    private final Rectangle screenBound;

    volatile static int timeLeft = 1;

    private float gameTime = 1 * 60 + 30;
    private final float aspectRatio;
    private int min, sec;

    GameScreenRightSideBar(GameWorld world) {
        this.world = world;
        hudCamera = new OrthographicCamera();
        min = 1;
        sec = 30;
        screenBound = new Rectangle(GameWorld.screenBound.getX() + GameWorld.screenBound.getWidth() + GameWorld.TILE_WIDTH,
                0, GameWorld.TILE_WIDTH * 3,
                GameWorld.TILE_HEIGHT * 10);
        aspectRatio = screenBound.getWidth() / screenBound.getHeight();
    }

    public void create() {
        stage = new Stage(new ExtendViewport(Play.V_WIDTH, Play.V_HEIGHT, hudCamera));

        Gdx.input.setInputProcessor(stage);

        TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("skin/powerupSkin.txt"));
        Skin skin = new Skin(Gdx.files.internal("skin/gameSkin.json"), buttonAtlas);

        // container for all UI widgets
        Table table = new Table(skin);
        Table tableBtm = new Table(skin);

        table.setBounds(screenBound.getX(), screenBound.getY() + 4 * screenBound.getHeight() / 5,
                screenBound.getWidth(), screenBound.getHeight() / 5);
        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("skin/skinTime.png"))));
        tableBtm.setBounds(screenBound.getX(), screenBound.getY(),
                screenBound.getWidth(), screenBound.getHeight() / 4);

        // table.setDebug(true);
        // tableBtm.setDebug(true);

        table.top();
        tableBtm.bottom();

        Label timeLeft = new Label("Time Left", skin);
        timeLeft.setAlignment(Align.top);
        heading = new Label(min + " : " + sec, skin);
        heading.setAlignment(Align.top);

        buttonA = new TextButton("", skin);
        buttonB = new TextButton("", skin);

        // putting stuff together
        table.align(Align.center);
        table.add(timeLeft).bottom().row();
        table.add(heading);

        tableBtm.add(buttonA).expandX().right().row();
        tableBtm.add(buttonB).expandX().left();

        stage.addActor(table);
        stage.addActor(tableBtm);
        stage.addActor(world.getMyTouchpad().getTouchpad());

        buttonA.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                world.getPlayer().spacePressed();
            }
        });

    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        gameTime -= delta;
        int minutes = (int) Math.floor(gameTime / 60.0f);
        int seconds = (int) (gameTime - minutes * 60.0f);
        timeLeft = minutes * 60 + seconds;
        heading.setText(String.format("%02d : %02d", minutes, seconds));

    }

    public void dispose() {
        stage.dispose();
    }


}

