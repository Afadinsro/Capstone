package com.adino.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class ReportsActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    Intent mapIntent = new Intent(ReportsActivity.this, MapActivity.class);
                    startActivity(mapIntent);
                    return true;
                case R.id.navigation_trending:
                    Intent trendingIntent = new Intent(ReportsActivity.this, MainActivity.class);
                    startActivity(trendingIntent);
                    return true;
                case R.id.navigation_capture:
                    Intent captureIntent = new Intent(ReportsActivity.this, CaptureActivity.class);
                    startActivity(captureIntent);
                    return true;
                case R.id.navigation_reports:
                    //Do nothing
                    return true;
                case R.id.navigation_contacts:
                    Intent contactsIntent = new Intent(ReportsActivity.this, ContactsActivity.class);
                    startActivity(contactsIntent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_reports);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
    }

}
