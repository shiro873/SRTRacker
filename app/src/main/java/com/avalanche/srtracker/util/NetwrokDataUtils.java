package com.avalanche.srtracker.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.avalanche.srtracker.model.SrDestinationLocations;
import com.avalanche.srtracker.model.SrLocation;
import com.avalanche.srtracker.model.User;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

public class NetwrokDataUtils {
    boolean result;
    MutableLiveData<User> userMutableLiveData;
    MutableLiveData<List<SrDestinationLocations>> destinationLocationsMutableLiveData;
    MutableLiveData<JSONObject> passwordChangeResponse;

    public MutableLiveData<User> getUser(String username){
        AndroidNetworking.get("")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(User.class, new ParsedRequestListener<User>() {
                    @Override
                    public void onResponse(User response) {
                        userMutableLiveData.setValue(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        // handle error
                    }
                });
        return userMutableLiveData;
    }

    public MutableLiveData<JSONObject> changeUserPassword(User user){
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(new  Gson().toJson(user));
        }catch (Exception e){

        }
        if(jsonObject != null){
            AndroidNetworking.post("")
                    .addJSONObjectBody(jsonObject) // posting any type of file
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            passwordChangeResponse.setValue(response);
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                            passwordChangeResponse.setValue(null);
                        }
                    });
        }
        return passwordChangeResponse;
    }

    public MutableLiveData<List<SrDestinationLocations>> getDestinationLocationsMutableLiveData() {
        AndroidNetworking.get("")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(User.class, new ParsedRequestListener<List<SrDestinationLocations>>() {
                    @Override
                    public void onResponse(List<SrDestinationLocations> response) {
                        destinationLocationsMutableLiveData.setValue(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        // handle error
                    }
                });
        return destinationLocationsMutableLiveData;
    }

    public boolean sendMrLocationData(SrLocation location){
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(new  Gson().toJson(location));
        }catch (Exception e){

        }
        if(jsonObject != null){
            AndroidNetworking.post("")
                    .addJSONObjectBody(jsonObject) // posting any type of file
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            result = true;
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                            result = false;
                        }
                    });
        }
        return result;
    }

    public boolean getMrLocations(List<SrLocation> locations){
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(new  Gson().toJson(locations));
        }catch (Exception e){

        }
        if(jsonObject != null){
            AndroidNetworking.post("")
                    .addJSONObjectBody(jsonObject) // posting any type of file
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            result = true;
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                            result = false;
                        }
                    });
        }
        return result;
    }
}
