package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mayying.tileMapGame.GameWorld;


/**
 * Created by May on 17/3/2015.
 */


public class SideBar {

    private Stage stage;
    private Label timer, scoreboard, description;
    private GameWorld world;
    private TextButton buttonA, buttonB, sound, question, close;
    private OrthographicCamera hudCamera;
    private final Rectangle screenBound;
    //  public static Touchpad touchpad;

    volatile static int timeLeft = 1;

    private float gameTime = 1 * 60 + 30;
    private int min, sec;

    SideBar(GameWorld world) {
        this.world = world;
        hudCamera = new OrthographicCamera();
        min = 1;
        sec = 30;
        screenBound = new Rectangle(GameWorld.screenBound.getX() + GameWorld.screenBound.getWidth() + GameWorld.TILE_WIDTH,
                0, GameWorld.TILE_WIDTH * 3,
                GameWorld.TILE_HEIGHT * 10);
        //aspectRatio = screenBound.getWidth() / screenBound.getHeight();
    }

    public void create() {
        stage = new Stage(new ExtendViewport(Play.V_WIDTH, Play.V_HEIGHT, hudCamera));
        InputMultiplexer inputMultiplexer=new InputMultiplexer();
//        Stage stage=new Stage();
//        stage.addActor(getMyTouchpad().getTouchpad());
//
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(world.getDirectionGestureDetector());
        Gdx.input.setInputProcessor(inputMultiplexer);

//        Gdx.input.setInputProcessor(stage);

        TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("skin/skin.txt"));
        Skin skin = new Skin(Gdx.files.internal("skin/gameSkin.json"), buttonAtlas);

        // container for all UI widgets
        Table table = new Table(skin);
        table.setFillParent(true);
        table.setBounds(0, 0, Play.V_WIDTH, Play.V_HEIGHT);
        table.align(Align.top);
        table.setDebug(true);
        // tableBtm.setDebug(true);

        timer = new Label("Time Left\n" + min + " : " + sec, skin, "timer");
        timer.setAlignment(Align.center);

        sound = new TextButton("", skin, "sound");
        question = new TextButton("", skin, "question");
        close = new TextButton("", skin, "close");

        scoreboard = new Label("Score Board", skin, "scoreboard");

        description = new Label("", skin, "description");

        world.getMyTouchpad().getTouchpad().setPosition(0, 0);

        buttonA = new TextButton("", skin);
        buttonB = new TextButton("", skin);

        Table subTable = new Table();
        subTable.add(buttonA).right().expandX().width(105).height(140).row();
        subTable.add(buttonB).left().expandX().width(105).height(140).row();

        // putting stuff together
        //table.align(Align.center);
        table.add(timer).top().left().expandX().height(140).width(210);
        table.add(sound).top();
        table.add(question).top();
        table.add(close).top().row();

        table.add(scoreboard).left().expandX().height(280).width(210);
        table.add(description).fill().colspan(3).row();

        table.add(world.getMyTouchpad().getTouchpad()).left().expandY().width(210);
        table.add(subTable).fill().colspan(3);

        //tableBtm.add(buttonA).expandX().right().row();
        //tableBtm.add(buttonB).expandX().left();

        stage.addActor(table);
        //stage.addActor(tableBtm);
//        stage.addActor(touchpad);

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
        timer.setText("Time Left\n" + String.format("%02d : %02d", minutes, seconds));

    }

    public void dispose() {
        stage.dispose();
    }


}

