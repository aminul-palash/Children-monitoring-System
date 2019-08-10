package com.example.finalproject.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.finalproject.Adapters.CallLogArrayAdapter;
import com.example.finalproject.Models.CallLogModel;
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

public class ShowCallLog extends Fragment {

    DatabaseReference databaseCall;
    ListView listView ;
            List<CallLogModel> CallLogsList = new ArrayList<CallLogModel>();
    public ShowCallLog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ///getActivity().setTitle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_call_log, container, false);
        // Inflate the layout for this fragment
        listView = view.findViewById(R.id.listViewCallLogs);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseCall = FirebaseDatabase.getInstance().getReference().child("CallLog").child(firebaseUser.getUid());

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        databaseCall.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //CallLogsList.clear();
                for (DataSnapshot callSnap : dataSnapshot.getChildren()) {
                    CallLogModel object = callSnap.getValue(CallLogModel.class);
                    ///Toast.makeText(ShowCallLogs.this,object.getCallName()+object.getCallNumber(),Toast.LENGTH_SHORT).show();
                    CallLogsList.add(object);
                }
                CallLogArrayAdapter adapter = new CallLogArrayAdapter(getActivity(),CallLogsList);

                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
