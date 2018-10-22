package com.avalanche.srtracker.GeofencingAlert;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.avalanche.srtracker.network.ApiClient;
import com.avalanche.srtracker.network.ApiInterface;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class GeofenceTransitionsIntentService extends IntentService {
  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * @param name Used to name the worker thread, important only for debugging.
   *
   */

  public GeofenceTransitionsIntentService(String name) {
        super(name);
  }

  protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
            /*String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());*/
                Log.e(TAG, "error");
                return;
            }

    // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

    // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL
                    || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

      // Get the geofences that were triggered. A single event can trigger
      // multiple geofences.


                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE));
                }else{
                    //deprecated in API 26
                    v.vibrate(1000);
                }
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();



                ApiInterface apiClient = ApiClient.getClient().create(ApiInterface.class);
                Call updateCall = apiClient.updateLocationReached(triggeringGeofences.get(0).getRequestId().toString());
                updateCall.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {

                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {

                    }
                });
            } else {

            }
    }
}