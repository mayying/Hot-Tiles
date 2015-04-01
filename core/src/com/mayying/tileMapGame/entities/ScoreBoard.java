package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by User on 01/4/15.
 */
// Separate score logic from player logic
public class ScoreBoard {
    private static ScoreBoard instance;
    private ArrayList<Score> scores;

    private ScoreBoard(){
        instance = this;
        scores = new ArrayList<Score>();
    }

    public static ScoreBoard getInstance(){
        if(instance == null){
            return new ScoreBoard();
        }else{
            return instance;
        }
    }
    public ArrayList<Score> getScores(){
        return scores;
    }

    public void register(Player player){
        scores.add(new Score(player));
    }

    public void incrementKills(int idx){
        // make sure the idx follows how the player is registered
        getScores().get(idx).incrementKills();
        updateScores();
    }

    public void incrementDeath(int idx){
        getScores().get(idx).incrementDeath();
        updateScores();
    }

    // Updates positions/sorting in scoreboard
    private void updateScores(){
        Collections.sort(scores);
        Gdx.app.log("Scores",scores.toString());
    }

    private class Score implements  Comparable{
        Player player;
        int kills, death;
        private Score(Player player){
            this.player = player;
            kills = death = 0;
            // TODO - initialize the sprite to display on scoreboard here
        }

        private float getScore(){
            return kills / (death+1);
        }

        private void incrementKills(){
            kills++;
        }
        private void incrementDeath(){
            death++;
        }

        @Override
        public int compareTo(Object another) {
            return this.getScore() < ((Score) another).getScore()? -1:1;
        }

        @Override
        public String toString() {
            // TODO - replace with player index or string?
            return String.format("%s - %s / %s | Score: %s", player, kills, death, this.getScore());
        }
    }

}
