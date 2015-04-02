package com.mayying.tileMapGame.entities.powerups.factory;

import com.badlogic.gdx.graphics.Texture;
import com.mayying.tileMapGame.GameWorld;
import com.mayying.tileMapGame.entities.Player;
import com.mayying.tileMapGame.entities.powerups.Blackout;
import com.mayying.tileMapGame.entities.powerups.Confusion;
import com.mayying.tileMapGame.entities.powerups.FireMine;
import com.mayying.tileMapGame.entities.powerups.FreezeMine;
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
                new FreezeMine(world.getDevicePlayer(), world.getDevicePlayer().getCollisionLayer()).use(null);
                break;
            case "Shield":
                new Invulnerability().use(new Player[]{world.getDevicePlayer()});
                break;
            case "Swap":
                new Swap().use(new Player[]{GameWorld.getPlayer(0)});
                break;
            case "Blackout":
                new Blackout().use(null);
                break;
            case "Confusion":
                new Confusion().use(new Player[]{GameWorld.getPlayer(0)});
                break;
            case "Fire Mine":
                new FireMine(world.getDevicePlayer(), world.getDevicePlayer().getCollisionLayer()).use(null);
                break;


        }
    }
}



