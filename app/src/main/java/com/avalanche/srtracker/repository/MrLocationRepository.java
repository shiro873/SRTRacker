package com.avalanche.srtracker.repository;

import android.app.Application;
import android.app.ListActivity;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.avalanche.srtracker.db.LocationsDb;
import com.avalanche.srtracker.model.SrLocation;

import java.util.List;

public class MrLocationRepository {
    LocationsDb db;

    public MrLocationRepository(Application application){
        db = LocationsDb.getDatabase(application.getApplicationContext());
    }

    public MrLocationRepository(Context context){
        db = LocationsDb.getDatabase(context);
    }

    public void insert(SrLocation location){
        db.locationDao().insert(location);
    }

    public void delete(){
        db.locationDao().deleteAll();
    }

    public LiveData<List<SrLocation>> getAll(){
        return db.locationDao().getAllLocations();
    }
}
