package com.example.mario_game_ex2.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mario_game_ex2.R;
import com.example.mario_game_ex2.Utils.SharedPreferencesManager;
import com.google.android.material.button.MaterialButton;

public class SecondMenuActivity extends AppCompatActivity {
    private MaterialButton second_BTN_back;
    private MaterialButton second_BTN_sensor;
    private MaterialButton second_BTN_slow_buttons;
    private MaterialButton second_BTN_fast_buttons;

    private int COLS;
    private int ROWS;
    private int GAMESPEED;
    private boolean isVibrator = true;
    private boolean sensorMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_menu);
        findViews();
        SharedPreferencesManager.init(this);
        GAMESPEED = getIntent().getIntExtra("updatedGameSpeed", 1); // get value from main menu screen
        COLS = getIntent().getIntExtra("updatedCols", 5); // get value from main menu screen
        ROWS = getIntent().getIntExtra("updatedRows", 8); // get value from main menu screen
        isVibrator = getIntent().getBooleanExtra("vibrateValue", true); // get value from main menu screen
        setListeners();
    }

    private void findViews() {
        second_BTN_back = findViewById(R.id.second_BTN_back);
        second_BTN_sensor = findViewById(R.id.second_BTN_sensor);
        second_BTN_slow_buttons = findViewById(R.id.second_BTN_slow_buttons);
        second_BTN_fast_buttons = findViewById(R.id.second_BTN_fast_buttons);
    }

    private void setListeners() {
        second_BTN_back.setOnClickListener(view -> backClicked());
        second_BTN_sensor.setOnClickListener(view -> playSensorMode());
        second_BTN_slow_buttons.setOnClickListener(view -> playButtonModeSlow());
        second_BTN_fast_buttons.setOnClickListener(view -> playButtonModeFast());
    }

    private void playButtonModeFast() {
        Intent intent = new Intent(this, MainGameActivity.class);
        intent.putExtra("updatedGameSpeed", 5);
        intent.putExtra("updatedCols", this.COLS);
        intent.putExtra("updatedRows", this.ROWS);
        intent.putExtra("vibrateValue", this.isVibrator);
        intent.putExtra("sensorMode", false);
        startActivity(intent);
    }

    private void playButtonModeSlow() {
        Intent intent = new Intent(this, MainGameActivity.class);
        intent.putExtra("updatedGameSpeed", 1);
        intent.putExtra("updatedCols", this.COLS);
        intent.putExtra("updatedRows", this.ROWS);
        intent.putExtra("vibrateValue", this.isVibrator);
        intent.putExtra("sensorMode", false);
        startActivity(intent);
    }

    private void playSensorMode() {
        Intent intent = new Intent(this, MainGameActivity.class);
        intent.putExtra("updatedGameSpeed", 3);
        intent.putExtra("updatedCols", this.COLS);
        intent.putExtra("updatedRows", this.ROWS);
        intent.putExtra("vibrateValue", this.isVibrator);
        intent.putExtra("sensorMode", true);
        startActivity(intent);
    }

    private void backClicked() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

}

