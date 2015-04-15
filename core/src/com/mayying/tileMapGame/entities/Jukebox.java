package com.mayying.tileMapGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

/**
 * Created by May on 29/3/2015.
 */
public class Jukebox {
    private static HashMap<String, Sound> sounds;
    private static HashMap<String, Music> musics;
    private static boolean mute = false;

    static {
        sounds = new HashMap<String, Sound>();
        musics = new HashMap<String, Music>();
    }

    public static void load(String name) {
//        Gdx.app.log("Jukebox", "Loading: " + name);
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/" + name + ".mp3"));
        sounds.put(name, sound);
    }

    public static void loadMusic(String name) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal("sounds/" + name + ".mp3"));
        musics.put(name, music);
        music.setLooping(true);
    }

    public static void play(String name) {
//        Gdx.app.log("Jukebox", "MUTE? " + mute + " Now playing: " + name);
        if (!mute) {
//            Gdx.app.log("Jukebox", "Now playing: " + name);
            sounds.get(name).play();
        }
    }

    public static void playMusic(String name) {
        if (!mute) {
//            Gdx.app.log("Jukebox", "Now playing: " + name);
            musics.get(name).play();
        }

        if (name.equals("buttonPressed"))
            musics.get(name).setVolume(2f);
    }

    public static void toggleMute(String name, boolean mute) {
        if (mute)
            musics.get(name).setVolume(0f);
        else
            musics.get(name).setVolume(1f);
    }

    public static void loop(String name) {
        sounds.get(name).loop();
    }

    public static void stop(String name) {
        sounds.get(name).stop();
    }

    public static void stopMusic(String name) {
        musics.get(name).stop();
    }

    public static void stopAll() {
        for (Sound s : sounds.values()) {
            s.stop();
        }
    }
}
