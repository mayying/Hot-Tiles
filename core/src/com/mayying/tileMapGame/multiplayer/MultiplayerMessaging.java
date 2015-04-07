package com.mayying.tileMapGame.multiplayer;

import java.util.List;

/**
 * Created by Luccan on 5/4/2015.
 */
public interface MultiplayerMessaging {
    public void BroadCastMessage(String msg);
    public List<String> getParticipants();
    public List<String> getJoinedParticipants();
    public String getMyId();
    public List<String> getMessageBuffer();
}