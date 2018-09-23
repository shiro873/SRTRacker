package com.avalanche.srtracker.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.avalanche.srtracker.db.LocationsDb;
import com.avalanche.srtracker.model.SrDestinationLocations;

import java.util.List;

public class MrDestLocRepository {
    private LocationsDb db;


    public MrDestLocRepository(Application application){
        db = LocationsDb.getDatabase(application.getApplicationContext());
    }

    public LiveData<List<SrDestinationLocations>> getAll(){
        return db.destinationLocationDao().getAllLocations();
    }

    public void inert(List<SrDestinationLocations> locations){
        SrDestinationLocations[] destinationLocations = locations.toArray(new SrDestinationLocations[locations.size()]);
        db.destinationLocationDao().insertAll(destinationLocations);
    }

    public void delete(){
        db.destinationLocationDao().deleteAll();
    }
}
