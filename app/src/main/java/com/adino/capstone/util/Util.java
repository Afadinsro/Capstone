package com.adino.capstone.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.adino.capstone.R;
import com.adino.capstone.model.Report;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static android.support.v4.content.ContextCompat.startActivity;
import static com.adino.capstone.util.Constants.DEFAULT_ZOOM;
import static com.adino.capstone.util.Constants.REPORTS;
import static com.adino.capstone.util.Constants.REQUEST_GPS_ENABLE;

/**
 * Created by afadinsro on 3/26/18.
 */

public final class Util {
    private static final String TAG = "Util";
    private static LatLng latLng;

    public static LatLng getDeviceLocation(final Context context){
        final LatLng[] latLngs = {null};
        FusedLocationProviderClient locationProviderClient =
                LocationServices.getFusedLocationProviderClient(context);
        try{
            if(Util.isGPSOn(context)) {
                Task location = locationProviderClient.getLastLocation();
                try {
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: Found location.");
                                Location currentLocation = (Location) task.getResult();
                                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                latLngs[0] = latLng;
                            } else {
                                Log.d(TAG, "onComplete: Couldn't find location");
                                Toast.makeText(context, "Unable to get current location.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "getDeviceLocation: " + e.getMessage());
                }
            }else{
                // GPS is off
                Log.d(TAG, "getDeviceLocation: GPS is off");
            }

        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }

        return latLngs[0];
    }

    public static LatLng getLatLng() {
        return latLng;
    }

    public static void setLatLng(LatLng mLatLng) {
        latLng = new LatLng(mLatLng.latitude, mLatLng.longitude);
    }

    /**
     * Checks if GPS is on or off
     * @param context Context
     * @return True if GPS is on and false if otherwise.
     */
    public static boolean isGPSOn(Context context){
        boolean gpsOn = false;
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (NullPointerException e){
            Log.d(TAG, "isGPSOn: Location manager is null.");
        }
        return gpsOn;
    }

    public static void promptGPSOff(final FragmentActivity activity){
        AlertDialog GPSOffDialog = new AlertDialog.Builder(activity)
                .setTitle("GPS turned off")
                .setMessage("GPS is disabled, in order to use the application properly you need to turn on the GPS of your device.\n\nTurn GPS on?")
                .setPositiveButton("Yes, Turn GPS On", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("No, Just Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setIcon(R.drawable.ic_image_black_24dp)
                .create();
        GPSOffDialog.show();
    }

    public static void promptGPSOff(final Activity activity){
        AlertDialog GPSOffDialog = new AlertDialog.Builder(activity)
                .setTitle("GPS turned off")
                .setMessage("GPS is disabled, in order to use the application properly you need to turn on the GPS of your device.\n\nTurn GPS on?")
                .setPositiveButton("Yes, Turn GPS On", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("No, Just Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setIcon(R.drawable.ic_image_black_24dp)
                .create();
        GPSOffDialog.show();
    }

    public static void initDefaultReports() {
        DatabaseReference reportsDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child(REPORTS);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String pushKey = reportsDatabaseReference.push().getKey(); // GET PUSH KEY

        // Default reports
        Report r1 = new Report("Cholera outbreak in Cape Coast", "26-02-2018", "EPIDEMIC",
                "https://firebasestorage.googleapis.com/v0/b/capstone-a7a6f.appspot.com/o/photos%2Ftest%20reports%2FCHOLERA.jpg?alt=media&token=29aec44d-c104-42e1-8d9b-e302c506ed9f",
                "Kotokraba - Cape Coast", -1, 1);
        reportsDatabaseReference.child(userID).child(pushKey).setValue(r1);
        pushKey = reportsDatabaseReference.push().getKey();
        Report r2 = new Report("Explosion at Atomic junction", "14-09-2017", "FIRE",
                "https://firebasestorage.googleapis.com/v0/b/capstone-a7a6f.appspot.com/o/photos%2Ftest%20reports%2Fatomic.jpg?alt=media&token=9f0877ce-82c2-408a-a4f9-15ba19d93c22",
                "Atomic Junction", -1, 1);
        reportsDatabaseReference.child(userID).child(pushKey).setValue(r2);
        pushKey = reportsDatabaseReference.push().getKey();
        Report r3 = new Report("Weija is flooded again!", "28-02-2018", "FLOOD",
                "https://firebasestorage.googleapis.com/v0/b/capstone-a7a6f.appspot.com/o/photos%2Ftest%20reports%2Fflood-default.jpg?alt=media&token=22f953f3-a390-45f0-a7c9-9948641c68db",
                "Weija", -1, 1);
        reportsDatabaseReference.child(userID).child(pushKey).setValue(r3);
        pushKey = reportsDatabaseReference.push().getKey();
        Report r4 = new Report("Accident on N1", "Accident on N1", "MOTOR ACCIDENT",
                "https://firebasestorage.googleapis.com/v0/b/capstone-a7a6f.appspot.com/o/photos%2Ftest%20reports%2Fmotor.jpg?alt=media&token=6d4398da-5fe8-4a54-a4b0-44e0616a6351",
                "N1 highway - Achimota overhead", -1, 1);
        reportsDatabaseReference.child(userID).child(pushKey).setValue(r4);
        pushKey = reportsDatabaseReference.push().getKey();
        Report r5 = new Report("Earthquake at Oyibi", "15-11-2015", "EARTHQUAKE",
                "https://firebasestorage.googleapis.com/v0/b/capstone-a7a6f.appspot.com/o/photos%2Ftest%20reports%2Fearthquake.jpg?alt=media&token=1ee41f86-a92d-426e-9691-270df2a87ecd",
                "Oyibi", -1, 1);
        reportsDatabaseReference.child(userID).child(pushKey).setValue(r5);
        pushKey = reportsDatabaseReference.push().getKey();
        Report r6 = new Report("Rainstorm kills 11 students", "03-06-2016", "METEOROLOGICAL",
                "https://firebasestorage.googleapis.com/v0/b/capstone-a7a6f.appspot.com/o/photos%2Ftest%20reports%2Frainstorm.jpg?alt=media&token=d65e18c0-ebbc-4d23-9146-eacf02ac043e",
                "Akim Asene Methodist School park", -1, 1);
        reportsDatabaseReference.child(userID).child(pushKey).setValue(r6);
    }
}
