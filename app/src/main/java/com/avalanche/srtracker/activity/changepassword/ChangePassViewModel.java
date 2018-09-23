package com.avalanche.srtracker.activity.changepassword;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.avalanche.srtracker.model.User;

import br.vince.easysave.EasySave;

public class ChangePassViewModel extends AndroidViewModel {
    EasySave save;

    public ChangePassViewModel(@NonNull Application application) {
        super(application);
        save = new EasySave(application.getApplicationContext());
    }

    public User getUser(){
        return save.retrieveModel("user", User.class);
    }
}
