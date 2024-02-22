package com.example.mario_game_ex2.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.mario_game_ex2.Logic.GameManager;
import com.example.mario_game_ex2.Logic.GameTool;
import com.example.mario_game_ex2.Logic.Opponent;
import com.example.mario_game_ex2.Logic.Player;
import com.example.mario_game_ex2.Logic.Reviver;
import com.example.mario_game_ex2.R;
import com.example.mario_game_ex2.Utils.MySharedPreferences;
import com.example.mario_game_ex2.Utils.SharedUtils;
import com.example.mario_game_ex2.Utils.SoundPlayer;
import com.example.mario_game_ex2.Utils.TopTenArr;
import com.example.mario_game_ex2.Models.PlayerDetails;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainGameActivity extends Activity implements SensorEventListener {

    private ShapeableImageView[] main_game_IMG_hearts; // hearts array
    private TableLayout main_game_TBL_matrix; // UI game board
    private MaterialTextView main_game_LBL_score; // score label
    private MaterialTextView main_game_LBL_fast; // speed label
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

    private boolean sensorMode;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private boolean isGameOverDialogShowing = false;
    private LocationManager locationManager;
    private float lat;
    private float lon;
    private static final String SP_KEY_TOPTEN = "SP_KEY_PLAYLIST";
    private boolean hasMoved = false;

    private static final int SENSOR_SENSITIVITY = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        SharedUtils.getInstance().hideSystemUI(this);
        findViews();

        // Initialize game components
        handler = new Handler();
        isGamePaused = false;
        addNewOpponentsScheduler = true;
        isGameOverDialogShowing = false;

        // Get game settings from intent extras
        GAMESPEED = getIntent().getIntExtra("updatedGameSpeed", 1);
        COLS = getIntent().getIntExtra("updatedCols", 3);
        ROWS = getIntent().getIntExtra("updatedRows", 5);
        isVibrator = getIntent().getBooleanExtra("vibrateValue", true);
        sensorMode = getIntent().getBooleanExtra("sensorMode", false);

        // Initialize game manager
        gameMan = new GameManager(ROWS, COLS, 3);
        board = gameMan.getBoard();

        // Set up UI based on game settings
        setMatrixAtStart();
        setListeners();
        if (sensorMode) {
            main_game_LBL_fast.setText("");
            setMovementListeners();
            setMovementButtonsNotVisible();
        } else {
            main_game_LBL_fast.setVisibility(View.INVISIBLE);
        }

        // Start the game loop
        play();
    }


    private void setMovementListeners() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mGyroscope != null) {
            mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void setMovementButtonsNotVisible() {
        this.main_game_BTN_right.setVisibility(View.INVISIBLE);
        this.main_game_BTN_left.setVisibility(View.INVISIBLE);
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
                    resetMoveFlag(); // Reset the move flag after each turn
                    if (sensorMode) {
                        registerSensorListeners(); // Re-register the sensor listener for the next turn
                    }
                }
                // Schedule the next iteration
                play();
            }
        }, (SECINTERVAL - (GAMESPEED * 100)));
    }

    private void resetMoveFlag() {
        hasMoved = true;
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
        hasMoved = true; // Set the flag to true to indicate that the player has moved
        if(sensorMode)
            unregisterSensorListener(); // Unregister the sensor listener after the player moves
    }

    // move player to right
    private void movePlayerRight() {
        if (!gameMan.movePlayerRight()) {
            playerCrashed();
        }
        board = gameMan.getBoard();
        refreshUI();
        hasMoved = true; // Set the flag to true to indicate that the player has moved
        if(sensorMode)
            unregisterSensorListener(); // Unregister the sensor listener after the player moves
    }

    // if player and opponent meet - show a toast & vibrate if enabled
    private void playerCrashed() {
        makeToast("Damn!");
        if (isVibrator) {
            vibrate();
        }
        SoundPlayer.getInstance(this).playCrashSound();
    }

    private void makeToast(String toastText) {
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
                main_game_IMG_hearts[0].setVisibility(View.INVISIBLE);
                main_game_IMG_hearts[1].setVisibility(View.INVISIBLE);
                main_game_IMG_hearts[2].setVisibility(View.INVISIBLE);
                showGameOverDialog(gameMan.getScore() + "");
                break;
        }
    }

    private void showGameOverDialog(String finalScore) {
        isGameOverDialogShowing = true;
        pauseGame();
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
                if (playerName.equals("")) {
                    makeToast("Enter player name!");
                } else if(playerName.length() > 13){
                    makeToast("Too long name!");
                }
                else {

                    ActivityCompat.requestPermissions(MainGameActivity.this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION}, 0);

                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    lat = (float) location.getLatitude();
                    lon = (float) location.getLongitude();

                    MySharedPreferences.init(getApplicationContext());

                    setListFromJson();
                    gameMan.addRecord(playerName, lon, lat, Integer.parseInt(finalScore));//add a new record

                    storeListToJson();
                    isGameOverDialogShowing = false;
                    dialog.dismiss(); // Close the dialog
                    finish();
                    navigateToMainMenu();
                }
            }
        });

        // Override the dialog's key listener to prevent the back button from closing the dialog
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    // Consume the event to prevent the dialog from being dismissed
                    return true;
                }
                return false;
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
        main_game_LBL_fast = findViewById(R.id.main_game_LBL_fast);
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
        if (!isGamePaused && !isGameOverDialogShowing) {
            pauseGame(); // Pause the game and show the pause dialog
        } else {
            super.onBackPressed(); // Allow normal back button behavior when game is already paused or game over dialog is showing
        }
    }

    // Call this method when you want to pause the game
    private void pauseGame() {
        if (this.sensorMode)
            mSensorManager.unregisterListener(this);
        isGamePaused = true;
        if (!isGameOverDialogShowing)
            showPauseDialog();
        handler.removeCallbacksAndMessages(null);
    }

    // Call this method when you want to resume the game
    private void resumeGame() {
        isGamePaused = false;
        if (sensorMode) {
            registerSensorListeners(); // Register sensor listeners if in sensor mode
        }
        play(); // Restart the game loop
    }

    // Register sensor listeners
    private void registerSensorListeners() {
        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mGyroscope != null) {
            mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            handleAccelerometerEvent(event);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            handleGyroscopeEvent(event);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void handleAccelerometerEvent(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        if (Math.abs(x) > Math.abs(y)) {
            if (x < -SENSOR_SENSITIVITY) {
                movePlayerRight();
            } else if (x > SENSOR_SENSITIVITY) {
                movePlayerLeft();
            }
        } else {
            if (y < -SENSOR_SENSITIVITY) {
                increaseGameSpeed();
            } else if (y > SENSOR_SENSITIVITY) {
                decreaseGameSpeed();
            }
        }
    }

    private void handleGyroscopeEvent(SensorEvent event) {
    }

    private void increaseGameSpeed() {
        // Increase game speed up to a certain limit
        if (GAMESPEED != 5) {
            GAMESPEED = 5;
            main_game_LBL_fast.setText("Fast!");
        }
    }

    private void decreaseGameSpeed() {
        // Decrease game speed down to a certain limit
        if (GAMESPEED != 1) {
            GAMESPEED = 1;
            main_game_LBL_fast.setText("Slow!");
        }
    }

    public void setListFromJson() {
        ArrayList<PlayerDetails> topten;
        String serializedObject = MySharedPreferences.getInstance().getString(SP_KEY_TOPTEN, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<PlayerDetails>>() {
            }.getType();
            topten = gson.fromJson(serializedObject, type);
            TopTenArr.setTopTens(topten);
        }
    }

    public void storeListToJson() {
        Gson gson = new Gson();
        String topten = gson.toJson(this.gameMan.getTopTen().getTopTen());
        MySharedPreferences.getInstance().putString(SP_KEY_TOPTEN, topten);//put jason string in SP

    }

    // Unregister sensor listener
    private void unregisterSensorListener() {
        mSensorManager.unregisterListener(this);
    }

}
