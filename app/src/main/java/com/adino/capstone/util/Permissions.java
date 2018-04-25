package com.adino.capstone.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static com.adino.capstone.util.Constants.REQUEST_CAMERA_PERMISSION;

/**
 * Created by afadinsro on 3/24/18.
 */

public final class Permissions {
    private static final String TAG = "Permissions";

    /**
     * Requests for given permissions for the given activity
     * @param activity Given Activity
     * @param permissions Permissions to request
     */
    public static void requestPermissions(Activity activity, String[] permissions, int tag){
        Log.d(TAG, "requestPermissions: Requesting for permissions...");
        ActivityCompat.requestPermissions(activity, permissions, tag);
    }

    /**
     * Checks if the given permission has been granted
     * @return True if the permission has been granted and false if otherwise.
     */
    public static boolean checkPermission(Context context, String permission) {
        Log.d(TAG, "checkPermission: Checking for permissions...");
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }
}
