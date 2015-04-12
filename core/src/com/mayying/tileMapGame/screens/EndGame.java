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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.multiplayer.MultiplayerMessaging;

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

    // k/d/score will be passed by using this format score;kills;deaths
    // identify which one is you

    public EndGame(GameWorld world) {
        this.world = world;
        this.playerName = new String[SideBar.finalRankPlayer.length];
        this.charName = new String[SideBar.finalRankPlayer.length];
        this.kd = new String[SideBar.finalRankPlayer.length];
        this.score = new String[SideBar.finalRankPlayer.length];

        for (int i = 0; i < playerName.length; i++) {
            playerName[i] = SideBar.finalRankPlayer[i];
            charName[i] = SideBar.finalRankCharName[i];
            kd[i] = SideBar.finalRankKD[i];
            score[i] = SideBar.finalRankScore[i];

            Gdx.app.log("EndGame", "playerName: " + playerName[i] + " charName: " + charName[i] + " kd: " + kd[i] + " score: " + score[i]);
        }
    }

    @Override
    public void show() {
        Gdx.app.log("EndGame", "EndGame still running");
        int myIndex = -1;

        // to determine if you win or not, need this value to identify which background sprite we should show in the screen
        for (int i = 0; i < playerName.length; i++) {
            if (world.getDevicePlayer().getName().equals(playerName[i])) {
                if (i == 0)
                    myIndex = i;
                break;
            }
        }

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
//        scoreBoard.align(Align.center);
        scoreBoard.add("Rank").width(150).center();
        scoreBoard.add("Player").width(278).center();
        scoreBoard.add("K/D").width(150).center();
        scoreBoard.add("Score").width(150).center().row();

        for (int i = 0; i < playerName.length; i++) {
            scoreBoard.add(String.valueOf(i + 1)).width(150).center();
            scoreBoard.add(playerName[i]).width(278).center();
            scoreBoard.add(kd[i]).width(150).center();
            scoreBoard.add(score[i]).width(150).center().row();
        }

        mainMenu = new TextButton("Main Menu", skin, "mainMenu");
        rematch = new TextButton("Rematch", skin, "rematch");

        table = new Table(skin);
        table.setFillParent(true);
        table.setBounds(0, 0, Play.V_WIDTH, Play.V_HEIGHT);
//        table.align(Align.center);

        table.add(scoreBoard).center().row();
        table.add(mainMenu);
        table.add(rematch);

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
        batch.dispose();
        skin.dispose();
        atlas.dispose();

    }
}
