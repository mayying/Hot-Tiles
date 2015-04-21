package com.mayying.tileMapGame.entities.powerups.factory;

import com.badlogic.gdx.graphics.Texture;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.powerups.Blackout;
import com.mayying.tileMapGame.entities.powerups.Confusion;
import com.mayying.tileMapGame.entities.powerups.Fire;
import com.mayying.tileMapGame.entities.powerups.FreezeMine;
import com.mayying.tileMapGame.entities.powerups.Haste;
import com.mayying.tileMapGame.entities.powerups.Invulnerability;
import com.mayying.tileMapGame.entities.powerups.Swap;

/**
 * Created by May on 31/3/2015.
 */
public class PowerUp {
    private String filenameVector, filenameBtn, filename, name, description;
    private GameWorld world;

    PowerUp(GameWorld world, String filenameVector, String filenameBtn, String filename, String name, String description) {
        this.world = world;
        this.filenameVector = filenameVector;
        this.filenameBtn = filenameBtn;
        this.filename = filename;
        this.name = name;
        this.description = description;
    }

    public Texture getTextureVector() {
        return new Texture("powerups/" + filenameVector);
    }

    public String getFilenameBtn() {
        return filenameBtn;
    }

    public String getFilename() {
        return filename;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void action() {

        switch (getName()) {
            case "Freeze Mine":
                new FreezeMine(world.getDevicePlayer(), world.getDevicePlayer().getCollisionLayer()).use();
                break;
            case "Shield":
                new Invulnerability().use();
                break;
            case "Swap":
                new Swap().use();
                break;
            case "Blackout":
                new Blackout().use();
                break;
            case "Confusion":
                new Confusion().use();
                break;
            case "Torch":
                new Fire().use();
                break;
            case "Frenzy":
                new Haste().use();
                break;
        }
    }
}



