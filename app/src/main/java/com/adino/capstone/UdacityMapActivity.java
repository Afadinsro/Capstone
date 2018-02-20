
package com.adino.capstone;


import android.support.v4.app.FragmentActivity;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

/**
public class UdacityMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map;

    /**
     * Id to identity LOCATION_SERVICE permission request.
     */

/**
import android.annotation.TargetApi;

private static final int REQUEST_LOCATION_SERVICE = 0;

    private Button btnSatellite;
    private Button btnHybrid;
    private Button btnMap;
    private Button btnNewYork;
    private Button btnSeattle;
    private boolean mapReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udacity_map);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnSatellite = (Button) findViewById(R.id.btnSatellite);
        btnSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady){
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            }
        });
        btnHybrid = (Button) findViewById(R.id.btnHybrid);
        btnHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady){
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }
        });
        btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady){
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });
        btnSeattle = (Button) findViewById(R.id.btnSeattle);
        btnSeattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng SEATTLE = new LatLng(47.6204, -122.3491);
                CameraPosition cameraPosition = new CameraPosition(SEATTLE, 12, 0, 65);
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000,null);
            }
        });
        btnNewYork = (Button) findViewById(R.id.btnNewYork);
        btnNewYork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng NEW_YORK = new LatLng(42.7247301, -78.0136943);
                CameraPosition cameraPosition = new CameraPosition(NEW_YORK, 12, 0, 65);
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */

/**
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        init(googleMap);
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean locationEnabled = false;
        // get Location setting
        try {
            locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
        }

        if (!locationEnabled) {
            Snackbar.make(btnSatellite, R.string.location_permission_prompt, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
        }
    }

    public boolean mayAccessLocation(){

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(LOCATION_SERVICE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (shouldShowRequestPermissionRationale(LOCATION_SERVICE)) {
            Snackbar.make(btnSatellite, R.string.location_permission_prompt, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{LOCATION_SERVICE}, REQUEST_LOCATION_SERVICE);
                        }
                    });
        }else {
            requestPermissions(new String[]{LOCATION_SERVICE}, REQUEST_LOCATION_SERVICE);
        }

        return false;
    }

    public void init(GoogleMap googleMap){

        map = googleMap;
        map.setBuildingsEnabled(true);
        // Add a marker in Gbawe and move the camera
        LatLng home = new LatLng(5.582830, -0.307473);
        // Set Ghana's boundaries
        LatLng ghana_SW = new LatLng(5.082787, -2.878441);
        LatLng ghana_NE = new LatLng(11.043421, 0.636050);
        LatLngBounds GHANA = new LatLngBounds(ghana_SW, ghana_NE);

        CameraPosition cameraPosition = new CameraPosition(home, 12, 0, 65);
        map.addMarker(new MarkerOptions().position(home).title("Home"));

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //Apply Ghana's boundary
        //map.moveCamera(CameraUpdateFactory.newLatLngBounds(GHANA, 50));
        // Constrain the camera target to the Adelaide bounds.
        //map.setLatLngBoundsForCameraTarget(GHANA);

        /*map.moveCamera(CameraUpdateFactory.newLatLng(home));
        map.animateCamera(CameraUpdateFactory.zoomTo(13));*/

/**
    }
}
*/