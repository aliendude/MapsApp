/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.example.pedro.endogen.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import java.util.Date;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(name = "myApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.endogen.pedro.example.com", ownerName = "backend.endogen.pedro.example.com", packagePath = ""))
public class MyEndpoint {

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Hi, " + name);

        return response;
    }
    @ApiMethod(name = "createMapMarker")
    public MyBean createMapMarker(@Named("data") String data) {

        String[] splittedData=data.split(" ");
       try {
           DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

           Entity mapMarker = new Entity("MapMarker");
           mapMarker.setProperty("name", splittedData[0]);
           mapMarker.setProperty("startTime", splittedData[1]);
           mapMarker.setProperty("endTime", splittedData[2]);
           mapMarker.setProperty("location", splittedData[3] + " " + splittedData[4]);
           mapMarker.setProperty("numOfParticipants", splittedData[5]);
           mapMarker.setProperty("desc", splittedData[6]);
           datastore.put(mapMarker);

           MyBean response = new MyBean();
           response.setData("Marker created.");
           return response;
       }catch(Exception e){
            MyBean response = new MyBean();
            response.setData("Sorry, we couldn't create your marker.");
           return response;
       }

    }
}

