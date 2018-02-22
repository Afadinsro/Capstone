package com.adino.capstone;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener,
        ReportsFragment.OnFragmentInteractionListener, ContactsFragment.OnFragmentInteractionListener,
        TrendingFragment.OnFragmentInteractionListener, OnMapReadyCallback{

    private static final int REQUEST_IMAGE_CAPTURE = 222;
    private static final int CAMERA_REQUEST = 244;
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 900;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_map:
                    fragmentTransaction.replace(R.id.content, new MapFragment()).commit();
                    return true;
                case R.id.navigation_trending:
                    fragmentTransaction.replace(R.id.content, new TrendingFragment()).commit();
                    return true;
                case R.id.navigation_capture:

                    return true;
                case R.id.navigation_reports:
                    fragmentTransaction.replace(R.id.content, new ReportsFragment()).commit();
                    return true;
                case R.id.navigation_contacts:
                    fragmentTransaction.replace(R.id.content, new ContactsFragment()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check if Google Play Services is working properly
        if(!isGoogleServicesOK()){
            Toast.makeText(this, "Google Play Service not working properly!", Toast.LENGTH_SHORT).show();
            finish();
        }
        
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_trending); // Set selected nav item to Trending
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        // Initialize content view to Trending
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, new TrendingFragment()).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                if (imageBitmap != null) {
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                }
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                //Intent goToAddDetailsIntent = new Intent(MainActivity.this, ReportDetailsActivity.class);
                //goToAddDetailsIntent.putExtra("image", byteArray);
                //startActivity(goToAddDetailsIntent);
            }
        }
    }

    private boolean isGoogleServicesOK(){
        Log.d(TAG, "isGoogleServicesOK: checking Google Play Services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isGoogleServicesOK: Google Play services is OK");
            //Toast.makeText(this, "Google Play services is OK", Toast.LENGTH_SHORT).show();
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isGoogleServicesOK: An error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        return false;
    }
}
