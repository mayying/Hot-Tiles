package com.mayying.tileMapGame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.mayying.tileMapGame.multiplayer.MultiplayerMessaging;
import com.mayying.tileMapGame.screens.MainMenu;
import com.mayying.tileMapGame.screens.Play;

public class TiledMapGame extends Game {

    String mode;
    MultiplayerMessaging multiplayerMessaging;

    public TiledMapGame(){
        super();
        //desktop launch
        mode = "desktop";
        this.multiplayerMessaging = null;
    }

    public TiledMapGame(MultiplayerMessaging multiplayerMessaging){
        super();
        mode = "android";
        this.multiplayerMessaging = multiplayerMessaging;
    }

    public static final int SCREEN_MAIN = 0;
    public static final int SCREEN_SIGNIN = 1;
    public static final int SCREEN_LOADING = 2;
    public void setMainMenuScreen(int screenId){
        if (screen instanceof MainMenu) {
            MainMenu s = (MainMenu) screen;
            switch (screenId) {
                case SCREEN_MAIN:
                    s.showMenu();
                    break;
                case SCREEN_SIGNIN:
                    s.showMenuSignIn();
                    break;
                case SCREEN_LOADING:
                    s.showLoading();
                    break;
                default:
                    Gdx.app.log("Error", "Invalid Main Menu Screen");
            }
        } else {
            Gdx.app.log("Error", "Screen is not pointing at MainMenu");
        }
    }
    public void startGame(){
        if (!(screen instanceof Play)) {
            this.setScreen(new Play(multiplayerMessaging));
        }
    }

    @Override
    public void create() {
        if (mode.equals("desktop")) {
            setScreen(new Play());
//            setScreen(new MainMenu());
        } else if (mode.equals("android")) {
            setScreen(new MainMenu(multiplayerMessaging));
        }
        // setScreen(new Splash());
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }
}
