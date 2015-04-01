package com.mayying.tileMapGame.entities.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;

/**
 * Created by May on 31/3/2015.
 */
public class PowerUpFactory {
    private ArrayList<PowerUpPrototype> powerups;
    private static PowerUpFactory powerUpFactory = null;

    // Hidden Constructor
    private PowerUpFactory() {
    }

    public static PowerUpFactory getInstance() {
        if (PowerUpFactory.powerUpFactory == null) {
            Json json = new Json();
            json.setElementType(PowerUpFactory.class, "powerups", PowerUpPrototype.class);
            powerUpFactory = json.fromJson(PowerUpFactory.class, Gdx.files.internal("powerups/powerups.json"));
        }
        return powerUpFactory;
    }

    public PowerUp createPowerUp(int id) {
        PowerUpPrototype powerUpPrototype = powerups.get(id);
        PowerUp powerUp = new PowerUp(powerUpPrototype.filenameVector, powerUpPrototype.filenameBtn, powerUpPrototype.filename, powerUpPrototype.name, powerUpPrototype.description);
        return powerUp;
    }


}

class PowerUpPrototype {
    public String filenameVector, filenameBtn, filename, name, description;
}