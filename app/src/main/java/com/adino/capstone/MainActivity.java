package com.adino.capstone;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adino.capstone.capture.DetailsActivity;
import com.adino.capstone.contacts.ContactsFragment;
import com.adino.capstone.map.MapFragment;
import com.adino.capstone.model.User;
import com.adino.capstone.reports.ReportsFragment;
import com.adino.capstone.trending.TrendingFragment;
import com.adino.capstone.util.BottomNavigationViewHelper;
import com.adino.capstone.util.Permissions;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static com.adino.capstone.util.Constants.DETAILS_TO_REPORTS;
import static com.adino.capstone.util.Constants.IMAGE_BYTE_ARRAY;
import static com.adino.capstone.util.Constants.IMAGE_FILE_ABS_PATH;
import static com.adino.capstone.util.Constants.PUSHED_REPORT_KEY;
import static com.adino.capstone.util.Constants.REQUEST_CAMERA_PERMISSION;
import static com.adino.capstone.util.Constants.REQUEST_GPS_ENABLE;
import static com.adino.capstone.util.Constants.REQUEST_IMAGE_INTENT;
import static com.adino.capstone.util.Constants.REQUEST_SIGN_IN;
import static com.adino.capstone.util.Constants.REQUEST_VIDEO_INTENT;
import static com.adino.capstone.util.Constants.USERS;
import static com.adino.capstone.util.Constants.USER_FIELD_SUBSCRIPTIONS;

public class MainActivity extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener,
        ReportsFragment.OnFragmentInteractionListener, ContactsFragment.OnFragmentInteractionListener,
        TrendingFragment.OnFragmentInteractionListener{

    private static final String TAG = "MainActivity";
    private int currentNavItem = -1;

    /**
     *
     */
    private boolean cameraPermissionGranted = false;
    private boolean storagePermissionGranted = false;
    private boolean signedIn = false;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userID = "";
    private String userTopics = "";
    private Bundle instanceState;

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
                    fragmentTransaction.replace(R.id.content, new MapFragment())
                            .addToBackStack(MapFragment.class.getName())
                            .commitAllowingStateLoss();
                    // Set ID to selected
                    currentNavItem = item.getItemId();
                    return true;
                case R.id.navigation_trending:
                    toggleCaptureButtons(View.GONE);
                    fragmentTransaction.replace(R.id.content, new TrendingFragment())
                            .addToBackStack(TrendingFragment.class.getName())
                            .commitAllowingStateLoss();
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
                    fragmentTransaction.replace(R.id.content, new ReportsFragment())
                            .addToBackStack(ReportsFragment.class.getName())
                            .commitAllowingStateLoss();
                    // Set ID to selected
                    currentNavItem = item.getItemId();
                    return true;
                case R.id.navigation_contacts:
                    toggleCaptureButtons(View.GONE);
                    fragmentTransaction.replace(R.id.content, new ContactsFragment())
                            .addToBackStack(ContactsFragment.class.getName())
                            .commitAllowingStateLoss();
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instanceState = savedInstanceState;
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    // User is signed in
                    signedIn = true;
                }else{
                    // User is not signed in
                    signedIn = false;
//                    AuthUI.IdpConfig phoneIdpConfig = new AuthUI.IdpConfig().;
                    startActivityForResult(
                            AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setPrivacyPolicyUrl("https://superapp.example.com/privacy-policy.html")
                            .setTosUrl("https://superapp.example.com/terms-of-service.html")
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                            .setLogo(R.mipmap.ic_launcher_round)
                            .setAvailableProviders(
                                    /*
                                    Use Arrays.asList() for multiple providers
                                    Use Collections.singletonList() for single provider
                                     */
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.PhoneBuilder()
                                                    .setDefaultCountryIso("gh")
                                                    .build(),
                                            new AuthUI.IdpConfig.GoogleBuilder()
                                                    .build()
                                    )
                            ).build(),
                            REQUEST_SIGN_IN
                    );
                }
            }
        };
        signedIn = FirebaseAuth.getInstance().getCurrentUser() != null;
        if(signedIn) {
            userID = firebaseAuth.getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference(USERS)
                    .child(userID).child(USER_FIELD_SUBSCRIPTIONS);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            try {
                                userTopics = dataSnapshot.getValue(String.class);
                                Log.d(TAG, "onDataChange: topics: " + userTopics);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("TAG", " it's null.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_INTENT) {
            if (resultCode == RESULT_OK && data != null) {
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

                // Navigate to DetailsActivity to add more details to the report
                Intent goToAddDetailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                goToAddDetailsIntent.putExtra(IMAGE_BYTE_ARRAY, byteArray); // Add image byte array as extra to the intent
                startActivity(goToAddDetailsIntent);

            }else if(resultCode == RESULT_CANCELED){
                // If capture was cancelled, select the previously selected nav item.
                navigation.setSelectedItemId(currentNavItem);
            }
        }else if (requestCode == REQUEST_VIDEO_INTENT) {
            if (resultCode == RESULT_OK) {
                // Get Video
            }else if(resultCode == RESULT_CANCELED){
//                navigation.setSelectedItemId(currentNavItem);
            }
        }else if(requestCode == REQUEST_SIGN_IN){
            if(resultCode == RESULT_OK){
                signedIn = true;
                init();
                Toast.makeText(this, "You are signed in!", Toast.LENGTH_SHORT).show();
            }else if (resultCode == RESULT_CANCELED){
                signedIn = false;
                finish();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CAMERA_PERMISSION:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "You cannot use camera without permission.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    cameraPermissionGranted = true;
                    Intent toImageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (toImageCaptureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(toImageCaptureIntent, REQUEST_IMAGE_INTENT);
                    }
                }
        }
    }


    /**
     * Adds the given image file to the gallery
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
     * Setter for cameraPermissionGranted
     * @param granted Boolean value to set to
     */
    public void setCameraPermissionGranted(boolean granted) {
        this.cameraPermissionGranted = granted;
    }

    public void setStoragePermissionGranted(boolean granted) {
        this.storagePermissionGranted = granted;
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: called");
        super.onPause();
        if(authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
//        currentNavItem = navigation.getSelectedItemId();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: called");
        super.onStart();
//        if(signedIn) {
//            init();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        currentNavItem = navigation.getSelectedItemId();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: called");
        super.onRestart();
//        if(signedIn) {
//            init();
//            navigation.setSelectedItemId(currentNavItem);
        Log.d(TAG, "onRestart: Nav item: " + currentNavItem);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
        if (signedIn) {
            init();
            // Update user's location
        }
    }

    private void init() {
        setContentView(R.layout.activity_main);

        initCameraPermission();

        initCaptureFABs();

        initBottomNav();
        /*
         Initialize content view to Trending
         A fragment transaction helps to switch between fragments in the MainActivity.
          */
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Navigate based on calling activity
        Intent callingIntent = getIntent();

        // Navigating from details to reports?
        if (callingIntent.getExtras() != null) {
            boolean detailsToReports = callingIntent.getExtras().getBoolean(DETAILS_TO_REPORTS);
            String path = callingIntent.getExtras().getString(IMAGE_FILE_ABS_PATH);
            String pushKey = callingIntent.getExtras().getString(PUSHED_REPORT_KEY);
            byte[] photo = callingIntent.getExtras().getByteArray(IMAGE_BYTE_ARRAY);
            // Check to make sure its from DetailsActivity
            if (detailsToReports) {
                navigation.setSelectedItemId(R.id.navigation_reports); // Set selected nav item to Reports
                fragmentTransaction.replace(currentNavItem, ReportsFragment.newInstance(pushKey,
                        path, photo)).commitAllowingStateLoss();
            } else {
                navigation.setSelectedItemId(R.id.navigation_trending); // Set selected nav item to Trending
                fragmentTransaction.replace(R.id.content, new TrendingFragment()).commitAllowingStateLoss();
            }
        } else {
            // No extras in calling intent
            navigation.setSelectedItemId(R.id.navigation_trending); // Set selected nav item to Trending
            fragmentTransaction.replace(R.id.content, new TrendingFragment()).commitAllowingStateLoss();
        }

        currentNavItem = navigation.getSelectedItemId(); // Update the selected nav item.
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("SelectedNav", currentNavItem);
        super.onSaveInstanceState(outState);
    }

    private void initCameraPermission() {
        //Initial check for whether camera permission has been granted or not
        String cameraPermission = android.Manifest.permission.CAMERA;
        boolean granted = Permissions.checkPermission(getApplicationContext(), cameraPermission);
        setCameraPermissionGranted(granted);
    }

    private void initCaptureFABs() {
        fab_capture_picture = (FloatingActionButton) findViewById(R.id.fab_capture_picture);
        fab_capture_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cameraPermissionGranted) {
                    // Request for permission to be granted
                    String[] permissions = {android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };
                    Permissions.requestPermissions(MainActivity.this, permissions, REQUEST_CAMERA_PERMISSION);
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
                if (!cameraPermissionGranted) {
                    // Request for permission to be granted
                    String[] permissions = {android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };
                    Permissions.requestPermissions(MainActivity.this, permissions, REQUEST_CAMERA_PERMISSION);
                } else {
                    Intent toVideoCaptureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (toVideoCaptureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(toVideoCaptureIntent, REQUEST_VIDEO_INTENT);
                    }
                }
            }
        });
    }

    private void initBottomNav() {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sign_out:
                // Sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
