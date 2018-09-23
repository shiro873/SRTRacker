package com.avalanche.srtracker.manager;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class LocationManager {
    LocationWorker locationWorkerworker;
    PeriodicWorkRequest locationWorkRequest;

    public LocationManager(@NonNull Context context, @NonNull Activity activity){
        locationWorkerworker = new LocationWorker(context, activity);
    }

    public void enqueueWork(){
        locationWorkRequest = new PeriodicWorkRequest.Builder(LocationWorker.class, 1, TimeUnit.HOURS).build();
        WorkManager.getInstance().enqueue(locationWorkRequest);
    }
}
