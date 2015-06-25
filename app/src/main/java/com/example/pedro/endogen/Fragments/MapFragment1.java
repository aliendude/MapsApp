package com.example.pedro.endogen.Fragments;

import android.app.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pedro.endogen.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



public class MapFragment1 extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(38.858641, -104.918241)).zoom(16).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

        } catch (InflateException e) {

        }
        return view;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
