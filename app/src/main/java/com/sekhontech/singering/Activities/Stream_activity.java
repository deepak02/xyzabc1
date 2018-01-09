package com.sekhontech.singering.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

public class Stream_activity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static ArrayList<Explore_model_item> stream_list = new ArrayList<>();
    private Explore_Tracks_Adapter list_Adapter;
    ListView listview_tracks;
    ProgressBar progressBarInitial;
    Toolbar toolbar;
    public RelativeLayout pDialog;
    Singering_Database db;
    public static String RECIEVER_DATA_1 = "com.sekhontech.singering_app";
    String uid;
    private AdView mAdView;
    AdmobExpressAdapterWrapper adapterWrapper;
    private static String KEY_SUCCESS = "success";
    FrameLayout framelay_no_tracks;
    private NativeExpressAdView mNativeExpressAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        db = new Singering_Database(Stream_activity.this);
        initDeclare();

        //   MobileAds.initialize(getApplicationContext(), getString(R.string.admob_express_unit_id));
        getData();

        //  ExpressNativeAds();
    }

    private void initDeclare() {
        framelay_no_tracks = (FrameLayout) findViewById(R.id.framelay_no_tracks);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Stream");
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        pDialog = (RelativeLayout) findViewById(R.id.progressBarHolder);
        progressBarInitial = (ProgressBar) findViewById(R.id.progressBarInitial);
        listview_tracks = (ListView) findViewById(R.id.list_view_tracks);
        listview_tracks.setOnItemClickListener(this);

        list_Adapter = new Explore_Tracks_Adapter(this, stream_list, 0, false);

    }

    private void getData() {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        client.get(Station_Util.URL + "stream.php?idu=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Explore_model_item model;
                try {
                    stream_list.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("stream");
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
                                model.Public = obj.getString("public");

                                if (!model.title.equalsIgnoreCase("")) {
                                    stream_list.add(model);
                                }
                            }

                            Log.e("Stream_list", "" + stream_list.size());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (stream_list.isEmpty()) {
                    framelay_no_tracks.setVisibility(View.VISIBLE);
                    pDialog.setVisibility(View.INVISIBLE);
                    listview_tracks.setVisibility(View.GONE);
                } else {
                    pDialog.setVisibility(View.INVISIBLE);
                    listview_tracks.setVisibility(View.VISIBLE);
                    framelay_no_tracks.setVisibility(View.GONE);
                    list_Adapter.setData(stream_list);
                    if (getResources().getBoolean(R.bool.Ads_check) == true) {
                        initListViewItems();
                    } else {
                        listview_tracks.setAdapter(list_Adapter);
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
                RelativeLayout container = new RelativeLayout(Stream_activity.this);
                container.setLayoutParams(lp);
                container.setBackgroundColor(Color.BLACK);
                TextView textView = new TextView(Stream_activity.this);
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

        adapterWrapper.setAdapter(list_Adapter); //wrapping your adapter with a AdmobExpressAdapterWrapper.

        //Sets the max count of ad blocks per dataset, by default it equals to 3 (according to the Admob's policies and rules)
        adapterWrapper.setLimitOfAds(10);

        //Sets the number of your data items between ad blocks, by default it equals to 10.
        //You should set it according to the Admob's policies and rules which says not to
        //display more than one ad block at the visible part of the screen,
        // so you should choose this parameter carefully and according to your item's height and screen resolution of a target devices
        adapterWrapper.setNoOfDataBetweenAds(10);

        adapterWrapper.setFirstAdIndex(3);

        listview_tracks.setAdapter(adapterWrapper); // setting an AdmobAdapterWrapper to a ListView
        adapterWrapper.notifyDataSetChanged();
    }

  /*  public boolean checkFavItem(Explore_model_item checkfav) {
        boolean check = false;
        //   List<Inicio_station> recents = RadioUtil.arr_recentstations;

        List<Explore_model_item> favs = Likes_activity.fav_adapter_data;
        if (favs != null) {
            for (Explore_model_item station_data : favs) {
                if (station_data.equals(checkfav)) {
                    check = true;
                    System.out.println("-----fav check true---" + check);
                    break;
                }
            }
        }
        System.out.println("-----fav check false---" + check);
        return check;
    }*/


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = listview_tracks.getItemAtPosition(position);
        try {
            Explore_model_item track_detail = (Explore_model_item) o;
            if (track_detail != null) {
                if (RadiophonyService.exoPlayer != null) {
                    RadiophonyService.exoPlayer.stop();
                    stopService(new Intent(Stream_activity.this, RadiophonyService.class));
                }
                MusicPlayer_Prefrence.getInstance(Stream_activity.this).save(Stream_activity.this, track_detail);
                MusicPlayer_Prefrence.getInstance(Stream_activity.this).save_position(position);

                startService(new Intent(Stream_activity.this, RadiophonyService.class));
                RadiophonyService.initialize(Stream_activity.this, track_detail, 1);
                play(true);
                Intent i = new Intent(this, Player.class);
                i.putExtra("position", position);
                i.putExtra("from", "song_lists_stream");
                i.putExtra("track_detail", track_detail);
                startActivity(i);
            }
        }catch (NullPointerException | ClassCastException e) {
            Log.e("Null_noti", "" + e);
        }

    }

    public void play(boolean play) {
        if (!play) {
            stopService(new Intent(Stream_activity.this, RadiophonyService.class));
        } else {
            startService(new Intent(Stream_activity.this, RadiophonyService.class));
        }
    }

    @Override
    public void onBackPressed() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));
                Log.e("check_times_hit", "check");
            }
        }, 1500); //time in millis
        super.onBackPressed();
/*
        sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));
*/
    }


}