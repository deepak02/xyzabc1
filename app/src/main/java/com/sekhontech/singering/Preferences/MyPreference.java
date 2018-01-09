package com.sekhontech.singering.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.sekhontech.singering.Models.Save_id_model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MyPreference {

    public static final String FAVORITES = "favorite";
    private static MyPreference instance = null;
    private Context context;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor editor;


    private MyPreference(Context context) {
        if (context == null)
            return;
        mPreferences =  context.getSharedPreferences(FAVORITES, Context.MODE_PRIVATE);
       // mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }


    public void clear()
    {
      editor = mPreferences.edit();
        editor.clear();
        editor.apply();
    }


    public static MyPreference getInstance(Context context) {
        if (instance == null || instance.context == null) {
            instance = new MyPreference(context);
        }
        return instance;
    }


    public void saveFavorites(Context context, List<Save_id_model> favorites) {

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        editor = mPreferences.edit();
        editor.putString(FAVORITES, jsonFavorites);
        editor.apply();

    }

    public void addFavorite(Context context, Save_id_model model) {
        List<Save_id_model> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<Save_id_model>();
        favorites.add(model);
        saveFavorites(context, favorites);

    }

    public List<Save_id_model> getFavorites(Context context) {
        List<Save_id_model> favorites;

        if (mPreferences.contains(FAVORITES)) {
            String jsonFavorites = mPreferences.getString(FAVORITES, null);
            Gson gson = new Gson();
            Save_id_model[] favoriteItems = gson.fromJson(jsonFavorites, Save_id_model[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Save_id_model>(favorites);
        } else
            return null;
        Collections.reverse(favorites);

        //  Collections.reverse(item_list_radio);
        return (ArrayList<Save_id_model>) favorites;

    }

    public void removeFavorite(Context context, Save_id_model model) {
        List<Save_id_model> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(model);
            Log.d("favorite lis", favorites.toString());
            saveFavorites(context, favorites);
        }
    }


    public boolean isFavorite(Save_id_model model) {
        ArrayList<Save_id_model> favo = (ArrayList<Save_id_model>) getFavorites(context);
        boolean fav = false;
        if (favo == null) {
        } else {
            if (favo.contains(model))
                fav = true;
            else
                fav = false;
        }
        return fav;
    }
}
