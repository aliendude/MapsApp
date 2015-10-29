package com.example.pedro.endogen.Fragments;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedro.endogen.Constants;
import com.example.pedro.endogen.Globals;
import com.example.pedro.endogen.R;
import com.example.pedro.endogen.SessionManager;
import com.example.pedro.myapplication.backend1.mapmarkers.Mapmarkers;
import com.example.pedro.myapplication.backend1.mapmarkers.model.MapMarker;
import com.example.pedro.myapplication.backend1.mapmarkers.model.MapMarkerCollection;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


public class MapFragment1 extends Fragment{

    MapView mMapView;
    private GoogleMap googleMap;


    private OnFragmentInteractionListener mListener;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private double mLongitude;
    private double mLatitude;
    private static View view;
    private Socket mSocket;
    private int numUsers;
    private String mUsername;
    private Map<String, Marker> usersMarkersMap =new HashMap<>();
    private Marker user_marker;

    {
        try {
            //socket to handle the calls to the location tracking server
            mSocket = IO.socket(Constants.LOC_TRACKING_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public static MapFragment1 newInstance(String param1, String param2) {
        MapFragment1 fragment = new MapFragment1();
        return fragment;
    }
    public void onSelLocPressed(){

        Log.e("pedro", "pressed");
    }
    public MapFragment1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("location changed", onLocationChanged);
        mSocket.on("user left", onUserLeft);
        mSocket.on("login", onLogin);
        mSocket.connect();
        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
        mUsername = sessionManager.getUserDetails().get("name");
        getLocation();
        mSocket.emit("add user", mUsername, mLongitude + "," + mLatitude);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        destroy();
    }
    public void destroy(){

        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new mesage", onNewMessage);
        mSocket.off("location changed", onLocationChanged);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        mSocket.off("login", onLogin);
    }

    public Socket getmSocket() {
        return mSocket;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Log.e("pedro","created");

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view =  inflater.inflate(R.layout.fragment_map, container, false);
            TextView nParticipantsText= (TextView) view.findViewById(R.id.location_tracking_nusers_text);
            //this will change when the socket connects
            nParticipantsText.setText("connecting...");
            mMapView = (MapView) view.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);

            mMapView.onResume();// needed to get the map to display immediately

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            googleMap = mMapView.getMap();
            new MapMarkerAsyncRetriever().execute();
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
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //1: Ruxton's Trading Post
            latitude =38.858855;
            longitude = -104.9205125;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Ruxton's Trading Post | 22 Ruxton Ave | 685-9042 | B1");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //2: Tracy Miller Studio Gallery
            latitude = 38.858972;
            longitude = -104.9203596;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Tracy Miller Studio Gallery | 16 Ruxton Ave | 650-0827 | B1");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);


            //3: Fare Bella Gallery/Edge Custom Framing
            latitude = 38.8589803;
            longitude = -104.9202603;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Fare Bella Gallery/Edge Custom Framing | 14 Ruxton Ave | 720-226-4315 | B1");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //4 :Piazza Navona Art Gallery & Cafe
            latitude =38.859008;
            longitude = -104.920079;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Piazza Navona Art Gallery & Cafe | 12 Ruxton Ave | 685-1077 | B1");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);
            //5 : Power of Touch Massage & Body Work
            latitude = 38.859033;
            longitude = -104.920011;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Power of Touch Massage & Body Work | 10 1/2 Ruxton Ave | 573-864-5790 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);
            //6: the hemp store
            latitude =38.859131;
            longitude = -104.9198539;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Hemp Store | 2 Ruxxon Ave | 685-1189 | B1");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //8: 38.858900, -104.919896 Hell's Kitchen Pizza | 9 Ruxton Ave | 685-4355 | B1

            latitude = 38.858900;
            longitude = -104.91989;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Hell's Kitchen Pizza | 9 Ruxton Ave | 685-4355 | B1");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //9: 38.858921, -104.919778 Cotton Club | 1 Ruxton Ave | 685-9234 | B1

            latitude =  38.858921;
            longitude = -104.919778;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Cotton Club | 1 Ruxton Ave | 685-9234 | B1");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);


            //10: 38.858926, -104.919595 The Loop | 965 Manitou Ave | 685-9344 | B2

            latitude =  38.858926;
            longitude = -104.919595;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Loop | 965 Manitou Ave | 685-9344 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //11: 38.858892, -104.919221 Osburn's Gift Shop | 951 Manitou Ave | 685-9614 | B2

            latitude =  38.858892;
            longitude = -104.919221;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Osburn's Gift Shop | 951 Manitou Ave | 685-9614 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //12: 38.858860, -104.919160 Russell Design Studio | 949 Manitou Ave | 685-1404 | B2

            latitude =   38.858860;
            longitude = -104.919160;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Russell Design Studio | 949 Manitou Ave | 685-1404 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //13: 38.858873, -104.919099 The Eagle Dancer 947 | Manitou Ave | 685-9462 | B2

            latitude =  38.858873;
            longitude = -104.919099;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Eagle Dancer 947 | Manitou Ave | 685-9462 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //14: 38.858849, -104.918986 The Leprechaun Shoppe | 943 Manitou Ave | 685-9213 | B2

            latitude =   38.858849;
            longitude = -104.918986;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Leprechaun Shoppe | 943 Manitou Ave | 685-9213 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //15: 38.858858, -104.918863 White Bear Traders | 941 Manitou Ave | 685-4600 | B2

            latitude =  38.858858;
            longitude = -104.918863;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("White Bear Traders | 941 Manitou Ave | 685-4600 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //16: 38.858828, -104.918750 Mushroom Monday Gifts | 937 Manitou Ave | 685-1142 | B2

            latitude =   38.858828;
            longitude = -104.918750;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Mushroom Monday Gifts | 937 Manitou Ave | 685-1142 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //17: 38.858801, -104.918551 European Café | 935 Manitou Ave | 685-3556 | B2
            latitude =   38.858801;
            longitude = -104.918551;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("European Café | 935 Manitou Ave | 685-3556 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //18: 38.858717, -104.918326 Gordon's Gold & Silver | 925 Manitou Ave | 640-0246 | B2

            latitude =  38.858717;
            longitude = -104.918326;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Gordon's Gold & Silver | 925 Manitou Ave | 640-0246 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //19: 38.858558, -104.918109 La Tienda | 921 Manitou Ave | 685-1961 | B3

            latitude =  38.858558;
            longitude = -104.918109;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("La Tienda | 921 Manitou Ave | 685-1961 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //20: 38.858394, -104.917970

            latitude = 38.858394;
            longitude =-104.917970;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //21: 38.858184, -104.917823 Susie Q's BBQ | 915 Manitou Ave | 282-0206 | B3

            latitude = 38.858184;
            longitude =  -104.917823 ;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Susie Q's BBQ | 915 Manitou Ave | 282-0206 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //22: 38.8580379,-104.9178538 Townhouse Lounge | 907 Manitou Ave | 685-1085 | B3

            latitude = 38.8580379;
            longitude =  -104.9178538;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Townhouse Lounge | 907 Manitou Ave | 685-1085 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //23: 38.857911, -104.917739 Olde Tyme Photography | 903 Manitou Ave | 685-9718 | B3

            latitude =   38.857911;
            longitude =  -104.917739;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Olde Tyme Photography | 903 Manitou Ave | 685-9718 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //24: 38.857758, -104.917153 PJ's Continental Bistro | 819 Manitou Ave | 685-1195 | C3

            latitude =    38.857758;
            longitude = -104.917153;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("PJ's Continental Bistro | 819 Manitou Ave | 685-1195 | C3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //25: 38.857603, -104.916858 Manitou Outpost | 807 Manitou Ave | 685-5026 | C3

            latitude =  38.857603;
            longitude = -104.916858;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title(" Manitou Outpost | 807 Manitou Ave | 685-5026 | C3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //26: 38.857566, -104.916708 Pikes Peak Chocolate | 805 Manitou Ave | 685-9600 | C3

            latitude =   38.857566;
            longitude =  -104.916708;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Pikes Peak Chocolate | 805 Manitou Ave | 685-9600 | C3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //27: 38.857525, -104.916640 La Henna Boheme | 801 Manitou Ave | 636-2626 | C4 (?) Silver Sparrow Beads | 803 Manitou Ave | 685-1226 | C4

            latitude =    38.857525;
            longitude =  -104.916640;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("La Henna Boheme | 801 Manitou Ave | 636-2626 | C4 (?) Silver Sparrow Beads | 803 Manitou Ave | 685-1226 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //28: 38.857385, -104.916421 Mountain Living Studio | 741 Manitou Ave | 685-0225 | C4

            latitude =    38.857385;
            longitude =  -104.916421;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Mountain Living Studio | 741 Manitou Ave | 685-0225 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //29: 38.857358, -104.916310 Red Dog Coffee | 739 Manitou Ave | 634-2626 | C4

            latitude =    38.857358;
            longitude =   -104.916310;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Red Dog Coffee | 739 Manitou Ave | 634-2626 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //30: 38.857344, -104.916224 Lane Mitchell Jewelers | 737 Manitou Ave | 685-2441 | C4

            latitude =    38.857344;
            longitude =   -104.916224;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title(" Lane Mitchell Jewelers | 737 Manitou Ave | 685-2441 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //31: 38.857342, -104.916142 Piramide Clothing Company | 735 Manitou Ave | 685-5912 | C4

            latitude =     38.857342;
            longitude =   -104.916142;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Piramide Clothing Company | 735 Manitou Ave | 685-5912 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //32: 38.857257, -104.916036 Mona Lisa Fondue Restaurant | 733 Manitou Ave | 685-0277 | C4

            latitude =     38.857257;
            longitude =   -104.916036;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Mona Lisa Fondue Restaurant | 733 Manitou Ave | 685-0277 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //33: 38.857236, -104.915888 Green Horse Gallery | 729 Manitou Ave | 685-0636 | C4

            latitude =     38.857236;
            longitude =   -104.915888;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Green Horse Gallery | 729 Manitou Ave | 685-0636 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //34: 38.857207, -104.915810 Spice Of Life | 727 Manitou Ave | 685-5284 | C4

            latitude =     38.857207;
            longitude =  -104.915810;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Spice Of Life | 727 Manitou Ave | 685-5284 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //35: 38.857171, -104.915652 Le Grande Accents Boutique| 725 Manitou Ave #1 | 685-5779 | C4

            latitude =     38.857171;
            longitude =  -104.915652;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Le Grande Accents Boutique| 725 Manitou Ave #1 | 685-5779 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //36; 38.857096, -104.915683 Manitou Brewing Company | 725 Manitou Ave #2 | 282-7709 | C4

            latitude =     38.857096;
            longitude =  -104.915683;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Manitou Brewing Company | 725 Manitou Ave #2 | 282-7709 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //37: 38.857033, -104.915434 C.K. Comics and Collectibles | 719 Manitou Ave | 344-9045 | C5

            latitude =    38.857033;
            longitude =   -104.915434;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("C.K. Comics and Collectibles | 719 Manitou Ave | 344-9045 | C5");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //38: 38.856996, -104.915329 Swirl Wine Bar | 717 Manitou Ave | 685-2294 | C5

            latitude =    38.856996;
            longitude =  -104.915329;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Swirl Wine Bar | 717 Manitou Ave | 685-2294 | C5");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //39: 38.856979, -104.915234 Third Eye Art Gallery | 715 Manitou Ave | 310-5733 | C5

            latitude =    38.856979;
            longitude =  -104.915234;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Third Eye Art Gallery | 715 Manitou Ave | 310-5733 | C5");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //40: 38.856974, -104.915163 Homes of Manitou | 713 Manitou Ave | 685-1212 | C5

            latitude =    38.856974;
            longitude =  -104.915163;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title(" Homes of Manitou | 713 Manitou Ave | 685-1212 | C5");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //41: 38.857349, -104.914473 Stage Coach Inn | 702 Manitou Ave | 685-9400 | C5

            latitude =   38.857349;
            longitude =  -104.914473;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Stage Coach Inn | 702 Manitou Ave | 685-9400 | C5");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //42: 38.857344, -104.915057 Messages Jayne's Way | 718 1/2 Manitou Ave | 439-1343 | C5

            latitude =   38.857344;
            longitude =  -104.915057;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Messages Jayne's Way | 718 1/2 Manitou Ave | 439-1343 | C5");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //43: 38.857303, -104.915075 Heart of Jerusalem Cafe | 718 Manitou Ave | 685-1315 | C5

            latitude =   38.857303;
            longitude =  -104.915075;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title(" Heart of Jerusalem Cafe | 718 Manitou Ave | 685-1315 | C5");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //44: 38.857323, -104.915191 Pasha Frozen Yogurt & Smoothies | 720 Manitou Ave | 694-9236 | C5

            latitude =    38.857323;
            longitude =  -104.915191;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Pasha Frozen Yogurt & Smoothies | 720 Manitou Ave | 694-9236 | C5");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);


            //45: 38.857450, -104.915346 Mavi Turkish Arts | 724 Manitou Ave | 229-3790 | B2

            latitude =    38.857450;
            longitude = -104.915346;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Mavi Turkish Arts | 724 Manitou Ave | 229-3790 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //46: 38.857423, -104.915444 Christmas in Manitou Gift Shop | 726 Manitou Ave | 685-4290 | C5

            latitude =    38.857423;
            longitude = -104.915444;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Christmas in Manitou Gift Shop | 726 Manitou Ave | 685-4290 | C5");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //47: 38.857488, -104.915473 Gigi's Animal Lovers Gift Shop | 728 Manitou Ave | 685-4772 | C5

            latitude =   38.857488;
            longitude = -104.915473;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Gigi's Animal Lovers Gift Shop | 728 Manitou Ave | 685-4772 | C5");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //48: 38.857519, -104.915528 The Keg Bar & Grill | 730 Manitou Ave | 685-9531 | C5

            latitude =   38.857519;
            longitude = -104.915528;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Keg Bar & Grill | 730 Manitou Ave | 685-9531 | C5");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //49: 38.857553, -104.915598 Mountains West T-Shirt Co. | 732 Manitou Ave | 685-9005 | C4

            latitude =    38.857553;
            longitude =  -104.915598;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title(" Mountains West T-Shirt Co. | 732 Manitou Ave | 685-9005 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //50: 38.857587, -104.915685 734 Manitou Ave | C4

            latitude =    38.857587;
            longitude =  -104.915685;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("734 Manitou Ave | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //51: 38.857636, -104.915748 The Taos Maos | 736 Manitou Ave | 685-1299 | C4

            latitude =   38.857636;
            longitude =  -104.915748;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Taos Maos | 736 Manitou Ave | 685-1299 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //52: 38.857643, -104.915790 740 Manitou Ave | C4

            latitude =    38.857643;
            longitude =  -104.915790;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("740 Manitou Ave | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //53: 38.857714, -104.915802 Whickerbill Gifts | 742 Manitou Ave | 685-1540 | C4

            latitude =    38.857714;
            longitude =  -104.915802;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Whickerbill Gifts | 742 Manitou Ave | 685-1540 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //54: 38.857729, -104.915839 The Ten Spot |742A Manitou Ave | 685-1545 | C4

            latitude =   38.857729;
            longitude =  -104.915839;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Ten Spot |742A Manitou Ave | 685-1545 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //55: 38.857741, -104.915888 Stick 'Em Up Decals | 744 Manitou Ave | 385-7788 | C4

            latitude =    38.857741;
            longitude =   -104.915888;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Stick 'Em Up Decals | 744 Manitou Ave | 385-7788 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //56: 38.857742, -104.915979 Glass Blowers of Manitou | 4 Cañon Ave | 685-1555 | C4

            latitude =    38.857742;
            longitude =   -104.915979;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Glass Blowers of Manitou | 4 Cañon Ave | 685-1555 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //57: 38.857780, -104.916061 Twin Beards Embroidery | 8 Cañon Ave | 685-1360 | C4

            latitude =   38.857780;
            longitude =   -104.916061;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Twin Beards Embroidery | 8 Cañon Ave | 685-1360 | C4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //58: 38.857987, -104.916168 Commonwheel Artists Co-op | 102 Cañon Ave | 685-1008 | B4

            latitude =    38.857987;
            longitude =   -104.916168;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Commonwheel Artists Co-op | 102 Cañon Ave | 685-1008 | B4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //59: 38.858068, -104.916235 Rocky Mountain Way Mercantile | 106 Cañon Ave | 685-1314 | B4

            latitude =    38.858068;
            longitude =   -104.916235;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Rocky Mountain Way Mercantile | 106 Cañon Ave | 685-1314 | B4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //60: 38.858119, -104.916254 108 Cañon Ave | B4

            latitude =    38.858119;
            longitude =   -104.916254;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("108 Cañon Ave | B4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //61: 38.858188, -104.916288 Good Karma Coffee Lounge & Deli | 110A Cañon Ave | 685-2325 | B4

            latitude =   38.858188;
            longitude =   -104.916288;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Good Karma Coffee Lounge & Deli | 110A Cañon Ave | 685-2325 | B4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //62: 38.858313, -104.916358 Goldminers Nuts & Candy | 110B Cañon Ave | 685-5302 | B4

            latitude =    38.858313;
            longitude =   -104.916358;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Goldminers Nuts & Candy | 110B Cañon Ave | 685-5302 | B4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //63: 38.858362, -104.916421 Manitou Chiropractic & Naturopath | 114 Cañon Ave | 685-1155 | B4

            latitude =     38.858362;
            longitude =    -104.916421;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Manitou Chiropractic & Naturopath | 114 Cañon Ave | 685-1155 | B4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //64: 38.858396, -104.916485 Local First Grocer | 116 Cañon Ave | 685-1501 | B4

            latitude =      38.858396;
            longitude =    -104.916485;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Local First Grocer | 116 Cañon Ave | 685-1501 | B4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //65: 38.858920, -104.916851 Crystal Wizard | 130 Cañon Ave | 685-1998 | B4

            latitude =      38.858920;
            longitude =    -104.916851;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Crystal Wizard | 130 Cañon Ave | 685-1998 | B4");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //66: 38.859696, -104.917262 The Cliff House / Red Mountain Bar & Grill | 306 Cañon Ave | 685-3000 | A3

            latitude =      38.859696;
            longitude =    -104.917262;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Cliff House / Red Mountain Bar & Grill | 306 Cañon Ave | 685-3000 | A3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //67: 38.858526, -104.916870 The Poppy Seed | 123 Cañon Ave | 685-5200 | C3

            latitude =      38.858526;
            longitude =    -104.916870;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Poppy Seed | 123 Cañon Ave | 685-5200 | C3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //68: 38.858079, -104.916959 Manitou Jack's | 814 Manitou Ave | 685-5004 | B3

            latitude =     38.858079;
            longitude =    -104.916959;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Manitou Jack's | 814 Manitou Ave | 685-5004 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //69: 38.858251, -104.917347 Colorado Custard Company | 906 Manitou Ave #100 | 685-5400 | B3

            latitude =     38.858251;
            longitude =    -104.917347;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Colorado Custard Company | 906 Manitou Ave #100 | 685-5400 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //70: 38.858353, -104.917258 Colorado Custom Metals | 906 Manitou Ave #101 | 227-0633 | B3

            latitude =     38.858353;
            longitude =    -104.917258;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Colorado Custom Metals | 906 Manitou Ave #101 | 227-0633 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //71: 38.858338, -104.917410 The Manitou Kitchen Shop | 906 Manitou Ave #102 | 685-9900 | B3

            latitude =     38.858338;
            longitude =    -104.917410;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Manitou Kitchen Shop | 906 Manitou Ave #102 | 685-9900 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //72: 38.858431, -104.917324 The Olive Tap | 906 Manitou Ave #103 | 358-9329 | B3

            latitude =     38.858431;
            longitude =    -104.917324;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Olive Tap | 906 Manitou Ave #103 | 358-9329 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //73: 38.858475, -104.917368 906 Manitou Ave #104 | B3

            latitude =     38.858475;
            longitude =    -104.917368;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("906 Manitou Ave #104 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //74: 38.858511, -104.917402 Salus | 906 Manitou Ave #105 | 685-1121 | B3

            latitude =     38.858511;
            longitude =    -104.917402;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Salus | 906 Manitou Ave #105 | 685-1121 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //75: 38.858548, -104.917429 Olive Tree Traders | 906 Manitou Ave #106 | 685-1443 | B3

            latitude =     38.858548;
            longitude =    -104.917429;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Olive Tree Traders | 906 Manitou Ave #106 | 685-1443 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //76: 38.858596, -104.917582 Bear Cave Imports | 906 Manitou Ave #107 | 634-5400 | B3

            latitude =     38.858596;
            longitude =    -104.917582;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Bear Cave Imports | 906 Manitou Ave #107 | 634-5400 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //77: 38.858710, -104.917592 Smoking Gift Headquarters | 918 Manitou Ave | 368-7276 | B3

            latitude =     38.858710;
            longitude =    -104.917592;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Smoking Gift Headquarters | 918 Manitou Ave | 368-7276 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //78: 38.858757, -104.917672 La Chemere Gift Shop | 920 Manitou Ave | 685-9992 | B3

            latitude =     38.858757;
            longitude =    -104.917672;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("La Chemere Gift Shop | 920 Manitou Ave | 685-9992 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //79: 38.859037, -104.917579 Penny Arcade & Amusements | 900 Manitou Ave | 685-9815 | B3

            latitude =    38.859037;
            longitude =   -104.917579;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Penny Arcade & Amusements | 900 Manitou Ave | 685-9815 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //80: 38.859026, -104.917268 Wood Studio | 205 Cañon Ave | 685-2414 | B3

            latitude =    38.859026;
            longitude =   -104.917268;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Wood Studio | 205 Cañon Ave | 685-2414 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //81: 38.858843, -104.917791 Royal Tavern | 924 Manitou Ave | 685-1404 | B2

            latitude =    38.858843;
            longitude =   -104.917791;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Royal Tavern | 924 Manitou Ave | 685-1404 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //82: 38.858903, -104.917977 Patsy's Chocolate & Gift Shop | 930 Manitou Ave | 685-9437 | B3

            latitude =    38.858903;
            longitude =    -104.917977;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Patsy's Chocolate & Gift Shop | 930 Manitou Ave | 685-9437 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //83: 38.859226, -104.917611 Mountain High Gallery & Gifts | 11 Arcade | 685-5396 | B3

            latitude =    38.859226;
            longitude =    -104.917611;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Mountain High Gallery & Gifts | 11 Arcade | 685-5396 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //84: 38.859314, -104.918000 D'Vine Whine | 934 Manitou Ave #108 | 685-10-30 | B3 (?) Darpino Studio Gallery | 934 Maniou Ave #107 | 339-9788 | B3 (?) Loft Expresso | 934 Manitou Ave #105 | 373-0582 | B3 (?) Theo's Toys | 934 Manitou Ave #103 | 247-8126 | B3 (?) 934 Manitou Ave #100 | B3
            latitude =    38.859314;
            longitude =   -104.918000;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("D'Vine Whine | 934 Manitou Ave #108 | 685-10-30 | B3 (?) Darpino Studio Gallery | 934 Maniou Ave #107 | 339-9788 | B3 (?) Loft Expresso | 934 Manitou Ave #105 | 373-0582 | B3 (?) Theo's Toys | 934 Manitou Ave #103 | 247-8126 | B3 (?) 934 Manitou Ave #100 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //85: 38.859172, -104.918585 Flying Eagle | 946 Manitou Ave | 685-5221 | B2

            latitude =    38.859172;
            longitude =    -104.917611;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Flying Eagle | 946 Manitou Ave | 685-5221 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //86: 38.858119, -104.916254 Santa Fe Springs 948 Manitou Ave | 685-0175 | B2

            latitude =    38.858119;
            longitude =   -104.916254;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Santa Fe Springs 948 Manitou Ave | 685-0175 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);


            //87: 38.859160, -104.918774 Kinfolks Mountain Shop | 950 Manitou Ave | 418-6180 | B2

            latitude =    38.858119;
            longitude =   -104.916254;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Santa Fe Springs 948 Manitou Ave | 685-0175 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //88: 38.859189, -104.918853 Southwest Silver Company | 952 Manitou Ave | 685-9197 | B3

            latitude =    38.859189;
            longitude =    -104.918853;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Southwest Silver Company | 952 Manitou Ave | 685-9197 | B3");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //89: 38.859206, -104.918924 Sahara Cafe | 954 Manitou Ave | 685-2303 | B2

            latitude =    38.859206;
            longitude =    -104.918924;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Sahara Cafe | 954 Manitou Ave | 685-2303 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //90: 38.859231, -104.919044 Mountain High Sportswear | 958 Manitou Ave | 645-9861 | B2

            latitude =    38.859231;
            longitude =    -104.919044;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Mountain High Sportswear | 958 Manitou Ave | 645-9861 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //91: 38.859242, -104.919198 Ancient Mariner Tavern | 962 Manitou Ave | 685-5503 | B2
            latitude =    38.859242;
            longitude =    -104.919198;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Ancient Mariner Tavern | 962 Manitou Ave | 685-5503 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //92: 38.859275, -104.919316 Marilyn's House of Fine Pizza | 964 Manitou Ave | 685-9104 | B2

            latitude =    38.859275;
            longitude =     -104.919316;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("Marilyn's House of Fine Pizza | 964 Manitou Ave | 685-9104 | B2");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //93: 38.859306, -104.919385 The Maté Factor | 966 Manitou Ave | 685-3235 | B2

            latitude =    38.859306;
            longitude =     -104.919385;
            // create marker
            marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title("The Maté Factor | 966 Manitou Ave | 685-3235 | B22");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_building));
            // adding marker
            googleMap.addMarker(marker);

            //adding user's location marker
            try {

                //getLocation();
                // user's location:
                marker = new MarkerOptions().position(new LatLng(mLatitude,mLongitude)).title("You");
                // Changing marker icon
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_user));
                // adding marker
                user_marker=googleMap.addMarker(marker);

            }catch (Exception e){
                Log.e("pedro","could not get your location");
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(38.858641, -104.918241)).zoom(17).build();
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(numUsers!=0)addParticipantsLog(numUsers);
    }
    public void onGoToMyLocationPressed(View view) {
        getLocation();
        user_marker.remove();
        MarkerOptions marker = new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("You");
        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_user));
        // adding marker
        user_marker=googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mLatitude,mLongitude)).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
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

    private void addParticipantsLog(int numUsers){
        TextView nParticipantsText= (TextView) view.findViewById(R.id.location_tracking_nusers_text);
        nParticipantsText.setText("Users online: "+numUsers);
        //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
    private void addLog(String log){
        Toast.makeText(getActivity(), log, Toast.LENGTH_LONG).show();
    }
    private int errorCount=0;
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   if(errorCount <20 || errorCount==0){
                       errorCount++;
                   }else{
                       errorCount=0;
                    Toast.makeText(getActivity().getApplicationContext(),R.string.error_connect2, Toast.LENGTH_LONG).show();
                       TextView nParticipantsText= (TextView) view.findViewById(R.id.location_tracking_nusers_text);
                       //this will change when the socket connects
                       nParticipantsText.setText("offline");
                   }

                }
            });
        }
    };
    private Emitter.Listener onLocationChanged = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String username = data.getString("username");
                        String location = data.getString("location").trim();
                        String latitude = location.split(",")[0];
                        String longitude = location.split(",")[1];
                            Log.e("pedro",username+" change location");
                        if(usersMarkersMap.containsKey(username)){
                            Marker usermarker=usersMarkersMap.get(username);
                            usermarker.remove();
                            usersMarkersMap.remove(username);

                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(Double.parseDouble(latitude),Double.parseDouble( longitude))).title(username);
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_user));
                            Marker marker= googleMap.addMarker(markerOptions);
                            usersMarkersMap.put(username,marker);
                            //usersMarkersMap.(username)
                        }else{
                            // create marker
                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(Double.parseDouble(latitude),Double.parseDouble( longitude))).title(username);
                            // Changing marker icon
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_user));
                            // adding marker
                            Log.e("pedro","marker added");
                            Log.e("lat",latitude);
                            Log.e("lon",longitude);
                            Marker marker= googleMap.addMarker(markerOptions);
                            usersMarkersMap.put(username,marker);
                        }

                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }

    };
    private Emitter.Listener onLogin = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        numUsers = data.getInt("numUsers");

                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("pedro",getResources().getString(R.string.message_welcome));
                    addParticipantsLog(numUsers);
                }
            });
        }

    };
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }
                    // addMessage(username, message);
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String location, latitude,longitude;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                        location = data.getString("location").trim();
                        latitude= location.split(",")[0];
                        longitude= location.split(",")[1];
                    } catch (JSONException e) {
                        return;
                    }
                    if(usersMarkersMap.containsKey(username)){
                        Marker usermarker=usersMarkersMap.get(username);
                        usermarker.remove();
                        usersMarkersMap.remove(username);

                        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(Double.parseDouble(latitude),Double.parseDouble( longitude))).title(username);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_user));
                        Marker marker= googleMap.addMarker(markerOptions);
                        usersMarkersMap.put(username,marker);
                        //usersMarkersMap.(username)
                    }else{
                        // create marker
                        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(Double.parseDouble(latitude),Double.parseDouble( longitude))).title(username);
                        // Changing marker icon
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_user));
                        // adding marker
                        Log.e("pedro","marker added");
                        Log.e("lat",latitude);
                        Log.e("lon",longitude);
                        Marker marker= googleMap.addMarker(markerOptions);
                        usersMarkersMap.put(username,marker);
                    }
                    addLog(getResources().getString(R.string.message_user_joined, username));
                    addParticipantsLog(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;

                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }

                    addLog(getResources().getString(R.string.message_user_left, username));
                    addParticipantsLog(numUsers);

                }
            });
        }
    };
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private void getLocation() {
        // Get the location manager
        LocationManager locationManager = (LocationManager)
                getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            //Log.e("pedro","si");
            buildAlertMessageNoGps();
        }
        else{
            //Log.e("pedro","no");
        }
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        final int[] locationChangedCounter = {-1};
        LocationListener loc_listener = new LocationListener() {

            public void onLocationChanged(Location l) {
                locationChangedCounter[0]++;
                if(locationChangedCounter[0]>1000 || locationChangedCounter[0]==0)
                {
                    locationChangedCounter[0]=0;
                    user_marker.remove();
                    // user's location:
                    mLatitude=l.getLatitude();
                    mLongitude=l.getLongitude();
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(l.getLatitude(), l.getLongitude())).title("You");
                    // Changing marker icon
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_user));
                    // adding marker
                    user_marker=googleMap.addMarker(marker);

                    mSocket.emit("location changed", l.getLatitude() + "," + l.getLongitude());
                   // Log.e("pedro","location_changed");
                    try {
                        Toast.makeText(getActivity(), "location changed", Toast.LENGTH_LONG).show();
                    }catch(NullPointerException e)
                    {
                        destroy();
                    }
                }
                //
            }
            public void onProviderEnabled(String p) {}
            public void onProviderDisabled(String p) {}
            public void onStatusChanged(String p, int status, Bundle extras) {}
        };
        locationManager.requestLocationUpdates(bestProvider, 0, 0, loc_listener);
        //location = locationManager.getLastKnownLocation(bestProvider);
        try {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
        } catch (NullPointerException e) {
            mLatitude = -1.0;
            mLongitude = -1.0;
            //Toast.makeText(getActivity(), "Please enable your GPS", Toast.LENGTH_LONG).show();
        }
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }


    private class MapMarkerAsyncRetriever extends AsyncTask<Void, Void, MapMarkerCollection>
    {

        public MapMarkerAsyncRetriever() {

        }
        private Mapmarkers mapMarkersService = null;
        @Override
        protected MapMarkerCollection doInBackground(Void... params) {
            if (mapMarkersService == null) {
                Mapmarkers.Builder builder = new Mapmarkers.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                        // otherwise they can be skipped
                        .setRootUrl(Constants.APPENGINE_URL)
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                    throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end of optional local run code

                mapMarkersService = builder.build();
            }
            try {
                return  mapMarkersService.getMapMarkers().execute();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final MapMarkerCollection result) {

            try{

                for (MapMarker element : result.getItems()) {
                    double longitude = Double.parseDouble(element.getLocation().split(" ")[1]);
                    double latitude = Double.parseDouble(element.getLocation().split(" ")[0]);
                    // create marker
                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title(element.getDescription());
                    // Changing marker icon
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_arrow_down));
                    // adding marker
                    Marker marker= googleMap.addMarker(markerOptions);

                }
            } catch(Exception e)
            {
                e.printStackTrace();
            }

        }
    }



}