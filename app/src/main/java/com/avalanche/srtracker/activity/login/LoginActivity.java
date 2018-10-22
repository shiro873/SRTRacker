package com.avalanche.srtracker.activity.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.TextKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.avalanche.srtracker.R;
import com.avalanche.srtracker.activity.home.MapsActivity;
import com.avalanche.srtracker.model.LoginLogs;
import com.avalanche.srtracker.model.Token;
import com.avalanche.srtracker.model.User;
import com.avalanche.srtracker.util.PermissionUtils;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    LoginViewModel model;
    PermissionUtils permissionUtils;

    EditText username;
    Button button;
    ProgressBar bar;

    LiveData<User> userLiveData;
    MutableLiveData<User> mutableLiveData;
    MutableLiveData<Token> tokenMutableLiveData;

    String uName;
    String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        permissionUtils = new PermissionUtils(this, this);
        permissionUtils.checkPermissions();

        model = ViewModelProviders.of(this).get(LoginViewModel.class);

        username = findViewById(R.id.txtUserName);
        button = findViewById(R.id.btnLogin);
        bar = findViewById(R.id.progressbar);
        bar.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login(){
        uName = username.getText().toString();
        bar.setVisibility(View.VISIBLE);
        /*if(!uName.equals("")){
            final Call<Token> tokenCall = model.getTokenFromNetwork(Integer.parseInt(uName));
            tokenCall.enqueue(new Callback<Token>() {
                @Override
                public void onResponse(Call<Token> call, Response<Token> response) {
                    if(response.code() != 500){
                        Token token = new Token();
                        token.setAccess_token(response.body().getAccess_token());
                        token.setExpires_in(response.body().getExpires_in());
                        token.setToken_type(response.body().getToken_type());
                        model.cacheToken(token);
                        model.cacheId(uName);
                        bar.setVisibility(View.INVISIBLE);

                        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                        startActivity(intent);
                    }else{
                        bar.setVisibility(View.INVISIBLE);
                        username.setError("Unauthorized access");
                    }
                }

                @Override
                public void onFailure(Call<Token> call, Throwable t) {
                    bar.setVisibility(View.INVISIBLE);
                    username.setError("Could not connect to internet");
                }
            });
        }else {
            username.setError("User Id empty");
        }*/
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    private LoginLogs getLogs(User user){
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy_HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        LoginLogs logs = new LoginLogs();
        logs.setLoggedIn(true);
        logs.setUserId(user.getUserId());
        logs.setLoginTime(currentDateandTime);
        logs.setLogoutTime("");

        return logs;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        permissionUtils.permissionResult(requestCode, permissions, grantResults);
    }
}
