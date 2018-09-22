package com.avalanche.srtracker.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.avalanche.srtracker.model.SrLocation;

import java.util.List;

@Dao
public interface SrLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SrLocation location);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(SrLocation... locations);

    @Query("DELETE FROM SrLocation")
    void deleteAll();

    @Query("SELECT * from SrLocation")
    LiveData<List<SrLocation>> getAllLocations();
}
