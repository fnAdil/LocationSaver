package com.example.GoogleBackgroundServiceApp;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
    SharedPreferences mPref;
    Button btn_start;
    TextView tv_address;
    SharedPreferences.Editor medit;
    Double latitude,longitude;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_start =  findViewById(R.id.button2);
        tv_address= findViewById(R.id.textView_1);
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();
        System.out.println("amınagoys1");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boolean_permission) {
                    System.out.println("gs1116");

                    if (mPref.getString("service", "").matches("")) {
                        medit.putString("service", "service").commit();
                        System.out.println("gs1117");

                        Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                        System.out.println("gs1118");
                        startService(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Service is already running", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enable the gps", Toast.LENGTH_SHORT).show();
                }

            }
        });

        fn_permission();
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION

                        },
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("11111111111111");
            if (intent!=null){
                final String action = intent.getAction();
                latitude = Double.valueOf(intent.getStringExtra("latutide"));
                longitude = Double.valueOf(intent.getStringExtra("longitude"));


                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                ref.child("asd").setValue(2);
                SimpleDateFormat shape = new SimpleDateFormat("y/M/d h:m:s");
                Date date = new Date();
                ref.child("location").child(shape.format(date)).child("lat").setValue(latitude);
                ref.child("location").child(shape.format(date)).child("lon").setValue(longitude);

                System.out.println("-------------------------------------------------------");
                tv_address.setText(latitude.toString());

            }
/*
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();


            SimpleDateFormat shape = new SimpleDateFormat("y/M/d h:m:s");
            Date date = new Date();
            ref.child("location").child(shape.format(date)).child("lat").setValue(intent.getStringExtra("latutide"));
            ref.child("location").child(shape.format(date)).child("lon").setValue(intent.getStringExtra("longitude"));

            System.out.println("-------------------------------------------------------");

            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));
            tv_address.getText();

*/
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("amınagoys3");
     /*
     * 11111
     * 11111
     * */
        registerReceiver(broadcastReceiver, new IntentFilter( GoogleService.str_receiver));
        System.out.println("amınagoys33333333333");

    }

    @Override
    protected void onPause() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        super.onPause();
        unregisterReceiver(broadcastReceiver);

    }

}