package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mayying.tileMapGame.entities.Jukebox;
import com.mayying.tileMapGame.entities.PlayerMetaData;
import com.mayying.tileMapGame.multiplayer.MultiplayerMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CharacterSelector Screen allowing players to choose their characters. Initially tried to do this by broadcasting
 * directly each player's selection. We quickly realized that there was a concurrency issue where the other player selects the
 * same character before the message was received, thereby allowing both players to use the same character.
 * We attempted to solve this by adding on the timestamp as well, using it as the comparator to decide who gets to
 * select it. However this design was not optimal as we would have to deselect and reselect the player's old selection
 * when the concurrency issue mentioned above occurred, giving the player a less than optimal experience.
 *
 * The current implementation chooses one of the players as the host. The other clients have to send requests and poll
 * for the characters that they want, and the host will decide whether they can select the character.
 */
// Must tap to show/sometimes still not received --> freeze time and selection until the data is obtained, detect with host
    // let host decide the indexes of the player
public class CharacterSelector implements Screen {

    private static final String TAG = "HT_CHARSEL";
    private Table charSelTable, subTable;
    private Sprite background;
    private SpriteBatch spriteBatch;
    private Stage stage;
    private TextureAtlas charAtlas;
    private Skin skin;
    private Label heading, timer;
    private TextButton[] textButton;
    private float timeLeft = 10;
    private int min, sec;
    private String mode;
//    private String myPlayerName, otherPlayerName = "", mode, myPlayerId, otherPlayerId;
    private HashMap<String, PlayerData> playerData = new HashMap<String, PlayerData>();
    private MultiplayerMessaging multiplayerMessaging;
    private boolean imTheHost;
    private HashMap<String, Boolean> otherReadyPlayers = new HashMap<String, Boolean>();

    public CharacterSelector() {
        mode = "desktop";
    }

    public CharacterSelector(MultiplayerMessaging multiplayerMessaging) {
        mode = "android";
        this.multiplayerMessaging = multiplayerMessaging;
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        background = new Sprite(new Texture(Gdx.files.internal("charSel/background.png")));
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ExtendViewport(Play.V_WIDTH, Play.V_HEIGHT));

        Gdx.input.setInputProcessor(stage);

        charAtlas = new TextureAtlas(Gdx.files.internal("charSel/players.txt"));
        skin = new Skin(Gdx.files.internal("charSel/charSel.json"), charAtlas);

        charSelTable = new Table(skin);
        charSelTable.setFillParent(true);
        charSelTable.setBounds(0, 0, Play.V_WIDTH, Play.V_HEIGHT);
        charSelTable.align(Align.center);

        heading = new Label("Please select your character", skin);

        subTable = new Table(skin);

        textButton = new TextButton[4];
        for (int i = 0; i < 4; i++) {
            textButton[i] = new TextButton("", skin, String.format("player%s", (i + 1)));
            textButton[i].getLabel().setWrap(true);
            final int finalI = i;
            textButton[i].addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Jukebox.play("buttonPressed");
                    toggleButton(finalI);
                }
            });
            subTable.add(textButton[i]);
        }

        subTable.align(Align.center);

        timer = new Label(String.valueOf(timeLeft), skin);

        charSelTable.add(heading).expandY().center().row();
        charSelTable.add(subTable).expandY().center().row();
        charSelTable.add(timer).expandY().center().row();

        stage.addActor(charSelTable);

        if (mode.equals("desktop")) {
            String myPlayerName = "Kim Jong Un Dafuq are you crazy";
            String myPlayerId = "me";
            playerData.put(myPlayerId, new PlayerData(myPlayerName, myPlayerId, -1));

            imTheHost = true;

        } else {
            imTheHost = multiplayerMessaging.getMyId().equals(multiplayerMessaging.getHostId());
            String myPlayerName = multiplayerMessaging.getMyName();
            String myPlayerId = multiplayerMessaging.getMyId();
            playerData.put(myPlayerId, new PlayerData(myPlayerName, myPlayerId, 0));

//            broadcastMyInfo();
            if(multiplayerMessaging.getJoinedParticipants().size() > 1) {
                while (playerData.size()<multiplayerMessaging.getJoinedParticipants().size()) {
                    Gdx.app.log(TAG, "Broadcasting info until others receive it...");
                    broadcastMyInfo();
                    synchronized (this) {
                        try {
                            wait(300l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    parseMessages();
                }
                // Wait for others to be ready, as of now this obviously doesn't work for more than 2 players due to duplicate messages
                while (otherReadyPlayers.keySet().size() < multiplayerMessaging.getJoinedParticipants().size()-1) {
                    synchronized (this) {
                        try {
                            wait(100l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    parseMessages();
                }
                // Clear the messages just in case.
                multiplayerMessaging.getMessageBuffer('c');

                Gdx.app.log(TAG, "Other players are ready!");
            }
        }
        Gdx.app.log(TAG, "I am the host: " + imTheHost);
        setDefaultCharacter();
        multiplayerMessaging.clearMessageBufferExcept('c');
    }

    private void broadcastMyInfo() {
        broadcastMessage("info", multiplayerMessaging.getMyId(), multiplayerMessaging.getMyName());
    }


    private void setDefaultCharacter() {
        Gdx.app.log(TAG, " Setting default character");
        if (imTheHost) {
            //dont question why. This is a bad hack.
            playerData.get(multiplayerMessaging.getMyId()).setSel(-1);
            toggleButton(0);
        } else {
            //try to select one
//            for (int i=0;i<textButton.length;i++) {
//                toggleButton(i);
//            }
        }
    }

    private void setPlayerSelection(int index, String playerId) {
        if (!playerData.containsKey(playerId)){ return;}
        textButton[index].setText(playerData.get(playerId).getName());
        textButton[index].setChecked(true);
        textButton[index].setDisabled(true);

        // Deselect the old button
        // To scale send buttonIndex, playerName (for button text), oldIndex
        int oldIndex = playerData.get(playerId).getSel();
        playerData.get(playerId).setSel(index);
        if (oldIndex==-1) { return; }

        for (String key : playerData.keySet()){
            if (playerData.get(key).getSel()==oldIndex){
                textButton[oldIndex].setText(playerData.get(key).getName());
                textButton[oldIndex].setChecked(true);
                textButton[oldIndex].setDisabled(true);
                return;
            }
        }
        textButton[oldIndex].setText("");
        textButton[oldIndex].setChecked(false);
        textButton[oldIndex].setDisabled(false);
    }


    // Do internal setting for toggling button
    private void toggleButton(int index) {
//        Gdx.app.log("toggleButton in CharacterSelector", index + " Disabled? " + textButton[index].isDisabled());
        if (imTheHost) {
            for (String key : playerData.keySet()){
                if (playerData.get(key).getSel() == index){
                    return; //this character is taken
                }
            }
            broadcastMessage("charsel", "host", String.valueOf(index));
            setPlayerSelection(index, multiplayerMessaging.getMyId());
        } else {
            broadcastMessage("charsel", "request", String.valueOf(index));
            // wait for server to reply
        }
    }

    boolean leavingGame = false;

    public void leaveGame() {
        leavingGame = true;
    }

    @Override
    public void render(float delta) {
        if (leavingGame) {
            leavingGame = false;
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(multiplayerMessaging));
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        background.draw(spriteBatch);
        spriteBatch.end();

        stage.act(delta);
        stage.draw();

        if (multiplayerMessaging.getJoinedParticipants().size()==playerData.size()) {
            timeLeft -= delta;
        }
        min = (int) Math.floor(timeLeft / 60.0f);
        sec = (int) (timeLeft - min * 60.0f);
        timer.setText(String.format("%01d", sec));

        parseMessages();
        // Switch screen to Play when time's up
        if (sec == 0) {
            //Clear the message buffer.
            multiplayerMessaging.getMessageBuffer('c');
            ArrayList<PlayerMetaData> metaData = new ArrayList<>();
            for (String key : playerData.keySet()) {
                metaData.add(new PlayerMetaData().setID(playerData.get(key).getId()).setModel(String.valueOf(playerData.get(key).getSel() + 1)).setName(playerData.get(key).getName()));
            }
//            if (mode.equals("android")) {
//                if (otherPlayerId != null)
//                    metaData.add(new PlayerMetaData().setID(otherPlayerId).setModel(String.valueOf(otherPlayerSel + 1)).setName(otherPlayerName));
//            }
            Gdx.app.log(TAG, "Metadata: " + metaData);
            ((Game) Gdx.app.getApplicationListener()).setScreen(new Play(multiplayerMessaging, metaData));
        }
    }

    private void parseMessages(){
        List<String> msgs = multiplayerMessaging.getMessageBuffer('c');
        for (String msg : msgs) {
            parse(msg);
        }
    }

    private void parse(String msg) {
        String[] message = msg.split(",");
        String command = message[1];
        if (command.equals("charsel")) {
            String type = message[2];
            int idx = Integer.valueOf(message[3]);
            switch (type) {
                //TODO client will fail if client A have not received client B info, but host approves client B's request
                case "host":
                    // Host = Kim Jong Un
                    Gdx.app.log(TAG,"Host selects "+idx);
                    setPlayerSelection(idx, message[0]);
                    break;
                case "request":
                    //else, ignore request.
                    if (imTheHost) {
                        // Check for index collision, reply if no collision, else ignore the user because I'm Kim
                        Gdx.app.log(TAG, "Client requested " + idx);
                        if (playerData.containsKey(message[0])) { //if info not received, no go.
                            for (String key : playerData.keySet()) {
                                if (playerData.get(key).getSel() == idx) {
                                    Gdx.app.log(TAG, "Clash with player " + key);
                                    return; //this character is taken
                                }
                            }
                            // Give client the ok signal
                            broadcastMessage("charsel", "reply", String.valueOf(idx), message[0]);
                            // Set client's selection
                            setPlayerSelection(idx, message[0]);
                        }
                    }
                    break;
                case "reply":
                    // lowly client
                    Gdx.app.log(TAG,"The Great Leader Approves of "+message[4]+"'s selection: "+idx);
                    setPlayerSelection(idx, message[4]);
                    break;


            }
        } else if (command.equals("info")) {
            // charsel, info, playerID, playerName
            String otherPlayerId = message[2];
            String otherPlayerName = message[3];
            playerData.put(otherPlayerId, new PlayerData(otherPlayerName, otherPlayerId, -1));
            if (imTheHost) {
                for (int i = 0; i < textButton.length; i++) {
                    boolean taken = false;
                    for (String key : playerData.keySet()) {
                        if (playerData.get(key).getSel() == i) {
                            taken = true;
                            break;
                        }
                    }
                    if (!taken){
                        Gdx.app.log(TAG,"The Great Leader Allocates character "+i+" for player "+otherPlayerName);
                        broadcastMessage("charsel", "reply", String.valueOf(i), otherPlayerId);
                        setPlayerSelection(i, otherPlayerId);
                        break;
                    }
                }
            }

            Gdx.app.log(TAG, "Player info from "+message[3]+" received.");
            broadcastMessage("rdy");
        }else if(command.equals("rdy")){
            otherReadyPlayers.put(message[0], true);
        }
        else {
            Gdx.app.log("HT_CHARSEL", "Unknown message format: " + msg);
        }
    }

    @Override
    public void resize(int width, int height) {
        subTable.invalidateHierarchy();
        subTable.setSize(width, height);

        charSelTable.invalidateHierarchy();
        charSelTable.setSize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        spriteBatch.dispose();
        charAtlas.dispose();
        skin.dispose();
    }

    private void broadcastMessage(String... args) {
        String msg = "";
        for (String arg : args) {
            msg += arg + ",";
        }
        if (msg.length()>1){
            msg = msg.substring(0, msg.length()-1);//remove comma
        }
        Gdx.app.log(TAG, "Broadcasting message: " + msg);
        multiplayerMessaging.broadcastMessage(msg);
    }

    class PlayerData {
        private String playerName;
        private String playerId;
        private int sel;
        public String getName(){ return playerName;}
        public String getId() { return playerId;}
        public synchronized int getSel() { return sel;}
        public synchronized void setSel(int sel) { this.sel = sel;}
        public PlayerData(String name, String id, int sel){
            this.playerName = name;
            this.playerId = id;
            this.sel = sel;
        }
    }

}