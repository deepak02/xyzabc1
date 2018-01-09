package com.sekhontech.singering.Models;

import java.io.Serializable;

/**
 * Created by ST_004 on 25-10-2016.
 */

public class Playlist_model_item implements Serializable {

    public String id;
    public String by;
    public String name;
    public String description;
    public String value_check;
    public String total_tracks;
    public String image;

public Playlist_model_item()
{
}

    public Playlist_model_item(String id, String by, String name, String description, String value_check,String total_tracks,String image) {
        this.id = id;
        this.by = by;
        this.name = name;
        this.description = description;
        this.value_check = value_check;
        this.total_tracks=total_tracks;
        this.image=image;
    }

    public Playlist_model_item(String id, String track_count) {
        this.id = id;
        this.total_tracks=track_count;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue_check() {
        return value_check;
    }

    public void setValue_check(String value_check) {
        this.value_check = value_check;
    }

    public String getTotal_tracks() {
        return total_tracks;
    }

    public void setTotal_tracks(String total_tracks) {
        this.total_tracks = total_tracks;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
