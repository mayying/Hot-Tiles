package com.mayying.tileMapGame.multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.ScoreBoard;

/**
 * Created by User on 28/3/15.
 */
public class MessageParser {
    private static final String TAG = "Parser";
    private GameWorld world;
    public MessageParser(GameWorld world){
        this.world = world;
    }
    /**
     * Parses the input string and runs the respective methods.
     *
     * @param inp - the String obtained from the server, in the format of [command],[args*]. Commands
     *            include:
     *            1) "position", playerIndex, x, y - Position, player affected, x and y coordinates (null to hide player when dead/cloak).
     *            2) "powerup-pickup" ? (KIV, boss say just let power ups appear differently on player screens)
     *            3) "effect", ID, args* - effect inflicted on player, ID of effect, args* depending on the effect (blackout, freeze etc)
     *            4) "game-state", ID - Game state to decide which screen to show/ progression of game
     */
    public void parse(String inp) {
        String[] message = inp.split(",");
        String command = message[0];
        switch (command) {
            case "position":
                Vector2 pos = new Vector2(Integer.valueOf(message[2]), Integer.valueOf(message[3]));
                Gdx.app.log(TAG, String.format("Position of player %s: %s, %s", message[1], message[2], message[3]));
                GameWorld.setPlayerPosition(Integer.valueOf(message[1]), pos);
                break;

            case "effect":
                String id = message[1];
                switch (id) {
                    case "freeze":
                        // Format: "effect","freeze",playerIdx, user
                        // Effect to update the client on a player getting frozen. Updates animation accordingly.
                        // Mostly just for the animation, since the player coordinates sent should be frozen as well
                        GameWorld.getPlayer(Integer.valueOf(message[3])).freeze(Integer.valueOf(message[4]));
                        break;
                    case "blackout":
                        // Format: "effect","blackout", [user] (for last hit purpose)
                        // Assumes that the message is only sent to those affected. If server does not support that
                        // change this to take in playerIdx and check if this device's player has the same idx
                        GameWorld.setBlackout();
                        world.getDevicePlayer().setLastHitBy(GameWorld.getPlayer(Integer.valueOf(message[3])));
                        break;
                    case "invert":
                        // Format: "effect","invert", [user] (for last hit purpose)
                        // Invert player's controls. Check for device's player's index if necessary
                        world.getDevicePlayer().invert();
                        world.getDevicePlayer().setLastHitBy(GameWorld.getPlayer(Integer.valueOf(message[3])));
                        break;

                    case "dieAndSpawn":
                        // Format: "effect","dieAndSpawn",playerIdx,x,y
                        // alerts device that someone has died and will spawn at x,y
                        GameWorld.getPlayer(Integer.valueOf(message[3])).dieAndSpawnAt(Integer.valueOf(message[3]), Integer.valueOf(message[4]));
                        break;
                    case "fireMine":
                        // Get player and burn, logic similar to freeze mine
                        break;
                    case "shield":
                        //for animation
                        break;
                }
                break;
            case "score":
                //format of "score",playerIdx,["k"/"d"]
                // increments k or d accordingly
                if(message[2].equals("k")) {
                    ScoreBoard.getInstance().incrementKills(Integer.valueOf(message[1]));
                }else if(message[2].equals("d")){
                    ScoreBoard.getInstance().incrementDeath(Integer.valueOf(message[1]));
                }

            default:
                Gdx.app.log(TAG, "No such command: " + message[0]);
        }
    }
}
