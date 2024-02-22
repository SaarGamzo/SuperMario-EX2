package com.example.mario_game_ex2.Utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mario_game_ex2.Logic.GameManager;
import com.example.mario_game_ex2.Models.PlayerDetails;
import com.example.mario_game_ex2.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FragmentList extends Fragment implements CallBackOnItemClickListener {

    private AdapterTopTen adapter_topTen;
    private RecyclerView list_topten;
    private static final String SP_KEY_TOPTEN = "SP_KEY_PLAYLIST";
    private int index;
    GameManager gameManager;
    private CallBackTopTenProtocol callBack_topTenProtocol;

    public void setCallBack_topTenProtocol(CallBackTopTenProtocol callBack_topTenProtocol) {
        this.callBack_topTenProtocol = callBack_topTenProtocol;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        gameManager = GameManager.set();

        findViews(view);
        transferDetails();

        initViews();

        return view;
    }

    private void initViews() {

        AdapterTopTen adapter_topTen = new AdapterTopTen(this.getContext(), gameManager.getTopTen().getTopTen(), this::onItemClick);
        list_topten.setLayoutManager(new LinearLayoutManager(this.getContext()));
        list_topten.setAdapter(adapter_topTen);
        list_topten.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (callBack_topTenProtocol != null) {
                    callBack_topTenProtocol.topTenDetails(gameManager.getTopTen().getTopTen().get(index));
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (callBack_topTenProtocol != null) {

                    callBack_topTenProtocol.topTenDetails(gameManager.getTopTen().getTopTen().get(rv.getChildAdapterPosition(getView())));
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

    }

    public void loadTopTenPlayers() {
        ArrayList<PlayerDetails> topTenPlayers = new ArrayList<>();
        MySharedPreferences.init(getContext());
        String topTenJson = MySharedPreferences.getInstance().getString("TOP_TEN_PLAYERS", null);
        if (topTenJson != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<PlayerDetails>>(){}.getType();
            topTenPlayers = gson.fromJson(topTenJson, listType);
        }
    }

    public void transferDetails() {

        MySharedPreferences.init(getContext());
        setListFromJson();
    }

    private void findViews(View view) {

        list_topten = view.findViewById(R.id.list_topten);
    }

    @Override
    public void onItemClick(int position) {
        index = position;
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
}
