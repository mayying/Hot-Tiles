package com.mayying.tileMapGame.multiplayer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Player;

/**
 * Created by User on 28/3/15.
 */
public class MessageParser {
    private static final String TAG = "Parser";

    /**
     * Parses the input string and runs the respective methods.
     * @param inp - the String obtained from the server, in the format of [command],[args*]. Commands
     *            include:
     *            1) "position", playerIndex, x, y - Position, player affected, x and y coordinates (null to hide player when dead/cloak).
     *            2) "powerup-pickup" ? (KIV, boss say just let power ups appear differently on player screens)
     *            3) "effect", ID, args* - effect inflicted on player, ID of effect, args* depending on the effect (blackout, freeze etc)
     *            4) "game-state", ID - Game state to decide which screen to show/ progression of game
     */
    public void parse(String inp){
        String[] message = inp.split(",");
        String command = message[0];
        switch (command){
            case "position":
                Vector2 pos = new Vector2(Integer.valueOf(message[2]), Integer.valueOf(message[3]));
                Gdx.app.log(TAG,String.format("Position of player %s: %s, %s", message[1], message[2], message[3]));
                GameWorld.setPlayerPosition(Integer.valueOf(message[1]), pos);
                break;

            case "effect":
                String id = message[1];
                switch (id){
                    case "freeze":
                        // Format: "effect","freeze",playerIdx
                        // Effect to update the client on a player getting frozen. Updates animation accordingly.
                        // Mostly just for the animation, since the player coordinates sent should be frozen as well
                        int playerIdx = Integer.valueOf(message[3]);
                        GameWorld.getPlayer(playerIdx).freeze();
                        break;
                    case "blackout":
                        // Format: "effect","blackout", [user] (for last hit purpose)
                        // Assumes that the message is only sent to those affected. If server does not support that
                        // change this to take in playerIdx and check if this device's player has the same idx
                        GameWorld.setBlackout();
                        break;
                    case "invert":
                        // Format: "effect","invert", [user] (for last hit purpose)
                        // Invert player's controls. Check for device's player's index if necessary
                        GameWorld.getPlayer().invert();
                        break;

                    case "dieAndSpawn":
                        // Format: "effect","dieAndSpawn",playerIdx,x,y
                        GameWorld.getPlayer(playerIdx).dieAndSpawnAt(Integer.valueOf(message[3]), Integer.valueOf(message[4]));
                        break;
                }
                break;
            default:
                Gdx.app.log(TAG, "No such command: "+message[0]);
        }
    }
}
