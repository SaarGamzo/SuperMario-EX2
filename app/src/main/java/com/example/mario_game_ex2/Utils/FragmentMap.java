package com.example.mario_game_ex2.Utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mario_game_ex2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class FragmentMap extends Fragment {

    private GoogleMap mMap;
    private LatLng latLng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initViews();

        return view;
    }

    private void initViews() {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_FRG_map);
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
            }
        });
    }
    public void zoomOnMap(double lat, double lon, String name) {
        latLng = new LatLng(lat, lon);
        if (latLng != null) {
            MarkerOptions markerOptions = new MarkerOptions();

            markerOptions.position(latLng);
            markerOptions.title(name + "'s record location");
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            mMap.addMarker(markerOptions);
        }
    }
}
