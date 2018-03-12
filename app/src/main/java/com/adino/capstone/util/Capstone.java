package com.adino.capstone.util;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

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
