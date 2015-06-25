package com.example.pedro.endogen.Fragments;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.example.pedro.endogen.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class SelectMapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static SelectMapFragment newInstance(String param1, String param2) {
        SelectMapFragment fragment = new SelectMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public void onSelLocPressed(){
        Log.e("pedro","pressed");
    }
    public SelectMapFragment() {
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


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(38.858641, -104.918241)).zoom(16).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            // Setting a click event handler for the map
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(final LatLng latLng) {
                    // Creating a marker
                    MarkerOptions markerOptions = new MarkerOptions();
                    // Setting the position for the marker
                    markerOptions.position(latLng);
                    // Setting the title for the marker.
                    // This will be displayed on taping the marker
                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                    // Clears the previously touched position
                    googleMap.clear();
                    // Animating to the touched position
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    // Placing a marker on the touched position
                    googleMap.addMarker(markerOptions);

                    Button myButton = new Button(getActivity());
                    myButton.setText("Select location");
                    myButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {

                            Intent intent = new Intent();
                            intent.putExtra("lat", latLng.latitude);
                            intent.putExtra("lng", latLng.longitude);
                            Log.e("pedroo","lat "+latLng.latitude+" lng "+ latLng.longitude);
                            getActivity().setResult(getActivity().RESULT_OK, intent);
                            getActivity().finish();

                        }
                    });
                    FrameLayout ll = (FrameLayout)getActivity().findViewById(R.id.main_container);
                    RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                    ll.addView(myButton, lp);
                }
            });

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
