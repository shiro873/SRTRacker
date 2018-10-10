package com.avalanche.srtracker.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SrLocation {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private double Longitude;
    private double Latitude;
    private String Image;
    private String UserIp;
    private String LocationName;
    private String DateTime;
    private double Kilometer;
    private String UserId;
    private int BatteryPerc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getUserIp() {
        return UserIp;
    }

    public void setUserIp(String userIp) {
        UserIp = userIp;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public double getKilometer() {
        return Kilometer;
    }

    public void setKilometer(double kilometer) {
        Kilometer = kilometer;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public int getBatteryPerc() {
        return BatteryPerc;
    }

    public void setBatteryPerc(int batteryPerc) {
        BatteryPerc = batteryPerc;
    }
}
