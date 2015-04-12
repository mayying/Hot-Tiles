package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.mayying.tileMapGame.entities.Player;
import com.mayying.tileMapGame.entities.ScoreBoard;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by May on 10/4/2015.
 */
public class EndGame implements Screen {
    private SpriteBatch batch;
    private Sprite background;
    private GameWorld world;
    private Stage stage;
    private Skin skin;
    private Table table, scoreBoard;
    private TextButton mainMenu, rematch;
    private TextureAtlas atlas;
    private ArrayList<Label> label = new ArrayList<>();
//    private ArrayList<ScoreBoard.Score> scores;
    HashMap<String,Boolean> rematchPoll = new HashMap<>();

    public EndGame(GameWorld world) {
        this.world = world;
        for (String p: Play.getMultiplayerMessaging().getJoinedParticipants()) {
            rematchPoll.put(p, false);
        }
    }

    @Override
    public void show() {
        Gdx.app.log("EndGame", "EndGame Screen initialized");
        ArrayList<ScoreBoard.Score> scores = ScoreBoard.getInstance().getScores();
        batch = new SpriteBatch();
        atlas = new TextureAtlas(Gdx.files.internal("endGame/endGame.txt"));
        Player devicePlayer = world.getDevicePlayer();
        if(scores.get(0).getPlayer().getName().equals(devicePlayer.getName()))
            background = new Sprite(new Texture(Gdx.files.internal("endGame/backgroundLose.png")));
        else {
            background = new Sprite(new Texture(Gdx.files.internal("endGame/" + devicePlayer.getModel() + "win.png")));
        }

        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ExtendViewport(Play.V_WIDTH, Play.V_HEIGHT));

        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("endGame/endGame.json"), atlas);

        scoreBoard = new Table(skin);
        scoreBoard.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("endGame/scoreBoard.png"))));
        scoreBoard.setSize(728, 400);
        scoreBoard.align(Align.top);
        //Headings
        scoreBoard.add(getCenteredLabel("Rank",skin)).width(150).padTop(100);
        scoreBoard.add(getCenteredLabel("Player",skin)).width(278).padTop(100);
        scoreBoard.add(getCenteredLabel("K/D",skin)).width(150).padTop(100);
        scoreBoard.add(getCenteredLabel("Score",skin)).width(150).padTop(100).row();


        for (int i = 0; i < scores.size(); i++) {
            ScoreBoard.Score s = scores.get(i);
            Player p = s.getPlayer();
            // Rank
            scoreBoard.add(getCenteredLabel(String.valueOf(i + 1),skin)).width(150).padTop(10);
            // Name
            scoreBoard.add(getCenteredLabel(p.getName(),skin)).padTop(10).width(278);
            // KD
            scoreBoard.add(getCenteredLabel(s.getKills() + " / " + s.getDeath(),skin)).padTop(10).width(150);
            // Score
            scoreBoard.add(getCenteredLabel(String.valueOf(s.getScore()), skin)).padTop(10).width(150).row();
        }
        mainMenu = new TextButton("Main Menu", skin, "mainMenu");
        mainMenu.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("EndGame", "touched");
                Play.getMultiplayerMessaging().leaveGame();
            }
        });

        rematch = new TextButton("Rematch", skin, "rematch");
        rematch.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rematchButtonClicked();
            }
        });

        table = new Table(skin);
        table.setFillParent(true);
        table.setBounds(0, 0, Play.V_WIDTH, Play.V_HEIGHT);
        table.add(scoreBoard).colspan(2).row();
        table.add(mainMenu);
        table.add(rematch);
//        table.setDebug(true);
//        scoreBoard.setDebug(true);
        stage.addActor(table);
    }

    private Label getCenteredLabel(String s, Skin skin){
        Label l = new Label(s, skin);
        l.setAlignment(Align.center);
        return l;
    }

    boolean leavingGame = false;
    public void leaveGame(){
        leavingGame = true;
    }

    boolean startGame = false;
    public void startGame() {
        startGame = true;
    }

    public void rematchButtonClicked(){
        //im the host
        if (Play.getMultiplayerMessaging().getMyId().equals(Play.getMultiplayerMessaging().getHostId())){
            setRematch(!rematchPoll.get(Play.getMultiplayerMessaging().getMyId()));
            Play.broadcastMessage("rematchAcknowledged," + Play.getMultiplayerMessaging().getMyId()
                    +"," + String.valueOf(rematchPoll.get(Play.getMultiplayerMessaging().getMyId())));
        } else {
            Play.broadcastMessage("rematchRequest," + String.valueOf(!rematchPoll.get(Play.getMultiplayerMessaging().getMyId())));
        }
    }

    public void setRematch(Boolean stat){
        rematchPoll.put(Play.getMultiplayerMessaging().getMyId(), stat);
        if (stat) {
            rematch.setText("cancel rematch");
        } else {
            rematch.setText("rematch");
        }
    }

    public void rematchCheck(){
        for (String msg : Play.getMultiplayerMessaging().getMessageBuffer()){
            String[] message = msg.split(",");
            String command = message[1];
            // <host_id>, <rematchAcknowledged>, <client_id>, <rematchPoll>
            if (command.equals("rematchAcknowledged") && Play.getMultiplayerMessaging().getHostId().equals(message[0])) {
                rematchPoll.put(message[2], Boolean.valueOf(message[3]));
                //client update text
                if (Play.getMultiplayerMessaging().getMyId().equals(message[2])){
                    setRematch(Boolean.valueOf(message[3]));
                }
            }
            //im the host
            // <client_id>, <rematchRequest>, <rematchPoll>
            if (Play.getMultiplayerMessaging().getMyId().equals(Play.getMultiplayerMessaging().getHostId())){
                if (command.equals("rematchRequest")) {
                    rematchPoll.put(message[0], Boolean.valueOf(message[2]));
                    Play.broadcastMessage("rematchAcknowledged,"+message[0]+","+message[2]);
                }
            }
        }
        for (String key : rematchPoll.keySet()){
            if (!rematchPoll.get(key))
                return;
        }
        Play.getMultiplayerMessaging().rematch();
    }

    @Override
    public void render(float delta) {
        if (leavingGame){
            leavingGame = false;
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(Play.getMultiplayerMessaging()));
        }
        if (startGame){
            startGame = false;
            ((Game) Gdx.app.getApplicationListener()).setScreen(new CharacterSelector(Play.getMultiplayerMessaging()));
        }
        rematchCheck();
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
