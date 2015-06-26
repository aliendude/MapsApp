package com.example.pedro.endogen;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.view.View;
import android.util.Pair;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pedro.endogen.Data.CallBackend;
import com.example.pedro.endogen.Fragments.*;
import com.example.pedro.endogen.Interface.TabListener;




public class MainActivity extends ActionBarActivity
        implements com.example.pedro.endogen.Fragments.LoginFragment.OnFragmentInteractionListener,
        MapFragment1.OnFragmentInteractionListener,
        CreateMapFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        ChatFragment.OnFragmentInteractionListener
{

    private String[] listElements={"Profile","Settings","About"};
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        //start login activity
        //Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //startActivity(intent);
        setContentView(R.layout.activity_main);

        //create the navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, listElements));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  //* host Activity *//*
                mDrawerLayout,         //* DrawerLayout object *//*
                R.drawable.ic_drawer,  //* nav drawer icon to replace 'Up' caret *//*
                R.string.drawer_open,  //* "open drawer" description *//*
                R.string.drawer_close  //* "close drawer" description *//*
        ) {

            //** Called when a drawer has settled in a completely closed state. *//*
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("Endogen");
            }

            //** Called when a drawer has settled in a completely open state. *//*
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Endogen");
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        ActionBar bar= getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab tab = bar.newTab()
                .setText(R.string.new_event)
                .setIcon(R.drawable.ic_action_map)
                .setTabListener(new TabListener<MapFragment1>(this, "Maps", MapFragment1.class));
        bar.addTab(tab);
        tab = bar.newTab()
                .setText(R.string.explore_events)
                .setIcon(R.drawable.ic_action_new)
                .setTabListener(new TabListener<CreateMapFragment>(this, "Create", CreateMapFragment.class));
        bar.addTab(tab);
        tab = bar.newTab()
                .setText(R.string.explore_events)
                .setIcon(R.drawable.ic_action_chat)
                .setTabListener(new TabListener<ChatFragment>(this, "Chat", ChatFragment.class));
        bar.addTab(tab);

        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        //bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        //bar.setIcon(R.drawable.ic_drawer1);
        bar.setHomeButtonEnabled(true);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.log_out) {
           // Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int position) {
        //falta implementar
        Log.e("pedro","fragment interaction");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //falta implementar
        Log.e("pedro","fragment interaction");
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
            Log.e("pedro",position+"");

        }

        /** Swaps fragments in the main content view */
        private void selectItem(int position) {
            if(position==0){
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                //fragmentManager.beginTransaction()
//                        .replace(R.id.frame, new EventFragment())
//                        .commit();

            }else
            if(position==1){
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.frame, new CreateEventFragment())
//                        .commit();

            }else
            if(position==2){
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.frame, new ProfileFragment())
//                        .commit();

            }
            else
            {
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.frame, new Fragment())
//                        .commit();

            }

            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            //setTitle(listElements);
            mDrawerLayout.closeDrawer(mDrawerList);
        }


    }
}


