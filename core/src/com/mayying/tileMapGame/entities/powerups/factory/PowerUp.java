package com.mayying.tileMapGame.entities.powerups.factory;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by May on 31/3/2015.
 */
public class PowerUp {
    private String filenameVector, filenameBtn, filename, name, description;

    PowerUp(String filenameVector, String filenameBtn, String filename, String name, String description) {
        this.filenameVector = filenameVector;
        this.filenameBtn = filenameBtn;
        this.filename = filename;
        this.name = name;
        this.description = description;
    }

    public Texture getTextureVector(){
        return new Texture("powerups/" + filenameVector);
    }

    public String getFilenameBtn(){
        return filenameBtn;
    }

    public String getFilename(){
        return filename;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public void action(String name){
        switch (name){
            case "Freeze Mine":
            case "Shield":
            case "Swap":
            case "Blackout":
            case "Confused":
        }
    }
}



