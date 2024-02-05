package com.example.mario_game_ex2.Logic;

public class GameTool {
    private int rowLocation;
    private int columnLocation;
    private boolean isPlayer;
    public GameTool(int rowLocation, int columnLocation) {
        this.rowLocation = rowLocation;
        this.columnLocation = columnLocation;
    }

    public int getRowLocation() {
        return rowLocation;
    }

    public int getColumnLocation() {
        return columnLocation;
    }

    public void setGameToolLocation(int row, int col){
        this.rowLocation = row;
        this.columnLocation = col;
    }
}
