package com.sekhontech.singering.Models;

import java.io.Serializable;

/**
 * Created by ST_004 on 22-09-2016.
 */

public class Following_model_item implements Serializable {

    public String user_id;
    public String username;
    public String first_name;
    public String last_name;
    public String country;
    public String city;
    public String image;
    public String follower_count;
    public String tracks_count;
    public String likes_count;

    public Following_model_item(){}


    public Following_model_item(String user_id, String username, String first_name, String last_name, String country, String city, String image, String follower_count, String tracks_count, String likes_count)
    {
        this.user_id=user_id;
        this.username=username;
        this.first_name=first_name;
        this.last_name=last_name;
        this.city=city;
        this.country=country;
        this.image=image;
        this.follower_count=follower_count;
        this.tracks_count=tracks_count;
        this.likes_count=likes_count;
    }


}
