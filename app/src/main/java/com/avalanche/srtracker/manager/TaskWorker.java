package com.avalanche.srtracker.manager;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.avalanche.srtracker.alarmmanager.DataService;
import com.avalanche.srtracker.util.DataSyncUtil;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class TaskWorker extends Worker {
    Context context;
    DataSyncUtil util;

    public TaskWorker(@NonNull Context context,
                      @NonNull WorkerParameters params){
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        context.startService(new Intent(context, DataService.class));
        return Result.SUCCESS;
    }
}
