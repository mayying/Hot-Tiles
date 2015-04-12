package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.ScoreBoard;
import com.mayying.tileMapGame.multiplayer.MultiplayerMessaging;

import java.util.ArrayList;

/**
 * Created by May on 10/4/2015.
 */
public class EndGame implements Screen {
    private SpriteBatch batch;
    private Sprite background;
    private String[] playerName, charName, kd, score;
    private MultiplayerMessaging multiplayerMessaging;
    private GameWorld world;
    private Stage stage;
    private Skin skin;
    private Table table, scoreBoard;
    private TextButton mainMenu, rematch;
    private TextureAtlas atlas;
    private ArrayList<Label> label;
    private ArrayList<ScoreBoard.Score> scores;

    // k/d/score will be passed by using this format score;kills;deaths
    // identify which one is you

    public EndGame(GameWorld world) {
        this.world = world;
        scores = ScoreBoard.getInstance().getScores();
        this.playerName = new String[scores.size()];
        this.charName = new String[scores.size()];
        this.kd = new String[scores.size()];
        this.score = new String[scores.size()];
        label = new ArrayList<>();

        for (int i = 0; i < playerName.length; i++) {
            playerName[i] = scores.get(i).getPlayer().getName();
            charName[i] = scores.get(i).getPlayer().getModel();
            kd[i] = scores.get(i).getKills() + " / " + scores.get(i).getDeath();
            score[i] = String.valueOf(scores.get(i).getScore());
            Gdx.app.log("EndGame", "playerName: " + playerName[i] + " charName: " + charName[i] + " kd: " + kd[i] + " score: " + score[i]);
        }
    }

    @Override
    public void show() {
        Gdx.app.log("EndGame", "EndGame Screen initialized");
        int myIndex = -1;

        // to determine if you win or not, need this value to identify which background sprite we should show in the screen
        for (int i = 0; i < playerName.length; i++) {
            if (i == playerName.length - 1 && world.getDevicePlayer().getName().equals(playerName[i])) {
                myIndex = i;
                break;
            }
        }

        Gdx.app.log("What is my index", myIndex + "");
        batch = new SpriteBatch();
        atlas = new TextureAtlas(Gdx.files.internal("endGame/endGame.txt"));
        if (myIndex == -1)
            background = new Sprite(new Texture(Gdx.files.internal("endGame/backgroundLose.png")));
        else {
            background = new Sprite(new Texture(Gdx.files.internal("endGame/" + charName[myIndex] + "win.png")));
        }

        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ExtendViewport(Play.V_WIDTH, Play.V_HEIGHT));

        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("endGame/endGame.json"), atlas);

        scoreBoard = new Table(skin);
        scoreBoard.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("endGame/scoreBoard.png"))));
        scoreBoard.setSize(728, 400);
        scoreBoard.align(Align.top);

        label.add(new Label("Rank", skin));
        label.add(new Label("Player", skin));
        label.add(new Label("K/D", skin));
        label.add(new Label("Score", skin));

        scoreBoard.add(label.get(0)).width(150);
        scoreBoard.add(label.get(1)).width(278);
        scoreBoard.add(label.get(2)).width(150);
        scoreBoard.add(label.get(3)).width(150).row();

        int rankIndex = 0;
        for (int i = playerName.length - 1; i > -1; i--) {
            label.add(new Label(String.valueOf(++rankIndex), skin));
            label.add(new Label(playerName[i], skin));
            label.add(new Label(kd[i], skin));
            label.add(new Label(score[i], skin));
        }

        int nameIndex = 1, scoreIndex = 3;

        for (int i = 0; i < label.size(); i++) {
            label.get(i).setAlignment(Align.center);
            if (i == nameIndex) {
                nameIndex += 4;
                scoreBoard.add(label.get(i)).width(278);
            } else if (i == scoreIndex) {
                scoreIndex += 4;
                scoreBoard.add(label.get(i)).width(150).row();
            } else
                scoreBoard.add(label.get(i)).width(150);
        }

        mainMenu = new TextButton("Main Menu", skin, "mainMenu");
        rematch = new TextButton("Rematch", skin, "rematch");

        table = new Table(skin);
        table.setFillParent(true);
        table.setBounds(0, 0, Play.V_WIDTH, Play.V_HEIGHT);

        table.add(scoreBoard).colspan(2).row();
        table.add(mainMenu);
        table.add(rematch);
//        table.setDebug(true);
        scoreBoard.setDebug(true);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        background.draw(batch);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        table.invalidateHierarchy();
        table.setSize(width, height);

        scoreBoard.invalidateHierarchy();
        scoreBoard.setSize(width, height);
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
        ScoreBoard.getInstance().reset();
        batch.dispose();
        skin.dispose();
        atlas.dispose();


    }
}
