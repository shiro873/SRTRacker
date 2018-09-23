package com.avalanche.srtracker.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.avalanche.srtracker.db.LocationsDb;
import com.avalanche.srtracker.model.LoginLogs;

public class LoginLogsRepository {
    private LocationsDb db;


    public LoginLogsRepository(Application application){
        db = LocationsDb.getDatabase(application.getApplicationContext());
    }

    public void insert(LoginLogs logs){
        db.loginLogsDao().insert(logs);
    }

    public void delete(){
        db.loginLogsDao().deleteAll();
    }

    public LiveData<LoginLogs> getTodaysLog(String datetime){
        return db.loginLogsDao().getLog(datetime);
    }

    public void update(LoginLogs log){
        db.loginLogsDao().update(log);
    }
}
