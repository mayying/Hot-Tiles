package com.mayying.tileMapGame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.mayying.tileMapGame.entities.Jukebox;
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
                    s.showMenu(0);
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
        } else if (screen instanceof EndGame) {
            inGame = false;
            EndGame s = (EndGame) screen;
            s.leaveGame();
        } else if (screen instanceof CharacterSelector) {
            inGame = false;
            CharacterSelector s = (CharacterSelector) screen;
            s.leaveGame();
        }
    }

    public boolean isInGame() {
        return inGame;
    }

    public char getScreenTag(){
        if (screen instanceof Play) {
            return 'p';
        } else if (screen instanceof EndGame) {
            return 'e';
        } else if (screen instanceof CharacterSelector) {
            return 'c';
        }
        return 'd';
    }

    public void loadSound(){
        Jukebox.loadMusic("mainMenu");
        Jukebox.loadMusic("background");
        Jukebox.load("freeze");
        Jukebox.load("confused");
        Jukebox.load("shield");
        Jukebox.load("freezeMine");
        Jukebox.load("fire");
        Jukebox.load("lightning");
        Jukebox.load("swap");
        Jukebox.load("blackout");
        Jukebox.load("buttonPressed");
        Jukebox.load("reminder");
        Jukebox.load("suicide");
        Jukebox.load("bloodlust");
    }

    @Override
    public void create() {
        loadSound();
        if (mode.equals("desktop")) {
            setScreen(new Play());
        } else if (mode.equals("android")) {
            setScreen(new MainMenu(multiplayerMessaging));
        }
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
