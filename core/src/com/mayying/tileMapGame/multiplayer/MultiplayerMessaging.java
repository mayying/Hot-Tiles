package com.mayying.tileMapGame.multiplayer;

import java.util.List;

/**
 * Created by Luccan on 5/4/2015.
 */
public interface MultiplayerMessaging {
    public void broadcastMessage(String msg);
    public List<String> getParticipants();
    public List<String> getJoinedParticipants();
    public String getMyId();
    public List<String> getMessageBuffer();
    public String getHostId();

    //for main menu usages
    public void startQuickGame();
    public void signIn();
    public void signOut();
    public void exit();
}