package com.sekhontech.singering.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ST_004 on 14-12-2016.
 */

public class Notification_on_off_prefrence {
    private static final String FAVORITES = "favorite";
    private static Notification_on_off_prefrence instance = null;
    private Context context;
    private int count = 0;

  /*  public int getCount() {
        return mPreferences.getInt("count", 1);
    }*/

    public int getCountLikes() {
        return mPreferences.getInt("count_likes", 1);
    }

    public int getCountCommment() {
        return mPreferences.getInt("count_Commment", 1);
    }

    public int getCountChat() {
        return mPreferences.getInt("count_Chat", 1);
    }

    public int getCountFriends() {
        return mPreferences.getInt("count_Friends", 1);
    }

    public int getCountEmailComment() {
        return mPreferences.getInt("count_EmailComment", 1);
    }

    public int getCountEmailLikes() {
        return mPreferences.getInt("count_EmailLikes", 1);
    }

    public int getCountEmialFriends() {
        return mPreferences.getInt("count_EmialFriends", 1);
    }


   /*     public void setCount(int count) {
        editor = mPreferences.edit();
        editor.putInt("count", count);
        editor.commit();
    }*/


    public void setCountLikes(int count) {
        editor = mPreferences.edit();
        editor.putInt("count_likes", count);
        editor.apply();
    }

    public void setCountCommment(int count) {
        editor = mPreferences.edit();
        editor.putInt("count_Commment", count);
        editor.apply();
    }

    public void setCountChat(int count) {
        editor = mPreferences.edit();
        editor.putInt("count_Chat", count);
        editor.apply();
    }

    public void setCountFriends(int count) {
        editor = mPreferences.edit();
        editor.putInt("count_Friends", count);
        editor.apply();
    }

    public void setCountEmailComment(int count) {
        editor = mPreferences.edit();
        editor.putInt("count_EmailComment", count);
        editor.apply();
    }

    public void setCountEmailLikes(int count) {
        editor = mPreferences.edit();
        editor.putInt("count_EmailLikes", count);
        editor.apply();
    }

    public void setCountEmialFriends(int count) {
        editor = mPreferences.edit();
        editor.putInt("count_EmialFriends", count);
        editor.apply();
    }

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor editor;


    private Notification_on_off_prefrence(Context context) {
        if (context == null)
            return;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }


    public static Notification_on_off_prefrence getInstance(Context context) {
        if (instance == null || instance.context == null) {
            instance = new Notification_on_off_prefrence(context);
        }
        return instance;
    }
}