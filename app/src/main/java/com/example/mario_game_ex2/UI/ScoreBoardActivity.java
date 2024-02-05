package com.example.mario_game_ex2.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mario_game_ex2.R;
import com.google.android.material.button.MaterialButton;

public class ScoreBoardActivity extends AppCompatActivity {

    private MaterialButton scoreboard_BTN_return;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        findViews();
        setListeners();
    }

    private void findViews () {
        scoreboard_BTN_return = findViewById(R.id.scoreboard_BTN_return);
    }

    private void setListeners() {
        scoreboard_BTN_return.setOnClickListener(view -> returnButtonClicked());
    }

    private void returnButtonClicked() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }
}