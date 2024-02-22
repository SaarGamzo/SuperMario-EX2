package com.example.mario_game_ex2.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mario_game_ex2.R;
import com.example.mario_game_ex2.Utils.CallBackTopTenProtocol;
import com.example.mario_game_ex2.Utils.FragmentList;
import com.example.mario_game_ex2.Utils.FragmentMap;
import com.example.mario_game_ex2.Models.PlayerDetails;
import com.example.mario_game_ex2.Utils.SharedUtils;

public class TopTenPanel extends AppCompatActivity {

    private FragmentList fragment_list;
    private FragmentMap fragment_map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_ten_pannel);
        SharedUtils.getInstance().hideSystemUI(this);
        fragment_list = new FragmentList();
        fragment_map = new FragmentMap();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.panel_LAY_map, fragment_map)
                .commit();

        fragment_list.setCallBack_topTenProtocol(callBack_topTenProtocol);

        getSupportFragmentManager().beginTransaction().add(R.id.panel_LAY_list, fragment_list).commit();

    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }

    private void showTopTenLocation(PlayerDetails playerDetails) {
        double lat = Double.parseDouble(playerDetails.getLatitude());
        double lon = Double.parseDouble(playerDetails.getLongitude());
        fragment_map.zoomOnMap(lat, lon, playerDetails.getName());
    }

    private void showFakeCoordinates1() {
        double lat = 666;
        double lon = 555;
        fragment_map.zoomOnMap(lat, lon, "fakeName");
    }

    CallBackTopTenProtocol callBack_topTenProtocol = new CallBackTopTenProtocol() {
        @Override
        public void topTenDetails(PlayerDetails details) {
            showTopTenLocation(details);
        }

        @Override
        public void showFakeCoordinates() {
            showFakeCoordinates1();
        }
    };
}
