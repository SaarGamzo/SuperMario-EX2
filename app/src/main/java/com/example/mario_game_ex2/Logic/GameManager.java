package com.example.mario_game_ex2.Logic;

import com.example.mario_game_ex2.Utils.TopTenArr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameManager {
    private final Player player; // player
    private int numberOfOpponentsGone = 0; // counter of opponents won - to calculate score
    private final ArrayList<Opponent> activeOpponents; // all active opponents
    private final ArrayList<Reviver> activeRevivers; // all active revivers
    private int strikes; // number of strikes
    private final GameTool[][] board; // board of GameTool
    private int COLS = 3;
    private int ROWS = 5;

    private int reviversCounter = 0;
    private static GameManager manager;
    private TopTenArr topTen;

    public GameManager() {
        board = new GameTool[ROWS][COLS];
        player = new Player(ROWS - 1, (COLS - 1) / 2); // set player location
        setUpBoard(); // set up board - all cells are null except player location
        activeOpponents = new ArrayList<Opponent>(); // set active opponents arraylist
        activeRevivers = new ArrayList<Reviver>(); // set active revivers arraylist
        this.strikes = 0; // player strikes
    }

    // return how many strikes the player have made
    public int getStrikes() {
        return strikes;
    }

    public GameManager(int rows, int columns, int lives) {
        ROWS = rows;
        COLS = columns;
        board = new GameTool[ROWS][COLS];
        player = new Player(ROWS - 1, (COLS - 1) / 2);
        setUpBoard();
        activeOpponents = new ArrayList<Opponent>();
        activeRevivers = new ArrayList<Reviver>();
        this.strikes = 0;
        topTen = TopTenArr.set();
    }

    // get board of GameTool
    public GameTool[][] getBoard() {
        return board;
    }

    // calculate score by double number of won opponents with 10
    public int getScore() {
        return numberOfOpponentsGone * 10;
    }

    // move player left - return false if a hit was made by action
    public boolean movePlayerLeft() {
        // player trying to move to the end of matrix - left border
        if (player.getColumnLocation() == 0) {
            // can't move
        } else {
            // player trying to move left to a free cell
            if (board[ROWS - 1][player.getColumnLocation() - 1] == null) {
                board[ROWS - 1][player.getColumnLocation()] = null;
                board[ROWS - 1][player.getColumnLocation() - 1] = player;
                player.setGameToolLocation(ROWS - 1, player.getColumnLocation() - 1);
            }
            // player trying to move left to a cell with opponent
            else if (board[ROWS - 1][player.getColumnLocation() - 1] instanceof Opponent) {
                activeOpponents.remove(board[ROWS - 1][player.getColumnLocation() - 1]);
                board[ROWS - 1][player.getColumnLocation()] = null;
                board[ROWS - 1][player.getColumnLocation() - 1] = player;
                player.setGameToolLocation(ROWS - 1, player.getColumnLocation() - 1);
                hit();
                return false;
            }// player trying to move left to a cell with reviver
            else if(board[ROWS - 1][player.getColumnLocation() - 1] instanceof Reviver){
                activeRevivers.remove(board[ROWS - 1][player.getColumnLocation() - 1]);
                board[ROWS - 1][player.getColumnLocation()] = null;
                board[ROWS - 1][player.getColumnLocation() - 1] = player;
                player.setGameToolLocation(ROWS - 1, player.getColumnLocation() - 1);
                reviveHit();
            }
        }
        return true;
    }

    // set all cells to be null except player cell at start of a game
    private void setUpBoard() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (i == player.getRowLocation() && j == player.getColumnLocation()) {
                    board[i][j] = player;  // Set player at the initial position
                } else {
                    board[i][j] = null;
                }
            }
        }
    }

    // move player right - return false if a hit was made by action
    public boolean movePlayerRight() {
        // player trying to move right - end of matrix - right border
        if (player.getColumnLocation() == COLS - 1) {
            // can't move
        } else {
            // player trying to move right to a free cell
            if (board[ROWS - 1][player.getColumnLocation() + 1] == null) {
                board[ROWS - 1][player.getColumnLocation()] = null;
                board[ROWS - 1][player.getColumnLocation() + 1] = player;
                player.setGameToolLocation(ROWS - 1, player.getColumnLocation() + 1);
            }
            // player trying to move right to a cell with opponent
            else if (board[ROWS - 1][player.getColumnLocation() + 1] instanceof Opponent) {
                activeOpponents.remove(board[ROWS - 1][player.getColumnLocation() + 1]);
                board[ROWS - 1][player.getColumnLocation()] = null;
                board[ROWS - 1][player.getColumnLocation() + 1] = player;
                player.setGameToolLocation(ROWS - 1, player.getColumnLocation() + 1);
                hit();
                return false;
            }// player trying to move right to a cell with reviver
            else if(board[ROWS - 1][player.getColumnLocation() + 1] instanceof Reviver){
                activeRevivers.remove(board[ROWS - 1][player.getColumnLocation() + 1]);
                board[ROWS - 1][player.getColumnLocation()] = null;
                board[ROWS - 1][player.getColumnLocation() + 1] = player;
                player.setGameToolLocation(ROWS - 1, player.getColumnLocation() + 1);
                reviveHit();
            }
        }
        return true;
    }

    // move all opponents down by one row and check if there's a hit with the player
    private boolean moveOpponentDown() {
        Iterator<Opponent> iterator = activeOpponents.iterator();
        boolean retStatus = true;
        while (iterator.hasNext()) { // iterate every active opponent
            Opponent opponent = iterator.next();
            int row = opponent.getRowLocation();
            int col = opponent.getColumnLocation();
            // Check if opponent is on the last row (bottom of the screen) - end of matrix
            if (row == ROWS - 1) {
                board[row][col] = null; // free cell
                numberOfOpponentsGone += 1; // score += 10
                iterator.remove(); // remove opponent from active opponents
            } else {
                int newRow = row + 1;
                if (newRow < ROWS && board[newRow][col] == null) { // opponent moving to a free cell
                    board[row][col] = null; // free cell
                    board[newRow][col] = opponent; // catch new cell
                    opponent.setGameToolLocation(newRow, col);
                } else { // opponent moving to a taken cell with player
                    board[row][col] = null; // free cell
                    iterator.remove();
                    hit();
                    retStatus = false;
                }
            }
        }
        return retStatus;
    }

    // move all revivers down by one row and check if there's meeting with the player
    private void moveReviversDown() {
        Iterator<Reviver> iterator = activeRevivers.iterator();
        while (iterator.hasNext()) { // iterate every active reviver
            Reviver reviver = iterator.next();
            int row = reviver.getRowLocation();
            int col = reviver.getColumnLocation();
            // Check if reviver is on the last row (bottom of the screen) - end of matrix
            if (row == ROWS - 1) {
                board[row][col] = null; // free cell
                iterator.remove(); // remove reviver from active opponents
            } else {
                int newRow = row + 1;
                if (newRow < ROWS && board[newRow][col] == null) { // reviver moving to a free cell
                    board[row][col] = null; // free cell
                    board[newRow][col] = reviver; // catch new cell
                    reviver.setGameToolLocation(newRow, col);
                } else if (newRow < ROWS && board[newRow][col] instanceof Player) { // reviver moving to a taken cell with player
                    board[row][col] = null; // free cell
                    iterator.remove();
                    reviveHit();
                }
            }
        }
    }

    // check if a player meet with revive symbol to get life if not full, if full - get extra 50 points!
    private void reviveHit() {
        if(getStrikes() < 3 && getStrikes() > 0){
            strikes--;
        }
        else{
            numberOfOpponentsGone += 5;
        }
    }

    // add new opponents to row 0
    private void addNewOpponents() {
        int numberOfNewOpponents = randomiseNumber(COLS); // at least 1 cell open in a row
        if (numberOfNewOpponents == 0) // add at least 1 opponent every turn
            numberOfNewOpponents++;
        int newOpponentColumn;
        for (int i = 0; i < numberOfNewOpponents; i++) {
            // randomise column to new opponent
            newOpponentColumn = randomiseNumber(COLS); // randomise column location
            while (board[0][newOpponentColumn] != null) { // find open cell
                newOpponentColumn = randomiseNumber(COLS);
            }
            // create the opponent:
            Opponent newOpponent = new Opponent(0, newOpponentColumn);
            activeOpponents.add(newOpponent);
            board[0][newOpponentColumn] = newOpponent; // set cell as opponent
        }
    }

    // add new reviver to row 0
    private void addNewReviver() {
        int newHeartColumn = randomiseNumber(COLS);
        // randomise column to new Reviver
        while (board[0][newHeartColumn] != null) { // find open cell
            newHeartColumn = randomiseNumber(COLS);
        }
        // create the Reviver:
        Reviver newReviver = new Reviver(0, newHeartColumn);
        activeRevivers.add(newReviver);
        board[0][newHeartColumn] = newReviver; // set cell as reviver
    }

    // randomise number from 0 to top
    private int randomiseNumber(int top) {
        Random random = new Random();
        return random.nextInt(top);
    }

    // when hit happened between player and opponent
    private void hit() {
        strikes++;
    }

    // play turn - one turn only move opponents down, second turn add new opponents and go on...
    public boolean playTurn(boolean addNewOpponents) {
        boolean retStatus;
        if (addNewOpponents) {
            reviversCounter++;
            retStatus = moveOpponentDown();
            addNewOpponents();
            moveReviversDown();
            if(reviversCounter%3==0){
                addNewReviver();
            }
        } else {
            retStatus = moveOpponentDown();
            moveReviversDown();
        }
        return retStatus; // return false if hit happened on this turn
    }

    public static GameManager set() {
        if (manager == null) {
            manager = new GameManager();
        }
        return manager;
    }

    public TopTenArr getTopTen() {
        return topTen;
    }

    public void addRecord(String name, double lon, double lat, int score) {
        topTen.addRecord(name, score, lat, lon);
    }
}
