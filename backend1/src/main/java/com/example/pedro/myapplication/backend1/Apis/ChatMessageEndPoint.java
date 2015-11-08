package com.example.pedro.myapplication.backend1.Apis;

import com.example.pedro.myapplication.backend1.Model.ChatMessage;
import com.example.pedro.myapplication.backend1.Model.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.googlecode.objectify.Key;

import java.util.List;
import java.util.logging.Logger;

import static com.example.pedro.myapplication.backend1.OfyService.ofy;

/**
 * Created by pedro on 23/07/15.
 */
@Api(name = "chatmessages", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend1.myapplication.pedro.example.com", ownerName = "backend1.myapplication.pedro.example.com", packagePath = ""))
public class ChatMessageEndPoint {

    private static final Logger log = Logger.getLogger(ChatMessageEndPoint.class.getName());

    @ApiMethod(name = "addChatMessage")
    public void addChatMessage( @Named("creator_id") Long creator_id, ChatMessage chatmessage) {
        User creator = ofy().load().key(Key.create(User.class, creator_id)).now();
        // log.info("pedro 2020096"+creator.getUsername());
        chatmessage.setCreator(creator);
        ofy().save().entity(chatmessage).now();
    }
    @SuppressWarnings({"cast", "unchecked"})
    @ApiMethod(name = "getChatMessages")
    public List<ChatMessage> getChatMessages()
    {
        return ofy().load().type(ChatMessage.class).order("time_created").list();
    }

}
