package com.avalanche.srtracker.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.avalanche.srtracker.model.SrLocation;
import com.avalanche.srtracker.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(User... users);

    @Query("DELETE FROM User")
    void deleteAll();

    @Query("SELECT * from User where username = :username")
    LiveData<List<User>> getUser(String username);
}
