package com.example.mario_game_ex2.Utils;

import com.example.mario_game_ex2.Models.PlayerDetails;

import java.util.Comparator;

public class ComparatorTopTenDetails implements Comparator<PlayerDetails> {
    @Override
    public int compare(PlayerDetails t1, PlayerDetails t2) {
        if(t1.getScore() > t2.getScore())
            return -1;
        if(t1.getScore() < t2.getScore())
            return 1;

        return 0;
    }
}
