package com.example.mohamed.challengeapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mohamed.challengeapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private SupportMapFragment mMapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(30.706680, 31.244771))
                .title("زفتي"));
        marker.setSnippet("Mahmoud");
        marker.showInfoWindow();


        marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(30.465993, 31.184831))
                .title("بنها"));
        marker.setSnippet("Eraky");
        marker.showInfoWindow();


        marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(30.565167, 31.157877))
                .title("قويسنا"));
        marker.setSnippet("Ali");
        marker.showInfoWindow();

        marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(30.544640, 31.268999))
                .title("Kafr Shokr"));
        marker.setSnippet("Nagwa");
        marker.showInfoWindow();
    }
}
