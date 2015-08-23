package com.example.pedro.myapplication.backend1.Apis;

import com.example.pedro.myapplication.backend1.Model.MapMarker;
import com.example.pedro.myapplication.backend1.Model.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.List;
import java.util.logging.Logger;

import static com.example.pedro.myapplication.backend1.OfyService.ofy;

/**
 * Created by pedro on 23/07/15.
 */
@Api(name = "users", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend1.myapplication.pedro.example.com", ownerName = "backend1.myapplication.pedro.example.com", packagePath = ""))
public class UserEndPoint {

    private static final Logger log = Logger.getLogger(UserEndPoint.class.getName());

    @ApiMethod(name = "addUser")
    public void addUser( User user) {
        ofy().save().entity(user).now();
    }

}
