package com.mayying.tileMapGame;

import com.badlogic.gdx.Game;
import com.mayying.tileMapGame.screens.Play;
import com.mayying.tileMapGame.screens.Splash;

public class TiledMapGame extends Game {

    @Override
    public void create() {
        setScreen(new Play());
//        setScreen(new Splash());
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
