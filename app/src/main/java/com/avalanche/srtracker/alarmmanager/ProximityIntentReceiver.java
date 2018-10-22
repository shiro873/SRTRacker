package com.avalanche.srtracker.alarmmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.avalanche.srtracker.network.ApiClient;
import com.avalanche.srtracker.network.ApiInterface;
import com.avalanche.srtracker.util.LocationService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProximityIntentReceiver extends BroadcastReceiver {
    Context context;
    double distance;
    LocationService service;
    private static int i = 70;
    private static final int NOTIFICATION_ID = 1000;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        service = new LocationService(context);
        Boolean entering = intent.getBooleanExtra(key, false);
        if (entering) {
            Log.d(getClass().getSimpleName(), "entering");
            Toast.makeText(context, "reached location", Toast.LENGTH_LONG);
            sendData();
        }
        else {
            Log.d(getClass().getSimpleName(), "exiting");
        }

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            v.vibrate(1000);
        }

    }

    public void sendData(){
        ApiInterface apiClient = ApiClient.getClient().create(ApiInterface.class);
        Call updateCall = apiClient.updateLocationReached("1");
        updateCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }
}
