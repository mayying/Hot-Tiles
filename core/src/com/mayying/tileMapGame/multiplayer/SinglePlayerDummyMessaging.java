package com.mayying.tileMapGame.multiplayer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.mayying.tileMapGame.screens.CharacterSelector;
import com.mayying.tileMapGame.screens.MainMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luccan on 9/4/2015.
 */
public class SinglePlayerDummyMessaging implements MultiplayerMessaging {

    private List<String> participants = new ArrayList<>();
    private List<String> messageBuffer = new ArrayList<>();
    private MultiplayerMessaging realMessaging;


    public SinglePlayerDummyMessaging(){
        participants.add("me");
        this.realMessaging = null;
    }
    public  SinglePlayerDummyMessaging(MultiplayerMessaging realMessaging){
        this();
        this.realMessaging = realMessaging;
    }

    @Override
    public void broadcastMessage(String msg) {
        //do nothin
    }


    @Override
    public List<String> getParticipants() {
        return participants;
    }

    @Override
    public List<String> getJoinedParticipants() {
        return participants;
    }

    @Override
    public List<String> getJoinedParticipantsName() {
        return participants;
    }

    @Override
    public String getMyId() {
        return "me";
    }

    @Override
    public String getMyName() {
        return "me";
    }

    @Override
    public List<String> getMessageBuffer(char screenTag) {
        return messageBuffer;
    }

    @Override
    public void clearMessageBufferExcept(char screenTag) {
        //do nothing
    }

    @Override
    public String getHostId() {
        return "me";
    }

    @Override
    public void startQuickGame() {
        //do nothing
    }

    @Override
    public void signIn() {
        //do nothing
    }

    @Override
    public void exit() {
        Gdx.app.exit();
    }

    @Override
    public void leaveGame() {
        if (this.realMessaging!=null) {
            //used in practice
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(this.realMessaging));
        } else {
            //TODO used in desktop mode
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(this));
        }
    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public void rematch(){
        //replay
        ((Game) Gdx.app.getApplicationListener()).setScreen(new CharacterSelector(this));
    }

    @Override
    public void setNoOfPlayers(int noOfPlayers) {
        //do nothing
    }

    @Override
    public int getNoOfPlayers() {
        return 4;
    }
}
