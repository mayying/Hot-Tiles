package com.mayying.tileMapGame.tween;

import com.badlogic.gdx.graphics.g2d.Sprite;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by May Ying on 1/3/2015.
 */
public class SpriteAccessor implements TweenAccessor<Sprite> {

    public static final int ALPHA = 0;
    public static final int POSITION = 1;
    public static final int ROTATION = 2;

    @Override
    public int getValues(Sprite target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case ALPHA:
                returnValues[0] = target.getColor().a;
                return 1;
            case POSITION:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;
            case ROTATION:
                returnValues[0] = target.getRotation();
                return 1;
            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Sprite target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case ALPHA:
                target.setColor(target.getColor().r, target.getColor().g, target.getColor().b, newValues[0]);
                break;
            case POSITION:
                target.setX(newValues[0]);
                target.setY(newValues[1]);
                break;
            case ROTATION:
                target.setRotation(newValues[0]);
                break;
            default:
                assert false;
        }
    }
}
