package com.mayying.tileMapGame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mayying.tileMapGame.TiledMapGame;
import com.mayying.tileMapGame.multiplayer.SinglePlayerDummyMessaging;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 720;
        config.width = 1280;
       // config.height = 1080;
       // config.width = 1920;
        config.vSyncEnabled = true;
		new LwjglApplication(new TiledMapGame(new SinglePlayerDummyMessaging()), config);
	}
}
