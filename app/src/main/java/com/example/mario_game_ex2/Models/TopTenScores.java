package com.example.mario_game_ex2.Models;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TopTenScores {
    private String name = "";
    private ArrayList<Score> scoreArrayList = new ArrayList<>();

    public TopTenScores(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Score> getScoreArrayList() {
        return scoreArrayList;
    }

    public void setScoreArrayList(ArrayList<Score> scoreArrayList) {
        this.scoreArrayList = scoreArrayList;
    }
    public TopTenScores addScore(Score score){
        this.scoreArrayList.add(score);
        return this;
    }

    private void sortTopTenScores(){
        // Sort the ArrayList in descending order of scores
        Collections.sort(this.scoreArrayList, new Comparator<Score>() {
            @Override
            public int compare(Score score1, Score score2) {
                // Sort in descending order of scores
                return Integer.compare(score2.getScore(), score1.getScore());
            }
        });
    }

    @Override
    public String toString() {
        return "TopTenScores{" +
                "name='" + name + '\'' +
                ", scoreArrayList=" + scoreArrayList +
                '}';
    }
}
