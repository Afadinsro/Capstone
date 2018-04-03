package com.adino.capstone.util;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.util.Log;

import com.adino.capstone.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.FirebaseDatabase;

import static com.adino.capstone.util.Constants.ERROR_DIALOG_REQUEST;

/**
 * Created by afadinsro on 3/12/18.
 */



public class Capstone extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable offline storage
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
