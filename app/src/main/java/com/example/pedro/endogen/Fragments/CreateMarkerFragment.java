package com.example.pedro.endogen.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.pedro.endogen.Data.MapMarkerAsyncTask;
import com.example.pedro.endogen.R;
import com.example.pedro.endogen.SelectMapActivity;
import com.example.pedro.myapplication.backend1.mapmarkers.model.MapMarker;


public class CreateMarkerFragment extends Fragment {


    private OnFragmentInteractionListener mListener;


    public static CreateMarkerFragment newInstance() {
        CreateMarkerFragment fragment = new CreateMarkerFragment();

        return fragment;
    }

    public CreateMarkerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_create_map, container, false);
        Button buttonSelectLocation = (Button)view.findViewById(R.id.buttonSelectLocation);
        buttonSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onButtonSelectLocationPressed(v);
            }
        });
        Button buttonCreateMap = (Button) view.findViewById(R.id.buttonCreateMap);
        buttonCreateMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onButtonCreateMapPressed(v);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            String lat = data.getStringExtra("lat");
            String lng = data.getStringExtra("lng");

            TextView labelLat=(TextView)getActivity().findViewById(R.id.labelLat);
            labelLat.setText(lat);
            TextView labelLng=(TextView)getActivity().findViewById(R.id.labelLng);
            labelLng.setText(lng);
        }catch(Exception e){}

    }
    public void onButtonSelectLocationPressed(View v)
    {
        Intent intent = new Intent(getActivity(), SelectMapActivity.class);
        startActivityForResult(intent, 0);
    }
    public void onButtonCreateMapPressed(View v)
    {
        EditText nameEt= (EditText)getActivity().findViewById(R.id.editText);
        String name= nameEt.getText()+"";
        EditText startTimeEt= (EditText)getActivity().findViewById(R.id.editText2);
        String startTime=startTimeEt.getText()+"";
        EditText endTimeEt= (EditText)getActivity().findViewById(R.id.editText3);
        String endTime=endTimeEt.getText()+"";
        TextView labelLat=(TextView)getActivity().findViewById(R.id.labelLat);
        String latlng=labelLat.getText()+"";
        TextView labelLng=(TextView)getActivity().findViewById(R.id.labelLng);
        latlng+=" "+labelLng.getText();
        EditText nOfParticipantsEt= (EditText)getActivity().findViewById(R.id.editText4);
        String nOfParticipants=nOfParticipantsEt.getText()+"";
        EditText descEt= (EditText)getActivity().findViewById(R.id.editText5  );
        String desc=descEt.getText()+"";

        String[] strArr= new String[2];
        strArr[0]="createMapMarker";
        MapMarker m = new MapMarker();
        m.setName(name);
        m.setStart(startTime);
        m.setEnd(endTime);
        m.setLocation(latlng);
        m.setNparticipants(nOfParticipants);
        m.setDescription(desc);
        MapMarkerAsyncTask callbackend1 = new MapMarkerAsyncTask(getActivity());
        callbackend1.execute(new Pair<String[],MapMarker>(strArr,m));
        getActivity().finish();
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
