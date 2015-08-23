package com.example.pedro.myapplication.backend1.Apis;

import com.example.pedro.myapplication.backend1.Model.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;

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

    @ApiMethod(name= "logIn")
    public User logIn(@Named("username") String username, @Named("password") String password){

        User current= ofy().load().type(User.class).filter("username",username).first().now();
        if(current != null){
            log.info("One found");
            log.info(current.getPassword());
            log.info(password);
            if(current.getPassword().equals( password)){
                log.info("Pass accepted");
                return current;

            }
        }
        return null;

    }

}
