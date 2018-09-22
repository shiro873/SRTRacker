package com.avalanche.srtracker.db;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.avalanche.srtracker.model.LoginLogs;
import com.avalanche.srtracker.model.SrDestinationLocations;
import com.avalanche.srtracker.model.SrLocation;
import com.avalanche.srtracker.model.User;

import java.util.concurrent.Executors;

@Database(entities = {SrLocation.class, SrDestinationLocations.class, LoginLogs.class, User.class},
        version = 1,
        exportSchema = false)
public abstract class LocationsDb extends RoomDatabase {
    public abstract SrLocationDao locationDao();
    public abstract UserDao userDao();
    public abstract SrDestinationLocationDao destinationLocationDao();
    public abstract LoginLogsDao loginLogsDao();

    private static LocationsDb INSTANCE;

    public static LocationsDb getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (LocationsDb.class){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        LocationsDb.class,
                        "SrLocation")
                        .addCallback(new RoomDatabase.Callback() {
                            @Override
                            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                super.onOpen(db);
                                final SrLocation location = new SrLocation();
                                location.setLocLat(0.0);
                                location.setLocLon(0.0);
                                location.setAddressName("");
                                location.setImage("");
                                location.setUserId("");
                                Executors.newSingleThreadExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        INSTANCE.locationDao().insert(location);
                                    }
                                });
                            }
                        })
                        .build();
            }
        }
        return INSTANCE;
    }
}
