package com.avalanche.srtracker.activity.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avalanche.srtracker.R;
import com.avalanche.srtracker.activity.home.MapsActivity;
import com.avalanche.srtracker.model.LoginLogs;
import com.avalanche.srtracker.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    LoginViewModel model;

    EditText username;
    EditText password;
    Button button;

    LiveData<User> userLiveData;
    MutableLiveData<User> mutableLiveData;

    String uName;
    String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        model = ViewModelProviders.of(this).get(LoginViewModel.class);

        username = findViewById(R.id.txtUserName);
        password = findViewById(R.id.txtPwd);
        button = findViewById(R.id.btnLogin);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login(){
        uName = username.getText().toString();
        pwd = password.getText().toString();

        userLiveData = model.getUserFromDb(uName);
        User user = userLiveData.getValue();

        if(user == null){
            mutableLiveData = model.getUserFromNetwork(uName);
            mutableLiveData.observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    if(pwd.equals(user.getPassword())){
                        model.insertUser(user);
                        model.cacheData(user);
                        model.insertLogs(getLogs(user));

                        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
        else {
            userLiveData.observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    if(pwd.equals(user.getPassword())){
                        model.cacheData(user);
                        model.insertLogs(getLogs(user));

                        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private LoginLogs getLogs(User user){
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy_HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        LoginLogs logs = new LoginLogs();
        logs.setIsLoggedIn(true);
        logs.setUserId(user.getUserId());
        logs.setLoginTime(currentDateandTime);
        logs.setLogoutTime("");

        return logs;
    }
}
