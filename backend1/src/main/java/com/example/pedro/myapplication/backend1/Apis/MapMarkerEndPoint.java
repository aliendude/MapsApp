package com.example.pedro.myapplication.backend1.Apis;

import com.example.pedro.myapplication.backend1.Model.MapMarker;
import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.users.User;

import java.util.List;
import java.util.logging.Logger;

import static com.example.pedro.myapplication.backend1.OfyService.ofy;

/**
 * Created by pedro on 23/07/15.
 */
@Api(name = "mapmarkers", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend1.myapplication.pedro.example.com", ownerName = "backend1.myapplication.pedro.example.com", packagePath = ""))
public class MapMarkerEndPoint {

    private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());

    @ApiMethod(name = "addMarker")
    public void addMarker( MapMarker marker) {
        ofy().save().entity(marker).now();
    }
    @SuppressWarnings({"cast", "unchecked"})
    @ApiMethod(name = "getMapMarkers")
    public List<MapMarker> getMapMarkers()
    {
        return ofy().load().type(MapMarker.class).list();
    }
}
