package com.example.finalproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.finalproject.Custom.CustomToast;
import com.example.finalproject.Models.DatasetModel;
import com.example.finalproject.Models.LocationModel;
import com.example.finalproject.Models.Naive;
import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVReader;

import java.io.InputStreamReader;
import java.util.Calendar;

public class TypeSelect extends AppCompatActivity {


    RadioButton parent,children;
    String type="";
    String data="YES";
    Button Enterbutton;
    DatabaseReference databaseLocation,databaseClusterData ,databaseDataset,databaseNaiveDataset;
    String Longitude="91.9694221";
    String Latitude="22.460109699999993";
    ProgressBar progressBar;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_select);
        Enterbutton=  findViewById(R.id.enterbutton);
        progressBar=findViewById(R.id.progressbar);
        ///Enterbutton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        relativeLayout = findViewById(R.id.relativelayout);
        relativeLayout.setVisibility(View.INVISIBLE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        ///DatabaseReference databaseCluserData = FirebaseDatabase.getInstance().getReference().child("ClusterData").child(firebaseUser.getUid());
       /// NightDataset = FirebaseDatabase.getInstance().getReference().child("NightDataset").child(firebaseUser.getUid());
       /// NoonDataset = FirebaseDatabase.getInstance().getReference().child("NoonDataset").child(firebaseUser.getUid());
        ///AfternoonDataset = FirebaseDatabase.getInstance().getReference().child("AfternoonDataset").child(firebaseUser.getUid());
        databaseLocation = FirebaseDatabase.getInstance().getReference().child("Location").child(firebaseUser.getUid());
        databaseClusterData = FirebaseDatabase.getInstance().getReference().child("ClusterData").child(firebaseUser.getUid());
        databaseDataset = FirebaseDatabase.getInstance().getReference().child("Dataset").child(firebaseUser.getUid());
        databaseNaiveDataset = FirebaseDatabase.getInstance().getReference().child("databaseNaiveDataset").child(firebaseUser.getUid());

        //parent.setVisibility(View.INVISIBLE);
        //children.setVisibility(View.INVISIBLE);


        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.WRITE_CONTACTS,
                android.Manifest.permission.READ_CALL_LOG,
                android.Manifest.permission.WRITE_CALL_LOG,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.ACCESS_NETWORK_STATE,

        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }else {
            Enterbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ///Toast.makeText(TypeSelect.this,Longitude+Latitude,Toast.LENGTH_SHORT).show();
                    if(type.equals("")){
                        new CustomToast().Show_Toast(TypeSelect.this, view,
                                "Please Select a Type.....");
                    }else if(type.equals("parent")){

                        Intent i = new Intent(TypeSelect.this,Parent.class);
                        i.putExtra("Longitude",Longitude);
                        i.putExtra("Latitude",Latitude);
                        i.putExtra("signalData",data);
                        startActivity(i);
                    }
                    else{
                        Intent i = new Intent(TypeSelect.this,Children.class);

                        startActivity(i);
                    }


                }
            });
        }

        progressBar= findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);


    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_children:
                if (checked)
                    type = "children";
                    break;
            case R.id.radio_parent:
                if (checked)
                    type = "parent";
                    break;
        }
    }///

    @Override
    public void onStart() {
        super.onStart();


        databaseNaiveDataset.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null)
                {

                    try {
                        CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.data)));//Specify asset file name
                        String[] nextLine;
                        while ((nextLine = reader.readNext()) != null) {

                            String a = nextLine[0];
                            String b = nextLine[1];
                            String c = nextLine[2];

                            String id = databaseNaiveDataset.push().getKey();
                            if(c.equals("facebook"))
                            databaseNaiveDataset.child("facebook").child(id).setValue(new Naive(a,b));
                            if(c.equals("youtube"))
                            databaseNaiveDataset.child("youtube").child(id).setValue(new Naive(a,b));
                            if(c.equals("chrome"))
                            databaseNaiveDataset.child("chrome").child(id).setValue(new Naive(a,b));

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //databaseNaiveDataset.setValue(null);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });









        databaseLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    for(DataSnapshot locationSnap : dataSnapshot.getChildren()){
                        LocationModel object = locationSnap.getValue(LocationModel.class);
                        Longitude = object.getLongitude();
                        Latitude = object.getLatitude();
                    }
                    //progressBar.setVisibility(View.GONE);

                    //Enterbutton.setVisibility(View.VISIBLE);
                    //parent.setVisibility(View.VISIBLE);
                    //children.setVisibility(View.VISIBLE);
                }
                ///Toast.makeText(TypeSelect.this,Latitude+Longitude,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        databaseDataset.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()==null)
                    {

                        ///NightDataset entry....
                        try {
                            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.one)));//Specify asset file name
                            String[] nextLine;
                            while ((nextLine = reader.readNext()) != null) {
                                ///Double a = Double.parseDouble(nextLine[0]);
                                ///Double b = Double.parseDouble(nextLine[1]);
                                String a = nextLine[0];
                                String b = nextLine[1];

                                String id = databaseDataset.child("NightDataset").push().getKey();
                                databaseDataset.child("NightDataset").child(id).setValue(new DatasetModel(a, b));

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ///NoonDataset  Entry....

                        try {
                            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.two)));//Specify asset file name
                            String[] nextLine;
                            while ((nextLine = reader.readNext()) != null) {
                                ///Double a = Double.parseDouble(nextLine[0]);
                                ///Double b = Double.parseDouble(nextLine[1]);
                                String a = nextLine[0];
                                String b = nextLine[1];

                                String id = databaseDataset.child("NoonDataset").push().getKey();
                                databaseDataset.child("NoonDataset").child(id).setValue(new DatasetModel(a, b));

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ///AfternoonDataset entry....

                        try {
                            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.three)));//Specify asset file name
                            String[] nextLine;
                            while ((nextLine = reader.readNext()) != null) {
                                ///Double a = Double.parseDouble(nextLine[0]);
                                ///Double b = Double.parseDouble(nextLine[1]);
                                String a = nextLine[0];
                                String b = nextLine[1];

                                String id = databaseDataset.child("AfternoonDataset").push().getKey();
                                databaseDataset.child("AfternoonDataset").child(id).setValue(new DatasetModel(a, b));

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                       /// progressBar.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.VISIBLE);
                    }else{
                        //progressBar.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

}
