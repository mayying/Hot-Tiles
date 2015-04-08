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

import java.util.List;

/**
 * Created by May on 8/4/2015.
 */
public class CharacterSelector implements Screen {

    private static final String TAG = "HT_CHARSEL";
    private Table charSelTable, subTable;
    private Sprite background;
    private SpriteBatch spriteBatch;
    private Stage stage;
    private TextureAtlas charAtlas;
    private Skin skin;
    private Label heading, timer;
    private TextButton[] textButton;
    private float timeLeft = 20;
    private int min, sec, otherPlayerSel = -1, mySel = -1;
    private String myPlayerName, otherPlayerName, mode, myCharacterName, otherCharacterName;
    private MultiplayerMessaging multiplayerMessaging;
    private boolean imTheHost;
    private int selection;

    public CharacterSelector() {
        mode = "desktop";
    }

    public CharacterSelector(MultiplayerMessaging multiplayerMessaging) {
        mode = "android";
        this.multiplayerMessaging = multiplayerMessaging;
    }

    @Override
    public void show() {
        imTheHost = multiplayerMessaging.getMyId().equals(multiplayerMessaging.getHostId());
        Gdx.app.log(TAG,"I am the host: "+imTheHost);
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
        if(imTheHost){
            toggleButton(0);
        }else{
            toggleButton(textButton.length - 1);
        }
    }

    private void setOtherPlayerSelection(int index) {
        textButton[index].setText(otherPlayerName);
        textButton[index].setChecked(true);
        textButton[index].setDisabled(true);

        // Deselect the old button
        if(otherPlayerSel != -1) {
            textButton[otherPlayerSel].setText("");
            textButton[otherPlayerSel].setChecked(false);
            textButton[otherPlayerSel].setDisabled(false);
        }
        otherPlayerSel = index;
    }

    private void setSelection(int index) {
        // LAZY SYNCHRONIZE, GOT PROBLEM THEN DO
        textButton[index].setText(myPlayerName);
        textButton[index].setChecked(true);
        textButton[index].setDisabled(true);

        // Deselect the old button
        if(mySel != -1) {
            textButton[mySel].setText("");
            textButton[mySel].setChecked(false);
            textButton[mySel].setDisabled(false);
        }
        mySel = index;
    }

    // Do internal setting for toggling button
    private void toggleButton(int index) {
//        Gdx.app.log("toggleButton in CharacterSelector", index + " Disabled? " + textButton[index].isDisabled());
        if (imTheHost) {
            if (index != otherPlayerSel && index != mySel) {
                broadcastMessage("charsel", "host", String.valueOf(index));
                setSelection(index);
            }
        } else {
            broadcastMessage("charsel", "request", String.valueOf(index));
            // wait for server to reply
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

        // Set Character Selection For Other Player
        //TODO: maybe use a background thread
        List<String> msgs = multiplayerMessaging.getMessageBuffer();
        for (String msg : msgs) {
            parse(msg);
        }

        //TODO: Uncomment me when ready for multiplayer
        // Switch screen to Play when time's up
        if (sec == 0) {
//            for (int i = 0; i < textButton.length; i++) {
//                if (textButton[i].isChecked() && textButton[i].getText().equals(myPlayerName)) {
//                    myCharacterName = String.valueOf(i + 1);
//                    break;
//                } else if (textButton[i].isChecked() && textButton[i].getText().equals(otherPlayerName)) {
//                    otherCharacterName = String.valueOf(i + 1);
//                }
//            }
            myCharacterName = String.valueOf(mySel);
            otherCharacterName = String.valueOf(otherPlayerSel);
            if (mode.equals("desktop"))
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Play());
            else if (mode.equals("android")) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Play(multiplayerMessaging));
            }

        }
    }

    private void parse(String msg) {
        String[] message = msg.split(",");
        String command = message[1];
        if(command.equals("charsel")){
            String type = message[2];
            int idx = Integer.valueOf(message[3]);
            switch (type) {
                case "host":
                    // Host = Kim Jong Un
                    setOtherPlayerSelection(idx);
                    break;
                case "request":
                    // Check for index collision, reply if no collision, else ignore the user because I'm Kim
                    if (idx != mySel) {
                        // Give client the ok signal
                        broadcastMessage("charsel", "reply", String.valueOf(idx));
                        // Set client's selection
                        setOtherPlayerSelection(idx);
                    }
                    break;
                case "reply":
                    // lowly client
                    setSelection(idx);
                    break;
            }
        }else{
            Gdx.app.log("HT_CHARSEL","Unknown message format: "+msg);
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
    private void broadcastMessage(String... args) {
        String msg = "";
        for (String arg : args) {
            msg += arg + ",";
        }
        Gdx.app.log(TAG, "Broadcasting message: " + msg);
        multiplayerMessaging.broadcastMessage(msg);
    }

}
