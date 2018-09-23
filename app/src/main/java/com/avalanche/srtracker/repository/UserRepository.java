package com.avalanche.srtracker.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.avalanche.srtracker.db.LocationsDb;
import com.avalanche.srtracker.model.User;

public class UserRepository {
    LocationsDb db;

    public UserRepository(Application application){
        db = LocationsDb.getDatabase(application.getApplicationContext());
    }

    public LiveData<User> getUserLiveData(String username) {
        return db.userDao().getUser(username);
    }

    public void insertUser(User user){
        db.userDao().insert(user);
    }

    public void deleteUser(){
        db.userDao().deleteAll();
    }

    public void updateUser(User user){
        db.userDao().updateUser(user);
    }

    public void getUserById(String userId){
        db.userDao().getUserById(userId);
    }
}
