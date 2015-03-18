package com.mayying.tileMapGame.entities.powerups;

/**
 * Created by User on 18/3/15.
 */

import com.badlogic.gdx.Gdx;

/**
 * A delayed thread to wait before performing some other task. For tasks that require constant checking
 * of time.
 */
public class DelayedThread extends Thread {
    long delay;
//    long start;
    public DelayedThread(long millis){
        this.delay = millis;
//        start = System.currentTimeMillis();
    }
    @Override
    public void run() {

//        while(!isInterrupted() && System.currentTimeMillis() - start < delay) {
//            try {
//                sleep(100l);
//            } catch (InterruptedException e) {
//                Gdx.app.log("Delay Thread", "Interrupted");
//                return;
//            }
//        }
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
