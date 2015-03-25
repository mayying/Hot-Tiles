package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.mayying.tileMapGame.entities.Player;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by HtooWaiYan on 20-Mar-15.
 */
public class SpawnPowerUps {

    private TiledMapTileLayer playableLayer;
//    private Batch batch;
    private Player player;
    private Rectangle playerBound;
    private Rectangle powerupBound;
    private ArrayList<PowerUp> poweruplist;
    private LandMine landmine;
    private float time;
    private float randomtime;


    public SpawnPowerUps(TiledMapTileLayer playablelayer,Player player) {
        this.player=player;
        this.playableLayer = playablelayer;
        playerBound = new Rectangle();
        powerupBound = new Rectangle();
        poweruplist = new ArrayList<PowerUp>();
//        for (int w = 0; w < 3; w++) {

            int i = new Random().nextInt(playableLayer.getWidth() - 8);
            int j = new Random().nextInt(playableLayer.getHeight()-2);

        // PLEASE ADD THE IMAGE FILES TO GIT IF YOU WANNA USE A CUSTOM ONE
            landmine = new LandMine(i, j, new Texture("img/shuriken.png"), playableLayer);
            landmine.setRandomSpawnTime();
            poweruplist.add(landmine);


        ///
        TestPowerUp blackout=new TestPowerUp(new Random().nextInt(playableLayer.getWidth() - 8),new Random().nextInt(playableLayer.getHeight()-2),new Texture("img/shuriken.png"), playableLayer);
        blackout.setRandomSpawnTime();
        poweruplist.add(blackout);
        ////
//        }

        time=0;
    }

    public void draw(Batch batch){
        // random time spawn
//        time+=Gdx.graphics.getDeltaTime();
        //
        playerBound=player.getBoundingRectangle();
        if(poweruplist.size()==1){

            LandMine mine=new LandMine(new Random().nextInt(playableLayer.getWidth() - 8), new Random().nextInt(playableLayer.getHeight()-2), new Texture("img/shuriken.png"), playableLayer);
            mine.setRandomSpawnTime();
            poweruplist.add(mine);
        }
        for(PowerUp i:poweruplist) {

            i.setCounttime(Gdx.graphics.getDeltaTime());
//            powerupBound=i.getSprite().getBoundingRectangle();

            if (Intersector.overlaps(playerBound, i.getBound())) {
                if (player.canPickPowerUp()){

                    i.setPickup(true);
                player.addPowerUp(i);
            }

//                Gdx.app.log("bound",(String) String.valueOf(Intersector.overlaps(playerBound, i.getBound())));
            }


//                landmine.getSprite().draw(batch);
            if(!i.isPickup()) {
//                Gdx.app.log("powerlist", String.valueOf(player.getArraylist().size()));
//                Gdx.app.log("time", String.valueOf(time));
//                Gdx.app.log("random time", String.valueOf(i.getRandomSpawnTime()));
                if (i.getCounttime() < i.getRandomSpawnTime() + 0.1 && i.getCounttime() > i.getRandomSpawnTime() - 0.1) {
                    i.setDraw(true);

                }


                if(i.getDraw()) {
                    i.setTime(Gdx.graphics.getDeltaTime());
                    if (i.getTime() <= 3) {
//                        Gdx.app.log("time", String.valueOf(i.getTime()));
                        i.getSprite().draw(batch);
                    } else {
//                        i.setDraw(false);
//                        i.setRandomSpawnTime();
//                        i.zero();
//                        time = 0;
                        i.dispose();
                        poweruplist.remove(i);
                        break;

                    }
                }



            }

            else{
//                time=0;
                i.dispose();
                poweruplist.remove(i);
                break;

            }

        }

    }


}
