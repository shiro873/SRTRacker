package com.avalanche.srtracker.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;

public class LocationUtils {
    private Context context;
    private Activity activity;
    private static LocationUtils INSTANCE = null;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private double latitude = 0.0;
    private double longitude = 0.0;

    public LocationUtils(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        //fusedLocationProviderClient.setP
    }

    public static LocationUtils getINSTANCE(Context context, Activity activity) {
        if (INSTANCE == null) {
            INSTANCE = new LocationUtils(context, activity);
        }
        return INSTANCE;
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                });
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistance(double oldLatitude, double oldLongitude, double newLatitude, double newLongitude) {
        if(oldLatitude == 0.0 && oldLongitude == 0.0){
            return 0;
        }
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(oldLatitude);
        startPoint.setLongitude(oldLongitude);

        Location endPoint=new Location("locationB");
        endPoint.setLatitude(newLatitude);
        endPoint.setLongitude(newLongitude);

        double distance=startPoint.distanceTo(endPoint);

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

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
}
