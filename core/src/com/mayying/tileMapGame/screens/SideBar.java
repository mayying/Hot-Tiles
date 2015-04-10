package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

import java.util.ArrayList;


/**
 * Created by May on 17/3/2015.
 */


public class SideBar implements Screen {

    private Stage stage;
    private Label timer, descriptionImg, descriptionText;
    private Label[] scoreBoard1st, scoreBoard2nd;
    private Skin skin;
    private GameWorld world;
    private ImageButton buttonA, buttonB, sound, question, close;
    private TextureAtlas buttonAtlas;
    private OrthographicCamera hudCamera;
    private Table table, descriptionTable, subTable, scoreBoardTable, scoreBoard1stTable, scoreBoard2ndTable;
    private LabelStyle labelStyle, playerStyle, player2Style;
    private ScoreBoard scoreBoard;
    private ArrayList<Score> score;
    private String powerUpName;
    private ImageButtonStyle imageButtonAStyle, imageButtonBStyle;

    volatile static int timeLeft = 1;

    private float gameTime = 60 + 30;
    private int min, sec;
    private boolean timeFrozen = true;
    private static boolean scoreUpdated = true;

    public SideBar(GameWorld world) {
        this.world = world;
        hudCamera = new OrthographicCamera();
        min = 1;
        sec = 30;
        labelStyle = new LabelStyle();
        // lmao should have made an arraylist/map to store them properly, initialize according to num players etc
        playerStyle = new LabelStyle();
        player2Style = new LabelStyle();
        player2Style.font = playerStyle.font = labelStyle.font = new BitmapFont(Gdx.files.internal("font/black.fnt"));
        imageButtonAStyle = new ImageButtonStyle();
        imageButtonBStyle = new ImageButtonStyle();
    }

    public void unfreezeGameTimer() {
        this.timeFrozen = false;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(Play.V_WIDTH, Play.V_HEIGHT, hudCamera));
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
//
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);

        buttonAtlas = new TextureAtlas(Gdx.files.internal("skin/skin.txt"));
        skin = new Skin(Gdx.files.internal("skin/gameSkin.json"), buttonAtlas);

        // container for all UI widgets
        table = new Table(skin);
        table.setFillParent(true);
        table.setBounds(0, 0, Play.V_WIDTH, Play.V_HEIGHT);
        table.align(Align.top);

        timer = new Label("Time Left\n" + min + " : " + sec, skin, "timer");
        timer.setAlignment(Align.center);

        sound = new ImageButton(skin, "sound");

        sound.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Jukebox.toggleMute();
                Jukebox.toggleMute();
            }
        });
        question = new ImageButton(skin, "question");
        close = new ImageButton(skin, "close");
        close.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Play.getMultiplayerMessaging().leaveGame();
//                Gdx.app.exit();
            }
        });

        scoreBoard = ScoreBoard.getInstance();
        ArrayList<Score> score = scoreBoard.getScores();

        scoreBoard1stTable = new Table(skin);
        scoreBoard1stTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("skin/win_score210x89.png"))));
        scoreBoard1st = new Label[2];
        if (score.size()>=2) {
            scoreBoard1st[0] = new Label("", skin, score.get(1).getPlayer().getName() + "head");
            playerStyle.background = skin.getDrawable(score.get(1).getPlayer().getName() + "head");
            scoreBoard1st[0].setStyle(playerStyle);
        }
        scoreBoard1st[1] = new Label("Score: 0", skin);

        scoreBoard1stTable.add(scoreBoard1st[0]).height(55).width(55).padTop(15).padLeft(5);
        scoreBoard1stTable.add(scoreBoard1st[1]).fill().expandX().padTop(10);

        scoreBoard2ndTable = new Table(skin);
        scoreBoard2ndTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("skin/score210x89.png"))));
        scoreBoard2nd = new Label[2];
        scoreBoard2nd[0] = new Label("", skin, score.get(0).getPlayer().getName() + "head");
        player2Style.background = skin.getDrawable(score.get(0).getPlayer().getName() + "head");
        scoreBoard2nd[0].setStyle(player2Style);
        scoreBoard2nd[1] = new Label("Score: 0", skin);

        scoreBoard2ndTable.add(scoreBoard2nd[0]).height(55).width(55).padTop(15).padLeft(5);
        scoreBoard2ndTable.add(scoreBoard2nd[1]).fill().expandX().padTop(10);

        scoreBoardTable = new Table();
        scoreBoardTable.add(scoreBoard1stTable).row();
        scoreBoardTable.add(scoreBoard2ndTable).row();

        descriptionImg = new Label("", skin);
        descriptionImg.setAlignment(Align.bottom);

        descriptionText = new Label("", skin);
        descriptionText.setWrap(true);
        descriptionText.setAlignment(Align.top);
        descriptionText.setFontScale(0.75f);

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
        subTable.add(buttonA).right().expandX().expandY().width(140).height(140).center().row();
        subTable.add(buttonB).left().expandX().width(140).height(140).row();

        descriptionTable = new Table();
        descriptionTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("skin/skinSquare280x210.png"))));
        descriptionTable.add(descriptionImg).padTop(40).width(90).height(90).row();
        descriptionTable.add(descriptionText).expandY().width(150).height(140).top().center();

        // putting stuff together
        //table.align(Align.center);
        table.add(timer).top().left().expandX().padTop(10).height(140).width(210);
        table.add(sound).top();
        table.add(question).top();
        table.add(close).top().row();

        table.add(scoreBoardTable).left().expandX().height(178).width(210);
        table.add(descriptionTable).fill().colspan(3).row();

        table.add(world.getMyTouchPad().getTouchPad()).left().expandY().width(210);
        table.add(subTable).fill().colspan(3);

        stage.addActor(table);

    }

    public void render(float delta) {
        if (timeLeft == 0) {
            timeLeft = 1; //Allow game to be restarted next time
            Play.getMultiplayerMessaging().leaveGame();
        } else {
            stage.act(delta);
            stage.draw();
            if (!timeFrozen) {
                gameTime -= delta;
                min = (int) Math.floor(gameTime / 60.0f);
                sec = (int) (gameTime - min * 60.0f);
                timeLeft = min * 60 + sec;
                timer.setText("Time Left\n" + String.format("%02d : %02d", min, sec));
            } else {
                timer.setText("Time Left\n" + "-- : --");
            }


            if (scoreUpdated) {
//                Gdx.app.log("HT_Sidebar", "Updating Board");
                updateBoard();
                scoreUpdated = false;
            }

            if (world.pickUpPowerUp()) {
                powerUpName = world.getPowerUp().getName();
                descriptionText.setText(powerUpName + "\n" + world.getPowerUp().getDescription());
                labelStyle.background = skin.getDrawable(world.getPowerUp().getFilename());
                descriptionImg.setStyle(labelStyle);

//                Gdx.app.log("SideBar", "ButtonA: " + buttonA.isDisabled() + " ButtonB: " + buttonB.isDisabled());
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
        score = scoreBoard.getScores();

        if (score.size()>=2) {
            playerStyle.background = skin.getDrawable(score.get(1).getPlayer().getName() + "head");
            scoreBoard1st[1].setText("Score: " + score.get(1).getScore());
        }

        player2Style.background = skin.getDrawable(score.get(0).getPlayer().getName() + "head");
        scoreBoard2nd[1].setText("Score: " + score.get(0).getScore());
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

        scoreBoard1stTable.invalidateHierarchy();
        scoreBoard1stTable.setSize(width, height);

        scoreBoard2ndTable.invalidateHierarchy();
        scoreBoard2ndTable.setSize(width, height);
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

    public void dispose() {
        stage.dispose();
        buttonAtlas.dispose();
        skin.dispose();
    }


}

