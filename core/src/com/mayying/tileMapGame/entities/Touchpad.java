package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.mayying.tileMapGame.GameWorld;

/**
 * Created by HtooWaiYan on 2/26/2015.
 */
public class Touchpad {

    private Stage stage;
    private com.badlogic.gdx.scenes.scene2d.ui.Touchpad touchpad;
    public com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle touchpadStyle;
    private Drawable touchBackground;
    private Drawable touchKnob;
    public Skin touchpadSkin;
    private Texture test;


    public Touchpad(GameWorld world) {
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
        touchpad = new com.badlogic.gdx.scenes.scene2d.ui.Touchpad(10, touchpadStyle);
        // touchpad.setBounds(20, -10, Gdx.graphics.getHeight() - world.getPlayer().getCollisionLayer().getTileHeight() * 5 - world.getPlayer().getHeight() / 2, Gdx.graphics.getHeight() - world.getPlayer().getCollisionLayer().getTileHeight() * 5 - world.getPlayer().getHeight() / 2);
        //touchpad.setBounds(-20 , -20, world.getPlayer().getCollisionLayer().getTileHeight() * 3, world.getPlayer().getCollisionLayer().getTileHeight() * 3);
        touchpad.setBounds(-20, -20, Gdx.graphics.getHeight()/3, Gdx.graphics.getHeight()/3);
    }

    public com.badlogic.gdx.scenes.scene2d.ui.Touchpad getTouchpad() {
        return touchpad;
    }

}
