package com.avalanche.srtracker.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.avalanche.srtracker.model.LocationModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class LocationUtil {
    Context context;
    Activity activity;
    private LocationListener listener;
    private LocationManager locationManager;
    private Location location;
    private double locLat = 0.0;
    private double locLon = 0.0;
    private LocationModel model;
    private LatLng latLng;


    public LocationUtil(Context ctx){
        this.context = ctx;
    }

    /*@SuppressLint("MissingPermission")
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
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            return latLng;
        }else {
            return null;
        }
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
    }*/

    public double getDistance(LocationModel oldData, LocationModel newData) {
        if(oldData.getLatitude() == 0){
            return 0;
        }
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(oldData.getLatitude());
        startPoint.setLongitude(oldData.getLongitude());

        Location endPoint=new Location("locationB");
        endPoint.setLatitude(newData.getLatitude());
        endPoint.setLongitude(newData.getLongitude());

        double distance=startPoint.distanceTo(endPoint);
        /*float[] results = new float[1];
        Location.distanceBetween(oldData.getLocLat(), oldData.getLocLon(),
                newData.getLocLat(), newData.getLocLon(), results);
        float distance = results[0];*/

        return distance/1000;
    }

    public String getLocationName(double lat, double lon){
        String addressStr = "";
        try{
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> myList = geocoder.getFromLocation(lat, lon, 1);
            Address address = (Address) myList.get(0);
            addressStr = address.getAddressLine(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        return addressStr;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }

    public double getDistance(double latitude, double longitude, double latitude1, double longitude1) {
        if(latitude == 0){
            return 0;
        }
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(latitude);
        startPoint.setLongitude(longitude);

        Location endPoint=new Location("locationB");
        endPoint.setLatitude(latitude1);
        endPoint.setLongitude(longitude1);

        double distance=startPoint.distanceTo(endPoint);
        /*float[] results = new float[1];
        Location.distanceBetween(oldData.getLocLat(), oldData.getLocLon(),
                newData.getLocLat(), newData.getLocLon(), results);
        float distance = results[0];*/

        return distance/1000;
    }
}
