package com.adino.capstone.capture;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ToggleButton;

import com.adino.capstone.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    //TODO: Add autofill for entering location in words.

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 111;
    private static final String TAG = "DetailsActivity";
    private boolean mLocationPermissionsGranted = false;
    public static final int DEFAULT_CAPTION_LENGTH_LIMIT = 1000;

    /**
     * Toggle Buttons
     */
    private ToggleButton tbtnFire;
    private ToggleButton tbtnEarthquake;
    private ToggleButton tbtnFlood;
    private ToggleButton tbtnMeteorological;
    private ToggleButton tbtnMotorAccident;
    private ToggleButton tbtnEpidemic;
    private ToggleButton selectedTbtn;

    /**
     * Firebase variables
     */
    private FirebaseStorage mFirebaseStorage;
    private ChildEventListener childEventListener;
    private StorageReference mPhotosStorageReference;
    private UploadTask uploadTask;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference messagesDatabaseReference;

    private EditText txtCaption;

    /**
     * Buttons
     */
    private FloatingActionButton fabSend;

    /**
     * Radio Button
     */
    private RadioButton radioOther;

    private ImageView imgReportPic;
    private byte[] photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getLocationPermission();

        //Initialize Toggle buttons
        tbtnEarthquake = (ToggleButton)findViewById(R.id.tbtn_earthquake);
        tbtnFire = (ToggleButton)findViewById(R.id.tbtn_fire);
        tbtnFlood = (ToggleButton)findViewById(R.id.tbtn_flood);
        tbtnEpidemic= (ToggleButton)findViewById(R.id.tbtn_epidemic);
        tbtnMeteorological = (ToggleButton)findViewById(R.id.tbtn_meteorological);
        tbtnMotorAccident = (ToggleButton)findViewById(R.id.tbtn_motor);

        radioOther = (RadioButton)findViewById(R.id.radio_other);
        radioOther.setChecked(false);

        fabSend = (FloatingActionButton) findViewById(R.id.fab_report_submit);
        fabSend.setEnabled(false);

        txtCaption = (EditText)findViewById(R.id.txt_report_caption);
        // Enable Send button when there's text to send
        txtCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                fabSend.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    fabSend.setEnabled(true);
                } else {
                    fabSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        txtCaption.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_CAPTION_LENGTH_LIMIT)});

        mFirebaseStorage = FirebaseStorage.getInstance();
        mPhotosStorageReference = mFirebaseStorage.getReference().child("photos");
        firebaseDatabase = FirebaseDatabase.getInstance();
        messagesDatabaseReference = firebaseDatabase.getReference().child("reports");

        imgReportPic = (ImageView) findViewById(R.id.img_report_pic);
        photo = getIntent().getByteArrayExtra("image");
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        imgReportPic.setImageBitmap(imageBitmap);


    }

    @Override
    protected void onStart() {
        super.onStart();
        attachToggleStateListeners();
        //Set Automobile by default
        tbtnFire.setChecked(true);
        //attach child event listener
        attachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //attach child event listener
        removeDatabaseReadListener();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this,
                FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                //initMap();
            }else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);

            }
        }else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0){
                    for(int g: grantResults) {
                        if (g != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: Permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: Permission granted");
                    mLocationPermissionsGranted = true;
                }
        }
    }

    /**
     *
     * @param selectedTbtn
     * @return
     */
    private DisasterCategory getSelectedCategory(ToggleButton selectedTbtn){
        DisasterCategory disasterCategory = null;
        if(selectedTbtn != null){
            if(selectedTbtn == tbtnFire){
                disasterCategory = DisasterCategory.FIRE;
            }else if(selectedTbtn == tbtnEarthquake){
                disasterCategory = DisasterCategory.EARTHQUAKE;
            }else if(selectedTbtn == tbtnMeteorological){
                disasterCategory = DisasterCategory.METEOROLOGICAL;
            }else if(selectedTbtn == tbtnMotorAccident){
                disasterCategory = DisasterCategory.MOTOR_ACCIDENT;
            }else if(selectedTbtn == tbtnEpidemic){
                disasterCategory = DisasterCategory.EPIDEMIC;
            }else if(selectedTbtn == tbtnFlood){
                disasterCategory = DisasterCategory.FLOOD;
            }
        }
        return disasterCategory;
    }

    /**
     *
     * @return
     */
    private String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        return df.format(c.getTime());
    }

    /**
     *
     * @return
     */
    private String getCaption(){
        return txtCaption.getText().toString();
    }

    /**
     *
     * @return
     */
    private String getLocation(){
        return "";
    }

    /**
     *
     */
    private void attachDatabaseReadListener(){
        if(childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    //mMessageAdapter.add(friendlyMessage);
                    //go back to home page if report is submitted successfully

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            messagesDatabaseReference.addChildEventListener(childEventListener);
        }
    }

    /**
     *
     */
    private void removeDatabaseReadListener(){
        if(childEventListener != null) {
            messagesDatabaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    /**
     *
     */
    private void attachToggleStateListeners() {
        tbtnFire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //tbtnFire.setBackgroundResource(R.drawable.ic_auto_checked);
                    toggleAllOthers(tbtnFire);
                } else {
                    //tbtnFire.setBackgroundResource(R.drawable.ic_auto_unchecked);
                }
            }
        });
    }

    /**
     * Toggle all other toggle buttons as unselected when one is selected
     * This method ensures that only one toggle button is selected
     * @param selected
     */
    private void toggleAllOthers(ToggleButton selected){
        Log.d(TAG, "toggleAllOthers: called");
        selectedTbtn = selected;
        ToggleButton[] toggleButtons = {tbtnFire, tbtnFire, tbtnEpidemic, tbtnEarthquake,
                tbtnMotorAccident, tbtnMeteorological};
        for(ToggleButton tbtn: toggleButtons){
            if(tbtn != selected){
                tbtn.setChecked(false);
            }
        }

    }

    private void radioOtherSelected(){
        toggleAllOthers(null);
    }
}
