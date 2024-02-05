package com.example.mario_game_ex2.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mario_game_ex2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class SettingsActivity extends AppCompatActivity {

    private Switch settings_BTN_vibrate;
    private SeekBar settings_SLIDER_rows;

    private SeekBar settings_SLIDER_gameSpeed;
    private SeekBar settings_SLIDER_columns;
    private MaterialTextView settings_numOfRow_TXT;
    private MaterialTextView settings_numOfCol_TXT;
    private MaterialTextView settings_gameSpeed_TXT;
    private MaterialButton settings_BTN_back;

    private int COLS;
    private int ROWS;
    private int GAMESPEED;
    private boolean vibratorValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViews();
        GAMESPEED = getIntent().getIntExtra("initialGameSpeed", 1); // get value from main activity
        COLS = getIntent().getIntExtra("initialCols", 3); // get value from main activity
        ROWS = getIntent().getIntExtra("initialRows", 5); // get value from main activity
        vibratorValue = getIntent().getBooleanExtra("vibrateValue", true); // get value from main activity

        // Set initial values to sliders and labels
        settings_SLIDER_gameSpeed.setProgress(GAMESPEED);
        settings_gameSpeed_TXT.setText("Game speed (low to high): " + GAMESPEED);
        settings_SLIDER_rows.setProgress(ROWS);
        settings_numOfRow_TXT.setText("Number of Rows: " + ROWS);
        settings_SLIDER_columns.setProgress(COLS);
        settings_numOfCol_TXT.setText("Number of Columns: " + COLS);
        settings_BTN_vibrate.setChecked(vibratorValue);
        //set listeners
        settings_SLIDER_gameSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings_gameSpeed_TXT.setText("Game speed (low to high): " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No action needed when the user starts tracking touch
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No action needed when the user stops tracking touch
            }
        });

        settings_BTN_back.setOnClickListener(view -> backClicked());

        settings_SLIDER_rows.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings_numOfRow_TXT.setText("Number of Rows: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No action needed when the user starts tracking touch
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No action needed when the user stops tracking touch
            }
        });

        settings_SLIDER_columns.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings_numOfCol_TXT.setText("Number of Columns: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No action needed when the user starts tracking touch
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No action needed when the user stops tracking touch
            }
        });
    }

    // find views
    private void findViews() {
        settings_BTN_vibrate = findViewById(R.id.settings_BTN_vibrate);
        settings_SLIDER_rows = findViewById(R.id.settings_SLIDER_rows);
        settings_SLIDER_columns = findViewById(R.id.settings_SLIDER_columns);
        settings_SLIDER_gameSpeed = findViewById(R.id.settings_SLIDER_gameSpeed);
        settings_numOfRow_TXT = findViewById(R.id.settings_numOfRow_TXT);
        settings_numOfCol_TXT = findViewById(R.id.settings_numOfCol_TXT);
        settings_gameSpeed_TXT = findViewById(R.id.settings_GameSpeed_TXT);
        settings_BTN_back = findViewById(R.id.settings_BTN_back);
    }

    // back button clicked
    private void backClicked() {
        // Return to the main menu or navigate to the main menu screen
        Intent intent = new Intent(SettingsActivity.this, MainMenuActivity.class);
        intent.putExtra("updatedGameSpeed", settings_SLIDER_gameSpeed.getProgress());
        intent.putExtra("updatedCols", settings_SLIDER_columns.getProgress());
        intent.putExtra("updatedRows", settings_SLIDER_rows.getProgress());
        intent.putExtra("vibrateValue", settings_BTN_vibrate.isChecked());
        startActivity(intent);
    }
}
