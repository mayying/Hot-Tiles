package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Game;
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
import com.mayying.tileMapGame.multiplayer.MultiplayerMessaging;

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
    private TextButton[] textButton;
    private float timeLeft = 6;
    private int min, sec;
    private String myPlayerName, otherPlayerName, mode, myCharacterName, otherCharacterName;
    private MultiplayerMessaging multiplayerMessaging;

    public CharacterSelector() {
        mode = "desktop";
    }

    public CharacterSelector(MultiplayerMessaging multiplayerMessaging) {
        mode = "android";
        this.multiplayerMessaging = multiplayerMessaging;
    }

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

        textButton = new TextButton[4];

        textButton[0] = new TextButton("", skin, "player1");
        textButton[0].addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                toggleButton(0);
            }
        });

        textButton[1] = new TextButton("", skin, "player2");
        textButton[1].addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                toggleButton(1);
            }
        });

        textButton[2] = new TextButton("", skin, "player3");
        textButton[2].addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                toggleButton(2);
            }
        });

        textButton[3] = new TextButton("", skin, "player4");
        textButton[3].addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                toggleButton(3);

            }
        });

        subTable.add(textButton[0]);
        subTable.add(textButton[1]);
        subTable.add(textButton[2]);
        subTable.add(textButton[3]);
        subTable.align(Align.center);

        timer = new Label(String.valueOf(timeLeft), skin);

        charSelTable.add(heading).expandY().center().row();
        charSelTable.add(subTable).expandY().center().row();
        charSelTable.add(timer).expandY().center().row();

        stage.addActor(charSelTable);

//        playerName = multiplayerMessaging.getMyName();
        myPlayerName = "May Ying";
        otherPlayerName = "Mother";
        setDefaultCharacter();
    }

    private void setDefaultCharacter() {
        for (int i = 0; i < textButton.length; i++) {
            if (!textButton[i].isChecked()) {
                toggleButton(i);
                textButton[i].setChecked(true);
                break;
            }
        }
    }

    // Do setting for toggling button
    private void toggleButton(int index) {
        Gdx.app.log("toggleButton in CharacterSelector", index + " Disabled? " + textButton[index].isDisabled());
        textButton[index].setText(myPlayerName);
        textButton[index].setDisabled(true);

        for (int i = 0; i < textButton.length; i++) {
            if (i != index) {
                textButton[i].setText("");
                textButton[i].setChecked(false);
                textButton[i].setDisabled(false);
            }
        }
    }

    public String getMyCharacterName() {
        return "player_" + myCharacterName + "_";
    }

    public String getOtherCharacterName() {
        return "player_" + myCharacterName + "_";
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

        if (sec == 0) {
            for (int i = 0; i < textButton.length; i++) {
                if (textButton[i].isChecked() && textButton[i].getText().equals(myPlayerName)) {
                    myCharacterName = String.valueOf(i + 1);
                    break;
                } else if (textButton[i].isChecked() && textButton[i].getText().equals(otherPlayerName)) {
                    otherCharacterName = String.valueOf(i + 1);
                }
            }

            if (mode.equals("desktop"))
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Play());
            else if (mode.equals("android")) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Play(multiplayerMessaging));
            }

        }
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
