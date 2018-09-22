package com.avalanche.srtracker.manager;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.Worker;

public class LocationWorker extends Worker {
    private Context context;

    public LocationWorker(@NonNull Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }
}
