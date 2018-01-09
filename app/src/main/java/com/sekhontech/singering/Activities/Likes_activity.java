package com.sekhontech.singering.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sekhontech.singering.Adapters.Explore_Tracks_Adapter;
import com.sekhontech.singering.Models.Explore_model_item;
import com.sekhontech.singering.Preferences.MusicPlayer_Prefrence;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Singering_Database;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.admobadapter.expressads.AdmobExpressAdapterWrapper;
import com.sekhontech.singering.admobadapter.expressads.NativeExpressAdViewHolder;
import com.sekhontech.singering.circleMenu.CircleActivity;
import com.sekhontech.singering.service.RadiophonyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Likes_activity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static Explore_Tracks_Adapter fav_list_Adapter;
    public static ArrayList<Explore_model_item> fav_adapter_data = new ArrayList<>();
    private static String KEY_SUCCESS = "success";
    public RelativeLayout pDialog;
    Toolbar toolbar_likes;
    FrameLayout framelay_no_likes;
    ListView listview_likes;
    String uid;
    Singering_Database db;
    AdmobExpressAdapterWrapper adapterWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes_activity);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        db = new Singering_Database(Likes_activity.this);
        init_declare();

        uid = getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("uid", "");

        set_likes_data(uid);
    }

    private void init_declare() {
        listview_likes = (ListView) findViewById(R.id.listview_likes);
        listview_likes.setOnItemClickListener(this);
        framelay_no_likes = (FrameLayout) findViewById(R.id.framelay_no_likes);
        toolbar_likes = (Toolbar) findViewById(R.id.toolbar_likes);
        pDialog = (RelativeLayout) findViewById(R.id.progressBarHolder);
        setSupportActionBar(toolbar_likes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.Likes);
        toolbar_likes.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fav_list_Adapter = new Explore_Tracks_Adapter(this, CircleActivity.adapter_data, 0, false);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //  sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = listview_likes.getItemAtPosition(position);
        try {
            Explore_model_item track_detail = (Explore_model_item) o;
            if (RadiophonyService.exoPlayer != null) {
                RadiophonyService.exoPlayer.stop();
                stopService(new Intent(Likes_activity.this, RadiophonyService.class));
            }
            MusicPlayer_Prefrence.getInstance(Likes_activity.this).save(Likes_activity.this, track_detail);
            MusicPlayer_Prefrence.getInstance(Likes_activity.this).save_position(position);

            startService(new Intent(Likes_activity.this, RadiophonyService.class));
            RadiophonyService.initialize(Likes_activity.this, track_detail, 1);
            play(true);
            Intent i = new Intent(this, Player.class);
            i.putExtra("position", position);
            i.putExtra("from", "song_lists");
            i.putExtra("track_detail", track_detail);
            startActivity(i);
        } catch (NullPointerException | ClassCastException e) {
            Log.e("Null_noti", "" + e);
        }

    }

    public void play(boolean play) {
        if (!play) {
            stopService(new Intent(Likes_activity.this, RadiophonyService.class));
        } else {
            startService(new Intent(Likes_activity.this, RadiophonyService.class));
        }
    }


    private void set_likes_data(String profile_user_id) {
        if (db.tableexist("liketable") == true) {
            db.deleteLikes();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "likes.php?usid=" + profile_user_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Explore_model_item model;
                try {
                    CircleActivity.adapter_data.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("like");
                            for (int i = 0; i < array.length(); i++) {
                                model = new Explore_model_item();

                                JSONObject obj = array.getJSONObject(i);
                                model.id = obj.getString("id");
                                model.uid = obj.getString("uid");
                                model.title = obj.getString("title");
                                model.description = obj.getString("description");
                                model.first_name = obj.getString("first_name");
                                model.last_name = obj.getString("last_name");
                                model.name = Station_Util.TRACK_PATH + obj.getString("name");
                                model.tag = obj.getString("tag");
                                model.art = obj.getString("art");
                                model.buy = obj.getString("buy");
                                model.record = obj.getString("record");
                                model.release = obj.getString("release");
                                model.license = obj.getString("license");
                                model.size = obj.getString("size");
                                model.download = obj.getString("download");
                                model.time = obj.getString("time");
                                model.likes = obj.getString("likes");
                                model.downloads = obj.getString("downloads");
                                model.views = obj.getString("views");

                                if (!model.title.equalsIgnoreCase("")) {
                                    CircleActivity.adapter_data.add(model);
                                }
                                // db.insertLikes(model.id, "true");

                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (CircleActivity.adapter_data.isEmpty()) {
                    framelay_no_likes.setVisibility(View.VISIBLE);
                    pDialog.setVisibility(View.INVISIBLE);
                    listview_likes.setVisibility(View.GONE);
                } else {
                    pDialog.setVisibility(View.INVISIBLE);
                    listview_likes.setVisibility(View.VISIBLE);
                    framelay_no_likes.setVisibility(View.GONE);
                    fav_list_Adapter.setData(CircleActivity.adapter_data);
                    if (getResources().getBoolean(R.bool.Ads_check) == true) {
                        initListViewItems();
                    } else {
                        listview_likes.setAdapter(fav_list_Adapter);
                    }
                }
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


    private void initListViewItems() {

        //creating your adapter, it could be a custom adapter as well
        //your test devices' ids
        String[] testDevicesIds = new String[]{getString(R.string.testDeviceID), AdRequest.DEVICE_ID_EMULATOR};
        //when you'll be ready for release please use another ctor with admobReleaseUnitId instead.
        adapterWrapper = new AdmobExpressAdapterWrapper(this, testDevicesIds) {
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent, int viewType) {

                //get ad view
                NativeExpressAdView adView = adViewHolder.getAdView();

                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                        AbsListView.LayoutParams.WRAP_CONTENT);
                RelativeLayout container = new RelativeLayout(Likes_activity.this);
                container.setLayoutParams(lp);
                container.setBackgroundColor(Color.BLACK);
                TextView textView = new TextView(Likes_activity.this);
                textView.setLayoutParams(lp);
                textView.setTextColor(Color.RED);

                container.addView(textView);
                //wrapping
                container.addView(adView);
                //return wrapper view
                return container;
            }
        };
        //By default the ad size is set to FULL_WIDTHx150
        //To set a custom size you should use an appropriate ctor
        //adapterWrapper = new AdmobExpressAdapterWrapper(this, testDevicesIds, new AdSize(AdSize.FULL_WIDTH, 150));

        adapterWrapper.setAdapter(fav_list_Adapter); //wrapping your adapter with a AdmobExpressAdapterWrapper.

        //Sets the max count of ad blocks per dataset, by default it equals to 3 (according to the Admob's policies and rules)
        adapterWrapper.setLimitOfAds(10);

        //Sets the number of your data items between ad blocks, by default it equals to 10.
        //You should set it according to the Admob's policies and rules which says not to
        //display more than one ad block at the visible part of the screen,
        // so you should choose this parameter carefully and according to your item's height and screen resolution of a target devices
        adapterWrapper.setNoOfDataBetweenAds(10);

        adapterWrapper.setFirstAdIndex(3);

        listview_likes.setAdapter(adapterWrapper); // setting an AdmobAdapterWrapper to a ListView

    }


}
