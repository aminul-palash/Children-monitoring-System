package com.example.finalproject.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Activities.ShowNextLocation;
import com.example.finalproject.Models.ClusterData;
import com.example.finalproject.Models.DatasetModel;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.finalproject.Utils.Utils.PerformMlOperation;

public class Next_Location_Fragment extends Fragment {

    int records;
    double[][] points ,means;
    String cluster1x;
    String cluster1y;
    String numberofpointscluster1;

    String cluster2x;
    String cluster2y;
    String numberofpointscluster2;

    String cluster3x;
    String cluster3y;
    String numberofpointscluster3;
    DatabaseReference MLDataset;
    Button predicButton;
    String x,y;
    String message ="";

    public Next_Location_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_next__location_, container, false);
        // Inflate the layout for this fragment
        setBodyText(view);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseCluserData = FirebaseDatabase.getInstance().getReference().child("ClusterData").child(firebaseUser.getUid());
        MLDataset = FirebaseDatabase.getInstance().getReference().child("Dataset").child(firebaseUser.getUid());
        predicButton = view.findViewById(R.id.predicButton);
        predicButton.setVisibility(View.INVISIBLE);
        x = getActivity().getIntent().getStringExtra("Longitude");
        y = getActivity().getIntent().getStringExtra("Latitude");
        ///Toast.makeText(getActivity(),x+y,Toast.LENGTH_SHORT).show();

        predicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getActivity(), ShowNextLocation.class);
                //Toast.makeText(getActivity(),x+y,Toast.LENGTH_SHORT).show();
                intent.putExtra("cluster1x", cluster1x);
                intent.putExtra("cluster1y", cluster1y);
                intent.putExtra("numberofpointscluster1", numberofpointscluster1);

                intent.putExtra("cluster2x", cluster2x);
                intent.putExtra("cluster2y", cluster2y);
                intent.putExtra("numberofpointscluster2", numberofpointscluster2);

                intent.putExtra("cluster3x", cluster3x);
                intent.putExtra("cluster3y", cluster3y);
                intent.putExtra("numberofpointscluster3", numberofpointscluster3);

                intent.putExtra("CurrentLongitude", x);
                intent.putExtra("CurrentLatitude", y);
                intent.putExtra("message",message);

                startActivity(intent);
            }
        });

        return view;
    }

    public void MLCalculation(double[][] points,int records){
        List<ClusterData> clusterData = new ArrayList<>();
        clusterData = PerformMlOperation(points,records);
        cluster1x = clusterData.get(0).getCluster1x();
        cluster1y = clusterData.get(0).getCluster1y();
        numberofpointscluster1 = clusterData.get(0).getNumberofpointscluster1();

        cluster2x = clusterData.get(0).getCluster2x();
        cluster2y = clusterData.get(0).getCluster2y();
        numberofpointscluster2 = clusterData.get(0).getNumberofpointscluster2();

        cluster3x = clusterData.get(0).getCluster3x();
        cluster3y = clusterData.get(0).getCluster3y();
        numberofpointscluster3 = clusterData.get(0).getNumberofpointscluster3();

        ///Toast.makeText(getActivity(), cluster3x, Toast.LENGTH_SHORT).show();

    }

    private void setBodyText(View view) {
        TextView textView = view.findViewById(R.id.bodytext);
        String text = "Here we estimate the possible next location by analysing previous Footprint of users.It may not give" +
                " accurate location but to get proper estimation needs to use long time.<br><br>" +
                "<font color=#cc0029>we are going to display three location in Map marker which describe the places as follow:</font> <br><br>" +
                "1.Possible next Location after current location.<br><br>" +
                "2.Most Visisted place at this moment.<br><br>" +
                "3.User current Location<br><br>";
        textView.setText(Html.fromHtml(text));
    }


    @Override
    public void onStart() {
        super.onStart();
        message ="AfternoonDataset";
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if(hourOfDay>=19 || hourOfDay<=8) message="NightDataset";
        if(hourOfDay>8 && hourOfDay<=16) message="NoonDataset";
        if(hourOfDay>16 && hourOfDay<19) message="AfternoonDataset";
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
        //Toast.makeText(getActivity(),hourOfDay,Toast.LENGTH_SHORT).show();
        MLDataset.child(message).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 List<Pair<Double,Double>> pairList = new ArrayList<>();
                 double[][] coordinate;
                 int cnt=0;
                for(DataSnapshot Snap : dataSnapshot.getChildren()) {
                        DatasetModel datasetModel = Snap.getValue(DatasetModel.class);
                        Double a = Double.parseDouble(datasetModel.getX());
                        Double b = Double.parseDouble(datasetModel.getY());
                        pairList.add(new Pair<Double, Double>(a, b));
                        cnt++;
                }
                coordinate = new double[cnt][2];
                int l = 0;
                for (Pair<Double, Double> data : pairList) {
                    coordinate[l][0] = data.first;
                    coordinate[l][1] = data.second;
                    l++;
                }
                MLCalculation(coordinate,cnt);
                predicButton.setVisibility(View.VISIBLE);

            }///Snapsot.....
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
