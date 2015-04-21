package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.mayying.tileMapGame.screens.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by User on 01/4/15.asd
 */
// Separate score logic from player logic
public class ScoreBoard {
    private static final String TAG = "HT_ScoreBoard";
    private static ScoreBoard instance;
    private HashMap<String, Score> playerMap;
    private ArrayList<Score> scores;

    private ScoreBoard() {
        instance = this;
        scores = new ArrayList<>();
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
        Score s = new Score(player);
        playerMap.put(player.getID(), s);
        scores.add(s);
    }

    public void incrementKillsAndOrDeath(String killerID, String victimID) {
        Gdx.app.log("score", getScores().toString());
        if (!killerID.equals("null"))
            playerMap.get(killerID).incrementKills();
        playerMap.get(victimID).incrementDeath();
        updateScores();
    }


    // Updates positions/sorting in scoreboard
    private void updateScores() {
        Collections.sort(scores);
//        Gdx.app.log(TAG, scores.toString());
        SideBar.onScoreUpdated();
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
        }

        public int getScore() {
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

        public int getKills() {
            return kills;
        }

        public int getDeath() {
            return death;
        }

        public int compareTo(Object another) {
            return this.getScore() < ((Score) another).getScore() ? -1 : 1;
        }

        @Override
        public String toString() {
            return String.format("\nPlayer %s - %s / %s | Score: %s", player.getModel(), kills, death, this.getScore());
        }
    }

}
