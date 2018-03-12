package com.adino.capstone;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adino.capstone.capture.DetailsActivity;
import com.adino.capstone.reports.ReportsFragment;
import com.adino.capstone.util.BottomNavigationViewHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.adino.capstone.util.Constants.ERROR_DIALOG_REQUEST;
import static com.adino.capstone.util.Constants.REQUEST_CAMERA_PERMISSION;
import static com.adino.capstone.util.Constants.REQUEST_IMAGE_INTENT;
import static com.adino.capstone.util.Constants.REQUEST_VIDEO_INTENT;

public class MainActivity extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener,
        ReportsFragment.OnFragmentInteractionListener, ContactsFragment.OnFragmentInteractionListener,
        TrendingFragment.OnFragmentInteractionListener, OnMapReadyCallback{

    private static final int REQUEST_IMAGE_CAPTURE = 222;
    private static final int CAMERA_REQUEST = 244;
    private static final String TAG = "MainActivity";
    private FirebaseDatabase firebaseDatabase;

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
                    toggleCaptureButtons(View.VISIBLE);
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

    private BottomNavigationView.OnNavigationItemReselectedListener onNavigationItemReselectedListener
            = new BottomNavigationView.OnNavigationItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.navigation_map:
                case R.id.navigation_trending:
                    break;
                case R.id.navigation_capture:
                    toggleCaptureButtons(View.GONE);
                    navigation.setSelectedItemId(currentNavItem);
                    break;
                case R.id.navigation_reports:
                case R.id.navigation_contacts:
                    break;

            }
        }
    };

    private String mCurrentPhotoPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if Google Play Services is working properly
        if (!isGoogleServicesOK()) {
            Toast.makeText(this, "Google Play Service not working properly!", Toast.LENGTH_SHORT).show();
            finish();
        }
        //Initial check for whether camera permission has been granted or not
        setRequestCameraPermission(checkCameraPermission());

        fab_capture_picture = (FloatingActionButton) findViewById(R.id.fab_capture_picture);
        fab_capture_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!camera_permission_granted) {
                    // Request for permission to be granted
                    requestCameraStoragePermission();
                    // Camera permission granted
                    if (camera_permission_granted) {
                        Intent toImageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (toImageCaptureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(toImageCaptureIntent, REQUEST_IMAGE_INTENT);
                        }
                    }
                } else {
                    // Permission granted already
                    Intent toImageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (toImageCaptureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(toImageCaptureIntent, REQUEST_IMAGE_INTENT);
                    }
                }
            }
        });
        fab_capture_video = (FloatingActionButton) findViewById(R.id.fab_capture_video);
        fab_capture_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!camera_permission_granted) {
                    requestCameraStoragePermission();
                    // Camera permission granted
                    if (camera_permission_granted) {
                        Intent toVideoCaptureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        startActivityForResult(toVideoCaptureIntent, REQUEST_VIDEO_INTENT);
                    }
                } else {
                    Intent toVideoCaptureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(toVideoCaptureIntent, REQUEST_VIDEO_INTENT);
                }
            }
        });

        /*
        Bottom navigation initialization
        Default animation is disabled for a better one.
        OnItemSelected and Reselected listeners attached.
        The reselected listener helps to manage reselecting the capture item better.
        After reselecting capture, go back to previously selected nav item
         */
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setOnNavigationItemReselectedListener(onNavigationItemReselectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        /*
         Initialize content view to Trending
         A fragment transaction helps to switch between fragments in the MainActivity.
          */
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Navigate based on calling activity
        Intent callingIntent = getIntent();

        boolean detailsToReports = false; // Navigating from details to reports?
        if (callingIntent.getExtras() != null){
            detailsToReports = callingIntent.getExtras().getBoolean("detailsToReports");
        }
        // Check to make sure its from DetailsActivity
        if (detailsToReports){
            navigation.setSelectedItemId(R.id.navigation_reports); // Set selected nav item to Reports
            fragmentTransaction.replace(currentNavItem, new ReportsFragment()).commitAllowingStateLoss();
        }else{
            Toast.makeText(this, "First time", Toast.LENGTH_SHORT).show();
            navigation.setSelectedItemId(R.id.navigation_trending); // Set selected nav item to Trending
            fragmentTransaction.replace(R.id.content, new TrendingFragment()).commitAllowingStateLoss();
        }

        currentNavItem = navigation.getSelectedItemId(); // Update the selected nav item.
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
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
                // Ensure an image is returned from the capture
                assert extras != null;
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                if (imageBitmap != null) {
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                }
                byte[] byteArray = byteArrayOutputStream.toByteArray(); // Get byte array

                // Save image to File
                File savedImageFile = saveImageToFile(byteArray);
                // Image successfully saved?
                if(savedImageFile != null) {
                    addPicToGallery(savedImageFile);
                    // Navigate to DetailsActivity to add more details to the report
                    Intent goToAddDetailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                    goToAddDetailsIntent.putExtra("image", byteArray); // Add image byte array as extra to the intent
                    startActivity(goToAddDetailsIntent);
                }else{
                    Toast.makeText(this, "Image could not be saved.", Toast.LENGTH_SHORT).show();
                }
            }else if(resultCode == RESULT_CANCELED){
                // If capture was cancelled, select the previously selected nav item.
                navigation.setSelectedItemId(currentNavItem);
            }
        }else if (requestCode == REQUEST_VIDEO_INTENT) {
            if (resultCode == RESULT_OK) {
                // Get Video
            }else if(resultCode == RESULT_CANCELED){
                navigation.setSelectedItemId(currentNavItem);
            }
        }else if(requestCode == 1){

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


    /**
     * Check if Google Play services is available
     * @return True if Google Play Services is available, false otherwise.
     */
    private boolean isGoogleServicesOK(){
        // Check if Google play services is okay
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available == ConnectionResult.SUCCESS){
            //Toast.makeText(this, "Google Play services is OK", Toast.LENGTH_SHORT).show();
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isGoogleServicesOK: An error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        return false;
    }

    /**
     * Saves the given image byte array to file and returns the file
     * @param bytes A byte array representation of the image to save
     * @return The file which the image was saved into, null otherwise
     */
    private File saveImageToFile(byte[] bytes) {
        FileOutputStream outputStream = null;
        File file = null;
        try {
            file = createImageFile();
            outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            Toast.makeText(this, "Image saved to: " + file, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(outputStream != null){
                try{
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }

    /**
     * Creates a file that captured image will be saved in
     * @return Returns the created file
     * @throws IOException Throws IO exception if file creation fails
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.ENGLISH).format(new Date());
        String imageFileName = "PHOTO_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",      /* suffix */
                storageDir         /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /**
     * Adds thegiven image file to the gallery
     * @param file Image file
     */
    private void addPicToGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * Toggles the visibility of the capture buttons
     * @param visibility Visibility to set to
     */
    private void toggleCaptureButtons(int visibility) {
        fab_capture_picture.setVisibility(visibility);
        fab_capture_video.setVisibility(visibility);
    }

    /**
     * Check if Camera permission has been granted
     * @return True if camera permission has been granted and false if otherwise.
     */
    private boolean checkCameraPermission() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests for Camera and Storage permissions
     */
    private void requestCameraStoragePermission(){
        String[] permissions = {android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_PERMISSION);
    }

    /**
     * Setter for camera_permission_granted
     * @param granted Boolean value to set to
     */
    private void setRequestCameraPermission(boolean granted){
        camera_permission_granted = granted;
    }
}
