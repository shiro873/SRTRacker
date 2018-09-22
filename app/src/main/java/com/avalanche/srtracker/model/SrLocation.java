package com.avalanche.srtracker.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SrLocation {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private double locLat;
    private double locLon;
    private String image;
    private String UserId;
    private String AddressName;
    private double distance;
    private int batteryParcentage;

    public int getBatteryParcentage() {
        return batteryParcentage;
    }

    public void setBatteryParcentage(int batteryParcentage) {
        this.batteryParcentage = batteryParcentage;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLocLat() {
        return locLat;
    }

    public void setLocLat(double locLat) {
        this.locLat = locLat;
    }

    public double getLocLon() {
        return locLon;
    }

    public void setLocLon(double locLon) {
        this.locLon = locLon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userIp) {
        UserId = userIp;
    }

    public String getAddressName() {
        return AddressName;
    }

    public void setAddressName(String addressName) {
        AddressName = addressName;
    }
}
