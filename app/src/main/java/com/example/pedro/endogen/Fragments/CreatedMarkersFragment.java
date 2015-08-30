package com.example.pedro.endogen.Fragments;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pedro.endogen.Constants;
import com.example.pedro.endogen.R;
import com.example.pedro.myapplication.backend1.mapmarkers.Mapmarkers;
import com.example.pedro.myapplication.backend1.mapmarkers.model.MapMarker;
import com.example.pedro.myapplication.backend1.mapmarkers.model.MapMarkerCollection;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class CreatedMarkersFragment extends Fragment implements AbsListView.OnItemClickListener {



    ArrayList markers= new ArrayList<MapMarker>();


    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;
    private View view;


    public static CreatedMarkersFragment newInstance() {
        CreatedMarkersFragment fragment = new CreatedMarkersFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CreatedMarkersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MapMarkerAsyncRetriever().execute();
        Log.e("pedro","fragment created!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_createdmarkers, container, false);

        return view;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
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
                if(result.size()>0)
                {
                    markers= (ArrayList) result.getItems();
                    mAdapter = new ListViewMarkerAdapter(getActivity(),markers);
                    // Set the adapter
                    mListView = (ListView) view.findViewById(R.id.markers_list_view);
                    mListView.setAdapter(mAdapter);

                    // Set OnItemClickListener so we can be notified on item clicks
                    mListView.setOnItemClickListener(CreatedMarkersFragment.this);
                }

            } catch(Exception e)
            {
                e.printStackTrace();
            }

        }
    }
}