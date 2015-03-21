package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.input.GestureDetector;

/**
 * Created by HtooWaiYan on 14-Mar-15.
 */
public class DirectionGestureDetector extends GestureDetector {
    public static Player player;
    public static class DirectionListener {
        void onLeft() {
//            player.stop();
            player.leftPressed();
        }

        void onRight(){
//            player.stop();
            player.rightPressed();
        }


        void onUp(){
//            player.stop();
            player.upPressed();
        };

        void onDown(){
//            player.stop();
            player.downPressed();
        };

//        void stop(){
//            player.stop();
//        }
    }
//    public interface DirectionListener {
//        void onLeft();
//
//        void onRight();
//
//        void onUp();
//
//        void onDown();
//    }

    public DirectionGestureDetector(DirectionListener directionListener,Player player) {
        super(new DirectionGestureListener(directionListener));
        this.player=player;
    }

    private static class DirectionGestureListener extends GestureAdapter{
        DirectionListener directionListener;

        public DirectionGestureListener(DirectionListener directionListener){
            this.directionListener = directionListener;
        }




        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            if(Math.abs(velocityX)>Math.abs(velocityY)){
                if(velocityX>0){
                    directionListener.onRight();
                }else{
                    directionListener.onLeft();
                }
            }else{
                if(velocityY>0){
                    directionListener.onDown();
                }else{
                    directionListener.onUp();
                }
            }
            return super.fling(velocityX, velocityY, button);
        }

    }

}
