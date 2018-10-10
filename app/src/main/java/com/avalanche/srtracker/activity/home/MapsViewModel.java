package com.avalanche.srtracker.activity.home;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.avalanche.srtracker.model.LoginLogs;
import com.avalanche.srtracker.model.SrDestinationLocations;
import com.avalanche.srtracker.model.SrLocation;
import com.avalanche.srtracker.model.Token;
import com.avalanche.srtracker.model.User;
import com.avalanche.srtracker.network.ApiClient;
import com.avalanche.srtracker.network.ApiInterface;
import com.avalanche.srtracker.repository.LoginLogsRepository;
import com.avalanche.srtracker.repository.MrDestLocRepository;
import com.avalanche.srtracker.repository.UserRepository;
import com.avalanche.srtracker.util.BatteryUtils;
import com.avalanche.srtracker.util.GeofenceUtils;
import com.avalanche.srtracker.util.LocationUtil;
import com.avalanche.srtracker.util.LocationUtils;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;

import br.vince.easysave.EasySave;
import retrofit2.Call;

public class MapsViewModel extends AndroidViewModel{
    GeofenceUtils geofenceUtils;
    BatteryUtils batteryUtils;

    MrDestLocRepository repository;
    UserRepository userRepository;
    LoginLogsRepository loginLogsRepository;

    MutableLiveData<List<SrDestinationLocations>> locationsMutableLiveData;

    EasySave save;


    public MapsViewModel(@NonNull Application application) {
        super(application);
        repository = new MrDestLocRepository(application);
        userRepository = new UserRepository(application);
        loginLogsRepository = new LoginLogsRepository(application);
        save = new EasySave(application.getApplicationContext());
        batteryUtils = new BatteryUtils();
    }

    public void initComponents(Context context, Activity activity){
        geofenceUtils = new GeofenceUtils(context.getApplicationContext(), activity);
    }

    public void insertMrLocations(List<SrDestinationLocations> locations){
        repository.inert(locations);
    }

    public void initGefence(List<Geofence> list){
        geofenceUtils.initGeofence(list);
    }

    public Call<List<SrDestinationLocations>> getDealerLcations(String empId, String date){
        ApiInterface apiClient = ApiClient.getClient().create(ApiInterface.class);
        Call<List<SrDestinationLocations>> call = apiClient.getLocations(empId, date);
        return call;
    }

    public List<Geofence> getGeofenceList(List<SrDestinationLocations> locations) {
        List<Geofence> list = new ArrayList<>();
        if (locations != null) {
            for (SrDestinationLocations loc : locations) {
                list.add(new Geofence.Builder()
                        // Set the circular region of this geofence.
                        .setCircularRegion(
                                loc.getLatitude(),
                                loc.getLongitude(),
                                100
                        )

                        // Set the expiration duration of the geofence. This geofence gets automatically
                        // removed after this period of time.
                        .setExpirationDuration(24 * 60 * 60 * 1000)

                        // Set the transition types of interest. Alerts are only generated for these
                        // transition. We track entry and exit transitions in this sample.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)

                        // Create the geofence.
                        .build());
            }
            return list;
        }else {
            return null;
        }

    }


    public MutableLiveData<List<SrDestinationLocations>> getLocationsMutableLiveData() {
        return locationsMutableLiveData;
    }

    public void removeGeofenceAlert(){
        geofenceUtils.removeGeofence();
    }

    public MutableLiveData<JSONObject> changePasswordNetwork(User user){
        //return new NetwrokDataUtils().changeUserPassword(user);
        return null;
    }

    public User getUserFromCache(){
        return save.retrieveModel("user", User.class);
    }

    public void clearCache(){
        save.saveModel("user", null);
    }

    public String getId(){
        return save.retrieveModel("id", String.class);
    }

    public SrLocation setImageLocation(Bitmap image, LatLng latLng){
        String stringImage = ConvertToBase64(image);
        LocationUtil util = new LocationUtil(getApplication().getApplicationContext());
        SrLocation location = new SrLocation();
        location.setImage(stringImage);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        location.setLocationName(util.getLocationName(latLng.latitude, latLng.longitude));
        location.setUserIp(util.getDeviceIMEI());
        location.setUserId(getId());
        location.setKilometer(0);
        location.setBatteryPerc(batteryUtils.getBatteryPercentage(getApplication().getApplicationContext()));
        return location;
    }

    public Call updateLocationReached(SrLocation location){
        ApiInterface apiCient = ApiClient.getTokenClient(getApplication()).create(ApiInterface.class);
        return apiCient.postTrackLog(location);
    }

    public String ConvertToBase64(Bitmap bitmap) {
        String myBase64Image = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
        return myBase64Image;
    }


    //image to base64
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}
