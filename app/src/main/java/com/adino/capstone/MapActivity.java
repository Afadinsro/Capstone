package com.adino.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MapActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    //Do nothing
                    return true;
                case R.id.navigation_trending:
                    Intent trendingIntent = new Intent(MapActivity.this, TrendingActivity.class);
                    startActivity(trendingIntent);
                    return true;
                case R.id.navigation_capture:
                    Intent captureIntent = new Intent(MapActivity.this, CaptureActivity.class);
                    startActivity(captureIntent);
                    return true;
                case R.id.navigation_reports:
                    Intent reportsIntent = new Intent(MapActivity.this, ReportsActivity.class);
                    startActivity(reportsIntent);
                    return true;
                case R.id.navigation_contacts:
                    Intent contactsIntent = new Intent(MapActivity.this, ContactsActivity.class);
                    startActivity(contactsIntent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_map);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
    }

}
