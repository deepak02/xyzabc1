package com.sekhontech.singering.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sekhontech.singering.Activities.My_following_activity;
import com.sekhontech.singering.Models.Save_id_model;
import com.sekhontech.singering.Models.Search_model_item;
import com.sekhontech.singering.Preferences.MyPreference;
import com.sekhontech.singering.Utilities.Singering_Database;
import com.sekhontech.singering.Utilities.Station_Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ST_004 on 13-04-2017.
 */

public class Test_code extends Service {
    static String check;
    private static String KEY_SUCCESS = "success";
    Singering_Database db;
    String uid;
    Runnable runnable;

    static public void initialize(String check1) {
        check = check1;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        db = new Singering_Database(this);


        MyPreference.getInstance(getApplicationContext()).clear();
        getFavourites();

        get_Following_person();


        check = getSharedPreferences("logincheck", MODE_PRIVATE).getString("check_one_time", "false");
        if (check != null) {
            if (check.equalsIgnoreCase("true")) {

            } else {
                SharedPreferences pref = getSharedPreferences("logincheck", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("check_one_time", "true");
                edit.apply();

            }
        }

    }

    private void getFavourites() {

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "likes.php?usid=" + uid, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {

                            runnable = new Runnable() {
                                @Override
                                public void run() {

                                    JSONArray array = null;
                                    try {
                                        Save_id_model save_id_model;
                                        array = response.getJSONArray("like");
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject obj = array.getJSONObject(i);
                                            save_id_model = new Save_id_model();
                                            save_id_model.id = obj.getString("id");
                                            String id = obj.getString("id");

                                            MyPreference.getInstance(Test_code.this).addFavorite(Test_code.this, save_id_model);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            };
                            new Thread(runnable).start();


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }


    private void get_Following_person() {

        if (db!=null)
        {
            if (db.tableexist("followtable") == true) {
                db.deleteFollow();
            }
        }


        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        // final ProgressDialog pDialog = new ProgressDialog(My_Profile_Screen.this);

        client.get(Station_Util.URL + "follows.php?followingid=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // pDialog.setMessage("Please wait...");
                // pDialog.setCancelable(true);
                // pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String user_id, username, first_name, last_name, country, city, image;
                //  Log.d("Tag", statusCode + "");
                //  Log.d("Response121", String.valueOf(response));

                try {
                    My_following_activity.following_adapter_data.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {

                            runnable = new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        Search_model_item model;

                                        JSONArray array = response.getJSONArray("following");
                                        for (int i = 0; i < array.length(); i++) {
                                            model = new Search_model_item();
                                            JSONObject obj = array.getJSONObject(i);
                                            model.user_id = obj.getString("idu");
                                            model.username = obj.getString("username");
                                            model.first_name = obj.getString("first_name");
                                            model.last_name = obj.getString("last_name");
                                            model.country = obj.getString("country");
                                            model.city = obj.getString("city");
                                            model.image = obj.getString("image");
                                            if (db!=null)
                                            {
                                                db.insertFollow(model.user_id, "true");
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            };
                            new Thread(runnable).start();


                        } else {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //pDialog.dismiss();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }

}
