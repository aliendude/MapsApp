package com.example.pedro.endogen;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import com.example.pedro.endogen.Fragments.ChatFragment;
import com.example.pedro.endogen.Fragments.CreateMarkerFragment;
import com.example.pedro.endogen.Fragments.CreatedMarkersFragment;
import com.example.pedro.endogen.Fragments.MapFragment1;
import com.example.pedro.endogen.Fragments.ProfileFragment;

public class LoggedUserActivity extends ActionBarActivity implements
        MapFragment1.OnFragmentInteractionListener,
        CreateMarkerFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        ChatFragment.OnFragmentInteractionListener,
        CreatedMarkersFragment.OnFragmentInteractionListener,
         ActionBar.TabListener{

    private String[] listElements={"Profile","Settings","About"};
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_user);
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
                //getSupportActionBar().setTitle("Manitou Springs");
            }

            //** Called when a drawer has settled in a completely open state. *//*
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // getSupportActionBar().setTitle("Manitou Springs");
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        final ActionBar bar= getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                bar.setSelectedNavigationItem(position);
            }
        });
        ActionBar.Tab tab = bar.newTab()
                .setText(R.string.explore_events)
                .setIcon(R.drawable.ic_action_map)
                .setTabListener(this);
        bar.addTab(tab);
        tab = bar.newTab()
                .setText(R.string.new_event)
                .setIcon(R.drawable.ic_action_new)
                .setTabListener(this);
        bar.addTab(tab);
        tab = bar.newTab()
                .setText(R.string.chat)
                .setIcon(R.drawable.ic_action_chat)
                .setTabListener(this);
        bar.addTab(tab);

        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        //bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        //bar.setIcon(R.drawable.ic_drawer1);
        bar.setHomeButtonEnabled(true);


    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        MapFragment1 mapFragment =  new MapFragment1();
        CreatedMarkersFragment createdMarkersFragment =new CreatedMarkersFragment();
        ChatFragment chatFragment = new ChatFragment();
        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    return mapFragment;
                case 1:
                    return createdMarkersFragment;
                default:
                    return chatFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }
    }

    public void onButtonCreateMarkerPressed(View v) {
        Intent intent = new Intent(LoggedUserActivity.this, CreateMarkerActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logged_user, menu);
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
            Intent intent = new Intent();
            setResult(2, intent);
            mAppSectionsPagerAdapter.mapFragment.destroy();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int position) {
        //falta implementar
        Log.e("pedro", "fragment interaction");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //falta implementar
        Log.e("pedro", "fragment interaction");
    }

    @Override
    public void onFragmentInteraction(String id) {
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
    @Override
    protected void onStart() {
        super.onStart();
        Globals.isStopped=false;
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        // Activity being restarted from stopped state
        Globals.isStopped=false;
    }
    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        Globals.isStopped=true;

    }


}
