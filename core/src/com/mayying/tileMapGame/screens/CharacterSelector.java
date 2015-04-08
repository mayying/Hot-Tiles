package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * Created by May on 8/4/2015.
 */
public class CharacterSelector implements Screen {

    private Table charSelTable, subTable;
    private Sprite background;
    private SpriteBatch spriteBatch;
    private Stage stage;
    private TextureAtlas charAtlas;
    private Skin skin;
    private Label heading, timer;
    private TextButton textButton1, textButton2, textButton3, textButton4;
    private float timeLeft = 6;
    private int min, sec, charSelected = 0;

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        background = new Sprite(new Texture(Gdx.files.internal("charSel/background.png")));
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ExtendViewport(Play.V_WIDTH, Play.V_HEIGHT));

        Gdx.input.setInputProcessor(stage);

        charAtlas = new TextureAtlas(Gdx.files.internal("charSel/players.txt"));
        skin = new Skin(Gdx.files.internal("charSel/charSel.json"), charAtlas);

        charSelTable = new Table(skin);
        charSelTable.setFillParent(true);
        charSelTable.setBounds(0, 0, Play.V_WIDTH, Play.V_HEIGHT);
        charSelTable.align(Align.center);

        heading = new Label("Please select your character", skin);

        subTable = new Table(skin);
        textButton1 = new TextButton("", skin, "player1");
        textButton1.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }
        });

        textButton2 = new TextButton("", skin, "player2");
        textButton2.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }
        });

        textButton3 = new TextButton("", skin, "player3");
        textButton3.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }
        });

        textButton4 = new TextButton("", skin, "player4");
        textButton4.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }
        });

        subTable.add(textButton1);
        subTable.add(textButton2);
        subTable.add(textButton3);
        subTable.add(textButton4);
        subTable.align(Align.center);

        timer = new Label(String.valueOf(timeLeft), skin);

        charSelTable.add(heading).expandY().center().row();
        charSelTable.add(subTable).expandY().center().row();
        charSelTable.add(timer).expandY().center().row();

        stage.addActor(charSelTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        background.draw(spriteBatch);
        spriteBatch.end();

        stage.act(delta);
        stage.draw();

        timeLeft -= delta;
        min = (int) Math.floor(timeLeft / 60.0f);
        sec = (int) (timeLeft - min * 60.0f);
        timer.setText(String.format("%01d", sec));

//        Gdx.app.log("TextButton1", " isChecked: " + textButton1.isChecked() + " isDisabled: " + textButton1.isDisabled());
//        Gdx.app.log("TextButton2", " isChecked: " + textButton2.isChecked() + " isDisabled: " + textButton2.isDisabled());
//        Gdx.app.log("TextButton3", " isChecked: " + textButton3.isChecked() + " isDisabled: " + textButton3.isDisabled());
//        Gdx.app.log("TextButton4", " isChecked: " + textButton4.isChecked() + " isDisabled: " + textButton4.isDisabled());

        if (textButton1.isChecked() && !textButton1.isDisabled()) {
            textButton1.setText("Player1");
            textButton2.setDisabled(true);
            textButton3.setDisabled(true);
            textButton4.setDisabled(true);
        }
        if (textButton2.isChecked() && !textButton2.isDisabled()) {
            textButton2.setText("Player1");
            textButton1.setDisabled(true);
            textButton3.setDisabled(true);
            textButton4.setDisabled(true);
        }
        if (textButton3.isChecked() && !textButton3.isDisabled()) {
            textButton3.setText("Player1");
            textButton1.setDisabled(true);
            textButton2.setDisabled(true);
            textButton4.setDisabled(true);
        } else if (textButton4.isChecked() && !textButton4.isDisabled()) {
            textButton4.setText("Player1");
            textButton1.setDisabled(true);
            textButton2.setDisabled(true);
            textButton3.setDisabled(true);
        } else if (!textButton1.isChecked()) {
            textButton1.setText("");
            textButton1.setDisabled(false);
            textButton2.setDisabled(false);
            textButton3.setDisabled(false);
            textButton4.setDisabled(false);
        } else if (!textButton2.isChecked()) {
            textButton2.setText("");
            textButton1.setDisabled(false);
            textButton2.setDisabled(false);
            textButton3.setDisabled(false);
            textButton4.setDisabled(false);
        } else if (!textButton3.isChecked()) {
            textButton3.setText("");
            textButton1.setDisabled(false);
            textButton2.setDisabled(false);
            textButton3.setDisabled(false);
            textButton4.setDisabled(false);
        } else if (!textButton4.isChecked()) {
            textButton4.setText("");
            textButton1.setDisabled(false);
            textButton2.setDisabled(false);
            textButton3.setDisabled(false);
            textButton4.setDisabled(false);
        }
//        Gdx.app.log(charSelected + "", textButton4.isChecked() + "");


    }

    @Override
    public void resize(int width, int height) {
        subTable.invalidateHierarchy();
        subTable.setSize(width, height);

        charSelTable.invalidateHierarchy();
        charSelTable.setSize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        spriteBatch.dispose();
        charAtlas.dispose();
        skin.dispose();
    }
}
