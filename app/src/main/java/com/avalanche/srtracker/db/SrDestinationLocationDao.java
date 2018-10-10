package com.avalanche.srtracker.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.avalanche.srtracker.model.SrDestinationLocations;

import java.util.List;

@Dao
public interface SrDestinationLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SrDestinationLocations location);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(SrDestinationLocations... locations);

    @Query("DELETE FROM SrDestinationLocations")
    void deleteAll();

    @Query("SELECT * from SrDestinationLocations")
    LiveData<List<SrDestinationLocations>> getAllLocations();
}