package com.example.mario_game_ex2.Utils;

import com.example.mario_game_ex2.Models.PlayerDetails;

import java.util.ArrayList;
import java.util.Collections;

public class TopTenArr {

    private static TopTenArr topTen_arr;
    private static ArrayList<PlayerDetails> topTens;

    public TopTenArr() {
        topTens = new ArrayList<PlayerDetails>();
        for (int i = 1; i <= 10; i++) {
            topTens.add(new PlayerDetails().setSerialNoImg(i));
        }
    }

    public static TopTenArr set() {
        if (topTen_arr == null) {
            topTen_arr = new TopTenArr();
        }
        return topTen_arr;
    }
    public static ArrayList<PlayerDetails> getTopTen() {
        return topTens;
    }
    @Override
    public String toString() {
        return "TopTenArr{" +

                "TopTen=" + topTens +
                '}';
    }

    public void addRecord(String name, int score, double lat, double lon) {
        PlayerDetails tTD = new PlayerDetails(name, score, lat, lon);

        if (topTens.size() < 8) {//was currentTopTenNum
            topTens.get(topTens.size()).setName(name);
            topTens.get(topTens.size()).setLocation(lat, lon);
            topTens.get(topTens.size()).setScore(score);
            topTens.get(topTens.size()).setSerialNoImg(topTens.size());

        } else {//there are aleady 10 Top-Ten, if the current's score is bigger than the last's
            if (score > topTens.get(9).getScore()) {
                topTens.get(9).setName(name);
                topTens.get(9).setLocation(lat, lon);
                topTens.get(9).setScore(score);
            }
        }
        Collections.sort(topTens, new ComparatorTopTenDetails());
        sortArrTopTen();
    }

    public void sortArrTopTen() {
        for (int i = 0; i < topTens.size(); i++) {
            topTens.get(i).setSerialNoImg(i + 1);
        }
    }

    public static void setTopTens(ArrayList<PlayerDetails> arr) {
        topTens = arr;
    }
}
