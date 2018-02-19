package com.adino.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class CaptureActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    Intent mapIntent = new Intent(CaptureActivity.this, MapActivity.class);
                    startActivity(mapIntent);
                    return true;
                case R.id.navigation_trending:
                    Intent trendingIntent = new Intent(CaptureActivity.this, MainActivity.class);
                    startActivity(trendingIntent);
                    return true;
                case R.id.navigation_capture:
                    //Do nothing
                    return true;
                case R.id.navigation_reports:
                    Intent reportsIntent = new Intent(CaptureActivity.this, ReportsActivity.class);
                    startActivity(reportsIntent);
                    return true;
                case R.id.navigation_contacts:
                    Intent contactsIntent = new Intent(CaptureActivity.this, ContactsActivity.class);
                    startActivity(contactsIntent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_capture);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
    }

}
