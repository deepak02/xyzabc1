package com.sekhontech.singering.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.sekhontech.singering.Models.Explore_model_item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ST_004 on 15-04-2016.
 */
public class MusicPlayer_Prefrence
{
    private static final String PLAYER = "player" ;
    private static final String CID = "cid" ;
    private static final String POSITION = "position";
    private static final String CATEGORY = "category";
    private static MusicPlayer_Prefrence instance = null;
    private Context context;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor editor;


    private MusicPlayer_Prefrence(Context context)
    {
        if (context == null)
            return;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static MusicPlayer_Prefrence getInstance(Context context)
    {
        if (instance == null || instance.context == null)
        {
            instance = new MusicPlayer_Prefrence(context);
        }
        return instance;
    }

    public void save(Context context, Explore_model_item recent)
    {
        Gson gson = new Gson();
        Log.d("LOGG", "SAVED REmoved");
        String jsonRecent = gson.toJson(recent);
        editor = mPreferences.edit();
        editor.putString(PLAYER,jsonRecent);
        editor.commit();

    }


    public void save_position(int position)
    {
        editor = mPreferences.edit();
        editor.putInt(MusicPlayer_Prefrence.POSITION,position);
        editor.apply();
    }

    public int Get_Saved_Position()
    {
        return mPreferences.getInt(MusicPlayer_Prefrence.POSITION,0);
    }

    public ArrayList<Explore_model_item> getPlayerList(Context context)
    {
        List<Explore_model_item> favorites;


        if (mPreferences.contains(PLAYER)) {
            String jsonFavorites = mPreferences.getString(PLAYER,null);
            Gson gson = new Gson();
            Explore_model_item[] favoriteItems = gson.fromJson(jsonFavorites,
                    Explore_model_item[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Explore_model_item>(favorites);
        } else
            return null;

        return (ArrayList<Explore_model_item>) favorites;
    }


    public Explore_model_item getSave(Context context)

    {
        Explore_model_item recent = null;
        if (mPreferences.contains(PLAYER))
        {
            String jsonRecent = mPreferences.getString(PLAYER, null);
            Gson gson = new Gson();
            recent = gson.fromJson(jsonRecent,Explore_model_item.class);
        }
        else
            return null;

        return recent;

    }
    public void clear()
    {
        editor = mPreferences.edit();
        editor.remove(PLAYER);
        editor.apply();
    }
}


