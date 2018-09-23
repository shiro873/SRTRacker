package com.avalanche.srtracker.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.avalanche.srtracker.model.LoginLogs;
import com.avalanche.srtracker.model.User;

import java.util.List;

@Dao
public interface LoginLogsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LoginLogs loginLog);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(LoginLogs... loginLogs);

    @Query("DELETE FROM LoginLogs")
    void deleteAll();

    @Query("SELECT * from LoginLogs where loginTime = :datetime")
    LiveData<LoginLogs> getLog(String datetime);

    @Update
    void update(LoginLogs log);


}
