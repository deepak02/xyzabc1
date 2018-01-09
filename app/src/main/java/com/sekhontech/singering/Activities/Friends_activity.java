package com.sekhontech.singering.Activities;

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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sekhontech.singering.Adapters.Adapter_Friends_activity;
import com.sekhontech.singering.Models.Recent_Activity_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.admobadapter.expressads.AdmobExpressAdapterWrapper;
import com.sekhontech.singering.admobadapter.expressads.NativeExpressAdViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Friends_activity extends AppCompatActivity {
    Toolbar toolbar;
    public RelativeLayout pDialog;
    ProgressBar progressBarInitial;
    ListView list_view_recent_avtivity;
    String uid;
    Adapter_Friends_activity recent_adapter;
    public static ArrayList<Recent_Activity_model_item> list_noti = new ArrayList<>();
    private static String KEY_SUCCESS = "success";
    AdmobExpressAdapterWrapper adapterWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_act);


        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }
        initDeclare();
        getData();
        //  set_count_read();

    }

    private void set_count_read() {

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "up_recent.php?idu=" + uid + "&val=1", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("FRIENDS ACTIVITY CLEAR", String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }


    private void initDeclare() {
        toolbar = (Toolbar) findViewById(R.id.toolbar123456);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.Friends_Activity);
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pDialog = (RelativeLayout) findViewById(R.id.progressBarHolder);
        progressBarInitial = (ProgressBar) findViewById(R.id.progressBarInitial);
        list_view_recent_avtivity = (ListView) findViewById(R.id.list_view_recent_avtivity);
        recent_adapter = new Adapter_Friends_activity(this, list_noti);
    }

    private void getData() {
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "recent_activity.php?idu=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Recent_Activity_model_item model;
                try {
                    list_noti.clear();
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {

                            JSONArray array = response.getJSONArray("activity");
                            for (int i = 0; i < array.length(); i++) {
                                model = new Recent_Activity_model_item();

                                JSONObject obj = array.getJSONObject(i);

                                if (obj.toString().contains("types")) {
                                    model.idu = obj.getString("idu");
                                    model.username = obj.getString("username");
                                    model.first_name = obj.getString("first_name");
                                    model.last_name = obj.getString("last_name");
                                    model.country = obj.getString("country");
                                    model.city = obj.getString("city");
                                    model.image = obj.getString("image");
                                    model.time = obj.getString("time");
                                    model.count = obj.getString("total");
                                    model.type = obj.getString("types");
                                    model.title = obj.getString("title");

                                    list_noti.add(model);
                                }
                            }
                        } else {
                            Toast.makeText(Friends_activity.this,R.string.No_Activity_Found,Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (pDialog.getVisibility() == View.VISIBLE) {
                    pDialog.setVisibility(View.INVISIBLE);
                    list_view_recent_avtivity.setVisibility(View.VISIBLE);
                    recent_adapter.setData(list_noti);
                    if (getResources().getBoolean(R.bool.Ads_check)==true)
                    {
                        initListViewItems();
                    }else {
                        list_view_recent_avtivity.setAdapter(recent_adapter);
                    }
                    //list_view_recent_avtivity.setAdapter(recent_adapter);
                } else {

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
       /* sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));*/
    }



    private void initListViewItems() {

        //creating your adapter, it could be a custom adapter as well
        //your test devices' ids
        String[] testDevicesIds = new String[]{getString(R.string.testDeviceID), AdRequest.DEVICE_ID_EMULATOR};
        //when you'll be ready for release please use another ctor with admobReleaseUnitId instead.
        adapterWrapper = new AdmobExpressAdapterWrapper(this, testDevicesIds){
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent, int viewType) {

                //get ad view
                NativeExpressAdView adView = adViewHolder.getAdView();

                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                        AbsListView.LayoutParams.WRAP_CONTENT);
                RelativeLayout container = new RelativeLayout(Friends_activity.this);
                container.setLayoutParams(lp);
                container.setBackgroundColor(Color.BLACK);
                TextView textView = new TextView(Friends_activity.this);
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

        adapterWrapper.setAdapter(recent_adapter); //wrapping your adapter with a AdmobExpressAdapterWrapper.
        //Sets the max count of ad blocks per dataset, by default it equals to 3 (according to the Admob's policies and rules)
        adapterWrapper.setLimitOfAds(10);
        //Sets the number of your data items between ad blocks, by default it equals to 10.
        //You should set it according to the Admob's policies and rules which says not to
        //display more than one ad block at the visible part of the screen,
        // so you should choose this parameter carefully and according to your item's height and screen resolution of a target devices
        adapterWrapper.setNoOfDataBetweenAds(10);
        adapterWrapper.setFirstAdIndex(3);

        list_view_recent_avtivity.setAdapter(adapterWrapper);// setting an AdmobAdapterWrapper to a ListView
        adapterWrapper.notifyDataSetChanged();

    }


}
