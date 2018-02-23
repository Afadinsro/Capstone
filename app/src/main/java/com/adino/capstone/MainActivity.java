package com.adino.capstone;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adino.capstone.capture.CaptureActivity;
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
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_INTENT = 500;
    private static final int REQUEST_VIDEO_INTENT = 200;

    private int currentNavItem;

    /**
     *
     */
    private boolean camera_permission_granted = false;

    private FloatingActionButton fab_capture_picture;
    private FloatingActionButton fab_capture_video;

    private BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_map:
                    toggleCaptureButtons(View.GONE);
                    fragmentTransaction.replace(R.id.content, new MapFragment()).commitAllowingStateLoss();
                    // Set ID to selected
                    currentNavItem = item.getItemId();
                    return true;
                case R.id.navigation_trending:
                    toggleCaptureButtons(View.GONE);
                    fragmentTransaction.replace(R.id.content, new TrendingFragment()).commitAllowingStateLoss();
                    // Set ID to selected
                    currentNavItem = item.getItemId();
                    return true;
                case R.id.navigation_capture:
                    /*Intent toCaptureActivity = new Intent(MainActivity.this, CaptureActivity.class);
                    startActivity(toCaptureActivity);*/
                    if(fab_capture_video.getVisibility() == View.VISIBLE){
                        toggleCaptureButtons(View.GONE);
                    }else {
                        toggleCaptureButtons(View.VISIBLE);
                    }
                    return true;
                case R.id.navigation_reports:
                    toggleCaptureButtons(View.GONE);
                    fragmentTransaction.replace(R.id.content, new ReportsFragment()).commitAllowingStateLoss();
                    // Set ID to selected
                    currentNavItem = item.getItemId();
                    return true;
                case R.id.navigation_contacts:
                    toggleCaptureButtons(View.GONE);
                    fragmentTransaction.replace(R.id.content, new ContactsFragment()).commitAllowingStateLoss();
                    // Set ID to selected
                    currentNavItem = item.getItemId();
                    return true;
            }
            return false;
        }
    };

    private void toggleCaptureButtons(int visibility) {
        fab_capture_picture.setVisibility(visibility);
        fab_capture_video.setVisibility(visibility);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check if Google Play Services is working properly
        if(!isGoogleServicesOK()){
            Toast.makeText(this, "Google Play Service not working properly!", Toast.LENGTH_SHORT).show();
            finish();
        }
        setRequestCameraPermission(checkCameraPermission());

        fab_capture_picture = (FloatingActionButton)findViewById(R.id.fab_capture_picture);
        fab_capture_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!camera_permission_granted) {
                    requestCameraPermission();
                    // Camera permission granted
                    if(camera_permission_granted) {
                        Intent toImageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(toImageCaptureIntent, REQUEST_IMAGE_INTENT);
                    }
                }else{
                    Intent toImageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(toImageCaptureIntent, REQUEST_IMAGE_INTENT);
                }
            }
        });
        fab_capture_video = (FloatingActionButton)findViewById(R.id.fab_capture_video);
        fab_capture_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!camera_permission_granted) {
                    requestCameraPermission();
                    // Camera permission granted
                    if(camera_permission_granted) {
                        Intent toVideoCaptureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        startActivityForResult(toVideoCaptureIntent, REQUEST_VIDEO_INTENT);
                    }
                }else{
                    Intent toVideoCaptureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(toVideoCaptureIntent, REQUEST_VIDEO_INTENT);
                }
            }
        });
        
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_trending); // Set selected nav item to Trending
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        // Initialize content view to Trending
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, new TrendingFragment()).commit();

        currentNavItem = navigation.getSelectedItemId();



    }

    /**
     *
     * @return
     */
    private boolean checkCameraPermission() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_CAMERA_PERMISSION);
    }

    private void setRequestCameraPermission(boolean granted){
        camera_permission_granted = granted;
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
        if (requestCode == REQUEST_IMAGE_INTENT) {
            if (resultCode == RESULT_OK) {
                // Get Image
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
            }else if(resultCode == RESULT_CANCELED){
                navigation.setSelectedItemId(currentNavItem);
            }
        }else if (requestCode == REQUEST_VIDEO_INTENT) {
            if (resultCode == RESULT_OK) {
                // Get Video
            }else if(resultCode == RESULT_CANCELED){
                navigation.setSelectedItemId(currentNavItem);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CAMERA_PERMISSION:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "You cannot use camera without permission.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    camera_permission_granted = true;

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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }
}
