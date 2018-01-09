package com.sekhontech.singering.Models;

import java.io.Serializable;

/**
 * Created by ST_004 on 19-09-2016.
 */
public class Search_model_item implements Serializable {
    public String user_id;
    public String username;
    public String first_name;
    public String last_name;
    public String country;
    public String city;
    public String image;


    public Search_model_item(){}


    public Search_model_item(String user_id,String username,String first_name,String last_name,String country,String city,String image)
    {
        this.user_id=user_id;
        this.username=username;
        this.first_name=first_name;
        this.last_name=last_name;
        this.country=country;
        this.city=city;
        this.image=image;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
