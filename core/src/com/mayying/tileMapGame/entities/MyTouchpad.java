package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.mayying.tileMapGame.GameWorld;

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
//
//    public Stage getStage() {
//        return stage;
//    }

//    public void setStage() {
//        stage = new Stage();
//        stage.addActor(touchpad);
//        Gdx.input.setInputProcessor(stage);
//
//        //creating buttons
//        skin = new Skin();
//        buttonAtlas = new TextureAtlas(Gdx.files.internal("xbox-buttons/out/buttons.pack"));
//        skin.addRegions(buttonAtlas);
//        font = new BitmapFont();
//        textButtonStyleA = new TextButton.TextButtonStyle();
//        textButtonStyleB = new TextButton.TextButtonStyle();
//
//        textButtonStyleA.font = textButtonStyleB.font = font; //= textButtonStyleX.font = textButtonStyleY.font ;
//        textButtonStyleA.up = skin.getDrawable("xbox-controller-a-button-md");
//        textButtonStyleA.down = skin.getDrawable("xbox-controller-b-button-md");
//        textButtonStyleB.up = skin.getDrawable("xbox-controller-b-button-md");
//
//        buttonA = new TextButton("", textButtonStyleA);
//        buttonB = new TextButton("", textButtonStyleB);
//
//        buttonA.setBounds(Gdx.graphics.getWidth() - world.getPlayer().getWidth() * 2 - 20, world.getPlayer().getHeight() + 70, world.getPlayer().getWidth() * 2, world.getPlayer().getHeight() + 50);
//        buttonB.setBounds(Gdx.graphics.getWidth() - world.getPlayer().getWidth() * 2 - 20, 5, world.getPlayer().getWidth() * 2, world.getPlayer().getHeight() + 50);
//
//        stage.addActor(buttonA);
//        stage.addActor(buttonB);
////        stage.addActor(buttonX);
////        stage.addActor(buttonY);
//        buttonA.addListener(new InputListener() {
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                return true;
//            }
//
//            @Override
//            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
//                if (System.currentTimeMillis() - lastPressed > 200) {
//                    //  lastPressed = System.currentTimeMillis();
//                    world.getPlayer().spacePressed();
////                    createNewBullet();
//                }
//
//            }
//        });


}
