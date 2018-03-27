package com.adino.capstone.util;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static com.adino.capstone.util.Constants.DEFAULT_ZOOM;

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
}
