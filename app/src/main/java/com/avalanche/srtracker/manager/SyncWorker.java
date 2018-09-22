package com.avalanche.srtracker.manager;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.Worker;

public class SyncWorker extends Worker {
    private Context context;

    public SyncWorker(@NonNull Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }
}
