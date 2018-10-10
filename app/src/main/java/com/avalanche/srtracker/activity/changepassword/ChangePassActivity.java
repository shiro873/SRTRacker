package com.avalanche.srtracker.activity.changepassword;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avalanche.srtracker.R;
import com.avalanche.srtracker.model.User;

public class ChangePassActivity extends AppCompatActivity {

  static final int REQUEST_CHANGE_PASSWORD = 5;

  EditText oldPass, newPass, confrimNewPass;
  Button button;

  ChangePassViewModel viewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_change_pass);

    viewModel = ViewModelProviders.of(this).get(ChangePassViewModel.class);

    oldPass = findViewById(R.id.txtOldPwd);
    newPass = findViewById(R.id.txtPwd);
    confrimNewPass = findViewById(R.id.txtPwdConfirm);

    button = findViewById(R.id.btnCangePwd);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        changePass();
      }
    });
  }

  private void changePass(){
    User user = viewModel.getUser();
    if(user.getPassword().equals(oldPass.getText().toString())){
      if(newPass.getText().toString().equals(confrimNewPass.getText().toString())){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("pass", newPass.getText().toString());
        setResult(Activity.RESULT_OK, returnIntent);
      }else {
        newPass.setError("Password not matched");
        confrimNewPass.setError("Password not matched");
      }
    }else {
      oldPass.setError("Password not matched");
    }
  }
}