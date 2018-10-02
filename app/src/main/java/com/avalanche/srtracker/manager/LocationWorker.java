package com.avalanche.srtracker.manager;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.avalanche.srtracker.model.SrLocation;
import com.avalanche.srtracker.model.Token;
import com.avalanche.srtracker.model.User;
import com.avalanche.srtracker.network.ApiClient;
import com.avalanche.srtracker.network.ApiInterface;
import com.avalanche.srtracker.repository.MrLocationRepository;
import com.avalanche.srtracker.repository.UserRepository;
import com.avalanche.srtracker.util.BatteryUtils;
import com.avalanche.srtracker.util.LocationUtils;

import java.io.IOException;

import androidx.work.Worker;
import br.vince.easysave.EasySave;
import retrofit2.Call;

public class LocationWorker extends Worker {
    private Context context;
    private LocationUtils locationUtils;
    private BatteryUtils batteryUtils;
    private SrLocation location;
    private UserRepository userRepository;
    private MrLocationRepository locationRepository;
    private final String RESULT_TAG = "location";
    private EasySave save;

    public LocationWorker(@NonNull Context context, @NonNull Activity activity){
        this.context = context;
        locationUtils = new LocationUtils(context, activity);
        batteryUtils = new BatteryUtils();
        location = new SrLocation();
        userRepository = new UserRepository(activity.getApplication());
        locationRepository = new MrLocationRepository(activity.getApplication());
        save = new EasySave(context.getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        setSrLocation();
        String userId = "";
        Call<SrLocation> srLocationCall = sendData();
        try{
            userId = srLocationCall.execute().body().getUserId();
        }catch (IOException e){
            e.printStackTrace();
        }
        if(userId.equals(save.retrieveModel("user", User.class).getUserId())){
            return Result.SUCCESS;
        }else {
            saveDataOffline();
            return Result.RETRY;
        }
    }

    private void setSrLocation(){
        locationUtils.getLocation();
        LocationUtils oldData = getCachedLat();
        if(locationUtils.getLatitude() > 0.0){
            location.setLocLat(locationUtils.getLatitude());
            location.setLocLon(locationUtils.getLongitude());
            location.setAddressName(locationUtils.getLocationName(location.getLocLat(), location.getLocLon()));
            location.setDistance(locationUtils.getDistance(oldData.getLatitude(), oldData.getLongitude(),
                    location.getLocLat(), location.getLocLon()));
            location.setBatteryParcentage(batteryUtils.getBatteryPercentage(context));
            location.setImage("");
            location.setUserId(save.retrieveModel("user", User.class).getUsername());
        }
        cacheLatLong();
    }

    private void saveDataOffline() {
        locationRepository.insert(location);
    }

    private Call<SrLocation> sendData() {
        //return new NetwrokDataUtils().sendMrLocationData(location);
        Token token = save.retrieveModel("token", Token.class);
        ApiInterface apiClient = ApiClient.getClient().create(ApiInterface.class);
        Call<SrLocation> srLocationCall = apiClient.postTrackLog(token.getAccess_token()
                ,token.getToken_type()+" "+token.getAccess_token()
                ,location);
        return srLocationCall;
    }

    private void cacheLatLong(){
        save.saveModel("latlon", locationUtils);
    }

    private LocationUtils getCachedLat(){
        return save.retrieveModel("latlon", LocationUtils.class);
    }
}
