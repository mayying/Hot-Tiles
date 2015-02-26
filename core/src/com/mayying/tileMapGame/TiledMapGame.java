package com.mayying.tileMapGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mayying.tileMapGame.screens.Play;

public class TiledMapGame extends Game {
	
	@Override
	public void create () {
        setScreen(new Play());
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
	public void render () {
        super.render();
	}

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }
}
