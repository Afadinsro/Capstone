package com.adino.capstone.util;

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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static android.support.v4.content.ContextCompat.startActivity;
import static com.adino.capstone.util.Constants.DEFAULT_ZOOM;
import static com.adino.capstone.util.Constants.REQUEST_GPS_ENABLE;

/**
 * Created by afadinsro on 3/26/18.
 */

public final class Util {
    private static final String TAG = "Util";
    private static LatLng latLng;

    public static void getDeviceLocation(final Context context){
        FusedLocationProviderClient locationProviderClient =
                LocationServices.getFusedLocationProviderClient(context);
        try{

            Task location = locationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: Found location.");
                        Location currentLocation = (Location)task.getResult();
                        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        setLatLng(latLng);
                    }else{
                        Log.d(TAG, "onComplete: Couldn't find location");
                        Toast.makeText(context, "Unable to get current location.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            location.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }


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
                        activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_GPS_ENABLE);
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
}
