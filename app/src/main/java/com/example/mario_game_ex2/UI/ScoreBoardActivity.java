package com.example.mario_game_ex2.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.gson.Gson;

import com.example.mario_game_ex2.Models.TopTenScores;
import com.example.mario_game_ex2.R;
import com.example.mario_game_ex2.Utils.SharedPreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScoreBoardActivity extends AppCompatActivity {

    private MaterialButton scoreboard_BTN_return;

    private MaterialTextView scoreboard_LBL_content;

    private List<String> topScoresList;

    private TopTenScores topTenScores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        findViews();
        setListeners();
        scoreboard_LBL_content.setText("AWA");
//        loadTopScores();
//        scoreboard_LBL_content.setText(topTenScores.toString());
    }

    private void findViews () {
        scoreboard_LBL_content = findViewById(R.id.scoreboard_LBL_content);
        scoreboard_BTN_return = findViewById(R.id.scoreboard_BTN_return);
    }

    private void loadTopScores(){
        topTenScores = new Gson().fromJson(SharedPreferencesManager.getInstance().getString("Top 10",""),TopTenScores.class);
    }

    private void setListeners() {
        scoreboard_BTN_return.setOnClickListener(view -> returnButtonClicked());
    }

    private void returnButtonClicked() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }
}