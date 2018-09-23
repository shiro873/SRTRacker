package com.avalanche.srtracker.activity.home;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.avalanche.srtracker.manager.LocationManager;
import com.avalanche.srtracker.model.LoginLogs;
import com.avalanche.srtracker.model.SrDestinationLocations;
import com.avalanche.srtracker.model.User;
import com.avalanche.srtracker.repository.LoginLogsRepository;
import com.avalanche.srtracker.repository.MrDestLocRepository;
import com.avalanche.srtracker.repository.UserRepository;
import com.avalanche.srtracker.util.GeofenceUtils;
import com.avalanche.srtracker.util.NetwrokDataUtils;
import com.google.android.gms.location.Geofence;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.vince.easysave.EasySave;

public class MapsViewModel extends AndroidViewModel{
    GeofenceUtils geofenceUtils;
    LocationManager manager;

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
    }

    public void initComponents(Context context, Activity activity){
        geofenceUtils = new GeofenceUtils(context.getApplicationContext(), activity);
        manager = new LocationManager(context.getApplicationContext(), activity);
        manager.enqueueWork();
        locationsMutableLiveData = new NetwrokDataUtils().getDestinationLocationsMutableLiveData();
    }

    public void insertMrLocations(List<SrDestinationLocations> locations){
        repository.inert(locations);
    }

    public void initGefence(List<Geofence> list){
        geofenceUtils.initGeofence(list);
    }

    public List<Geofence> getGeofenceList(List<SrDestinationLocations> locations){
        List<Geofence> list = new ArrayList<>();
        for (SrDestinationLocations loc: locations) {
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
        }


    public MutableLiveData<List<SrDestinationLocations>> getLocationsMutableLiveData() {
        return locationsMutableLiveData;
    }

    public void removeGeofenceAlert(){
        geofenceUtils.removeGeofence();
    }

    public LiveData<LoginLogs> getTodaysLog(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy_HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        LiveData<LoginLogs> logs = loginLogsRepository.getTodaysLog(currentDateandTime);
        return logs;
    }

    public void updateLog(LoginLogs logs){
        loginLogsRepository.update(logs);
    }

    public MutableLiveData<JSONObject> changePasswordNetwork(User user){
        return new NetwrokDataUtils().changeUserPassword(user);
    }

    public void changePasswordDb(User user){
        userRepository.updateUser(user);
    }

    public User getUserFromCache(){
        return save.retrieveModel("user", User.class);
    }

    public void clearCache(){
        save.saveModel("user", null);
    }
}
