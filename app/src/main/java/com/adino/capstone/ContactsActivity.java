package com.adino.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class ContactsActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    Intent mapIntent = new Intent(ContactsActivity.this, MapActivity.class);
                    startActivity(mapIntent);
                    return true;
                case R.id.navigation_trending:
                    Intent trendingIntent = new Intent(ContactsActivity.this, TrendingActivity.class);
                    startActivity(trendingIntent);
                    return true;
                case R.id.navigation_capture:
                    Intent captureIntent = new Intent(ContactsActivity.this, TrendingActivity.class);
                    startActivity(captureIntent);
                    return true;
                case R.id.navigation_reports:
                    Intent reportsIntent = new Intent(ContactsActivity.this, ReportsActivity.class);
                    startActivity(reportsIntent);
                    return true;
                case R.id.navigation_contacts:
                    //Do nothing
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_contacts);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
    }

}
