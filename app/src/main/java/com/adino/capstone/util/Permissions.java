package com.adino.capstone.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import static com.adino.capstone.util.Constants.REQUEST_CAMERA_PERMISSION;

/**
 * Created by afadinsro on 3/24/18.
 */

public final class Permissions {

    /**
     * Requests for given permissions for the given activity
     * @param activity Given Activity
     * @param permissions Permissions to request
     */
    public static void requestPermissions(Activity activity, String[] permissions,int tag){
        ActivityCompat.requestPermissions(activity, permissions, tag);
    }

    /**
     * Checks if the given permission has been granted
     * @return True if the permission has been granted and false if otherwise.
     */
    public static boolean checkPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }
}
