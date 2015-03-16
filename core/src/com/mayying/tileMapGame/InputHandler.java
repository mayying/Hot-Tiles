package com.mayying.tileMapGame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mayying.tileMapGame.entities.MyTouchpad;
import com.mayying.tileMapGame.entities.Player;

/**
 * Created by Luccan on 2/3/2015.
 */
public class InputHandler implements InputProcessor {

    private Player player;
    private MyTouchpad myTouchpad;
    private Stage stage;

    public InputHandler(Player player) {
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Input.Keys.RIGHT == keycode) {
            player.rightPressed();
        } else if (Input.Keys.LEFT == keycode) {
            player.leftPressed();
        } else if (Input.Keys.UP == keycode) {
            player.upPressed();
        } else if (Input.Keys.DOWN == keycode) {
            player.downPressed();
        } else if (Input.Keys.SPACE == keycode) {
            player.spacePressed();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (Input.Keys.LEFT == keycode || Input.Keys.RIGHT == keycode) {
            player.leftRightReleased();
        } else if (Input.Keys.UP == keycode || Input.Keys.DOWN == keycode) {
            player.upDownReleased();
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
