package com.mayying.tileMapGame.tween;

import com.badlogic.gdx.scenes.scene2d.Actor;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by May Ying on 1/3/2015.
 */
public class ActorAccessor implements TweenAccessor<Actor> {

    public static final int ALPHA = 0, RGB = 1, POSITION = 2;

    @Override
    public int getValues(Actor target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case ALPHA:
                returnValues[0] = target.getColor().a;
                return 1;
            case RGB:
                returnValues[0] = target.getColor().r;
                returnValues[1] = target.getColor().g;
                returnValues[2] = target.getColor().b;
                return 3;
            case POSITION:
                returnValues[0] = target.getX();
                return 1;
            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Actor target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case ALPHA:
                target.setColor(target.getColor().r, target.getColor().g, target.getColor().b, newValues[0]);
                break;
            case RGB:
                target.setColor(newValues[0], newValues[1], newValues[2], target.getColor().a);
                break;
            case POSITION:
                target.setX(newValues[0]);
                break;
            default:
                assert false;
        }
    }
}
