package com.adino.disasteraide.capture;

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
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.adino.disasteraide.MainActivity;
import com.adino.disasteraide.R;
import com.adino.disasteraide.map.PlaceAutocompleteAdapter;
import com.adino.disasteraide.model.DisasterCategory;
import com.adino.disasteraide.model.Report;
import com.adino.disasteraide.util.Permissions;
import com.adino.disasteraide.util.Util;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.adino.disasteraide.util.Constants.DETAILS_TO_REPORTS;
import static com.adino.disasteraide.util.Constants.IMAGE_BYTE_ARRAY;
import static com.adino.disasteraide.util.Constants.IMAGE_FILE_ABS_PATH;
import static com.adino.disasteraide.util.Constants.PHOTOS;
import static com.adino.disasteraide.util.Constants.PUSHED_REPORT_KEY;
import static com.adino.disasteraide.util.Constants.REPORTS;
import static com.adino.disasteraide.util.Constants.REQUEST_GPS_ENABLE;
import static com.adino.disasteraide.util.Constants.REQUEST_LOCATION_PERMISSION;
import static com.adino.disasteraide.util.Constants.UPLOAD_MEDIA_TAG;
import static com.adino.disasteraide.util.Constants.WORLD_LAT_LNG_BOUNDS;

public class DetailsActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.OnConnectionFailedListener {

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
    private DatabaseReference reportsDatabaseReference;
    FirebaseJobDispatcher jobDispatcher;

    /**
     * Text Fields
     */
    private EditText txtCaption;
    private EditText txtOtherCategory;
    private EditText txtLocation;
    private AutoCompleteTextView txtAutoLocation;
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
    private double latitude;
    private double longitude;

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
    private File imageFile;
    private String mCurrentPhotoPath;
    private String key;

    private GeoDataClient geoDataClient;
    private PlaceAutocompleteAdapter adapter;

    private Context context;
    private boolean isGPSOn = false;
    private boolean locationPermissionGranted = false;
    private LatLng latLng;
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = getApplicationContext();

        /*
        Location permissions
         */
        String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        // Check if location permissions have been granted
        if(Permissions.checkPermission(context, permissions[0])){
            if(Permissions.checkPermission(context, permissions[1])){
                locationPermissionGranted = true;
            }else {
                // Ask for location permission if not granted already
                Log.d(TAG, "onCreate: Requesting for permission");
                //ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_PERMISSION);
                Permissions.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION);
            }
        }else {
            // Ask for location permission if not granted already
            //ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_PERMISSION);
            Permissions.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION);
        }
        initViews();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mPhotosStorageReference = mFirebaseStorage.getReference().child(PHOTOS);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reportsDatabaseReference = firebaseDatabase.getReference().child(REPORTS);
        jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getBaseContext()));

    }

    private void initViews() {
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
        setOnClickListenerFAB(inputMethodManager);

        // Display date
        txtDate = (TextView)findViewById(R.id.txt_date);
        txtDate.setText(getCurrentDate());

        txtCaption = (EditText)findViewById(R.id.txt_report_caption);
        txtCaption.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_CAPTION_LENGTH_LIMIT)});

        txtAutoLocation = (AutoCompleteTextView)findViewById(R.id.txt_report_location_words);
        initLocationAutoComplete();

        // Display captured image
        imgReportPic = (ImageView) findViewById(R.id.img_report_pic);
        photo = getIntent().getByteArrayExtra(IMAGE_BYTE_ARRAY);
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        imgReportPic.setImageBitmap(imageBitmap);
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
                }else if(txtAutoLocation.getText().toString().isEmpty()){
                    // No location in words entered
                    Snackbar.make(fabSend, "Enter location in words for effective response...", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Set focus on location field
                                    txtAutoLocation.setFocusableInTouchMode(true);
                                    txtAutoLocation.requestFocus();
                                    assert inputMethodManager != null;
                                    inputMethodManager.showSoftInput(txtAutoLocation, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }).show();
                }else {
                    // All fields validated
                    Log.d(TAG, "onClick: All fields validated");
                    Snackbar.make(fabSend, "Sending report...", Snackbar.LENGTH_LONG).show();

                    Intent callingIntent = getIntent();

                    photo = callingIntent.getByteArrayExtra(IMAGE_BYTE_ARRAY);
                    imageFile = saveImageToFile(photo);
                    String pushKey = uploadReport();

                    // TODO upload image in background
                    // Create a new dispatcher using the Google Play driver.
                    Bundle jobParameters = new Bundle();
                    /*
                    Add Job parameters
                    1. PUSHED_REPORT_KEY - the key generated using the push() method. This will be
                    used to update the imageURL of the report when image is successfully uploaded online
                    2. IMAGE_FILE_ABS_PATH - Absolute path of the image stored on disk
                     */
                    jobParameters.putString(PUSHED_REPORT_KEY, pushKey);
                    jobParameters.putString(IMAGE_FILE_ABS_PATH, mCurrentPhotoPath);
                    Job uploadJob = createUploadMediaJob(jobDispatcher, jobParameters);


                    jobDispatcher.mustSchedule(uploadJob);
                    Log.d(TAG, "onClick: Job scheduled");
                    //uploadImage();

                    // TODO navigate to reports immediately with the image file and a pending status
                    Intent backToReportsIntent = new Intent(DetailsActivity.this, MainActivity.class);
                    backToReportsIntent.putExtra("detailsToReports", true);
                    backToReportsIntent.putExtra(PUSHED_REPORT_KEY, pushKey);
                    backToReportsIntent.putExtra(IMAGE_FILE_ABS_PATH, mCurrentPhotoPath);
                    backToReportsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(backToReportsIntent);
                }
            }
        });
    }

    /**
     * Upload report to FirebaseDatabase with imageURL as file location on disk
     * imageURL will be changed immediately file is successfully uploaded to storage in background
     * @return key for the uploaded report
     */
    private String uploadReport() {
        // Initial image url is the location of the file on disk
        // This will be changed when the
        String imageURL = Uri.fromFile(imageFile).toString();

        category = (radioOther.isChecked()) ? txtOtherCategory.getText().toString() :
                getSelectedCategory(selectedTbtn).toString();
        date = getCurrentDate();
        caption = getCaption();
        location = getLocationInWords();
        Report report = new Report(caption, date, category, imageURL, location);
        String pushKey = reportsDatabaseReference.push().getKey(); // GET PUSH KEY
        reportsDatabaseReference.child(userID).child(pushKey).setValue(report);
        return pushKey;
    }


    /**
     * Creates a new job using the FirebaseJobDispatcher and the job parameters given.
     * @param dispatcher FirebaseJobDispatcher
     * @param jobParameters Job parameters
     * @return Job
     */
    private Job createUploadMediaJob(FirebaseJobDispatcher dispatcher, Bundle jobParameters) {
        Log.d(TAG, "createUploadMediaJob: Creating job...");
        return dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(UploadMediaService.class)
                // uniquely identifies the job
                .setTag(UPLOAD_MEDIA_TAG)
                // add parameters
                .setExtras(jobParameters)
                // one-off job
                .setRecurring(false)
                // don't persist past a device reboot
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // start ASAP
                .setTrigger(Trigger.executionWindow(0,0))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with linear backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                    // run on any network
                    Constraint.ON_ANY_NETWORK
                )
                .build();
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
    protected void onResume() {
        super.onResume();
        isGPSOn = Util.isGPSOn(context);
        if(!isGPSOn){
            Util.promptGPSOff(DetailsActivity.this);
        }else {
            // Get device GPS location
            latLng = Util.getDeviceLocation(context);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //attach child event listener
        removeDatabaseReadListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
            case REQUEST_LOCATION_PERMISSION:
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
     * Get the current location of the user in words
     * @return Location in words
     */
    private String getLocationInWords(){
        return txtAutoLocation.getText().toString();
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
                    Toast.makeText(DetailsActivity.this, "Error occurred! Report not sent!", Toast.LENGTH_SHORT).show();
                }
            };
            reportsDatabaseReference.addChildEventListener(childEventListener);
        }
    }

    /**
     * Remove database read listeners
     */
    private void removeDatabaseReadListener(){
        if(childEventListener != null) {
            reportsDatabaseReference.removeEventListener(childEventListener);
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
            //uploadTask = photoRef.putFile(file);
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
                location = getLocationInWords();
                Report report = new Report(caption, date, category, imageURL, location);

                reportsDatabaseReference.push().setValue(report);
                // Get push key
                String key = reportsDatabaseReference.getKey();
                Intent backToReportsIntent = new Intent(DetailsActivity.this, MainActivity.class);
                backToReportsIntent.putExtra(DETAILS_TO_REPORTS, true);
                backToReportsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backToReportsIntent);
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

    private void initLocationAutoComplete(){

        // Initialize GeoDataClient
        geoDataClient = Places.getGeoDataClient(context, null);

        // Initialize Places autocomplete adapter
        adapter = new PlaceAutocompleteAdapter(context, geoDataClient,
                WORLD_LAT_LNG_BOUNDS, null);
        // Set autocomplete edit text view adapter
        txtAutoLocation.setAdapter(adapter);

        txtAutoLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG, "onEditorAction: Called.");
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){


                }
                return false;
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();
        if (errorCode == ConnectionResult.API_UNAVAILABLE){
            Snackbar.make(txtAutoLocation, "Google Places API unavailable. Autocompletion is unavailable.",
                    Snackbar.LENGTH_SHORT);
        }else if (errorCode == ConnectionResult.NETWORK_ERROR){
            Snackbar.make(txtAutoLocation, "Network error. Check your internet connection. Autocompletion is unavailable.",
                    Snackbar.LENGTH_SHORT);
        }else if (errorCode == ConnectionResult.CANCELED){
            Snackbar.make(txtAutoLocation, "Connection cancelled. Check your internet connection. Autocompletion is unavailable.",
                    Snackbar.LENGTH_SHORT);
        }else if (errorCode == ConnectionResult.TIMEOUT){
            Snackbar.make(txtAutoLocation, "Connection timed out. Check your internet connection and try again. Autocompletion is unavailable.",
                    Snackbar.LENGTH_SHORT);
        }
    }
}
