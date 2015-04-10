package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by User on 10/4/15.
 */
public class PlayerMetaData {

    private String name, model, ID;
    private TextureAtlas atlas;

    public PlayerMetaData() {
    }

    @Override
    public String toString() {
        return String.format("%s - %s, %s", ID, name, model);
    }

    /**
     * @param name the readable name
     */
    public PlayerMetaData setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @param index the character sprite model/index
     */
    public PlayerMetaData setModel(String index) {
        if(index.equals("4")){
            Gdx.app.log("HT_PlayerMeta","Model for player 4 is not done yet");
            index = "1";
        }
        this.model = String.format("player_%s_", index);
        atlas = new TextureAtlas(String.format("img/player%s.txt", index));
        return this;
    }

    /**
     * @param ID GPS ID
     */
    public PlayerMetaData setID(String ID) {
        this.ID = ID;
        return this;
    }

    /**
     *
     * @return readable name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return Model number of the player.
     */
    public String getModel() {
        return model;
    }

    /**
     *
     * @return GPS ID
     */
    public String getID() {
        return ID;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }
}
