package com.sekhontech.singering.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sekhontech.singering.Adapters.Followers_adapter;
import com.sekhontech.singering.Models.Search_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Followers extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listview_followers;
    FrameLayout framelay_no_followers;
    private Followers_adapter list_Adapter;
    public static ArrayList<Search_model_item> adapter_data;
    String profile_user_id;
    Toolbar toolbar_followers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        init_declare();
        get_intent_data();


        adapter_data = new ArrayList<>();
        list_Adapter = new Followers_adapter(this, adapter_data,0);
    }

    private void get_intent_data() {
        if (getIntent().hasExtra("profile_user_id")) {
            profile_user_id = getIntent().getStringExtra("profile_user_id");
        }
        set_follower_data(profile_user_id);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void set_follower_data(String profile_user_id) {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        final ProgressDialog pDialog = new ProgressDialog(Followers.this);

        client.get(Station_Util.URL+"follows.php?followers=true&followerid=" + profile_user_id, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String user_id, username, first_name, last_name, country, city, image;
                try {
                    JSONArray array = response.getJSONArray("follower");
                    for (int i = 0; i < array.length(); i++) {


                        JSONObject obj = array.getJSONObject(i);
                        user_id = obj.getString("idu");
                        username = obj.getString("username");
                        first_name = obj.getString("first_name");
                        last_name = obj.getString("last_name");
                        country = obj.getString("country");
                        city = obj.getString("city");
                        image = obj.getString("image");

                        adapter_data.add(new Search_model_item(user_id, username, first_name, last_name, country, city, image));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

           /*     list_Adapter.setData(adapter_data);
                listview_followers.setAdapter(list_Adapter);*/
                if (adapter_data.isEmpty()) {
                    framelay_no_followers.setVisibility(View.VISIBLE);
                    listview_followers.setVisibility(View.GONE);
                } else {
                    listview_followers.setVisibility(View.VISIBLE);
                    framelay_no_followers.setVisibility(View.GONE);
                    list_Adapter.setData(adapter_data);
                    listview_followers.setAdapter(list_Adapter);
                    list_Adapter.notifyDataSetChanged();
                }

                pDialog.dismiss();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDialog.dismiss();
            }
        });

    }

    private void init_declare() {
        toolbar_followers = (Toolbar) findViewById(R.id.toolbar_followers);
        setSupportActionBar(toolbar_followers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.Followers);
        toolbar_followers.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        framelay_no_followers = (FrameLayout) findViewById(R.id.framelay_no_followers);
        listview_followers = (ListView) findViewById(R.id.listview_followers);
        listview_followers.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = listview_followers.getItemAtPosition(position);
        Search_model_item profile_detail = (Search_model_item) o;

        Intent i = new Intent(this, Profile_Screen_visit.class);
        i.putExtra("position", position);
        i.putExtra("profile_detail", profile_detail);
        startActivity(i);
    }
}
