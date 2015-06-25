package com.example.pedro.endogen;


import android.net.Uri;
import android.support.v4.app.FragmentTransaction;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.pedro.endogen.Fragments.SelectMapFragment;
import com.google.android.gms.maps.MapFragment;


public class SelectMapActivity extends ActionBarActivity implements SelectMapFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_map);
        SelectMapFragment newFragment = new SelectMapFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_container,(Fragment)newFragment);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
