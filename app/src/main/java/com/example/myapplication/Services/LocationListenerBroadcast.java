package com.example.myapplication.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;


import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LocationListenerBroadcast extends BroadcastReceiver {
    private static final String TAG = "LocationListenerBroadcast";

    public static final String ACTION_PROCESS_UPDATES = "com.example.myapplication.action" + ".PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        int counter= 0;
        if(intent != null){
            Random randomColor = new Random();
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATES.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if(result != null){
                    List<Location> locationsResult = result.getLocations();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                    for (Location loc:locationsResult) {

                        ref.child("location_"+counter).child("lat").setValue(loc.getLatitude());
                        ref.child("location_"+counter).child("lon").setValue(loc.getLongitude());
                        System.out.println("-------------------------------------------------------");

                        counter++;
                    }

                }
            }
        }
    }
}
