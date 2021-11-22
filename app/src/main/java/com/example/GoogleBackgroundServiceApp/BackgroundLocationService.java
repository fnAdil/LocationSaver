package com.example.GoogleBackgroundServiceApp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class BackgroundLocationService extends Service implements LocationListener {

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private final Handler mHandler = new Handler();
    long notify_interval = 60000;//60 sec
    public static String str_receiver = "service.receiver";
    Intent intent;


    public BackgroundLocationService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);
        intent = new Intent(str_receiver);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void fnGetlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {
            Toast.makeText(getApplicationContext(), "Gps ve İnternet bağlantısı yok!", Toast.LENGTH_LONG).show();
        } else {
            //network var ise
            if (isNetworkEnable) {
                location = null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, this);
                }
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Toast.makeText(getApplicationContext(), "Lokasyon: "+latitude+"--"+longitude, Toast.LENGTH_SHORT).show();
                        Log.e("latitude: ",location.getLatitude()+"");
                        Log.e("longitude: ",location.getLongitude()+"");

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat shape = new SimpleDateFormat("y/M/d h:m:s");
                        Date date = new Date();
                        ref.child("location").child(shape.format(date)).child("lat").setValue(latitude);
                        ref.child("location").child(shape.format(date)).child("lon").setValue(longitude);

                        fnUpdate(location);
                    }else{
                       Toast.makeText(getApplicationContext(), "Lokasyon bilgisine erişilemiyor!", Toast.LENGTH_SHORT).show();
                       Log.e("uyarı: ","Lokasyona bilgisi yok");
                    }
                }

            }
            //network yok fakat gps var ise
            if (isGPSEnable&&!(isNetworkEnable)){
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,60000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location!=null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.e("latitude: ",location.getLatitude()+"");
                        Log.e("longitude: ",location.getLongitude()+"");

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat shape = new SimpleDateFormat("y/M/d h:m:s");
                        Date date = new Date();
                        ref.child("location").child(shape.format(date)).child("lat").setValue(latitude);
                        ref.child("location").child(shape.format(date)).child("lon").setValue(longitude);

                        fnUpdate(location);
                    }
                }
            }
        }

    }
    //inner class
    private class TimerTaskToGetLocation extends TimerTask{
        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    fnGetlocation();
                }
            });

        }
    }
    //lokasyonu güncelle
    private void fnUpdate(Location location){
        intent.putExtra("latutide",location.getLatitude()+"");
        intent.putExtra("longitude",location.getLongitude()+"");
        sendBroadcast(intent);
    }


}
