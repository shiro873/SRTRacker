package com.avalanche.srtracker.manager;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.avalanche.srtracker.db.LocationsDb;
import com.avalanche.srtracker.model.SrLocation;
import com.avalanche.srtracker.model.User;
import com.avalanche.srtracker.util.BatteryUtils;
import com.avalanche.srtracker.util.LocationUtils;
import com.google.gson.Gson;

import java.util.List;

import androidx.work.Data;
import androidx.work.Worker;

public class LocationWorker extends Worker {
    private Context context;
    private LocationUtils locationUtils;
    private BatteryUtils batteryUtils;
    private SrLocation location;
    private LocationsDb db;
    private final String RESULT_TAG = "location";

    public LocationWorker(@NonNull Context context, @NonNull Activity activity){
        this.context = context;
        locationUtils = new LocationUtils(context, activity);
        batteryUtils = new BatteryUtils();
        location = new SrLocation();
        db = LocationsDb.getDatabase(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        setSrLocation();
        Gson gson = new Gson();
        String jsonData = gson.toJson(location);
        boolean hasSent = sendData(jsonData);
        if(hasSent){
            return Result.SUCCESS;
        }else {
            saveDataOffline(jsonData);
            return Result.RETRY;
        }
        //return Result.SUCCESS;
    }

    private void setSrLocation(){
        locationUtils.getLocation();
        if(locationUtils.getLatitude() > 0.0){
            location.setLocLat(locationUtils.getLatitude());
            location.setLocLon(locationUtils.getLongitude());
            location.setAddressName(locationUtils.getLocationName(location.getLocLat(), location.getLocLon()));
            location.setDistance(locationUtils.getDistance(0.0, 0.0, location.getLocLat(), location.getLocLon()));
            location.setBatteryParcentage(batteryUtils.getBatteryPercentage(context));
            location.setImage("");
            location.setUserId("shiro");
        }
    }

    private void saveDataOffline(String jsonData) {
    }

    private boolean sendData(String jsonData) {
        return false;
    }

}
