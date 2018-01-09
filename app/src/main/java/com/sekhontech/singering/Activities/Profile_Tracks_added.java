package com.sekhontech.singering.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sekhontech.singering.Adapters.Explore_Tracks_Adapter;
import com.sekhontech.singering.Models.Explore_model_item;
import com.sekhontech.singering.Preferences.MusicPlayer_Prefrence;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Singering_Database;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.circleMenu.CircleActivity;
import com.sekhontech.singering.service.RadiophonyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Profile_Tracks_added extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{
    ListView listview_tracks;
    Toolbar toolbar_tracks;
    String profile_user_id;
    public RelativeLayout pDialog;
    private Explore_Tracks_Adapter list_Adapter;
    Singering_Database db;
    private static String KEY_SUCCESS = "success";
    FrameLayout framelay_no_tracks;
    Data_Recievertracksadded reciever;
    public static String RECIEVER_DATA_1 = "com.sekhontech.singering_app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__tracks_added);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        db = new Singering_Database(Profile_Tracks_added.this);
        init_declare();
        get_intent_data();

        reciever = new Data_Recievertracksadded();
        registerReceiver(reciever, new IntentFilter(RECIEVER_DATA_1));

    }

    public class Data_Recievertracksadded extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("-----player reciever-----");
            list_Adapter.notifyDataSetChanged();
        }
    }

    private void get_intent_data() {
        if (getIntent().hasExtra("profile_user_id")) {
            profile_user_id = getIntent().getStringExtra("profile_user_id");
        }
        set_tracks_data(profile_user_id);
    }

    private void set_tracks_data(String profile_user_id) {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL+"visitorpro.php?visitertracks=tracks&requestid=" + profile_user_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Explore_model_item model;
                try {
                    CircleActivity.adapter_data.clear();
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("tracks");
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
                                model.Public=obj.getString("public");

                                if (!model.title.equalsIgnoreCase("")) {
                                    CircleActivity.adapter_data.add(model);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (CircleActivity.adapter_data.isEmpty()) {
                    framelay_no_tracks.setVisibility(View.VISIBLE);
                    pDialog.setVisibility(View.INVISIBLE);
                    listview_tracks.setVisibility(View.GONE);
                } else {
                    pDialog.setVisibility(View.INVISIBLE);
                    listview_tracks.setVisibility(View.VISIBLE);
                    framelay_no_tracks.setVisibility(View.GONE);
                    list_Adapter.setData(CircleActivity.adapter_data);
                    listview_tracks.setAdapter(list_Adapter);
                }

                /*
                if (pDialog.getVisibility() == View.VISIBLE) {
                    pDialog.setVisibility(View.INVISIBLE);
                    listview_tracks.setVisibility(View.VISIBLE);
                    list_Adapter.setData(Explore.adapter_data);
                    listview_tracks.setAdapter(list_Adapter);
                } else {
                }*/
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


/*    public boolean checkFavItem(Explore_model_item checkfav) {
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


    private void init_declare() {
        framelay_no_tracks=(FrameLayout)findViewById(R.id.framelay_no_tracks);
        pDialog = (RelativeLayout) findViewById(R.id.progressBarHolder);
        listview_tracks = (ListView) findViewById(R.id.listview_tracks);
        listview_tracks.setOnItemClickListener(this);
        toolbar_tracks = (Toolbar) findViewById(R.id.toolbar_tracks);
        setSupportActionBar(toolbar_tracks);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Tracks");
        toolbar_tracks.setTitleTextColor(0xffffffff);

        toolbar_tracks.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        list_Adapter = new Explore_Tracks_Adapter(this, CircleActivity.adapter_data,0,false);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = listview_tracks.getItemAtPosition(position);
        Explore_model_item track_detail = (Explore_model_item) o;
        if (RadiophonyService.exoPlayer != null) {
            RadiophonyService.exoPlayer.stop();
            stopService(new Intent(Profile_Tracks_added.this, RadiophonyService.class));
        }

        MusicPlayer_Prefrence.getInstance(Profile_Tracks_added.this).save(Profile_Tracks_added.this,track_detail);
        MusicPlayer_Prefrence.getInstance(Profile_Tracks_added.this).save_position(position);

        startService(new Intent(Profile_Tracks_added.this, RadiophonyService.class));
        RadiophonyService.initialize(Profile_Tracks_added.this, track_detail, 1);
        play(true);
        Intent i = new Intent(Profile_Tracks_added.this, Player.class);
        i.putExtra("position", position);
        i.putExtra("from","song_lists");
        i.putExtra("track_detail", track_detail);
        startActivity(i);
    }


    public void play(boolean play) {
        if (!play) {
            stopService(new Intent(Profile_Tracks_added.this, RadiophonyService.class));
        } else {
            startService(new Intent(Profile_Tracks_added.this, RadiophonyService.class));
        }
    }
}
