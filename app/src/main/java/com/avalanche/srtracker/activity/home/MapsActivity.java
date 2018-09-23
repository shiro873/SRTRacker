package com.avalanche.srtracker.activity.home;

import android.Manifest;
import android.app.PendingIntent;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.avalanche.srtracker.GeofencingAlert.GeofenceTransitionsIntentService;
import com.avalanche.srtracker.R;
import com.avalanche.srtracker.activity.changepassword.ChangePassActivity;
import com.avalanche.srtracker.model.LoginLogs;
import com.avalanche.srtracker.model.SrDestinationLocations;
import com.avalanche.srtracker.model.User;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_CHANGE_PASSWORD = 5;
    Bitmap imageBitmap;
    FloatingActionButton camera;
    FloatingActionButton sendToServer;
    MapsViewModel viewModel;

    MutableLiveData<List<SrDestinationLocations>> locationsMutableLiveData;

    FloatingActionMenu menu;
    FloatingActionMenu resetPassword, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        viewModel = ViewModelProviders.of(this).get(MapsViewModel.class);

        locationsMutableLiveData = viewModel.getLocationsMutableLiveData();
        locationsMutableLiveData.observe(this, new Observer<List<SrDestinationLocations>>() {
            @Override
            public void onChanged(@Nullable List<SrDestinationLocations> srDestinationLocations) {
                initGeofence(srDestinationLocations);
            }
        });


        camera = findViewById(R.id.imageButton);
        sendToServer = findViewById(R.id.sendButton);
        menu = findViewById(R.id.floatingMenu);
        resetPassword = findViewById(R.id.changePassword);
        logout = findViewById(R.id.logout);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        sendToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        logout.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        resetPassword.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, ChangePassActivity.class);
                startActivityForResult(intent, REQUEST_CHANGE_PASSWORD);
            }
        });
    }


    //initialize geofence
    public void initGeofence(List<SrDestinationLocations> srDestinationLocations){
        viewModel.initComponents(this, this);
        viewModel.insertMrLocations(srDestinationLocations);
        List<Geofence> geofences = viewModel.getGeofenceList(srDestinationLocations);
        viewModel.initGefence(geofences);
    }

    //user logout
    public void logout(){
        LiveData<LoginLogs> logsLiveData = viewModel.getTodaysLog();
        logsLiveData.observe(this, new Observer<LoginLogs>() {
            @Override
            public void onChanged(@Nullable LoginLogs logs) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy_HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                logs.setLogoutTime(currentDateandTime);
                viewModel.updateLog(logs);
                viewModel.removeGeofenceAlert();
                viewModel.clearCache();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



    //camera operations
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
        }
        if(requestCode == REQUEST_CHANGE_PASSWORD && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            String password = extras.getString("pass", "");

            if(!password.equals("")){
                final User user = viewModel.getUserFromCache();
                user.setPassword(password);
                MutableLiveData<JSONObject> mutableLiveData = viewModel.changePasswordNetwork(user);
                mutableLiveData.observe(this, new Observer<JSONObject>() {
                    @Override
                    public void onChanged(@Nullable JSONObject jsonObject) {
                        viewModel.changePasswordDb(user);
                    }
                });
            }
        }
    }
}
