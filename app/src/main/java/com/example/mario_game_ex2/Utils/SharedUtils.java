package com.example.mario_game_ex2.Utils;

import android.app.Activity;
import android.view.View;

public class SharedUtils {

    private static SharedUtils instance;

    // Private constructor to prevent instantiation from other classes
    private SharedUtils() {
    }

    // Method to get the singleton instance
    public static SharedUtils getInstance() {
        if (instance == null) {
            instance = new SharedUtils();
        }
        return instance;
    }
    // Method to hide only the top notification bar (status bar)
    public void hideSystemUI(Activity activity) {
        // Hide the status bar only
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
