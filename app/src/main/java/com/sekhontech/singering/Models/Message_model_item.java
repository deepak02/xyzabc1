package com.sekhontech.singering.Models;

/**
 * Created by ST_004 on 23-11-2016.
 */

public class Message_model_item {
    public String id;
    public String from;
    public String to;
    public String message;
    public String read;
    public String time;


    public Message_model_item() {
    }

    public Message_model_item(String id, String from, String to, String message, String read,String time) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.read = read;
        this.time=time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }
}
