package com.example.mario_game_ex2.Models;

import java.util.Date;

public class Score {
    private String name = "";
    private Date date = null;
    private int score = 0;

    public Score(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Score{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", score=" + score +
                '}';
    }
}
