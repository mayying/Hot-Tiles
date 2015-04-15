package com.mayying.tileMapGame.multiplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luccan on 5/4/2015.
 */
public class MessageBuffer {
    private List<String> list;

    public MessageBuffer(){
        list = new ArrayList<String>();
    }

    public List<String> getList(){
        synchronized (this) {
            List<String> ret = list;
            list = new ArrayList<String>();
            return ret;
        }
    }

    public List<String> getList(String filter){
        synchronized (this){
            List<String> ret = list;
            list = new ArrayList<String>();
            for (String s : ret){
                if (!s.endsWith(filter)){
                    list.add(s);
                }
            }
            ret.removeAll(list);
            return ret;
        }
    }

    public void add(String msg){
        synchronized (this){
            list.add(msg);
        }
    }

}
