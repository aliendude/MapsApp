package com.example.pedro.endogen.Fragments;

import android.app.Activity;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.example.pedro.endogen.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



public class MapFragment1 extends Fragment{

    MapView mMapView;
    private GoogleMap googleMap;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private double mLongitude;
    private double mLatitude;

    public static MapFragment1 newInstance(String param1, String param2) {
        MapFragment1 fragment = new MapFragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public void onSelLocPressed(){

        Log.e("pedro","pressed");
    }
    public MapFragment1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view =  inflater.inflate(R.layout.fragment_map, container, false);
            mMapView = (MapView) view.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);

            mMapView.onResume();// needed to get the map to display immediately

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            googleMap = mMapView.getMap();

            //æ geolocator example...
            //Geocoder geocoder = new Geocoder(getActivity());
            //List address =geocoder.getFromLocationName("1001 Manitou Ave Manitou Springs, CO 80829", 1);
            //Address location = (Address)address.get(0);
            //location.getLatitude();
            //location.getLongitude();

            //7. the mountain man:
            double latitude =38.8592041;
            double longitude = -104.9197969;
            // create marker
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Mountain Man | 1001 Manitou Avenue | 645-9861 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            // adding marker
            googleMap.addMarker(marker);

            //1: Ruxton's Trading Post
            latitude =38.858855;
            longitude = -104.9205125;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Ruxton's Trading Post | 22 Ruxton Ave | 685-9042 | B1");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            // adding marker
            googleMap.addMarker(marker);

            //2: Tracy Miller Studio Gallery
            latitude = 38.858972;
            longitude = -104.9203596;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Tracy Miller Studio Gallery | 16 Ruxton Ave | 650-0827 | B1");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            // adding marker
            googleMap.addMarker(marker);


            //3: Fare Bella Gallery/Edge Custom
            latitude = 38.8589803;
            longitude = -104.9202603;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Fare Bella Gallery/Edge Custom Framing | 14 Ruxton Ave | 720-226-4315 | B1");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            // adding marker
            googleMap.addMarker(marker);

            //4 :Piazza Navona Art Gallery & Cafe
            latitude =38.859008;
            longitude = -104.920079;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Piazza Navona Art Gallery & Cafe | 12 Ruxton Ave | 685-1077 | B1");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            // adding marker
            googleMap.addMarker(marker);
            //5 : Power of Touch Massage & Body Work
            latitude = 38.859033;
            longitude = -104.920011;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Power of Touch Massage & Body Work | 10 1/2 Ruxton Ave | 573-864-5790 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            // adding marker
            googleMap.addMarker(marker);
            //6: the hemp store
            latitude =38.859131;
            longitude = -104.9198539;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Hemp Store | 2 Ruxxon Ave | 685-1189 | B1");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            // adding marker
            googleMap.addMarker(marker);

            //8: 38.858900, -104.919896
            //9: 38.858921, -104.919778
            //10: 38.858926, -104.919595
            //11: 38.858892, -104.919221
            //12: 38.858860, -104.919160
            //13: 38.858873, -104.919099
            //14: 38.858849, -104.918986
            //15: 38.858858, -104.918863
            //16: 38.858828, -104.918750
            //17: 38.858801, -104.918551
            //18: 38.858717, -104.918326
            //19: 38.858558, -104.918109
            //20: 38.858394, -104.917970
            //21: 38.858184, -104.917823
            //22: 38.8580379,-104.9178538
            //23: 38.857911, -104.917739
            //24: 38.857758, -104.917153
            //25: 38.857603, -104.916858
            //26: 38.857566, -104.916708
            //27: 38.857525, -104.916640
            //28: 38.857385, -104.916421
            //29: 38.857358, -104.916310
            //30: 38.857344, -104.916224
            //31: 38.857342, -104.916142
            //32: 38.857257, -104.916036
            //33: 38.857236, -104.915888
            //34: 38.857207, -104.915810
            //35: 38.857171, -104.915652
            //36; 38.857096, -104.915683
            //37: 38.857033, -104.915434
            //38: 38.856996, -104.915329
            //39: 38.856979, -104.915234
            //40: 38.856974, -104.915163
            //41: 38.857349, -104.914473
            //42: 38.857344, -104.915057
            //43: 38.857303, -104.915075
            //44: 38.857323, -104.915191
            

            //adding user's location marker
            try {
                getLocation();
                // user's location:
                marker = new MarkerOptions().position(new LatLng(mLatitude,mLongitude)).title("You");
                // Changing marker icon
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                // adding marker
                googleMap.addMarker(marker);

            }catch (Exception e){
                Log.e("pedro","could not get your location");
            }


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(38.858641, -104.918241)).zoom(16).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            Button buttonGoToMyLocationPressed = (Button) view.findViewById(R.id.button_go_to_my_location);
            buttonGoToMyLocationPressed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onGoToMyLocationPressed(v);
                }
            });

        } catch (InflateException e) {
           e.printStackTrace();
        }
        return view;
    }
    public void onGoToMyLocationPressed(View view) {
        getLocation();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mLatitude,mLongitude)).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getLocation() {
        // Get the location manager
        LocationManager locationManager = (LocationManager)
                getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        LocationListener loc_listener = new LocationListener() {

            public void onLocationChanged(Location l) {}

            public void onProviderEnabled(String p) {}

            public void onProviderDisabled(String p) {}

            public void onStatusChanged(String p, int status, Bundle extras) {}
        };
        locationManager
                .requestLocationUpdates(bestProvider, 0, 0, loc_listener);
        location = locationManager.getLastKnownLocation(bestProvider);
        try {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
        } catch (NullPointerException e) {
            mLatitude = -1.0;
            mLongitude = -1.0;
        }
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
