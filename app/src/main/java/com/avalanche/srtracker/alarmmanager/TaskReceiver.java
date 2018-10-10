package com.avalanche.srtracker.alarmmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.avalanche.srtracker.util.DataSyncUtil;


public class TaskReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, DataService.class));
    }
}
