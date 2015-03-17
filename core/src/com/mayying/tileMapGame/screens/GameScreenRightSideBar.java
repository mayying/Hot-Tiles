package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.mayying.tileMapGame.GameWorld;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mayying.tileMapGame.entities.MyTouchpad;


/**
 * Created by May on 17/3/2015.
 */


public class GameScreenRightSideBar {

    private Stage stage;
    private Table table;
    private BitmapFont black;
    private Label heading;
    private Skin skin;
    private TextureAtlas buttonAtlas;
    private GameWorld world;
    private TextButtonStyle textButtonStyleA, textButtonStyleB;
    private TextButton buttonA, buttonB;

    volatile static int timeLeft = 1;

    private float gameTime = 1 * 60 + 30;
    private int min, sec;

    GameScreenRightSideBar(GameWorld world) {
        this.world = world;
        min = 1;
        sec = 30;
    }

    public void create() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        buttonAtlas = new TextureAtlas(Gdx.files.internal("xbox-buttons/out/buttons.pack"));
        skin = new Skin(buttonAtlas);
        // container for all UI widgets
        table = new Table(skin);
        table.setBounds(world.getPlayer().getCollisionLayer().getTileWidth() * 16 + world.getPlayer().getWidth() / 2,
                world.getPlayer().getCollisionLayer().getTileHeight() * 8,
                world.getPlayer().getCollisionLayer().getTileWidth() * 2,
                world.getPlayer().getCollisionLayer().getTileWidth() * 2);

        black = new BitmapFont(Gdx.files.internal("font/black.fnt"), false);

        textButtonStyleA = new TextButtonStyle();
        textButtonStyleB = new TextButton.TextButtonStyle();

        textButtonStyleA.up = skin.getDrawable("xbox-controller-a-button-md");
        textButtonStyleA.down = skin.getDrawable("xbox-controller-b-button-md");
        textButtonStyleB.up = skin.getDrawable("xbox-controller-b-button-md");
        textButtonStyleA.font = textButtonStyleB.font = black;

        buttonA = new TextButton("", textButtonStyleA);
        buttonB = new TextButton("", textButtonStyleB);
        //buttonA.setBounds(Gdx.graphics.getWidth() - world.getPlayer().getWidth() * 2 - 20, world.getPlayer().getHeight() + 70, world.getPlayer().getWidth() * 2, world.getPlayer().getHeight() + 50);
        //buttonB.setBounds(Gdx.graphics.getWidth() - world.getPlayer().getWidth() * 2 - 20, 5, world.getPlayer().getWidth() * 2, world.getPlayer().getHeight() + 50);
        //buttonA.setSize(20,20);
        //buttonB.setSize(20,20);


        // creating heading
        LabelStyle timerStyle = new LabelStyle(black, Color.BLACK);

        heading = new Label(min + " : " + sec, timerStyle);

        // putting stuff together
        table.add(heading);
        table.row();
        table.add(buttonA).size(50,50);
        table.row();
        table.add(buttonB).size(50,50);
        table.row();

        stage.addActor(table);
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

    public void dispose(){
        stage.dispose();
    }




}

