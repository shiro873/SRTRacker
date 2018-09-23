package com.avalanche.srtracker.activity.login;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.avalanche.srtracker.db.LocationsDb;
import com.avalanche.srtracker.model.LoginLogs;
import com.avalanche.srtracker.model.User;
import com.avalanche.srtracker.repository.LoginLogsRepository;
import com.avalanche.srtracker.repository.UserRepository;
import com.avalanche.srtracker.util.NetwrokDataUtils;

import br.vince.easysave.EasySave;

public class LoginViewModel extends AndroidViewModel {

    UserRepository userRepository;
    LoginLogsRepository logsRepository;
    MutableLiveData<User> userLiveData;
    EasySave save;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        logsRepository = new LoginLogsRepository(application);
        save = new EasySave(application.getApplicationContext());
    }

    public LiveData<User> getUserFromDb(String username){
        return userRepository.getUserLiveData(username);
    }

    public MutableLiveData<User> getUserFromNetwork(String username){
        userLiveData = new NetwrokDataUtils().getUser(username);
        return userLiveData;
    }

    public void insertUser(User user){
        userRepository.insertUser(user);
    }

    public void insertLogs(LoginLogs logs){
        logsRepository.insert(logs);
    }

    public void cacheData(User user){
        save.saveModel("user", user);
    }
}
