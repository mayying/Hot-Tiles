package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.entities.ScoreBoard;
import com.mayying.tileMapGame.entities.ScoreBoard.Score;
import com.mayying.tileMapGame.entities.powerups.factory.PowerUp;
import com.mayying.tileMapGame.tween.ActorAccessor;

import java.util.ArrayList;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by May on 17/3/2015.
 */
public class SideBar implements Screen {

    private Stage stage;
    private Label timer, descriptionImg, descriptionText;
    private Skin skin;
    private GameWorld world;
    private TextureAtlas buttonAtlas;
    private OrthographicCamera hudCamera;
    private Table table, descriptionTable, subTable, scoreBoardTable;
    private LabelStyle labelStyle;
    private TweenManager tweenManager;
    private ImageButton buttonA, buttonB;
    private ImageButtonStyle imageButtonAStyle, imageButtonBStyle;

    private Label[][] scoreBoardLabel;
    private Table[] scoreBoardSubTable;
    private LabelStyle[] playerStyle;

    private ScoreBoard scoreBoard;

    private boolean muteMusic = false, muteSfx = false;
    private static boolean scoreUpdated = true;
    private float gameTime = 60 + 30;
    private int min, sec;
    private boolean timeFrozen = true;

    private int NUM_OF_PLAYER = 2;
    volatile static int timeLeft = 1;

    public SideBar(GameWorld world, int noOfPlayer) {
        this.world = world;
        NUM_OF_PLAYER = noOfPlayer;
        hudCamera = new OrthographicCamera();
        labelStyle = new LabelStyle();
        playerStyle = new LabelStyle[NUM_OF_PLAYER];

        for (int i = 0; i < NUM_OF_PLAYER; i++) {
            playerStyle[i] = new LabelStyle();
            playerStyle[i].font = new BitmapFont(Gdx.files.internal("font/black.fnt"));
        }

        labelStyle.font = playerStyle[0].font;

        imageButtonAStyle = new ImageButtonStyle();
        imageButtonBStyle = new ImageButtonStyle();
    }

    public void unfreezeGameTimer() {
        this.timeFrozen = false;
    }

    public boolean isTimeFrozen() {
        return timeFrozen;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(Play.V_WIDTH, Play.V_HEIGHT, hudCamera));

        tweenManager = new TweenManager();
        Tween.registerAccessor(Sprite.class, new ActorAccessor());

        InputMultiplexer inputMultiplexer = new InputMultiplexer();

        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);

        buttonAtlas = new TextureAtlas(Gdx.files.internal("skin/skin.txt"));
        skin = new Skin(Gdx.files.internal("skin/gameSkin.json"), buttonAtlas);

        // container for all UI widgets
        table = new Table(skin);
        table.setFillParent(true);
        table.setBounds(0, 0, Play.V_WIDTH, Play.V_HEIGHT);
        table.align(Align.top);

        timer = new Label(min + ":" + sec, skin, "timer");
        timer.setAlignment(Align.center);

        ImageButton music = new ImageButton(skin, "music");
        music.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!muteMusic) {
                    muteMusic = true;
                    Jukebox.toggleMuteMusic("background", muteMusic);
                } else {
                    muteMusic = false;
                    Jukebox.toggleMuteMusic("background", muteMusic);

                }
            }
        });

        ImageButton sound = new ImageButton(skin, "sound");
        sound.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!muteSfx)
                    muteSfx = true;
                else
                    muteSfx = false;
                Jukebox.toggleMuteSfx(muteSfx);
            }
        });

        ImageButton close = new ImageButton(skin, "close");
        close.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Play.getMultiPlayerMessaging().leaveGame();
            }
        });

        scoreBoard = ScoreBoard.getInstance();
        ArrayList<Score> score = scoreBoard.getScores();

        // +1 for lightning place holder, cause I lazy to create a new subTable
        scoreBoardLabel = new Label[NUM_OF_PLAYER + 1][2];

        int index = NUM_OF_PLAYER - 1;

        for (int i = 0; i < NUM_OF_PLAYER; i++) {
            scoreBoardLabel[i][0] = new Label("", skin, score.get(index).getPlayer().getModel() + "head");
            playerStyle[i].background = skin.getDrawable(score.get(index--).getPlayer().getModel() + "head");
            scoreBoardLabel[i][0].setStyle(playerStyle[i]);
            scoreBoardLabel[i][1] = new Label("Score: 0", skin);
        }

        scoreBoardLabel[NUM_OF_PLAYER][0] = new Label("Zap it!", skin, "lightningFont");
        scoreBoardLabel[NUM_OF_PLAYER][1] = new Label("", skin, "lightning");

        // +1 for lightning place holder, cause I lazy to create a new subTable
        scoreBoardSubTable = new Table[NUM_OF_PLAYER + 1];
        scoreBoardTable = new Table();

        for (int i = 0; i < NUM_OF_PLAYER; i++) {
            scoreBoardSubTable[i] = new Table(skin);
            if (i == 0)
                scoreBoardSubTable[i].setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("skin/win_score210x89.png"))));
            else
                scoreBoardSubTable[i].setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("skin/score210x89.png"))));
            scoreBoardSubTable[i].add(scoreBoardLabel[i][0]).width(45).height(45).padTop(15).padLeft(15);
            scoreBoardSubTable[i].add(scoreBoardLabel[i][1]).fill().expandX().padTop(10).padLeft(10);
            scoreBoardTable.add(scoreBoardSubTable[i]).row();
        }

        scoreBoardSubTable[NUM_OF_PLAYER] = new Table(skin);
        scoreBoardSubTable[NUM_OF_PLAYER].add(scoreBoardLabel[NUM_OF_PLAYER][0]).padTop(15).padLeft(30).fill().expandX();
        scoreBoardSubTable[NUM_OF_PLAYER].add(scoreBoardLabel[NUM_OF_PLAYER][1]).padTop(15).padLeft(30).right().height(55).width(55);
        scoreBoardTable.add(scoreBoardSubTable[NUM_OF_PLAYER]).row();

        descriptionImg = new Label("", skin);
        descriptionImg.setAlignment(Align.bottom);

        descriptionText = new Label("", skin, "description");
        descriptionText.setWrap(true);
        descriptionText.setAlignment(Align.top);

        world.getMyTouchPad().getTouchPad().setPosition(0, 0);

        buttonA = new ImageButton(skin);
        buttonA.setDisabled(true);
        buttonA.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                buttonA.setDisabled(true);
                buttonA.setChecked(true);
                PowerUp p = world.getDevicePlayer().getPowerUp(0);
                if (p != null) {
                    p.action();
                    world.getDevicePlayer().removePowerUp(0);
                }
            }
        });

        buttonB = new ImageButton(skin);
        buttonB.setDisabled(true);
        buttonB.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                buttonB.setDisabled(true);
                buttonB.setChecked(true);
                PowerUp p = world.getDevicePlayer().getPowerUp(1);
                if (p != null) {
                    p.action();
                    world.getDevicePlayer().removePowerUp(1);
                }
            }
        });

        subTable = new Table();
        subTable.add(buttonA).right().fill().expandX().expandY().width(140).height(140).row();
        subTable.add(buttonB).left().fill().expandX().width(140).height(140).row();

        descriptionTable = new Table();
        descriptionTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("skin/skinSquare280x210.png"))));
        descriptionTable.add(descriptionImg).padTop(40).width(90).height(90).row();
        descriptionTable.add(descriptionText).expandY().width(150).height(140).top().center();

        // putting stuff together
        table.add(timer).top().left().padTop(0.02f * table.getHeight()).width(210);
        table.add(music).top();
        table.add(sound).top();
        table.add(close).top().row();

        table.add(scoreBoardTable).left().expandX().height(178).width(210);
        table.add(descriptionTable).fill().colspan(3).padTop(0.05f * table.getHeight()).row();

        table.add(world.getMyTouchPad().getTouchPad()).left().expandY().width(300).height(300);
        table.add(subTable).fill().colspan(3);
        stage.addActor(table);
//        table.setDebug(true);
        animate(0);
    }

    public void showLightning(boolean show) {
        if (show && !scoreBoardSubTable[NUM_OF_PLAYER].isVisible()) {
            scoreBoardTable.add(scoreBoardSubTable[NUM_OF_PLAYER]).row();
            scoreBoardSubTable[NUM_OF_PLAYER].setVisible(true);
        } else if (!show) {
            scoreBoardSubTable[NUM_OF_PLAYER].setVisible(false);
        }
    }

    public void animate(int condition) {
        switch (condition) {
            case 0:
                Timeline.createSequence().beginSequence()
                        .push(Tween.to(scoreBoardLabel[NUM_OF_PLAYER][1], ActorAccessor.RGB, .5f).target(1, 1, 0))
                        .push(Tween.to(scoreBoardLabel[NUM_OF_PLAYER][1], ActorAccessor.RGB, .5f).target(0, 1, 0))
                        .push(Tween.to(scoreBoardLabel[NUM_OF_PLAYER][1], ActorAccessor.RGB, .5f).target(1, 0, 0))
                        .end().repeat(Tween.INFINITY, 0).start(tweenManager);
                break;
            case 1:
                Timeline.createSequence().beginSequence()
                        .push(Tween.to(timer, ActorAccessor.RGB, .5f).target(1, 0, 0))
                        .end().repeat(Tween.INFINITY, 0).start(tweenManager);
                break;
        }
    }

    float tick = 5.05f;

    public void render(float delta) {
        tweenManager.update(Gdx.graphics.getDeltaTime());
        if (timeLeft == 0) {
            timeLeft = 1; //Allow game to be restarted next time
            ((Game) (Gdx.app.getApplicationListener())).setScreen(new EndGame(world));
//            Play.getMultiplayerMessaging().leaveGame();
        } else {
            stage.act(delta);
            stage.draw();
            if (!timeFrozen) {
                if ((gameTime < tick)) {
                    Jukebox.play("reminder");
                    animate(1);
                    tick -= 1;
                }
                gameTime -= delta;
                min = (int) Math.floor(gameTime / 60.0f);
                sec = (int) (gameTime - min * 60.0f);
                timeLeft = min * 60 + sec;
                timer.setText(String.format("%02d:%02d", min, sec));
            } else
                timer.setText("--:--");

            if (scoreUpdated) {
                updateBoard();
                scoreUpdated = false;
            }

            if (world.pickUpPowerUp()) {
                String powerUpName = world.getPowerUp().getName();
                descriptionText.setText(powerUpName + "\n" + world.getPowerUp().getDescription());
                labelStyle.background = skin.getDrawable(world.getPowerUp().getFilename());
                descriptionImg.setStyle(labelStyle);

                if (buttonA.isDisabled()) {
                    buttonA.setDisabled(false);
                    buttonA.setChecked(false);
                    imageButtonAStyle.imageUp = skin.getDrawable(world.getPowerUp().getFilenameBtn());
                    imageButtonAStyle.imageChecked = skin.getDrawable("skinRound140x140");
                    buttonA.setStyle(imageButtonAStyle);

                } else if (buttonB.isDisabled()) {
                    buttonB.setDisabled(false);
                    buttonB.setChecked(false);
                    imageButtonBStyle.imageUp = skin.getDrawable(world.getPowerUp().getFilenameBtn());
                    imageButtonBStyle.imageChecked = skin.getDrawable("skinRound140x140");
                    buttonB.setStyle(imageButtonBStyle);
                }
            }
        }

    }

    // there was brave attempt to implement elegant methods. there was failure. tears were shed, rares were lost T_T

    public static void onScoreUpdated() {
        scoreUpdated = true;
    }

    private void updateBoard() {
        ArrayList<Score> score = scoreBoard.getScores();
        int index = NUM_OF_PLAYER - 1;
        for (int i = 0; i < NUM_OF_PLAYER; i++) {
            playerStyle[i].background = skin.getDrawable(score.get(index).getPlayer().getModel() + "head");
            scoreBoardLabel[i][1].setText("Score: " + score.get(index--).getScore());
        }

    }

    @Override
    public void resize(int width, int height) {
        table.invalidateHierarchy();
        table.setSize(width, height);

        descriptionTable.invalidateHierarchy();
        descriptionTable.setSize(width, height);

        subTable.invalidateHierarchy();
        subTable.setSize(width, height);

        scoreBoardTable.invalidateHierarchy();
        scoreBoardTable.setSize(width, height);

        for (int i = 0; i < NUM_OF_PLAYER; i++) {
            scoreBoardSubTable[i].invalidateHierarchy();
            scoreBoardSubTable[i].setSize(width, height);
        }
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

    public void dispose() {
        stage.dispose();
        buttonAtlas.dispose();
        skin.dispose();
    }


}

