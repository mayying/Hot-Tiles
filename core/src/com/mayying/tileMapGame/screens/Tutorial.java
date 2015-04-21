package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.multiplayer.MultiplayerMessaging;
import com.mayying.tileMapGame.multiplayer.SinglePlayerDummyMessaging;

import java.util.List;

/**
 * Created by May on 18/4/2015.
 */
public class Tutorial implements Screen, MultiplayerMessaging{
    private SpriteBatch spriteBatch;
    private Sprite[] sprite;
    private OrthographicCamera camera;
    private Stage stage;
    private TextureAtlas buttonAtlas;
    private Skin skin;
    private Table table;
    private Texture[] texture;
    private ImageButton closeButton, leftButton, rightButton;

    private MultiplayerMessaging multiplayerMessaging;

    private int currentPage = 1;

    public Tutorial(MultiplayerMessaging multiplayerMessaging) {
        this.multiplayerMessaging = multiplayerMessaging;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();

        spriteBatch = new SpriteBatch();

        texture = new Texture[3];

        sprite = new Sprite[3];

        stage = new Stage(new ExtendViewport(Play.V_WIDTH, Play.V_HEIGHT, camera));

        Gdx.input.setInputProcessor(stage);

        buttonAtlas = new TextureAtlas(Gdx.files.internal("tutorial/buttonSkin.txt"));
        skin = new Skin(Gdx.files.internal("tutorial/tutorial.json"), buttonAtlas);

        table = new Table();
        table.setFillParent(true);
        table.setBounds(0, 0, Play.V_WIDTH, Play.V_HEIGHT);

        closeButton = new ImageButton(skin, "close");
        closeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Jukebox.play("buttonPressed");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leaveGame();
            }
        });

        leftButton = new ImageButton(skin, "left");

        leftButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Jukebox.play("buttonPressed");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                changePage(--currentPage);
            }
        });

        rightButton = new ImageButton(skin, "right");
        rightButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Jukebox.play("buttonPressed");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (currentPage == 3)
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new CharacterSelector(new SinglePlayerDummyMessaging(multiplayerMessaging)));
                else
                    changePage(++currentPage);
            }
        });

        table.add(leftButton).expand().left();
        table.add(rightButton).expand().right().row();
        table.add(closeButton).expandX().bottom().right();

        leftButton.setVisible(false);
        rightButton.setVisible(true);

        changePage(currentPage);

        stage.addActor(table);
    }

    public void changePage(int index) {
        if (sprite[index - 1] == null) {
            sprite[index - 1] = new Sprite(new Texture(Gdx.files.internal("tutorial/tutorial" + index + ".png")));
            sprite[index - 1].setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        switch (index) {
            case 1:
                rightButton.setVisible(true);
                leftButton.setVisible(false);
                break;
            case 2:
            case 3:
                leftButton.setVisible(true);
                rightButton.setVisible(true);
                break;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (sprite[currentPage - 1] != null) {
            spriteBatch.begin();
            sprite[currentPage - 1].draw(spriteBatch);
            spriteBatch.end();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        table.invalidateHierarchy();
        table.setSize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        spriteBatch.dispose();
        buttonAtlas.dispose();
        skin.dispose();
    }

    @Override
    public void broadcastMessage(String msg) {

    }

    @Override
    public List<String> getParticipants() {
        return null;
    }

    @Override
    public List<String> getJoinedParticipants() {
        return null;
    }

    @Override
    public List<String> getJoinedParticipantsName() {
        return null;
    }

    @Override
    public String getMyId() {
        return null;
    }

    @Override
    public String getMyName() {
        return null;
    }

    @Override
    public List<String> getMessageBuffer(char screenTag) {
        return null;
    }

    @Override
    public void clearMessageBufferExcept(char screenTag) {

    }

    @Override
    public String getHostId() {
        return null;
    }

    @Override
    public void sendInvitations() {

    }

    @Override
    public void seeInvitations() {

    }

    @Override
    public void startQuickGame() {

    }

    @Override
    public void signIn() {

    }

    @Override
    public void exit() {

    }

    @Override
    public void leaveGame() {
        if (this.multiplayerMessaging!=null) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(this.multiplayerMessaging));
        } else {
            //TODO used in desktop mode
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(this));
        }
    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public void rematch() {

    }

    @Override
    public void setNoOfPlayers(int noOfPlayers) {

    }

    @Override
    public int getNoOfPlayers() {
        return 0;
    }
}
