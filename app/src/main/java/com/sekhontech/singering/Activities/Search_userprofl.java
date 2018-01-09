package com.sekhontech.singering.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.sekhontech.singering.Adapters.Search_Adapter;
import com.sekhontech.singering.Models.Search_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.admobadapter.expressads.AdmobExpressAdapterWrapper;
import com.sekhontech.singering.admobadapter.expressads.NativeExpressAdViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Search_userprofl extends AppCompatActivity implements TextWatcher, AdapterView.OnItemClickListener {
    public static ArrayList<Search_model_item> adapter_data = new ArrayList<>();
    private static String KEY_SUCCESS = "success";
    Toolbar toolbar;
    EditText edt_txt_search_user;
    ListView listview_search_item;
    FrameLayout frame_check;
    RequestParams requestParams;
    AdmobExpressAdapterWrapper adapterWrapper;
    private Search_Adapter search_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_userprofl);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }
        init_declare();
    }

    private void init_declare() {
        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Search Profile");
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        edt_txt_search_user = (EditText) findViewById(R.id.edt_txt_search_user);
        listview_search_item = (ListView) findViewById(R.id.listview_search_item);
        frame_check = (FrameLayout) findViewById(R.id.frame_check);
        edt_txt_search_user.addTextChangedListener(this);
        listview_search_item.setOnItemClickListener(this);


        adapter_data = new ArrayList<>();
        search_Adapter = new Search_Adapter(getApplicationContext(), adapter_data);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        frame_check.setVisibility(View.VISIBLE);
        listview_search_item.setVisibility(View.GONE);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!edt_txt_search_user.getText().toString().equals("")) {
            listview_search_item.setVisibility(View.VISIBLE);
            frame_check.setVisibility(View.GONE);
            String search = edt_txt_search_user.getText().toString();
            get_data(search);
        }
           /* frame_check.setVisibility(View.VISIBLE);
            listview_search_item.setVisibility(View.GONE);*/
    }

    private void get_data(String search) {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        requestParams = new RequestParams();
        requestParams.add("search", search);

        //  final ProgressDialog pDialog = new ProgressDialog(Search_userprofl.this);

        client.post(this, Station_Util.URL + "search.php", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                //  pDialog.setMessage("Please wait...");
                // pDialog.setCancelable(true);
                // pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // Log.d("Tag12345", statusCode + "");
                //Log.d("Response_image", String.valueOf(response));
                try {
                    adapter_data.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("search");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String user_id = obj.getString("idu");
                                String first_name = obj.getString("first_name");
                                String last_name = obj.getString("last_name");
                                String username = obj.getString("username");
                                String image = obj.getString("image");
                                String country = obj.getString("country");
                                String city = obj.getString("city");
                                adapter_data.add(new Search_model_item(user_id, username, first_name, last_name, country, city, image));
                            }
                        }
                    } else {
                        Toast.makeText(Search_userprofl.this, "No result found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                search_Adapter.setData(adapter_data);
                if (getResources().getBoolean(R.bool.Ads_check) == true) {
                    initListViewItems();
                } else {
                    listview_search_item.setAdapter(search_Adapter);
                }
                // listview_search_item.setAdapter(search_Adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //  pDialog.dismiss();
                Toast.makeText(Search_userprofl.this, "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //  pDialog.dismiss();
                Toast.makeText(Search_userprofl.this, "Error", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFinish() {
                super.onFinish();
                // pDialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = listview_search_item.getItemAtPosition(position);
        try {
            Search_model_item profile_detail = (Search_model_item) o;
            Intent i = new Intent(this, Profile_Screen_visit.class);
            i.putExtra("position", position);
            i.putExtra("profile_detail", profile_detail);
            startActivity(i);
        } catch (NullPointerException | ClassCastException e) {
            Log.e("Null_noti", "" + e);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
/*
        sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));
*/
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
                RelativeLayout container = new RelativeLayout(Search_userprofl.this);
                container.setLayoutParams(lp);
                TextView textView = new TextView(Search_userprofl.this);
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

        adapterWrapper.setAdapter(search_Adapter); //wrapping your adapter with a AdmobExpressAdapterWrapper.

        //Sets the max count of ad blocks per dataset, by default it equals to 3 (according to the Admob's policies and rules)
        adapterWrapper.setLimitOfAds(10);

        //Sets the number of your data items between ad blocks, by default it equals to 10.
        //You should set it according to the Admob's policies and rules which says not to
        //display more than one ad block at the visible part of the screen,
        // so you should choose this parameter carefully and according to your item's height and screen resolution of a target devices
        adapterWrapper.setNoOfDataBetweenAds(10);

        adapterWrapper.setFirstAdIndex(3);

        listview_search_item.setAdapter(adapterWrapper); // setting an AdmobAdapterWrapper to a ListView

    }

}
