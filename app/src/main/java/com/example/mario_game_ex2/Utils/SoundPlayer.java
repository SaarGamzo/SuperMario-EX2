package com.example.mario_game_ex2.Utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.mario_game_ex2.R;

public class SoundPlayer {
    private static SoundPlayer instance;
    private MediaPlayer mediaPlayer;
    private Context context;

    // Private constructor to prevent instantiation from outside
    private SoundPlayer(Context context) {
        this.context = context.getApplicationContext();
    }

    // Get the singleton instance
    public static synchronized SoundPlayer getInstance(Context context) {
        if (instance == null) {
            instance = new SoundPlayer(context);
        }
        return instance;
    }

    public void playCrashSound() {
        // Release any previously used MediaPlayer
        release();
        // Create and start MediaPlayer for the crash sound
        mediaPlayer = MediaPlayer.create(context, R.raw.crash);
        mediaPlayer.start();
    }

    public void playStarSound() {
        // Release any previously used MediaPlayer
        release();
        // Create and start MediaPlayer for the coin sound
        mediaPlayer = MediaPlayer.create(context, R.raw.coin);
        mediaPlayer.start();
    }

    // Method to release the MediaPlayer
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
