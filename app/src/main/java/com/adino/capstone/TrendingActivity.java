package com.adino.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class TrendingActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    Intent mapIntent = new Intent(TrendingActivity.this, MapActivity.class);
                    startActivity(mapIntent);
                    return true;
                case R.id.navigation_trending:
                    //Do nothing
                    return true;
                case R.id.navigation_capture:
                    Intent captureIntent = new Intent(TrendingActivity.this, CaptureActivity.class);
                    startActivity(captureIntent);
                    return true;
                case R.id.navigation_reports:
                    Intent reportsIntent = new Intent(TrendingActivity.this, ReportsActivity.class);
                    startActivity(reportsIntent);
                    return true;
                case R.id.navigation_contacts:
                    Intent contactsIntent = new Intent(TrendingActivity.this, ContactsActivity.class);
                    startActivity(contactsIntent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_trending);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
    }

}
