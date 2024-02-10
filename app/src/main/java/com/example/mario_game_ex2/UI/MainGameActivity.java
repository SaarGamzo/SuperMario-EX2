package com.example.mario_game_ex2.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mario_game_ex2.Logic.GameManager;
import com.example.mario_game_ex2.Logic.GameTool;
import com.example.mario_game_ex2.Logic.Opponent;
import com.example.mario_game_ex2.Logic.Player;
import com.example.mario_game_ex2.Logic.Reviver;
import com.example.mario_game_ex2.Models.Score;
import com.example.mario_game_ex2.Models.TopTenScores;
import com.example.mario_game_ex2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.example.mario_game_ex2.Models.Score;
import com.example.mario_game_ex2.Models.TopTenScores;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MainGameActivity extends Activity {

    private ShapeableImageView[] main_game_IMG_hearts; // hearts array
    private TableLayout main_game_TBL_matrix; // UI game board
    private MaterialTextView main_game_LBL_score; // score label
    private MaterialButton main_game_BTN_right; // right button
    private MaterialButton main_game_BTN_left; // left button
    private int COLS; // board columns
    private int ROWS; // board rows
    private int GAMESPEED; // game speed
    private boolean isVibrator; // determine if vibrate is enabled or disabled by user
    private GameManager gameMan; // game manager object
    private GameTool[][] board; // board of game tools which will be reflected on the screen
    private Handler handler; // handler to use timer
    private boolean addNewOpponentsScheduler = true; // scheduler to add new opponents or not

    private static final int SECINTERVAL = 1000;

    private static final int SCREENHEIGHTBETWEENLAYOUTS = 1700;

    private boolean isGamePaused = false;

    private TopTenScores topTenScores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        findViews();
        topTenScores = new TopTenScores();
        topTenScores.setName("Top 10 Players");
        isGamePaused = false;
        handler = new Handler();
        GAMESPEED = getIntent().getIntExtra("updatedGameSpeed", 1); // get value from settings screen
        COLS = getIntent().getIntExtra("updatedCols", 3); // get Columms value from main menu with default 3
        ROWS = getIntent().getIntExtra("updatedRows", 5); // get Rows value from main menu with default 5
        isVibrator = getIntent().getBooleanExtra("vibrateValue", true); // get if the game include vibrates or not
        gameMan = new GameManager(ROWS, COLS, 3); // create game manager
        board = gameMan.getBoard(); // get board of GameTool's
        setMatrixAtStart(); // align UI with content of board at start of a new game
        setListeners();
        play(); // play game function
    }

    private void setListeners() {
        main_game_BTN_right.setOnClickListener(view -> movePlayerRight()); // set right button listener
        main_game_BTN_left.setOnClickListener(view -> movePlayerLeft()); // set left button listener
    }

    // play function, play turn every 1 second
    private void play() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isGamePaused) {
                    if (!gameMan.playTurn(addNewOpponentsScheduler)) {
                        playerCrashed();
                    }
                    addNewOpponentsScheduler = !addNewOpponentsScheduler; // Toggle the boolean value
                    board = gameMan.getBoard();
                    refreshUI();
                }
                // Schedule the next iteration
                play();
            }
        }, (SECINTERVAL - (GAMESPEED * 100)));
    }


    // align UI to the board at starting a new game
    private void setMatrixAtStart() {
        main_game_TBL_matrix.post(new Runnable() {
            @Override
            public void run() {
                // Calculate the dimensions of the table
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                // Set images sizes equaly
                int imageWidth = screenWidth / COLS;
                int imageHeight = SCREENHEIGHTBETWEENLAYOUTS / ROWS;
                // Clear any existing views
                main_game_TBL_matrix.removeAllViews();
                for (int row = 0; row < ROWS; row++) {
                    TableRow tableRow = new TableRow(MainGameActivity.this);
                    for (int col = 0; col < COLS; col++) {
                        ShapeableImageView imageView = new ShapeableImageView(MainGameActivity.this);
                        TableRow.LayoutParams params = new TableRow.LayoutParams(
                                imageWidth,
                                imageHeight
                        );
                        imageView.setLayoutParams(params);
                        imageView.setAdjustViewBounds(true);
                        if (board[row][col] == null) { // if cell is empty
                            imageView.setImageResource(android.R.color.transparent);
                        } else {
                            if (board[row][col] instanceof Player) { // if cell is a player
                                imageView.setImageResource(R.drawable.mario);
                            } else if (board[row][col] instanceof Opponent) { // if cell is opponent (not relevant at start)
                                imageView.setImageResource(R.drawable.opponent);
                            } else if (board[row][col] instanceof Reviver) {
                                imageView.setImageResource(R.drawable.star);
                            }
                        }
                        tableRow.addView(imageView);
                    }
                    main_game_TBL_matrix.addView(tableRow);
                }
            }
        });
    }

    // move player left
    private void movePlayerLeft() {
        if (!gameMan.movePlayerLeft()) {
            playerCrashed();
        }
        board = gameMan.getBoard();
        refreshUI();
    }

    // move player to right
    private void movePlayerRight() {
        if (!gameMan.movePlayerRight()) {
            playerCrashed();
        }
        board = gameMan.getBoard();
        refreshUI();
    }

    // if player and opponent meet - show a toast & vibrate if enabled
    private void playerCrashed() {
        makeToast("Damn!");
        if (isVibrator) {
            vibrate();
        }
    }

    private void makeToast(String toastText){
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
    }

    // Update the matrix in refreshUI method
    private void refreshUI() {
        // Update all icons in the screen
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                GameTool cellValue = board[row][col];
                updateCell(row, col, cellValue);
            }
        }
        main_game_LBL_score.setText(gameMan.getScore() + "");
        syncHeartsWithStrikes();
    }

    private void syncHeartsWithStrikes() {
        int strikes = gameMan.getStrikes();
        switch (strikes) {
            case 0:
                main_game_IMG_hearts[0].setVisibility(View.VISIBLE);
                main_game_IMG_hearts[1].setVisibility(View.VISIBLE);
                main_game_IMG_hearts[2].setVisibility(View.VISIBLE);
                break;
            case 1:
                main_game_IMG_hearts[0].setVisibility(View.VISIBLE);
                main_game_IMG_hearts[1].setVisibility(View.VISIBLE);
                main_game_IMG_hearts[2].setVisibility(View.INVISIBLE);
                break;
            case 2:
                main_game_IMG_hearts[0].setVisibility(View.VISIBLE);
                main_game_IMG_hearts[1].setVisibility(View.INVISIBLE);
                main_game_IMG_hearts[2].setVisibility(View.INVISIBLE);
                break;
            case 3:
                // Game Over!
                showGameOverDialog(gameMan.getScore() + "");
                break;
        }
    }

    private void showGameOverDialog(String finalScore) {
        isGamePaused = true;
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.game_over_dialog);

        // Find views in the dialog layout
        MaterialTextView lblGameOver = dialog.findViewById(R.id.lblGameOver);
        MaterialTextView lblScore = dialog.findViewById(R.id.lblScore);
        EditText edtPlayerName = dialog.findViewById(R.id.edtPlayerName);
        MaterialButton btnSubmit = dialog.findViewById(R.id.btnSubmit);

        // Set up the labels and score
        lblGameOver.setText("Game Over!");
        lblScore.setText("Score: " + finalScore);

        // Set up the button click listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerName = edtPlayerName.getText().toString();
                if(playerName.equals("")){
                    makeToast("Enter player name!");
                }else {
                    // Inside showGameOverDialog method
                    SharedPreferences prefs = getSharedPreferences("TopScores", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    int score = Integer.parseInt(finalScore);
                    // add new Score to the topTenScores
                    Score newScore = new Score();
                    newScore.setName(playerName);
                    newScore.setDate(new Date());
                    newScore.setScore(score);
                    topTenScores.addScore(newScore);

                    navigateToMainMenu();
                    dialog.dismiss(); // Close the dialog
                }
            }
        });
        // Show the dialog
        dialog.show();
    }

    private void navigateToMainMenu() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }


    // update UI cell image with value from GameTool board
    private void updateCell(int row, int col, GameTool cellValue) {
        TableRow tableRow = (TableRow) main_game_TBL_matrix.getChildAt(row);
        ShapeableImageView imageView = (ShapeableImageView) tableRow.getChildAt(col); // grab image of needed cell
        if (cellValue == null) {
            // Set an empty cell image
            imageView.setImageResource(android.R.color.transparent);
        } else if (board[row][col] instanceof Player) {
            // Set Player
            imageView.setImageResource(R.drawable.mario);
        } else if (board[row][col] instanceof Opponent) {
            // Set Opponent
            imageView.setImageResource(R.drawable.opponent);
        } else if (board[row][col] instanceof Reviver) {
            // Set Opponent
            imageView.setImageResource(R.drawable.star);
        }
    }

    // find all view objects in activity_main_game.xml
    private void findViews() {
        main_game_IMG_hearts = new ShapeableImageView[]{
                findViewById(R.id.main_game_IMG_heart1),
                findViewById(R.id.main_game_IMG_heart2),
                findViewById(R.id.main_game_IMG_heart3)
        };
        main_game_LBL_score = findViewById(R.id.main_game_LBL_score);
        main_game_TBL_matrix = findViewById(R.id.main_game_TBL_matrix);
        main_game_BTN_right = findViewById(R.id.main_game_BTN_right);
        main_game_BTN_left = findViewById(R.id.main_game_BTN_left);
    }

    // vibrate phone in case of crash
    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.EFFECT_HEAVY_CLICK));
        } else {
            v.vibrate(200);
        }
    }

    @Override
    public void onBackPressed() {
        pauseGame();
    }

    // Call this method when you want to pause the game
    private void pauseGame() {
        isGamePaused = true;
        showPauseDialog();
    }

    // Call this method when you want to resume the game
    private void resumeGame() {
        isGamePaused = false;
        // Perform tasks to resume the game (e.g., restart the timer)
    }

    // Show a pause dialog with resume and quit buttons when back button clicked
    private void showPauseDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.pause_dialog);

        TextView headline = dialog.findViewById(R.id.textViewHeadline);
        headline.setText("Paused");

        MaterialButton resumeButton = dialog.findViewById(R.id.buttonResume);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                resumeGame();
            }
        });

        MaterialButton quitButton = dialog.findViewById(R.id.buttonQuit);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                quitGame();
            }
        });
        dialog.show();
    }

    // Call this method when you want to quit the game
    private void quitGame() {
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(MainGameActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    // Remove any remaining callbacks to prevent memory leaks
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
