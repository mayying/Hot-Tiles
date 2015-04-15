package com.mayying.tileMapGame.multiplayer;

import java.util.List;

/**
 * Created by Luccan on 5/4/2015.
 */
public interface MultiplayerMessaging {
    public void broadcastMessage(String msg);
    public void broadcastMessage(String msg, String screenTag);
    public List<String> getParticipants();
    public List<String> getJoinedParticipants();
    public List<String> getJoinedParticipantsName();
    public String getMyId();
    public String getMyName();
    public List<String> getMessageBuffer();
    public List<String> getMessageBuffer(String filter);
    public String getHostId();

    //for main menu usages
    public void startQuickGame();
    public void signIn();
    public void exit();
    public void leaveGame();
    public boolean isLoggedIn();
    public void rematch();
}