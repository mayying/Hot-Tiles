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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.multiplayer.ConnectionHelper;
import com.mayying.tileMapGame.multiplayer.MultiplayerMessaging;
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
    private SpriteBatch batch;
    private Sprite background;
    private TextButton buttonMinus, buttonFriends, buttonPlus, buttonTutorial, buttonExit;
    private Label heading;
    private Stage stage;
    private TextureAtlas buttonAtlas;
    private Skin skin;
    private TweenManager tweenManager;
    private Table table;
    private OrthographicCamera camera;

    private MultiplayerMessaging multiplayerMessaging;

    private List<Actor> menuActors = new ArrayList<Actor>();
    private String mode;
    private boolean startGame;

    public MainMenu() {
        mode = "desktop";
    }

    public MainMenu(MultiplayerMessaging multiplayerMessaging) {
        mode = "android";
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

        table = new Table(skin);
        table.setFillParent(true);
        table.setBounds(0, 0, Play.V_WIDTH, Play.V_HEIGHT);
//        table.align(Align.top);

        heading = new Label("Hot Tiles", skin);

        multiplayerMessaging.setNoOfPlayers(2);

        buttonMinus = new TextButton("-", skin, "plusMinus");
        menuActors.add(buttonMinus);
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
        menuActors.add(buttonFriends);
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
        menuActors.add(buttonPlus);
        buttonPlus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (multiplayerMessaging.getNoOfPlayers() < 4) {
                    int noOfPlayers = multiplayerMessaging.getNoOfPlayers() + 1;
                    buttonFriends.setText(String.valueOf(noOfPlayers) + "P");
                    multiplayerMessaging.setNoOfPlayers(noOfPlayers);
                    Jukebox.play("buttonPressed");
                }
            }
        });

        buttonTutorial = new TextButton("Tutorial", skin);
        menuActors.add(buttonTutorial);
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

        Gdx.app.log("MainMenu.java", "switchScreen: " + startGame + " ConnectionHelper.STATE: " + ConnectionHelper.STATE);

        buttonExit = new TextButton("Exit", skin);
        menuActors.add(buttonExit);

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

        Gdx.app.log("Main Menu", "show called: " + buttonExit.getWidth() + " " + buttonExit.getHeight());

        //tableMenu
        table = new Table(skin);
        table.setFillParent(true);
        table.setBounds(0, 0, Play.V_WIDTH, Play.V_HEIGHT);
        table.align(Align.top);

        table.add(heading).height(210).colspan(3).expandX().row();
        table.add(buttonMinus).right().padBottom(20);
        table.add(buttonFriends).padBottom(20);
        table.add(buttonPlus).left().padBottom(20).row();
        table.add(buttonTutorial).colspan(3).padBottom(20).expandX().row();
        table.add(buttonExit).colspan(3).expandX().row();

        stage.addActor(table);

        showMenuSignIn();

        Timeline.createSequence().beginSequence()
                .push(Tween.set(background, SpriteAccessor.ALPHA).target(0))
                .push(Tween.set(heading, ActorAccessor.ALPHA).target(0))
                .push(Tween.set(buttonMinus, ActorAccessor.ALPHA).target(0))
                .push(Tween.set(buttonFriends, ActorAccessor.ALPHA).target(0))
                .push(Tween.set(buttonPlus, ActorAccessor.ALPHA).target(0))
                .push(Tween.set(buttonTutorial, ActorAccessor.ALPHA).target(0))
                .push(Tween.set(buttonExit, ActorAccessor.ALPHA).target(0))
                .push(Tween.from(background, SpriteAccessor.ALPHA, 1f).target(0))
                .push(Tween.to(background, SpriteAccessor.ALPHA, 1f).target(1))
                .push(Tween.to(heading, ActorAccessor.ALPHA, .5f).target(1))
                .push(Tween.to(buttonMinus, ActorAccessor.ALPHA, .2f).target(1))
                .push(Tween.to(buttonFriends, ActorAccessor.ALPHA, .2f).target(1))
                .push(Tween.to(buttonPlus, ActorAccessor.ALPHA, .2f).target(1))
                .push(Tween.to(buttonTutorial, ActorAccessor.ALPHA, .2f).target(1))
                .push(Tween.to(buttonExit, ActorAccessor.ALPHA, .2f).target(1))
                .end().start(tweenManager);

        Jukebox.stopMusic("background");
        Jukebox.playMusic("mainMenu");
    }

    public void clearMenu() {
        for (Actor actor : menuActors) {
            actor.setVisible(false);
        }
    }

    public void showMenuSignIn() {
        clearMenu();
        if (buttonTutorial != null && buttonFriends != null && buttonExit != null) {
            buttonTutorial.setVisible(true);
            buttonMinus.setVisible(true);
            buttonFriends.setVisible(true);
            buttonPlus.setVisible(true);
            buttonExit.setVisible(true);
        }
    }

    public void showLoading() {
        clearMenu();
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
            if (mode.equals("desktop"))
                ((Game) Gdx.app.getApplicationListener()).setScreen(new CharacterSelector());
            else if (mode.equals("android")) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new CharacterSelector(multiplayerMessaging));
            }
        }
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
        batch.dispose();
        buttonAtlas.dispose();
        skin.dispose();
    }
}
