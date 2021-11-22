package com.example.GoogleBackgroundServiceApp;


import androidx.annotation.NonNull;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
    SharedPreferences mPref;
    Button btn_start;
    TextView textView_1;
    SharedPreferences.Editor medit;
    Double latitude,longitude;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_start =  findViewById(R.id.button2);
        textView_1= findViewById(R.id.textView_1);
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();

        checkPermission();

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });


    }
    public void checkPermission(){
        boolean_permission=!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED);

        if(!boolean_permission){
            textView_1.setText(R.string.permissionMessage);
            Toast.makeText(getApplicationContext(), "Lütfen konum servislerini aktifleştirin", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    0);

        }else{
            Toast.makeText(getApplicationContext(), "Servis çalışır durumda", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), BackgroundLocationService.class);
            startService(intent);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Lütfen izinleri kabul edin", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null){
                latitude = Double.valueOf(intent.getStringExtra("latutide"));
                longitude = Double.valueOf(intent.getStringExtra("longitude"));

                Log.e("latitude: ",latitude+"");
                Log.e("longitude: ",longitude+"");

                Toast.makeText(getApplicationContext(), "Lokasyon: "+latitude+"--"+longitude, Toast.LENGTH_SHORT).show();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                SimpleDateFormat shape = new SimpleDateFormat("y/M/d h:m:s");
                Date date = new Date();
                ref.child("location").child(shape.format(date)).child("lat").setValue(latitude);
                ref.child("location").child(shape.format(date)).child("lon").setValue(longitude);
                textView_1.setText("latitude: "+latitude.toString()+"\tlongitude: "+longitude.toString());

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter( BackgroundLocationService.str_receiver));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);

    }

}
