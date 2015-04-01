package com.mayying.tileMapGame.entities.powerups;

/**
 * Created by User on 18/3/15.
 */

import com.badlogic.gdx.Gdx;
import com.mayying.tileMapGame.entities.Player;

/**
 * A delayed thread to wait before performing some other task. For tasks that require constant checking
 * of time.
 */
public class DelayedThread extends Thread {
    private long delay;
    private Player player;
//    long start;
    public DelayedThread(long millis){
        this.delay = millis;
//        start = System.currentTimeMillis();
    }

    public DelayedThread(long millis, Player player){
        this.delay = millis;
        this.player = player;
//        start = System.currentTimeMillis();
    }

    public Player getPlayer(){ return player; }

    @Override
    public void run() {
        synchronized (this) {
            try {
                wait(delay);
            } catch (InterruptedException e) {
//                e.printStackTrace();
                Gdx.app.log("Delay Thread", "Interrupted");
                return;
            }
        }
        // Override this method. Call super and then perform whatever needs to be done after the delay
        // e.g. set boolean flag
    }
}
