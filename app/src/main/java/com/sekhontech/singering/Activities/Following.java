package com.sekhontech.singering.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class Following extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Toolbar toolbar_following;
    FrameLayout framelay_no_following;
    ListView listview_following;
    String profile_user_id;
    private static String KEY_SUCCESS = "success";
    public static ArrayList<Search_model_item> list_following;
    private Followers_adapter list_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        init_declare();
        get_intent_data();

        list_following = new ArrayList<>();
        list_Adapter = new Followers_adapter(this, list_following,0);
    }

    private void get_intent_data() {
        if (getIntent().hasExtra("profile_user_id")) {
            profile_user_id = getIntent().getStringExtra("profile_user_id");
        }
        set_following_data(profile_user_id);
    }

    private void set_following_data(String profile_user_id) {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);


        final ProgressDialog pDialog = new ProgressDialog(Following.this);

        client.get(Station_Util.URL + "follows.php?followingid=" + profile_user_id, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
                pDialog.show();
            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Search_model_item model;
                try {
                   list_following.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
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
                                list_following.add(model);
                                Log.e("count123", String.valueOf(array.length()));
                            }
                        } else {
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (list_following.isEmpty()) {
                    framelay_no_following.setVisibility(View.VISIBLE);
                    listview_following.setVisibility(View.GONE);
                } else {
                    listview_following.setVisibility(View.VISIBLE);
                    framelay_no_following.setVisibility(View.GONE);
                    list_Adapter.setData(list_following);
                    listview_following.setAdapter(list_Adapter);
                    list_Adapter.notifyDataSetChanged();
                }

                pDialog.dismiss();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                pDialog.dismiss();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
            }
        });

    }

    private void init_declare() {
        toolbar_following = (Toolbar) findViewById(R.id.toolbar_following);
        setSupportActionBar(toolbar_following);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.Following);
        toolbar_following.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        framelay_no_following = (FrameLayout) findViewById(R.id.framelay_no_following);
        listview_following = (ListView) findViewById(R.id.listview_following);
        listview_following.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = listview_following.getItemAtPosition(position);
        Search_model_item profile_detail = (Search_model_item) o;

        Intent i = new Intent(this, Profile_Screen_visit.class);
        i.putExtra("position", position);
        i.putExtra("profile_detail", profile_detail);
        startActivity(i);
    }
}
