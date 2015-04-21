package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

/**
 * Created by HtooWaiYan on 2/26/2015.
 */
public class MyTouchPad {
    private Touchpad touchpad;
    private final Skin touchPadSkin;

    public Touchpad getTouchPad() {
        return touchpad;
    }

    public MyTouchPad() {
        touchPadSkin = new Skin(Gdx.files.internal("skin/gameSkin.json"), new TextureAtlas(Gdx.files.internal("skin/skin.txt")));
        touchpad = new Touchpad(10, touchPadSkin);
    }


}