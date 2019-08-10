package com.example.finalproject.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.finalproject.Adapters.CallLogArrayAdapter;
import com.example.finalproject.Adapters.HistoryArrayAdapter;
import com.example.finalproject.Models.CallLogModel;
import com.example.finalproject.Models.HistoryModel;
import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstalledAppFragment extends Fragment {

    DatabaseReference databaseHistory;
    ListView listView ;
    List<String> AppList = new ArrayList<String>();

    public InstalledAppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_, container, false);
        // Inflate the layout for this fragment
        listView = view.findViewById(R.id.listViewHistory);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseHistory = FirebaseDatabase.getInstance().getReference().child("InstalledApp").child(firebaseUser.getUid());

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        databaseHistory.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AppList.clear();

                for (DataSnapshot callSnap : dataSnapshot.getChildren()) {
                    String object = callSnap.getValue(String.class);

                    ////Toast.makeText(getActivity(),object,Toast.LENGTH_SHORT).show();
                    AppList.add(object);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(),R.layout.installedapp_layout_view,(ArrayList<String>) AppList);

                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
