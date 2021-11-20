package com.example.GoogleBackgroundServiceApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class BackgroundLocationBroadcast extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATES = "com.example.myapplication.action" + ".PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATES.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if(result != null){
                    List<Location> locationsResult = result.getLocations();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                    SimpleDateFormat shape = new SimpleDateFormat("y/M/d h:m:s");
                    Date date = new Date();

                    for (Location loc:locationsResult) {
                        ref.child("location").child(shape.format(date)).child("lat").setValue(loc.getLatitude());
                        ref.child("location").child(shape.format(date)).child("lon").setValue(loc.getLongitude());

                        System.out.println("-------------------------------------------------------");
                    }
                }
            }
        }
    }
}
