package com.example.finalproject.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Activities.Activity_Predict;
import com.example.finalproject.Models.Naive;
import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Activity_Predict_Fragment extends Fragment {

    DatabaseReference MLDataset;
    Button predicButton;
    TextView textView ;
    String timee="1";
    String loocation="QK";
    int fcnt=0,bcnt=0,ycnt=0;
    //String s;
    final Map<String, Integer> facebook = new HashMap<String, Integer>();
    final Map<String, Integer> youtube = new HashMap<String, Integer>();
    final Map<String, Integer> browser = new HashMap<String, Integer>();
    Double facebookTotal=1.0,facebookInstanceTotal=1.0,youtubeTotal=1.0,youtubeInstanceTotal=1.0;
    Double browserTotal=1.0,browserInstanceTotal;

    public Activity_Predict_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_activity__predict_, container, false);
        textView = view.findViewById(R.id.bodytext);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        ///DatabaseReference databaseCluserData = FirebaseDatabase.getInstance().getReference().child("ClusterData").child(firebaseUser.getUid());
        MLDataset = FirebaseDatabase.getInstance().getReference().child("databaseNaiveDataset").child(firebaseUser.getUid());
        predicButton = view.findViewById(R.id.predicButton);
        predicButton.setVisibility(View.INVISIBLE);
        Start();
        predicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookTotal=((facebook.get(timee)*1.0)/(fcnt*2))*((facebook.get(loocation)*1.0)/(fcnt*2))*((fcnt*1.0)/(fcnt+ycnt+bcnt));
                youtubeTotal=((youtube.get(timee)*1.0)/(fcnt*2))*((youtube.get(loocation)*1.0)/(fcnt*2))*((ycnt*1.0)/(fcnt+ycnt+bcnt));
                browserTotal=((browser.get(timee)*1.0)/(fcnt*2))*((browser.get(loocation)*1.0)/(fcnt*2))*((bcnt*1.0)/(fcnt+ycnt+bcnt));
                System.out.println("Facebook = "+facebookTotal);
                System.out.println("Youtube = "+youtubeTotal);
                System.out.println("Browser = "+browserTotal);

                System.out.println("Facebook = "+facebook.get(loocation));
                System.out.println("Facebook = "+(facebook.get(timee)));

                System.out.println("youtube = "+youtube.get(loocation));
                System.out.println("youtube = "+(youtube.get(timee)));

                System.out.println("browser = "+browser.get(loocation));
                System.out.println("browser = "+(browser.get(timee)));

                ///System.out.println((fcnt*1.0/(fcnt+ycnt+bcnt)));
                String ans;
                if(facebookTotal>=youtubeTotal){
                    if(facebookTotal>=browserTotal){ans="facebook";}
                    else ans="browser";
                }else{
                    if(youtubeTotal>=browserTotal) ans="youtube";
                    else ans="browser";
                }
                Intent intent=new Intent(getActivity(), Activity_Predict.class) ;
                intent.putExtra("ans",ans);
                intent.putExtra("time",timee);
                intent.putExtra("location",loocation);

                startActivity(intent);
            }
        });

        return view;
    }

    public void  Start() {

        MLDataset.child("facebook").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                      fcnt++;
                      Naive obj = snapshot.getValue(Naive.class);
                      String location = obj.getLocation();
                      String time = obj.getTime();
                      //System.out.println("facebook");
                      ///Toast.makeText(getActivity(),time,Toast.LENGTH_SHORT).show();
                      Integer t=facebook.get(time);
                      //System.out.println("111111111111111111111111111111111111111111111111111111111111111111111111111");
                      ///System.out.println(t);
                      ///Toast.makeText(getActivity(),Integer.toString(t),Toast.LENGTH_SHORT).show();
                      if(t!=null){
                          t++;
                      }else{
                          t=1;
                      }
                    facebook.put(time,t);
                    t = facebook.get(location);
                    if(t!=null){
                        t++;
                    }else{
                        t=1;
                    }
                    facebook.put(location,t);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



       MLDataset.child("youtube").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ycnt++;
                    Naive obj = snapshot.getValue(Naive.class);
                    String location = obj.getLocation();
                    String time = obj.getTime();
                    Integer t = youtube.get(time);
                    if (youtube.get(time) == null) {
                        t = 1;
                    } else {
                        t++;
                    }
                    youtube.put(time, t);

                    t = youtube.get(location);
                    if (youtube.get(location) == null) {
                        t = 1;
                    } else {
                        t++;
                    }
                    youtube.put(location, t);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        MLDataset.child("chrome").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    bcnt++;
                    Naive obj = snapshot.getValue(Naive.class);
                    String location = obj.getLocation();
                    String time = obj.getTime();
                    Integer t = browser.get(time);
                    if (browser.get(time) == null) {
                        t = 1;
                    } else {
                        t++;
                    }
                    browser.put(time, t);

                    t = browser.get(location);
                    if (browser.get(location) == null) {
                        t = 1;
                    } else {
                        t++;
                    }
                    browser.put(location, t);
                }
                predicButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
