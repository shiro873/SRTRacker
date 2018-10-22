package com.avalanche.srtracker.alarmmanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.avalanche.srtracker.model.SrDestinationLocations;
import com.avalanche.srtracker.repository.MrDestLocRepository;
import com.avalanche.srtracker.util.DataSyncUtil;
import com.avalanche.srtracker.util.LocationService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class DataService extends Service {
    DataSyncUtil dataSyncUtil;
    Context context;
    Handler handler;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    LatLng currenrLoc;

    private LocationListener listener;
    private LocationManager locationManager;
    private Location location;
    private double locLat = 0.0;
    private double locLon = 0.0;

    private MrDestLocRepository destLocRepository;

    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds
    private static final long POINT_RADIUS = 1000; // in Meters
    private static final long PROX_ALERT_EXPIRATION = -1;
    private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
    private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";
    private static final String PROX_ALERT_INTENT = "my";
    private static final NumberFormat nf = new DecimalFormat("##.########");

    public DataService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initLocation();
        destLocRepository = new MrDestLocRepository(this.getApplication());
        this.context = this;
        /*handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {


                currenrLoc = new LatLng(0,0);
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location taskResult = task.getResult();
                        try{
                            if(taskResult != null){
                                currenrLoc = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                                dataSyncUtil = new DataSyncUtil(context, currenrLoc);
                            }
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                        }catch (Exception e){
                            recursiveLocationRequest();
                        }
                    }
                });

                //Location location = fusedLocationProviderClient.getLastLocation().getResult();

                handler.postDelayed(this, 30 * 60 * 1000); //now is every 15 minutes
            }

        }, 0);*/

        currenrLoc = new LatLng(0,0);
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location taskResult = task.getResult();
                try{
                    if(taskResult != null){
                        currenrLoc = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                        dataSyncUtil = new DataSyncUtil(context, currenrLoc);
                    }
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                }catch (Exception e){
                    recursiveLocationRequest();
                }
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    public void initLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5 * 60 * 1000);
        locationRequest.setNumUpdates(1);
        if (currenrLoc == null) {
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else {
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currenrLoc = new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude());
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        getLocation();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,listener);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        addPromimity();
    }

    @SuppressLint("MissingPermission")
    public void recursiveLocationRequest(){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location taskResult = task.getResult();
                try{
                    if(taskResult != null){
                        currenrLoc = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                        dataSyncUtil = new DataSyncUtil(context, currenrLoc);
                    }
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                }catch (Exception e){
                    recursiveLocationRequest();
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void addProximityAlert(double latitude, double longitude) {
        Intent intent = new Intent(PROX_ALERT_INTENT);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        locationManager.addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                POINT_RADIUS, // the radius of the central point of the alert region, in meters
                PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );
        IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
        registerReceiver(new ProximityIntentReceiver(), filter);
    }

    private void addPromimity(){
        List<SrDestinationLocations> locations = new ArrayList<>();
        SrDestinationLocations locations1 = new SrDestinationLocations();
        locations1.setLatitude(23.716119);
        locations1.setLongitude(90.399858);
        locations.add(locations1);
        SrDestinationLocations locations2 = new SrDestinationLocations();
        locations2.setLatitude(23.711574);
        locations2.setLongitude(90.399874);
        locations.add(locations2);
        if(locations != null){
            for (SrDestinationLocations item : locations) {
                addProximityAlert(item.getLatitude(), item.getLongitude());
            }
        }
    }

    //location util


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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,listener);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null){
            locLat = location.getLatitude();
            locLon = location.getLongitude();

        }else {

        }
        return new LatLng(locLat, locLon);
    }
}
