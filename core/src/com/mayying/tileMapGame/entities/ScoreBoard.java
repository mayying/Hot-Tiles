package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by User on 01/4/15.asd
 */
// Separate score logic from player logic
public class ScoreBoard {
    private static ScoreBoard instance;
    private ArrayList<Score> scores;
    private HashMap<String, Integer> playerMap; // maps player's id to his index here so I dont have to change everything

    private ScoreBoard() {
        instance = this;
        scores = new ArrayList<Score>();
        playerMap = new HashMap<>();
        playerMap = new HashMap<>();
    }

    public static ScoreBoard getInstance() {
        if (instance == null) {
            Gdx.app.log("ScoreBoard", "Initialized new ScoreBoard");
            return new ScoreBoard();
        } else {
            return instance;
        }
    }

    public ArrayList<Score> getScores() {
        return scores;
    }

    public void register(Player player) {
        playerMap.put(player.getID(), scores.size());
        scores.add(new Score(player));
    }

    public void incrementKillsAndOrDeath(String killerID, String victimID) {
        // TODO - might have to fix this, send something more unique than a "null" string
        if (!killerID.equals("null")) {
            getScores().get(playerMap.get(killerID)).incrementKills();
        }

        getScores().get(playerMap.get(victimID)).incrementDeath();
        updateScores();
    }
//    public void incrementKills(int idx) {
//        // make sure the idx follows how the player is registered
//        getScores().get(idx).incrementKills();
//        updateScores();
//    }
//
//    public void incrementDeath(int idx) {
//        getScores().get(idx).incrementDeath();
//        updateScores();
//    }

    // Updates positions/sorting in scoreboard
    private void updateScores() {
        Collections.sort(scores);
        Gdx.app.log("Scores",scores.toString());
    }

    public void reset() {
        instance = null;
    }

    public class Score implements Comparable {
        Player player;
        int kills, death;

        private Score(Player player) {
            this.player = player;
            kills = death = 0;
            // TODO - initialize the sprite to display on scoreboard here
        }

        public float getScore() {
            return kills - death;
        }

        private void incrementKills() {
            kills++;
        }

        private void incrementDeath() {
            death++;
        }

        public Player getPlayer() {
            return player;
        }

        @Override
        public int compareTo(Object another) {
            return this.getScore() < ((Score) another).getScore() ? -1 : 1;
        }

        @Override
        public String toString() {
            return String.format("\nPlayer %s - %s / %s | Score: %s" + "\n", player.getID(), kills, death, this.getScore());
        }
    }

}
