package com.sekhontech.singering.Activities;

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
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.Adapters.Playlist_Adapter;
import com.sekhontech.singering.Models.Playlist_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.admobadapter.expressads.AdmobExpressAdapterWrapper;
import com.sekhontech.singering.admobadapter.expressads.NativeExpressAdViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Playlist_activity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static ArrayList<Playlist_model_item> playlist_adapter_data = new ArrayList<>();
    public static ArrayList<Playlist_model_item> list_playlist_track_count = new ArrayList<>();
    private static String KEY_SUCCESS = "success";
    Toolbar toolbar_playlist;
    FrameLayout framelay_no_playlist;
    ListView listview_playlist;
    RequestParams requestParams;
    String uid;
    Playlist_Adapter playlist_adapter;
    RelativeLayout pDialog;
    AdmobExpressAdapterWrapper adapterWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        init_Declare();
        playlist_adapter = new Playlist_Adapter(Playlist_activity.this, playlist_adapter_data, 1);

        get_playlist_data();
    }

    private void init_Declare() {

        pDialog = (RelativeLayout) findViewById(R.id.progressBarHolder);
        listview_playlist = (ListView) findViewById(R.id.listview_playlist1);
        listview_playlist.setClickable(true);
        listview_playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listview_playlist.getItemAtPosition(position);
                try {
                    Playlist_model_item playlist_detail = (Playlist_model_item) o;
                    Intent i = new Intent(Playlist_activity.this, Playlist_tracks_list.class);
                    i.putExtra("position", position);
                    i.putExtra("playlist_detail", playlist_detail);
                    startActivity(i);
                    finish();
                } catch (NullPointerException | ClassCastException e) {
                    Log.e("Null_noti", "" + e);
                }

            }
        });


        framelay_no_playlist = (FrameLayout) findViewById(R.id.framelay_no_playlist);
        toolbar_playlist = (Toolbar) findViewById(R.id.toolbar_playlist);
        setSupportActionBar(toolbar_playlist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Playlist");
        toolbar_playlist.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
/*
        sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));
*/
    }

    private void get_playlist_data() {
        final int count = 0;
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        requestParams = new RequestParams();
        requestParams.add("uid", uid);

        //  final ProgressDialog pDialog = new ProgressDialog(Player.this);

        client.post(this, Station_Util.URL + "playlist.php?showlist=show", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                //   pDialog.setMessage("Please wait...");
                //   pDialog.setCancelable(true);
                //  pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // Log.d("Tag12345", statusCode + "");
                //Log.d("Response_image", String.valueOf(response));
                try {
                    playlist_adapter_data.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("show-list");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);

                                String id = obj.getString("id");
                                String by = obj.getString("by");
                                String name = obj.getString("name");
                                String description = obj.getString("description");
                                String value_check = obj.getString("public");
                                String track_count = obj.getString("total_tracks");
                                String image = obj.getString("image");
                                playlist_adapter_data.add(new Playlist_model_item(id, by, name, description, value_check, track_count, image));
                                hasDuplicates(playlist_adapter_data);
                            }
                            //get_playlist_data_count();
                        }
                    } else {
                        Toast.makeText(Playlist_activity.this, "No result found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (playlist_adapter_data.isEmpty()) {
                    framelay_no_playlist.setVisibility(View.VISIBLE);
                    listview_playlist.setVisibility(View.GONE);
                    pDialog.setVisibility(View.INVISIBLE);
                } else {
                    pDialog.setVisibility(View.INVISIBLE);
                    listview_playlist.setVisibility(View.VISIBLE);
                    framelay_no_playlist.setVisibility(View.GONE);
                    playlist_adapter.setData(playlist_adapter_data);
                    if (getResources().getBoolean(R.bool.Ads_check) == true) {
                        initListViewItems();
                    } else {
                        listview_playlist.setAdapter(playlist_adapter);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(Playlist_activity.this, "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(Playlist_activity.this, "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(Playlist_activity.this, "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();
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
                RelativeLayout container = new RelativeLayout(Playlist_activity.this);
                container.setLayoutParams(lp);
                container.setBackgroundColor(Color.BLACK);
                TextView textView = new TextView(Playlist_activity.this);
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

        adapterWrapper.setAdapter(playlist_adapter); //wrapping your adapter with a AdmobExpressAdapterWrapper.

        //Sets the max count of ad blocks per dataset, by default it equals to 3 (according to the Admob's policies and rules)
        adapterWrapper.setLimitOfAds(10);

        //Sets the number of your data items between ad blocks, by default it equals to 10.
        //You should set it according to the Admob's policies and rules which says not to
        //display more than one ad block at the visible part of the screen,
        // so you should choose this parameter carefully and according to your item's height and screen resolution of a target devices
        adapterWrapper.setNoOfDataBetweenAds(10);

        adapterWrapper.setFirstAdIndex(3);

        listview_playlist.setAdapter(adapterWrapper); // setting an AdmobAdapterWrapper to a ListView

    }

    public void hasDuplicates(List<Playlist_model_item> p_cars) {
        final List<String> usedNames = new ArrayList<>();
        Iterator<Playlist_model_item> it = p_cars.iterator();
        while (it.hasNext()) {
            Playlist_model_item car = it.next();
            final String name = car.getId();
            if (usedNames.contains(name)) {
                it.remove();
            } else {
                usedNames.add(name);
            }
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
}
