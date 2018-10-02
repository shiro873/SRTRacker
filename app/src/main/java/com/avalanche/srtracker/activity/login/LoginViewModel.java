package com.avalanche.srtracker.activity.login;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.avalanche.srtracker.model.LoginLogs;
import com.avalanche.srtracker.model.Token;
import com.avalanche.srtracker.model.User;
import com.avalanche.srtracker.network.ApiClient;
import com.avalanche.srtracker.network.ApiInterface;
import com.avalanche.srtracker.repository.LoginLogsRepository;
import com.avalanche.srtracker.repository.UserRepository;


import br.vince.easysave.EasySave;
import retrofit2.Call;

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
        //userLiveData = new NetwrokDataUtils().getUser(username);
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

    public void cacheToken(Token token){
        save.saveModel("token", token);
    }

    public Call<Token> getTokenFromNetwork(int userId){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Token> tokenCall = apiInterface.getToken(userId, "", "password");
        return tokenCall;
    }

    public void insertTokenToDb(Token token){

    }
}
