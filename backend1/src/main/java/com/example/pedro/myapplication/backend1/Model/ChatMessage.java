package com.example.pedro.myapplication.backend1.Model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by pedro on 10/29/15.
 */
@Entity
public class ChatMessage {
    @Id
    private Long id;
    @Index
    private Ref<User> created_by;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }

    private String message;

    private String time_created;

    public void setCreator(User value) { created_by = Ref.create(value); }

}
