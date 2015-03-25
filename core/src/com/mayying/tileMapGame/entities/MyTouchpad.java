package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

/**
 * Created by HtooWaiYan on 2/26/2015.
 */
public class MyTouchpad {
    private Touchpad touchpad;

    public Touchpad getTouchpad() {
        return touchpad;
    }

    public MyTouchpad() {
        //Create a touchpad skin
        TextureAtlas touchpadAtlas = new TextureAtlas(Gdx.files.internal("skin/skin.txt"));
        Skin touchpadSkin = new Skin(Gdx.files.internal("skin/gameSkin.json"), touchpadAtlas);

        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, touchpadSkin);

       // touchpad.setBounds(30, 30, 140, 140);

    }


}
