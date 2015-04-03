package com.mayying.tileMapGame.entities.powerups.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.mayying.tileMapGame.GameWorld;

import java.util.ArrayList;

/**
 * Created by May on 31/3/2015.
 */
public class PowerUpFactory {
    private ArrayList<PowerUpPrototype> powerups;
    private static PowerUpFactory powerUpFactory = null;
    private GameWorld world;


    // Hidden Constructor
    private PowerUpFactory() {
    }

    public static PowerUpFactory getInstance(GameWorld world) {

        if (PowerUpFactory.powerUpFactory == null) {
            Json json = new Json();
            json.setElementType(PowerUpFactory.class, "powerups", PowerUpPrototype.class);
            powerUpFactory = json.fromJson(PowerUpFactory.class, Gdx.files.internal("powerups/powerups.json"));
            powerUpFactory.world = world;
        }
        return powerUpFactory;
    }

    public PowerUp createPowerUp(int id) {
        PowerUpPrototype powerUpPrototype = powerups.get(id);
        return new PowerUp(world, powerUpPrototype.filenameVector, powerUpPrototype.filenameBtn, powerUpPrototype.filename, powerUpPrototype.name, powerUpPrototype.description);
    }


    public void reset() {
        powerUpFactory = null;
    }
}

class PowerUpPrototype {
    public String filenameVector, filenameBtn, filename, name, description;
}