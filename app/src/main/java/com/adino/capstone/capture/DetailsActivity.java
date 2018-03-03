package com.adino.capstone.capture;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.adino.capstone.MainActivity;
import com.adino.capstone.R;
import com.adino.capstone.model.DisasterCategory;
import com.adino.capstone.model.Report;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    /**
     * Text Fields
     */
    private EditText txtCaption;
    private EditText txtOtherCategory;
    private EditText txtLocation;
    private TextView txtDate;

    /**
     * Report variables
     */
    private String category;
    private String caption;
    private String date;
    private String imageURL = "";
    private String videoURL = "";
    private String location = "";

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
    private String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getLocationPermission();
        //InputMethodManager
        final InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        //Initialize Toggle buttons
        tbtnEarthquake = (ToggleButton)findViewById(R.id.tbtn_earthquake);
        tbtnFire = (ToggleButton)findViewById(R.id.tbtn_fire);
        tbtnFlood = (ToggleButton)findViewById(R.id.tbtn_flood);
        tbtnEpidemic= (ToggleButton)findViewById(R.id.tbtn_epidemic);
        tbtnMeteorological = (ToggleButton)findViewById(R.id.tbtn_meteorological);
        tbtnMotorAccident = (ToggleButton)findViewById(R.id.tbtn_motor);

        txtOtherCategory = (EditText)findViewById(R.id.txt_other);
        radioOther = (RadioButton)findViewById(R.id.radio_other);
        radioOther.setChecked(false);
        radioOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioOtherSelected();
                txtOtherCategory.setEnabled(true);
                assert inputMethodManager != null;
                inputMethodManager.showSoftInput(txtOtherCategory, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        fabSend = (FloatingActionButton) findViewById(R.id.fab_report_submit);

        // Display date
        txtDate = (TextView)findViewById(R.id.txt_date);
        txtDate.setText(getCurrentDate());

        txtCaption = (EditText)findViewById(R.id.txt_report_caption);
        txtCaption.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_CAPTION_LENGTH_LIMIT)});

        txtLocation = (EditText)findViewById(R.id.txt_report_location_words);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mPhotosStorageReference = mFirebaseStorage.getReference().child("photos");
        firebaseDatabase = FirebaseDatabase.getInstance();
        messagesDatabaseReference = firebaseDatabase.getReference().child("reports");

        // Display captured image
        imgReportPic = (ImageView) findViewById(R.id.img_report_pic);
        photo = getIntent().getByteArrayExtra("image");
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        imgReportPic.setImageBitmap(imageBitmap);

        setOnClickListenerFAB(inputMethodManager);
    }

    /**
     * Sets the OnClickListener for the submit FAB
     * @param inputMethodManager A final instance of an InputMethodManager
     */
    private void setOnClickListenerFAB(final InputMethodManager inputMethodManager)    {
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Submit button clicked");

                if(txtCaption.getText().toString().isEmpty()){
                    // No caption entered
                    Snackbar.make(fabSend, "You need to enter a caption...", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Set focus on caption field
                                    txtCaption.setFocusableInTouchMode(true);
                                    txtCaption.requestFocus();
                                    assert inputMethodManager != null;
                                    inputMethodManager.showSoftInput(txtCaption, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }).show();
                }else if(!radioOther.isChecked() && !isAnyCategorySelected()) {
                    // No category selected and other not checked
                    Snackbar.make(fabSend, "You need to select a category...", Snackbar.LENGTH_LONG)
                            .setAction("OK", null).show();
                }else if(radioOther.isChecked() && txtOtherCategory.getText().toString().isEmpty()){
                    // Other checked but no text entered for category
                    Snackbar.make(fabSend, "You need to enter a category...", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Set focus on other category field
                                    txtOtherCategory.setFocusableInTouchMode(true);
                                    txtOtherCategory.requestFocus();
                                    assert inputMethodManager != null;
                                    inputMethodManager.showSoftInput(txtOtherCategory, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }).show();
                }else if(txtLocation.getText().toString().isEmpty()){
                    // No location in words entered
                    Snackbar.make(fabSend, "Enter location in words for effective response...", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Set focus on location field
                                    txtLocation.setFocusableInTouchMode(true);
                                    txtLocation.requestFocus();
                                    assert inputMethodManager != null;
                                    inputMethodManager.showSoftInput(txtLocation, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }).show();
                }else {
                    // All fields validated
                    Snackbar.make(fabSend, "Sending report...", Snackbar.LENGTH_LONG).show();
                    uploadImage();
                }

            }
        });
    }

    /**
     * CHecks if any category has been selected
     * @return True if a category has been selected and false otherwise
     */
    private boolean isAnyCategorySelected() {
        return tbtnFlood.isChecked() || tbtnFire.isChecked() || tbtnMotorAccident.isChecked() ||
                tbtnMeteorological.isChecked() || tbtnEarthquake.isChecked() || tbtnEpidemic.isChecked();
    }

    @Override
    protected void onStart() {
        super.onStart();
        attachToggleStateListeners();
        //attach child event listener
        attachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //attach child event listener
        removeDatabaseReadListener();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    /**
     * Requests for the location permission if it hasn't been granted already
     */
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

    /**
     * Called when a permission request is issued and completed
     * @param requestCode Request code
     * @param permissions Permissions
     * @param grantResults Grant results
     */
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
     * Gets the category of the selected category, if the category is predefined
     * User specified categories in the 'Other' section are retrieved differently
     * @param selectedTbtn The selected toggle button
     * @return A ReportCategory enum
     */
    @org.jetbrains.annotations.Contract(pure = true)
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
     *  Get the current date of the system
     * @return A string representation of the date
     */
    private String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        return df.format(c.getTime());
    }

    /**
     * Returns the caption of the report
     * @return the report's caption
     */
    @NonNull
    private String getCaption(){
        return txtCaption.getText().toString();
    }

    /**
     * Get the current location of the user
     * @return
     */
    private String getLocation(){
        return "";
    }

    /**
     *  Attaches a read listener to the Firebase  database object
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
     * Remove database read listeners
     */
    private void removeDatabaseReadListener(){
        if(childEventListener != null) {
            messagesDatabaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    /**
     * Disables the 'Other' section when a toggle button is selected
     */
    private void disableOtherSection(){
        txtOtherCategory.setEnabled(false);
        radioOther.setChecked(false);
    }

    /**
     * Attaches a toggle state listener to all toggle buttons
     */
    private void attachToggleStateListeners() {
        tbtnFire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbtnFire.setBackgroundResource(R.drawable.ic_flame_checked);
                    toggleAllOthers(tbtnFire);
                    disableOtherSection();
                } else {
                    tbtnFire.setBackgroundResource(R.drawable.ic_flame);
                }
            }
        });
        tbtnFlood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbtnFlood.setBackgroundResource(R.drawable.ic_flood_checked);
                    toggleAllOthers(tbtnFlood);
                    disableOtherSection();
                } else {
                    tbtnFlood.setBackgroundResource(R.drawable.ic_flood);
                }
            }
        });
        tbtnEpidemic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbtnEpidemic.setBackgroundResource(R.drawable.ic_medicines_checked);
                    toggleAllOthers(tbtnEpidemic);
                    disableOtherSection();
                } else {
                    tbtnEpidemic.setBackgroundResource(R.drawable.ic_medicines);
                }
            }
        });
        tbtnEarthquake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbtnEarthquake.setBackgroundResource(R.drawable.ic_earthquake_checked);
                    toggleAllOthers(tbtnEarthquake);
                    disableOtherSection();
                } else {
                    tbtnEarthquake.setBackgroundResource(R.drawable.ic_earthquake);
                }
            }
        });
        tbtnMeteorological.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbtnMeteorological.setBackgroundResource(R.drawable.ic_storm_checked);
                    toggleAllOthers(tbtnMeteorological);
                    disableOtherSection();
                } else {
                    tbtnMeteorological.setBackgroundResource(R.drawable.ic_storm);
                }
            }
        });
        tbtnMotorAccident.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbtnMotorAccident.setBackgroundResource(R.drawable.ic_car_collision_checked);
                    toggleAllOthers(tbtnMotorAccident);
                    disableOtherSection();
                } else {
                    tbtnMotorAccident.setBackgroundResource(R.drawable.ic_car_collision);
                }
            }
        });
    }

    /**
     * Toggle all other toggle buttons as unselected when one is selected
     * This method ensures that only one toggle button is selected
     * @param selected Selected toggle button
     */
    private void toggleAllOthers(ToggleButton selected){
        Log.d(TAG, "toggleAllOthers: called");
        selectedTbtn = selected;
        ToggleButton[] toggleButtons = {tbtnFire, tbtnFlood, tbtnEpidemic, tbtnEarthquake,
                tbtnMotorAccident, tbtnMeteorological};
        for(ToggleButton tbtn: toggleButtons){
            if(tbtn != selected){
                tbtn.setChecked(false);
            }
        }

    }

    /**
     * Toggle all toggle buttons off when other radio button is selected
     */
    private void radioOtherSelected(){
        toggleAllOthers(null);
    }

    /**
     *  Creates an image in Pictures directory of app for saving the captured image
     * @return A file for the image to be stored in
     * @throws IOException Throws this exception if creation of the file fails
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.ENGLISH).format(new Date());
        String imageFileName = "PHOTO_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
     *  Uploads the captured image to Firebase storage using an upload task
     *  On successful upload, a record of the report is uploaded to the database
     *  with all report details including a link to the image in storage
     */
    private void uploadImage(){
        try {
            File imageFile = createImageFile();
            Uri file = Uri.fromFile(imageFile);
            String photoFIle = getIntent().getStringExtra("photoUri");
            StorageReference photoRef = mPhotosStorageReference.child(file.getLastPathSegment());

            // Create file metadata including the content type
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();
            uploadTask = photoRef.putFile(file);
            uploadTask = photoRef.putBytes(photo, metadata);

        }catch (IOException e){
            e.printStackTrace();
        }

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                assert downloadUrl != null;
                imageURL = downloadUrl.toString();

                category = (radioOther.isChecked()) ? txtOtherCategory.getText().toString() :
                        getSelectedCategory(selectedTbtn).toString();
                date = getCurrentDate();
                caption = getCaption();
                Report report = new Report(caption, date, category, imageURL, location);

                messagesDatabaseReference.push().setValue(report);
                Intent backToMainIntent = new Intent(DetailsActivity.this, MainActivity.class);
                backToMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backToMainIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(fabSend, "Image uploaded failed!", Snackbar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fabSend.callOnClick();
                            }
                        }).show();
            }
        });
    }
}
