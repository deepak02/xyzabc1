package com.sekhontech.singering.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.sekhontech.singering.Models.Playlist_model_item;
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

public class Playlist_tracks_list extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static String RECIEVER_DATA_1 = "com.sekhontech.singering_app";
    private static String KEY_SUCCESS = "success";
    public RelativeLayout pDialog;
    ListView listview_tracks;
    Toolbar toolbar_tracks;
    String playlist_id;
    Singering_Database db;
    FrameLayout framelay_no_tracks;
    Data_Recieverplaylist reciever;
    Playlist_model_item model;
    //public static ArrayList<Explore_model_item> adapter_data;
    private Explore_Tracks_Adapter list_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_tracks_list);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }


        db = new Singering_Database(Playlist_tracks_list.this);
        init_declare();
        get_intent_data();

        reciever = new Data_Recieverplaylist();
        registerReceiver(reciever, new IntentFilter(RECIEVER_DATA_1));

      /*  if (db.tableexist("liketable") == true) {
            db.deleteLikes();
        }*/
    }

    private void get_intent_data() {
        if (getIntent().hasExtra("playlist_detail")) {
            model = (Playlist_model_item) getIntent().getSerializableExtra("playlist_detail");
            playlist_id = model.getId();

            Station_Util.playlist_ID = playlist_id;
        }
        set_tracks_data(playlist_id);
    }

    private void set_tracks_data(String playlist_id) {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "playlist.php?playlists=yes&listid=" + playlist_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Explore_model_item model;
                try {
                    CircleActivity.adapter_data.clear();
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("play-list");
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
                                    CircleActivity.adapter_data.add(model);
                                }

                               /* if (checkFavItem(model)) {
                                    db.insertLikes(model.id, "true");
                                } else {
                                    db.insertLikes(model.id, "false");
                                }*/
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

    private void init_declare() {
        framelay_no_tracks = (FrameLayout) findViewById(R.id.framelay_no_tracks);
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


        list_Adapter = new Explore_Tracks_Adapter(this, CircleActivity.adapter_data, 1, false);
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = listview_tracks.getItemAtPosition(position);
        try {
            Explore_model_item track_detail = (Explore_model_item) o;
            if (RadiophonyService.exoPlayer != null) {
                RadiophonyService.exoPlayer.stop();
                stopService(new Intent(Playlist_tracks_list.this, RadiophonyService.class));
            }
            MusicPlayer_Prefrence.getInstance(Playlist_tracks_list.this).save(Playlist_tracks_list.this, track_detail);
            MusicPlayer_Prefrence.getInstance(Playlist_tracks_list.this).save_position(position);

            startService(new Intent(Playlist_tracks_list.this, RadiophonyService.class));
            RadiophonyService.initialize(Playlist_tracks_list.this, track_detail, 1);
            play(true);
            Intent i = new Intent(Playlist_tracks_list.this, Player.class);
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
            stopService(new Intent(Playlist_tracks_list.this, RadiophonyService.class));
        } else {
            startService(new Intent(Playlist_tracks_list.this, RadiophonyService.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reciever);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Playlist_tracks_list.this, Playlist_activity.class);
        startActivity(intent);
        finish();
    }

    public class Data_Recieverplaylist extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("-----player reciever-----");
            list_Adapter.notifyDataSetChanged();

        }
    }
}
