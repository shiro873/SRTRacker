package com.avalanche.srtracker.util;

import android.content.Context;
import android.util.Log;

import com.avalanche.srtracker.model.LocationModel;
import com.avalanche.srtracker.model.SrLocation;
import com.avalanche.srtracker.model.Token;
import com.avalanche.srtracker.model.User;
import com.avalanche.srtracker.network.ApiClient;
import com.avalanche.srtracker.network.ApiInterface;
import com.avalanche.srtracker.repository.MrLocationRepository;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.work.Worker;
import br.vince.easysave.EasySave;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import projects.shiro.easylocation.EasyLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataSyncUtil {
    private Context context;
    private LocationUtil locationUtils;
    private BatteryUtils batteryUtils;
    private SrLocation location;
    private MrLocationRepository locationRepository;
    private EasySave save;
    private LatLng latLng;

    public DataSyncUtil(Context context, LatLng latLng){
        this.context = context;
        this.latLng = latLng;
        batteryUtils = new BatteryUtils();
        location = new SrLocation();
        locationRepository = new MrLocationRepository(context.getApplicationContext());
        save = new EasySave(context.getApplicationContext());
        locationUtils = new LocationUtil(context);
        syncData(context);
    }

    private void syncData(Context context){
        setSrLocation();
        Call<SrLocation> srLocationCall = sendData();
        srLocationCall.enqueue(new Callback<SrLocation>() {
            @Override
            public void onResponse(Call<SrLocation> call, Response<SrLocation> response) {
                if(response.code() == 500){
                    saveDataOffline();
                }
            }

            @Override
            public void onFailure(Call<SrLocation> call, Throwable t) {
                saveDataOffline();
            }
        });
    }

    private void setSrLocation(){
        LocationUtil oldData = getCachedLat();
        LatLng oldModel = new LatLng(0, 0);
        try{
            oldModel = oldData.getLocation();
        }catch (Exception e){

        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        LatLng model = latLng;
        if(model != null){
            location.setLatitude(model.latitude);
            location.setLongitude(model.longitude);
            location.setLocationName(locationUtils.getLocationName(model.latitude, model.longitude));
            if(oldModel != null){
                location.setKilometer(locationUtils.getDistance(oldModel.latitude, oldModel.latitude,
                        location.getLatitude(), location.getLongitude()));
            }else {
                location.setKilometer(0);
            }
            location.setBatteryPerc(batteryUtils.getBatteryPercentage(context));
            location.setImage("");
            location.setDateTime(currentDateandTime);
            location.setUserId("101");
            location.setUserIp(locationUtils.getDeviceIMEI());
        }
        Log.d("LocSet", "Location set");
    }

    private void saveDataOffline() {
        locationRepository.insert(location);
    }

    private Call<SrLocation> sendData() {
        Token token = save.retrieveModel("token", Token.class);
        ApiInterface apiClient = ApiClient.getTokenClient(context).create(ApiInterface.class);
        Call<SrLocation> srLocationCall = apiClient.postTrackLog(location);
        return srLocationCall;
    }

    private void cacheLatLong(){
        save.saveModel("latlon", locationUtils);
    }

    private LocationUtil getCachedLat(){
        return save.retrieveModel("latlon", LocationUtil.class);
    }
}
