package com.example.pedro.endogen.Data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import com.example.pedro.myapplication.backend1.mapmarkers.model.MapMarker;
import com.example.pedro.myapplication.backend1.registration.Registration;
import com.example.pedro.myapplication.backend1.mapmarkers.Mapmarkers;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by pedro on 23/07/15.
 */
public class MapMarkerAsyncTask extends AsyncTask<Pair< String[], MapMarker>, Void, String>{
    private static Mapmarkers mapMarkersService = null;
    private GoogleCloudMessaging gcm;
    private Context context;


    public MapMarkerAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Pair<String[], MapMarker>... params) {
        if (mapMarkersService == null) {
            Mapmarkers.Builder builder = new Mapmarkers.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                    // otherwise they can be skipped
                    .setRootUrl("http://10.0.3.2:8080/_ah/api/")
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

        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }

            msg = "Marker created";
            String[] data = params[0].first;
            MapMarker markerToCreate = params[0].second;


            if (data[0]=="createMapMarker")
            {
                mapMarkersService.addMarker(markerToCreate).execute();
            }
            else if (data[0]=="getMarkers")
            {

            }

        } catch (IOException ex) {
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
    }

}
