package com.mayying.tileMapGame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.mayying.tileMapGame.multiplayer.MultiplayerMessaging;
import com.mayying.tileMapGame.screens.CharacterSelector;
import com.mayying.tileMapGame.screens.EndGame;
import com.mayying.tileMapGame.screens.MainMenu;
import com.mayying.tileMapGame.screens.Play;

public class TiledMapGame extends Game {

    String mode;
    MultiplayerMessaging multiplayerMessaging;
    boolean inGame = false;

    public TiledMapGame() {
        super();
        //desktop launch
        mode = "desktop";
        this.multiplayerMessaging = null;
    }

    public TiledMapGame(MultiplayerMessaging multiplayerMessaging) {
        super();
        mode = "android";
        this.multiplayerMessaging = multiplayerMessaging;
    }

    public static final int SCREEN_MAIN = 0;
    public static final int SCREEN_SIGNIN = 1;
    public static final int SCREEN_LOADING = 2;

    public void setMainMenuScreen(int screenId) {
        if (screen instanceof MainMenu) {
            MainMenu s = (MainMenu) screen;
            switch (screenId) {
                case SCREEN_MAIN:
//                    s.showMenu();
//                    break;
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

    public void startGame() {
        if (screen instanceof MainMenu) {
            inGame = true;
            MainMenu s = (MainMenu) screen;
            s.startGame();
        } else if (screen instanceof EndGame) {
            inGame = true;
            EndGame s = (EndGame) screen;
            s.startGame();
        }
//        if (!(screen instanceof Play)) {
////            setScreen(new Play(multiplayerMessaging));
//            ((Game) Gdx.app.getApplicationListener()).setScreen(new Play(multiplayerMessaging));
//        }
    }

    public void leaveGame() {
        if (screen instanceof Play) {
            inGame = false;
            Play s = (Play) screen;
            s.leaveGame();
        } else if (screen instanceof EndGame){
            inGame = false;
            EndGame s = (EndGame) screen;
            s.leaveGame();
        }
    }

    public boolean isInGame() {
        return inGame;
    }

    @Override
    public void create() {
        if (mode.equals("desktop")) {
            setScreen(new CharacterSelector());
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
