package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by HtooWaiYan on 2/26/2015.
 */
public class MyTouchpad {

    private Stage stage;
    private TouchpadStyle touchpadStyle;
    private Touchpad touchpad;
    private Drawable touchBackground;
    private Drawable touchKnob;
    private Skin touchpadSkin;

    public Touchpad getTouchpad() {
        return touchpad;
    }

    public MyTouchpad() {
        //Create a touchpad skin
        touchpadSkin = new Skin();

        //Set background image
        touchpadSkin.add("touchBackground", new Texture("img/touchBackground.png"));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture("img/touchKnob (Custom).png"));

        //Create TouchPad Style
        touchpadStyle = new com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle();

        //Create Drawable's from TouchPad skin
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");

        //Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, touchpadStyle);
        // touchpad.setBounds(20, -10, Gdx.graphics.getHeight() - world.getPlayer().getCollisionLayer().getTileHeight() * 5 - world.getPlayer().getHeight() / 2, Gdx.graphics.getHeight() - world.getPlayer().getCollisionLayer().getTileHeight() * 5 - world.getPlayer().getHeight() / 2);
        //touchpad.setBounds(-20 , -20, world.getPlayer().getCollisionLayer().getTileHeight() * 3, world.getPlayer().getCollisionLayer().getTileHeight() * 3);
//        touchpad.setBounds(-20, -20, Gdx.graphics.getHeight() / 3, Gdx.graphics.getHeight() / 3);
        touchpad.setBounds(30, 30, 140, 140);
        touchKnob.setMinHeight(100);
        touchKnob.setMinWidth(100);

    }


}
