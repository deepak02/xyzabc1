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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sekhontech.singering.Adapters.Notifications_Adapter;
import com.sekhontech.singering.Models.Notification_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.admobadapter.expressads.AdmobExpressAdapterWrapper;
import com.sekhontech.singering.admobadapter.expressads.NativeExpressAdViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Notifications extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Toolbar toolbar;
    public static ArrayList<Notification_model_item> list_noti = new ArrayList<>();
    String uid;
    private Notifications_Adapter noti_adapter;
    public RelativeLayout pDialog;
    ProgressBar progressBarInitial;
    ListView list_view_notification;
    FrameLayout frame_no_noti;
    AdmobExpressAdapterWrapper adapterWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }


        initDeclare();

        getData();
        set_count_read();
    }

    private void set_count_read() {
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "up_notify.php?idu=" + uid + "&val=1", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("NOTI CLEAR", String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void initDeclare() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.Notifications);
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        frame_no_noti = (FrameLayout) findViewById(R.id.frame_no_noti);
        pDialog = (RelativeLayout) findViewById(R.id.progressBarHolder);
        progressBarInitial = (ProgressBar) findViewById(R.id.progressBarInitial);
        list_view_notification = (ListView) findViewById(R.id.list_view_notification);
        list_view_notification.setOnItemClickListener(this);
        noti_adapter = new Notifications_Adapter(this, list_noti);
    }


    private void getData() {
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        client.get(Station_Util.URL + "notifications.php?idu=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Notification_model_item model;
                try {
                    list_noti.clear();

                    JSONArray array = response.getJSONArray("notification");
                    for (int i = 0; i < array.length(); i++) {
                        model = new Notification_model_item();

                        JSONObject obj = array.getJSONObject(i);

                        model.count = obj.getString("count");
                        model.from = obj.getString("from");
                        model.to = obj.getString("to");
                        model.parent = obj.getString("parent");
                        model.child = obj.getString("child");
                        model.time = obj.getString("time");
                        model.username = obj.getString("username");
                        model.first_name = obj.getString("first_name");
                        model.last_name = obj.getString("last_name");
                        model.image = obj.getString("image");
                        model.types = obj.getString("types");

                        list_noti.add(model);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (list_noti.isEmpty()) {
                    pDialog.setVisibility(View.INVISIBLE);
                    frame_no_noti.setVisibility(View.VISIBLE);
                    list_view_notification.setVisibility(View.GONE);
                } else {
                    pDialog.setVisibility(View.INVISIBLE);
                    frame_no_noti.setVisibility(View.GONE);
                    list_view_notification.setVisibility(View.VISIBLE);
                    noti_adapter.setData(list_noti);
                    if (getResources().getBoolean(R.bool.Ads_check) == true) {
                        initListViewItems();
                    } else {
                        list_view_notification.setAdapter(noti_adapter);
                    }
                    //list_view_notification.setAdapter(noti_adapter);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));
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
                RelativeLayout container = new RelativeLayout(Notifications.this);
                container.setLayoutParams(lp);
                container.setBackgroundColor(Color.BLACK);
                TextView textView = new TextView(Notifications.this);
                textView.setLayoutParams(lp);
                textView.setTextColor(Color.RED);
                container.addView(textView);
                container.addView(adView);
                return container;
            }
        };
        //By default the ad size is set to FULL_WIDTHx150
        //To set a custom size you should use an appropriate ctor
        //adapterWrapper = new AdmobExpressAdapterWrapper(this, testDevicesIds, new AdSize(AdSize.FULL_WIDTH, 150));
        adapterWrapper.setAdapter(noti_adapter); //wrapping your adapter with a AdmobExpressAdapterWrapper.
        //Sets the max count of ad blocks per dataset, by default it equals to 3 (according to the Admob's policies and rules)
        adapterWrapper.setLimitOfAds(10);
        //Sets the number of your data items between ad blocks, by default it equals to 10.
        //You should set it according to the Admob's policies and rules which says not to
        //display more than one ad block at the visible part of the screen,
        // so you should choose this parameter carefully and according to your item's height and screen resolution of a target devices
        adapterWrapper.setNoOfDataBetweenAds(10);
        adapterWrapper.setFirstAdIndex(3);
        list_view_notification.setAdapter(adapterWrapper); // setting an AdmobAdapterWrapper to a ListView
        adapterWrapper.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = list_view_notification.getItemAtPosition(position);
        try {
            Notification_model_item item_detail = (Notification_model_item) o;
            Intent i = new Intent(Notifications.this, Comments_activity.class);
            i.putExtra("position", position);
            i.putExtra("item_detail", item_detail);
            startActivity(i);
        } catch (NullPointerException | ClassCastException e) {
            Log.e("Null_noti", "" + e);
        }
    }
}
