package com.example.finalproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.finalproject.Fragments.ShowCallLog;
import com.example.finalproject.Models.DatasetModel;
import com.example.finalproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class ShowNextLocation extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;


    Double cluster1x;
    Double cluster1y;
    Double numberofpointscluster1;

    Double cluster2x;
    Double cluster2y;
    Double numberofpointscluster2;

    Double cluster3x;
    Double cluster3y;
    Double numberofpointscluster3;

    Double CurrentLongitude=91.9694221;Double CurrentLatitude=22.460109699999993;
    Double nearx=22.460109699999993,neary=91.9694221,mostx=22.460109699999993,mosty=91.9694221;
    String message;
    DatabaseReference MLDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_next_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        BitmapDescriptor bitmapDescriptor
                = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_AZURE);

        Intent i  = getIntent();
        message = i.getStringExtra("message");
        Toast.makeText(ShowNextLocation.this,message,Toast.LENGTH_LONG).show();
        cluster1x = Double.parseDouble(i.getStringExtra("cluster1x").substring(0,9));
        cluster1y = Double.parseDouble(i.getStringExtra("cluster1y").substring(0,9));
        numberofpointscluster1 = Double.parseDouble(i.getStringExtra("numberofpointscluster1"));

        cluster2x = Double.parseDouble(i.getStringExtra("cluster2x").substring(0,9));
        cluster2y = Double.parseDouble(i.getStringExtra("cluster2y").substring(0,9));
        numberofpointscluster2 = Double.parseDouble(i.getStringExtra("numberofpointscluster2"));

        cluster3x = Double.parseDouble(i.getStringExtra("cluster3x").substring(0,9));
        cluster3y = Double.parseDouble(i.getStringExtra("cluster3y").substring(0,9));
        numberofpointscluster3 = Double.parseDouble(i.getStringExtra("numberofpointscluster3"));

        CurrentLongitude = Double.parseDouble(i.getStringExtra("CurrentLongitude").substring(0,9));
        CurrentLatitude = Double.parseDouble(i.getStringExtra("CurrentLatitude").substring(0,9));


        if(numberofpointscluster1>=numberofpointscluster3 && numberofpointscluster1>=numberofpointscluster2)
        { mostx=cluster1x;mosty=cluster1y; }
        if(numberofpointscluster2>=numberofpointscluster3 && numberofpointscluster2>=numberofpointscluster1)
        { mostx=cluster2x;mosty=cluster2y; }
        if(numberofpointscluster3>=numberofpointscluster1 && numberofpointscluster3>=numberofpointscluster2)
        { mostx=cluster3x;mosty=cluster3y; }

        double minDistance = 999999999;
        double d = DistanceCalculate(cluster1x,cluster1y,CurrentLongitude,CurrentLatitude,minDistance);
        ///Toast.makeText(ShowNextLocation.this,"d1 = "+Double.toString(d),Toast.LENGTH_SHORT).show();
        if(d<minDistance){  nearx=cluster1x;neary=cluster1y; minDistance=d; }
        d = DistanceCalculate(cluster2x,cluster2y,CurrentLongitude,CurrentLatitude,minDistance);
        ///Toast.makeText(ShowNextLocation.this,"d1 = "+Double.toString(d),Toast.LENGTH_SHORT).show();
        if(d<minDistance){  nearx=cluster2x;neary=cluster2y; minDistance=d; }
        d = DistanceCalculate(cluster3x,cluster3y,CurrentLatitude,CurrentLongitude,minDistance);
        if(d<minDistance){  nearx=cluster3x;neary=cluster3y; minDistance=d; }

        ///Toast.makeText(ShowNextLocation.this,"d = "+Double.toString(d),Toast.LENGTH_SHORT).show();

        ///Toast.makeText(ShowNextLocation.this,Double.toString(nearx)+Double.toString(neary),Toast.LENGTH_LONG).show();
        ///Toast.makeText(ShowNextLocation.this,Double.toString(mostx)+Double.toString(mosty),Toast.LENGTH_LONG).show();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        MLDataset = FirebaseDatabase.getInstance().getReference().child("Dataset").child(firebaseUser.getUid()).child(message);

        String id = MLDataset.push().getKey();
        MLDataset.child(id).setValue(new DatasetModel(Double.toString(CurrentLatitude),Double.toString(CurrentLongitude)));
    }

    static double DistanceCalculate(double clusterx, double clustery, double currentx,double currenty,double min) {
        double distance = Math.sqrt(Math.pow((clusterx - currentx), 2) + Math.pow((clustery - currenty), 2));
        return distance;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng BD = new LatLng(mostx,mosty);
        mMap.addMarker(new MarkerOptions().position(BD).title("Nearest Next Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(BD));

        LatLng AB = new LatLng(CurrentLatitude,CurrentLongitude);
        moveToCurrentLocation(AB);
        mMap.addMarker(new MarkerOptions().position(AB).title("Current Position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(AB));

        LatLng CD = new LatLng(nearx,neary);
        moveToCurrentLocation(CD);
        mMap.addMarker(new MarkerOptions().position(CD).title("Next Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(CD));





    }

    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,16));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);


    }

}
