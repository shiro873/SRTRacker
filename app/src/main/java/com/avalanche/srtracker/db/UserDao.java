package com.avalanche.srtracker.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
  LiveData<User> getUser(String username);

  @Query("SELECT * FROM User where userId = :userId")
  LiveData<User> getUserById(String userId);

  @Update
  void updateUser(User user);
}