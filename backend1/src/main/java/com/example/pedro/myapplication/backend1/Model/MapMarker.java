package com.example.pedro.myapplication.backend1.Model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;
/**
 * Created by pedro on 23/07/15.
 */
@Entity
public class MapMarker {

    @Id
    private Long key;

    private String name;

    private String start;

    private String end;

    private String location;

    private String nparticipants;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNparticipants() {
        return nparticipants;
    }

    public void setNparticipants(String nparticipants) {
        this.nparticipants = nparticipants;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
