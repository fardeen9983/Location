package com.example.android.location;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private final int ERROR_SERVICES = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isServicesLatest()){
            init();
        }
    }

    public void init(){
        Button map = findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MapActivity.class);
                startActivity(intent);
            }
        });
    }
    public boolean isServicesLatest() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            //everything's fine
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //fixable error
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_SERVICES);
            dialog.show();
        } else //no can't do anything
            Toast.makeText(this, "You can't make Map requests", Toast.LENGTH_SHORT).show();
        return false;
    }
}
