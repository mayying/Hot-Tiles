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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.multiplayer.MultiPlayerMessaging;
import com.mayying.tileMapGame.tween.ActorAccessor;
import com.mayying.tileMapGame.tween.SpriteAccessor;

import java.util.ArrayList;
import java.util.List;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by May on 2/4/2015.
 */
public class MainMenu implements Screen {
    public static boolean friendsPlay = false;
    private SpriteBatch batch;
    private Sprite background;
    private TextButton buttonMinus, buttonFriends, buttonPlus, buttonTutorial, buttonExit, buttonInvite, buttonJoin, buttonPlay, buttonBack;
    private ImageButton buttonAchievement, buttonLeaderBoard, buttonSettings;
    private Label heading;
    private Stage stage;
    private TextureAtlas buttonAtlas;
    private Skin skin;
    private TweenManager tweenManager;
    private Table table, subTable, sideTable;
    private OrthographicCamera camera;

    private MultiPlayerMessaging multiplayerMessaging;

    private List<Actor> firstPageActors = new ArrayList<>(), secondPageActors = new ArrayList<>();
    private boolean startGame;

    public MainMenu(MultiPlayerMessaging multiplayerMessaging) {
        this.multiplayerMessaging = multiplayerMessaging;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        startGame = false;

        tweenManager = new TweenManager();
        Tween.registerAccessor(Actor.class, new ActorAccessor());
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());

        batch = new SpriteBatch();
        background = new Sprite(new Texture(Gdx.files.internal("mainMenu/background.png")));
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(new ExtendViewport(Play.V_WIDTH, Play.V_HEIGHT, camera));

        Gdx.input.setInputProcessor(stage);

        buttonAtlas = new TextureAtlas(Gdx.files.internal("mainMenu/buttonSkin.txt"));
        skin = new Skin(Gdx.files.internal("mainMenu/mainMenu.json"), buttonAtlas);

        heading = new Label("Hot Tiles", skin);

        multiplayerMessaging.setNoOfPlayers(2);

        buttonAchievement = new ImageButton(skin, "achievement");
        buttonAchievement.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Jukebox.play("buttonPressed");
                multiplayerMessaging.displayAchievement();
            }
        });

        buttonLeaderBoard = new ImageButton(skin, "leaderBoard");
        buttonLeaderBoard.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Jukebox.play("buttonPressed");
                multiplayerMessaging.displayLeaderBoardScore();
            }
        });

        buttonSettings = new ImageButton(skin, "settings");
        buttonSettings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Jukebox.play("buttonPressed");
            }
        });

        sideTable = new Table();
        sideTable.setFillParent(true);
        sideTable.align(Align.topLeft);
        sideTable.add(buttonAchievement).padTop(Play.V_HEIGHT * 0.1f).padBottom(20).row();
        sideTable.add(buttonLeaderBoard).padBottom(20).row();
        sideTable.add(buttonSettings).row();

        buttonPlay = new TextButton("Play", skin);
        firstPageActors.add(buttonPlay);
        buttonPlay.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Jukebox.play("buttonPressed");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showMenu(1);
            }
        });

        buttonTutorial = new TextButton("Tutorial", skin);
        firstPageActors.add(buttonTutorial);
        buttonTutorial.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Jukebox.play("buttonPressed");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
//                SideBar.NUM_OF_PLAYER = 1;
                stage.addAction(Actions.sequence(Actions.moveBy(-stage.getWidth(), 0, 0.25f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new Tutorial(multiplayerMessaging));
                    }
                })));
            }
        });

        buttonExit = new TextButton("Exit", skin);
        firstPageActors.add(buttonExit);

        buttonExit.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Jukebox.play("buttonPressed");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Jukebox.stopAll();
                Jukebox.stopMusic("mainMenu");
                multiplayerMessaging.exit();
            }
        });

        buttonMinus = new TextButton("-", skin, "plusMinus");
        secondPageActors.add(buttonMinus);
        buttonMinus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (multiplayerMessaging.getNoOfPlayers() > 2) {
                    int noOfPlayers = multiplayerMessaging.getNoOfPlayers() - 1;
                    buttonFriends.setText(String.valueOf(noOfPlayers) + "P");
                    multiplayerMessaging.setNoOfPlayers(noOfPlayers);
                    Jukebox.play("buttonPressed");
                }
            }
        });

        buttonFriends = new TextButton("2P", skin);
        secondPageActors.add(buttonFriends);
        buttonFriends.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Jukebox.play("buttonPressed");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (multiplayerMessaging.isLoggedIn()) {
                    showLoading();
                    multiplayerMessaging.startQuickGame();
                } else {
                    showLoading();
                    multiplayerMessaging.signIn();
                }
            }
        });

        buttonPlus = new TextButton("+", skin, "plusMinus");
        secondPageActors.add(buttonPlus);
        buttonPlus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Jukebox.play("buttonPressed");
                if (multiplayerMessaging.getNoOfPlayers() < 4) {
                    int noOfPlayers = multiplayerMessaging.getNoOfPlayers() + 1;
                    buttonFriends.setText(String.valueOf(noOfPlayers) + "P");
                    multiplayerMessaging.setNoOfPlayers(noOfPlayers);
                    Jukebox.play("buttonPressed");
                }
            }
        });

        buttonInvite = new TextButton("Invite", skin);
        secondPageActors.add(buttonInvite);
        buttonInvite.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Jukebox.play("buttonPressed");
                multiplayerMessaging.sendInvitations();
                friendsPlay = true;
            }
        });

        buttonJoin = new TextButton("Join", skin);
        secondPageActors.add(buttonJoin);
        buttonJoin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Jukebox.play("buttonPressed");
                multiplayerMessaging.seeInvitations();
                friendsPlay = true;
            }
        });

        buttonBack = new TextButton("Back", skin);
        secondPageActors.add(buttonBack);
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Jukebox.play("buttonPressed");
                showMenu(0);
            }
        });

        //tableMenu
        table = new Table(skin);
        table.setFillParent(true);
        table.setBounds(0, 0, Play.V_WIDTH, Play.V_HEIGHT);
        table.align(Align.top);

        subTable = new Table(skin);
        subTable.add(buttonMinus).padRight(Play.V_WIDTH / 10).padLeft(Play.V_WIDTH / 6);
        subTable.add(buttonFriends);
        subTable.add(buttonPlus).padLeft(Play.V_WIDTH / 10).padRight(Play.V_WIDTH / 6).row();
        stage.addActor(table);
        stage.addActor(sideTable);

        showMenu(0);

        Timeline.createSequence().beginSequence()
                .push(Tween.set(background, SpriteAccessor.ALPHA).target(0))
                .push(Tween.set(heading, ActorAccessor.ALPHA).target(0))
                .push(Tween.set(buttonPlay, ActorAccessor.ALPHA).target(0))
                .push(Tween.set(buttonTutorial, ActorAccessor.ALPHA).target(0))
                .push(Tween.set(buttonExit, ActorAccessor.ALPHA).target(0))
                .push(Tween.from(background, SpriteAccessor.ALPHA, 1f).target(0))
                .push(Tween.to(background, SpriteAccessor.ALPHA, 1f).target(1))
                .push(Tween.to(heading, ActorAccessor.ALPHA, .5f).target(1))
                .push(Tween.to(buttonPlay, ActorAccessor.ALPHA, .2f).target(1))
                .push(Tween.to(buttonTutorial, ActorAccessor.ALPHA, .2f).target(1))
                .push(Tween.to(buttonExit, ActorAccessor.ALPHA, .2f).target(1))
                .end().start(tweenManager);

        Jukebox.stopMusic("background");
        Jukebox.playMusic("mainMenu");
    }

    public void showMenu(int page) {
        if (heading != null)
            table.clear();
        switch (page) {
            case 0:
                table.add(heading).height(210).colspan(3).expandX().row();
                table.add(buttonPlay).colspan(3).padBottom(15).expandX().row();
                table.add(buttonTutorial).colspan(3).padBottom(15).expandX().row();
                table.add(buttonExit).colspan(3).expandX().row();
                break;
            case 1:
                table.add(heading).height(210).colspan(2).expandX().row();
                table.add(subTable).colspan(2).expandX().padBottom(15).row();
                table.add(buttonInvite).right().padRight(Play.V_WIDTH / 30).padBottom(15);
                table.add(buttonJoin).left().padLeft(Play.V_WIDTH / 30).padBottom(15).row();
                table.add(buttonBack).colspan(2).expandX().row();
                break;
        }
    }

    public void showLoading() {
        table.clear();
    }

    public void startGame() {
        startGame = true;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tweenManager.update(delta);

        batch.begin();
        background.draw(batch);
        batch.end();

        stage.act(delta);
        stage.draw();

        if (startGame) {
            startGame = false;
            ((Game) Gdx.app.getApplicationListener()).setScreen(new CharacterSelector(multiplayerMessaging));
        }
    }

    @Override
    public void resize(int width, int height) {
        table.invalidateHierarchy();
        table.setSize(width, height);

        subTable.invalidateHierarchy();
        subTable.setSize(width, height);

        sideTable.invalidateHierarchy();
        sideTable.setSize(width, height);
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
        batch.dispose();
        buttonAtlas.dispose();
        skin.dispose();
    }
}
