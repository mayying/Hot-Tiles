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
    private static float volume = 1;

    static {
        sounds = new HashMap<String, Sound>();
        musics = new HashMap<String, Music>();
    }

    public static void load(String name) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/" + name + ".mp3"));
        sounds.put(name, sound);
        sound.setLooping(0, true);
    }

    public static void loadMusic(String name) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal("sounds/" + name + ".mp3"));
        musics.put(name, music);
        music.setLooping(true);
    }

    public static void play(String name) {
        sounds.get(name).play(volume);
        if (name.equals("suicide"))
            sounds.get(name).loop();
    }

    public static void playMusic(String name) {
        musics.get(name).play();
    }

    public static void toggleMuteMusic(String name, boolean mute) {
        if (mute)
            musics.get(name).setVolume(0f);
        else
            musics.get(name).setVolume(1f);
    }

    public static void toggleMuteSfx(boolean mute) {
        for (Sound s : sounds.values()) {
            if (mute) {
                s.setVolume(0, 0f);
                volume = 0f;
            } else {
                s.setVolume(0, 1f);
                volume = 1f;
            }
        }
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
