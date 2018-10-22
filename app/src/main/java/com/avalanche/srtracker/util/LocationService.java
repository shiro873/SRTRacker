package com.avalanche.srtracker.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;


public class LocationService {
    Context context;
    Activity activity;
    private LocationListener listener;
    private LocationManager locationManager;
    private Location location;
    private double locLat = 0.0;
    private double locLon = 0.0;
    private LatLng model;


    public LocationService(Context ctx){
        this.context = ctx;
    }

    @SuppressLint("MissingPermission")
    public LatLng getLocation() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locLat = location.getLatitude();
                locLon = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        };
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return null;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,listener);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null){
            locLat = location.getLatitude();
            locLon = location.getLongitude();

        }else {

        }
        return new LatLng(locLat, locLon);
    }

    public LocationManager getLocationManager(){
        return locationManager;
    }

    @SuppressLint("MissingPermission")
    public Location getLastLocation() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locLat = location.getLatitude();
                locLon = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        };
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return null;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,listener);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null){
            return location;
        }else {
            return null;
        }
    }



}
