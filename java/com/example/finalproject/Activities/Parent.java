package com.example.finalproject.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.finalproject.Fragments.Activity_Predict_Fragment;
import com.example.finalproject.Fragments.Contact_List_Fragment;
import com.example.finalproject.Fragments.Foot_Print_Fragment;
import com.example.finalproject.Fragments.History_Fragment;
import com.example.finalproject.Fragments.HomeFragment;
import com.example.finalproject.Fragments.InstalledAppFragment;
import com.example.finalproject.Fragments.RuningApp_Fragment;
import com.example.finalproject.Fragments.ShowCallLog;
import com.example.finalproject.Fragments.Next_Location_Fragment;
import com.example.finalproject.R;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;

public class Parent extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Fragment newFragment = new HomeFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.frameContainer, newFragment);
            ft.addToBackStack(null);
            ft.commit();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        Fragment newFragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frameContainer, newFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.parent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_home) {

        } else if (id == R.id.nav_contact) {
            Fragment newFragment = new Contact_List_Fragment();
            displaySelectedFragment(newFragment);

        } else if (id == R.id.nav_run_app) {
            Fragment newFragment = new RuningApp_Fragment();
            displaySelectedFragment(newFragment);

        } else if (id == R.id.nav_call_log) {
            Fragment newFragment = new ShowCallLog();
            displaySelectedFragment(newFragment);

        } else if (id == R.id.nav_app) {
            Fragment fragment = new InstalledAppFragment();
            displaySelectedFragment(fragment);
        }
        else if (id == R.id.nav_history) {
            Fragment fragment = new History_Fragment();
            displaySelectedFragment(fragment);

        }else if (id == R.id.nav_todaysTrack) {
            Fragment fragment = new Foot_Print_Fragment();
            displaySelectedFragment(fragment);

        }else if (id == R.id.nav_menu_predict) {
            Fragment fragment = new Next_Location_Fragment();
            displaySelectedFragment(fragment);

        }else if (id == R.id.nav_signOut) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Parent.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
            startActivity(intent);
        }else if(id==R.id.nav_menu_predict_activity){
            Fragment fragment = new Activity_Predict_Fragment();
            displaySelectedFragment(fragment);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void displaySelectedFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, fragment);
        fragmentTransaction.commit();
    }
}
