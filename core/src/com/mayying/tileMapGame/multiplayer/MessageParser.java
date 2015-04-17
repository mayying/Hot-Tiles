package com.mayying.tileMapGame.multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.entities.Player;
import com.mayying.tileMapGame.entities.ScoreBoard;
import com.mayying.tileMapGame.entities.powerups.DelayedThread;
import com.mayying.tileMapGame.screens.Play;

import java.util.Arrays;
//import com.mayying.tileMapGame.entities.ScoreBoard;

/**
 * Created by User on 28/3/15.
 */
public class MessageParser {
    public static final String COMMAND_POSITION = "position";

    private static final String TAG = "HT_Parser";
    public static final String POWERUP_PICKED_UP = "a";
    public static final String LIGHTNING = "b";

    /**
     * Parses the input string and runs the respective methods. A better design would be to start this
     * in a separate thread / use wait() + notify() or CyclicBarrier to notify it when a message is added to the message buffer.
     * Would also be nice to implement message parser callbacks in other classes that require it. IT'S TOO LATE NOW BUT CAN STILL WRITE IN REPORT
     * <p/>
     * Param should be in the following format:
     * [Sender id], [Command], [Command Parameter1], [Command Parameter2], [...]
     *
     * @param inp - the String obtained from the server, in the format of [command],[args*]. Commands
     *            include:
     *            1) "position", playerIndex, x, y - Position, player affected, x and y coordinates (null to hide player when dead/cloak).
     *            2) "powerup-pickup" ? (KIV, boss say just let power ups appear differently on player screens)
     *            3) "effect", ID, args* - effect inflicted on player, ID of effect, args* depending on the effect (blackout, freeze etc)
     *            4) "game-state", ID - Game state to decide which screen to show/ progression of game
     */
    public static void parse(String inp) {
        String[] message = inp.split(",");
        final String senderId = message[0];
        String command = message[1];
        GameWorld world = GameWorld.getInstance();
        if (world != null) {
            final Player player = world.getDevicePlayer();
            switch (command) {
                case COMMAND_POSITION:
//                Gdx.app.log(TAG, String.format("Position of player %s: %s, %s", senderId, message[2], message[3]));
                    world.setPlayerPosition(senderId, new Vector2(Integer.valueOf(message[2]), Integer.valueOf(message[3])), Integer.valueOf(message[4]));
                    if (world.getPlayer(senderId).isFrozen || world.getPlayer(senderId).isDead || world.getPlayer(senderId).isSwapped) {
                        world.getPlayer(senderId).animate(Gdx.graphics.getDeltaTime() * 20);
                    }
                    world.getPlayer(senderId).animate(Gdx.graphics.getDeltaTime() * 45); //testing out animation. not sure what delta time should be
                    break;

                case "effect":
                    String id = message[2];
                    switch (id) {
                        case "freeze":
                            // Format: "effect","freeze",playerIdx, user
                            // Effect to update the client on a player getting frozen. Updates animation accordingly.
                            // Mostly just for the animation, since the player coordinates sent should be frozen as well
                            world.getPlayer(message[3]).freeze(senderId); //sender is the person who put the mine
                            break;
                        case "fireMine":
                            // Get player and burn, logic similar to freeze mine
                            world.getPlayer(message[3]).burn(senderId);
                            break;
                        case "blackout":
                            // Format: "effect","blackout", [user] (for last hit purpose)
                            // Assumes that the message is only sent to those affected. If server does not support that
                            // change this to take in playerIdx and check if this device's player has the same idx
                            world.setBlackout();
                            player.setLastHitBy(senderId);
                            break;
                        case "invert":
                            // Format: "effect","invert", [user] (for last hit purpose)
                            // Invert player's controls. Check for device's player's index if necessary
                            player.invert();
                            player.setLastHitBy(senderId);
                            break;
                        case "dieAndSpawn":
                            // Format: "effect","dieAndSpawn",x , y
                            // alerts device that someone has died and will spawn at x,y. Score is updated separately. ( updated by die() )
                            world.getPlayer(senderId).dieAndSpawnAt(Integer.valueOf(message[3]), Integer.valueOf(message[4]));
                            break;
                        case "shield":
                            // Format: "effect","shield", [user] (for animation)
                            world.getPlayer(senderId).shield();
                            break;
                        case "swap":
                            // Format: "effect", "swap", x, y , mode, target
                            final Player sender =  world.getPlayer(senderId);
                            final Player target = world.getPlayer(message[6]);
                            final int x = Integer.valueOf(message[3]);
                            final int y = Integer.valueOf(message[4]);
                            if (message[5].equals("1")) {
                                Jukebox.play("swap");
                                // Animation on the 2 players
                                sender.toggleSwap(true);
                                target.toggleSwap(true);
                                sender.animate(Gdx.graphics.getDeltaTime() * 20);
                                target.animate(Gdx.graphics.getDeltaTime() * 20);

                                // If you are the target, broadcast back your location
                                if (player.getID().equals(message[6])) {
                                    Vector2 playerPos = player.getPlayerPosition();
                                    int xCoord = (int) playerPos.x;
                                    int yCoord = (int) playerPos.y;
                                    Play.broadcastMessage("effect", "swap", String.valueOf(xCoord), String.valueOf(yCoord), "0", senderId);
                                }
                                // only setting last hit for the victim.
                                target.setLastHitBy(senderId);
                            }

                            new DelayedThread(200l) {
                                @Override
                                public void run() {
                                    super.run();
                                    if (!target.isInvulnerable) {
                                        target.setPlayerPosition(x, y);
                                        target.toggleSwap(false);
                                        sender.toggleSwap(false);
                                    }
                                }
                            }.start();

                            break;
                    }
                    break;
                case "score":
                    // Format: "score", killerIdx, victimIdx
                    // increments k and d accordingly if applicable, killerIdx = -1 if no update to kills
                    ScoreBoard.getInstance().incrementKillsAndOrDeath(message[2], message[3]);
                    break;
                case "ready":
                    world.playerReady(senderId, Long.valueOf(message[2]));
                    break;
                case POWERUP_PICKED_UP:
                    GameWorld.getInstance().getSpawnPowerUps().reset();
                    break;
                case LIGHTNING:
//                Gdx.app.log("HT_LIGHTNING","LIGHTNING STRIKE");
                    // Format: LIGHTNING, x, y
                    world.lightningAt(Float.valueOf(message[2]), Float.valueOf(message[3]), senderId);
                    break;
                default:
                    Gdx.app.log(TAG, "No such command: " + message[0]);
            }
        } else {
            Gdx.app.log(TAG, "World has been destroyed. Not handling message: " + Arrays.toString(message));
        }
    }


}
