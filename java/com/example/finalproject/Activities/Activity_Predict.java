package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.finalproject.Models.DatasetModel;
import com.example.finalproject.Models.Naive;
import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Activity_Predict extends AppCompatActivity {

    DatabaseReference MLDataset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__predict);

        Intent intent = getIntent();
        String str = intent.getStringExtra("ans");
        String time = intent.getStringExtra("time");
        String location = intent.getStringExtra("location");
        TextView textView = findViewById(R.id.textView);
        textView.setText(str);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        MLDataset = FirebaseDatabase.getInstance().getReference().child("databaseNaiveDataset").child(firebaseUser.getUid()).child("ans");

        String id = MLDataset.push().getKey();
        MLDataset.child(id).setValue(new Naive(time,location));

    }
}
