package com.avalanche.srtracker.activity.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.avalanche.srtracker.R;
import com.avalanche.srtracker.activity.login.LoginActivity;
import com.avalanche.srtracker.alarmmanager.DataService;
import com.avalanche.srtracker.alarmmanager.TaskReceiver;
import com.avalanche.srtracker.manager.TaskWorker;
import com.avalanche.srtracker.model.LocationModel;
import com.avalanche.srtracker.model.SrDestinationLocations;
import com.avalanche.srtracker.model.SrLocation;
import com.avalanche.srtracker.model.User;
import com.avalanche.srtracker.util.LocationUtil;
import com.avalanche.srtracker.util.LocationUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import projects.shiro.easylocation.EasyLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_CHANGE_PASSWORD = 5;
    Bitmap imageBitmap;
    FloatingActionButton camera;
    FloatingActionButton sendToServer;
    MapsViewModel viewModel;

    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    PeriodicWorkRequest request;


    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    LatLng currenrLoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewModel = ViewModelProviders.of(this).get(MapsViewModel.class);

        startAlert();
        //initGeofence();

        camera = findViewById(R.id.imageButton);
        sendToServer = findViewById(R.id.sendButton);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        sendToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImageData();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            currenrLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(currenrLoc).title("Marker in Sydney"));
                            //mMap.moveCamera(CameraUpdateFactory.newLatLng(currenrLoc));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currenrLoc, 16));
                        }
                    }
                });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            dispatchTakePictureIntent();
        }  else if (id == R.id.nav_logout) {
            logout();

        } else if (id == R.id.nav_send) {
            setImageData();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String currentDateandTime = sdf.format(new Date());
            storeImage(imageBitmap, "img" + currentDateandTime +".jpeg");
        }
    }

    @Override
    public void onBackPressed() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(MapsActivity.this, DataService.class);
        startService(intent);
    }


    //initialize geofence
    public void initGeofence(){
        viewModel.initComponents(this, this);
        String id = viewModel.getId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());
        Call<List<SrDestinationLocations>> listCall = viewModel.getDealerLcations(id, currentDateandTime);
        listCall.enqueue(new Callback<List<SrDestinationLocations>>() {
            @Override
            public void onResponse(Call<List<SrDestinationLocations>> call, Response<List<SrDestinationLocations>> response) {
                List<SrDestinationLocations> list = new ArrayList<>();
                list = response.body();
                SrDestinationLocations locations = new SrDestinationLocations();
                locations.setLatitude(23.7154494);
                locations.setLongitude(90.4005535);
                locations.setLocationName("37 Becharam Dewri, Dhaka, Bangladesh");
                if(list != null){
                    List<Geofence> list1 = viewModel.getGeofenceList(list);
                    viewModel.initGefence(list1);
                }
            }

            @Override
            public void onFailure(Call<List<SrDestinationLocations>> call, Throwable t) {

            }
        });

    }

    //user logout
    public void logout(){
        viewModel.clearCache();
        WorkManager.getInstance().cancelAllWork();
        Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
        startActivity(intent);
    }




    @SuppressLint("MissingPermission")
    public void initLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setNumUpdates(1);
        if(currenrLoc == null){
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }else {
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currenrLoc = new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude());
                mMap.addMarker(new MarkerOptions().position(currenrLoc).title("Marker in Sydney"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(currenrLoc));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currenrLoc, 16));
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    //camera operations
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @SuppressLint("MissingPermission")
    public void setImageData(){
        Toast.makeText(this, "Sending data.", Toast.LENGTH_LONG).show();
        Context context = this;
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            currenrLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            SrLocation location1 = viewModel.setImageLocation(imageBitmap, currenrLoc);
                            viewModel.updateLocationReached(location1).enqueue(new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) {
                                    if(response.code() == 201){
                                        imageBitmap = null;
                                        Toast.makeText(context, "Successfully sent image", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call call, Throwable t) {
                                    Toast.makeText(context, "Please try again later", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
    }

    public void startAlert() {
        /*int timeInMin = 2 * 60;

        Intent intent = new Intent(this, TaskReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 234, intent, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1 * 1000), pendingIntent);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), timeInMin * 10000, pendingIntent);*/
        request = new PeriodicWorkRequest.Builder(TaskWorker.class, 16, TimeUnit.MINUTES).build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("backTask", ExistingPeriodicWorkPolicy.KEEP, request);

        /*Intent intent = new Intent(MapsActivity.this, DataService.class);
        startService(intent);*/
    }

    public boolean storeImage(Bitmap imageData, String filename) {
        // get path to external storage (SD card)
        String directoryPath = Environment.getExternalStorageDirectory() + "/avalanche";
        //File mFolder = new File(getFilesDir() + "/avalanche");
        File mFolder = new File(directoryPath);
        File imgFile = new File(mFolder.getAbsolutePath() + "/"+filename);
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }

        FileOutputStream fos = null;
        try {
            if (!imgFile.exists()) {
                imgFile.createNewFile();
            }
            fos = new FileOutputStream(imgFile);
            imageData.compress(Bitmap.CompressFormat.JPEG,70, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
