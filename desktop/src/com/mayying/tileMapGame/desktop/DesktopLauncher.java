package com.mayying.tileMapGame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mayying.tileMapGame.TiledMapGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 648;
        config.width = 1080;
		new LwjglApplication(new TiledMapGame(), config);
	}
}
