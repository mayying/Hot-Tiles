package com.mayying.tileMapGame;

import com.badlogic.gdx.Game;
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

    @Override
    public void create() {
        if (mode.equals("desktop")) {
            setScreen(new MainMenu());
        } else if (mode.equals("android")) {
            setScreen(new Play(multiplayerMessaging));
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
