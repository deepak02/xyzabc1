package com.sekhontech.singering.circleMenu;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.sekhontech.singering.Activities.About_app;
import com.sekhontech.singering.Activities.Explore;
import com.sekhontech.singering.Activities.Login;
import com.sekhontech.singering.Activities.Player;
import com.sekhontech.singering.Adapters.Adapter_most_liked_recycler;
import com.sekhontech.singering.Adapters.Adapter_most_popular_recycler;
import com.sekhontech.singering.Adapters.Adapter_recent_recycler;
import com.sekhontech.singering.Models.Explore_model_item;
import com.sekhontech.singering.Notifications.Notification_Serviceplay;
import com.sekhontech.singering.Preferences.MusicPlayer_Prefrence;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.admobadapter.expressads.AdmobExpressRecyclerAdapterWrapper;
import com.sekhontech.singering.admobadapter.expressads.NativeExpressAdViewHolder;
import com.sekhontech.singering.service.RadiophonyService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class Guest_activity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {


    public static final String RECIEVER_NOTI_PLAYPAUSE = "Player";
    public static final String MyPREFERENCES = "MyPrefs";
    // CircularProgressBar circularProgressBar;
    public static String RECIEVER_DATA = "com.sekhontech.singering_app";
    public static ArrayList<Explore_model_item> adapter_data = new ArrayList<>();
    public static ArrayList<Explore_model_item> fav_adapter_data = new ArrayList<>();
    public static ArrayList<Explore_model_item> list_recycler_most_likes = new ArrayList<Explore_model_item>();
    public static ArrayList<Explore_model_item> list_recycler_recent = new ArrayList<Explore_model_item>();
    public static ArrayList<Explore_model_item> list_recycler_mst_popular = new ArrayList<Explore_model_item>();
    public static FrameLayout frame_footer;
    public static ImageView Iv_track_image;
    public static ImageView Iv_play_pause;
    public static TextView Tv_footer_track_name, Tv_footer_tag_name;
    private static Context context;
    private static String KEY_SUCCESS = "success";
    boolean doubleBackToExitPressedOnce = false;
    Data_Reciever reciever;
    PopupWindow pwindo_menu;
    PopupWindow pwindo;
    Data_Reciever1 receiver1;
    RadiophonyService service;
    SharedPreferences sharedpref;
    CircleImageView circleImageView;
    String str_userimage, str_username, str_cover_image, totaltracks_count = "", follower_count = "", likes_count = "";
    ImageView Iv_cover, Iv_about;
    int counter;
    TextView view, view_two;
    RequestQueue requestQueue;
    /*   private Adapter_recycler_most_liked adapter_most_likes;
       private Adapter_recycler_recent adapter_recent;
       private Adapter_recycler_mst_popular adapter_mst_popular;*/
    AdmobExpressRecyclerAdapterWrapper adapterWrapper;
    private TextView txt_username_profile, txt_city_country;
    private CircleImageView profile_image;
    private ImageButton IB_menu_items;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TextView txt_username, txt_count_tracks, txt_likes_count, empty_most_liked, empty_recent, empty_popular, empty_recent_user;
    private RecyclerView recycler_view_most_liked, recycler_view_recent, recycler_view_popular;
    private Adapter_most_liked_recycler adapter_most_likes;
    private Adapter_recent_recycler adapter_recent;
    private Adapter_most_popular_recycler adapter_mst_popular;

    private String[] mItemTexts = new String[]{"", "", "", "", "", "", ""};
    private int[] mItemImgs = new int[]{
            R.drawable.playlist_icon, R.drawable.ic_explore, R.drawable.friends_icon,
            R.drawable.message_icon, R.drawable.thumb_up_button,
            R.drawable.stream_icon, R.drawable.settings};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guest_activity);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }
        context = this;
        initdeclare();

        sharedpref = getSharedPreferences("logincheck", Context.MODE_PRIVATE);

        str_userimage = sharedpref.getString("image", "");
        str_cover_image = sharedpref.getString("cover", "");
        str_username = sharedpref.getString("username", "");
        reciever = new Data_Reciever();
        registerReceiver(reciever, new IntentFilter(RECIEVER_DATA));

        receiver1 = new Data_Reciever1();
        registerReceiver(receiver1, new IntentFilter(RECIEVER_NOTI_PLAYPAUSE));
        initNavigationDrawer();
        //circularMenu();

        get_recent_recycler();
        get_mst_popular_recycler();
        get_Most_Likes_recycler();

        // requestQueue = Volley.newRequestQueue(this); // 'this' is the Context
        // get_mst_popular_recycler_volly();
        //  get_mst_popular_recycler_volly();

    }

    private void get_mst_popular_recycler_volly() {

   /*     Map<String, String> jsonParams = new HashMap<String, String>();   ////POST METHOD

        jsonParams.put("email", "user@gmail.com");
        jsonParams.put("username", "user");
        jsonParams.put("password", "pass");*/

        String URL = "https://kreenk.com/myapi/most_popular.php";
       /* new JSONObject(jsonParams),*/    ////POST METHOD
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, URL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("fi", String.valueOf(response));
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
        requestQueue.add(jsonObjectRequest);
    }

    private void get_mst_popular_recycler() {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "most_popular.php", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("Tag1", statusCode + "");
                // Log.d("Response_most_popular", String.valueOf(response));

                Explore_model_item model;
                try {
                    list_recycler_mst_popular.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {

                            JSONArray array = response.getJSONArray("Most Popular");
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
                                    list_recycler_mst_popular.add(model);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (list_recycler_mst_popular == null) {
                    empty_popular.setVisibility(View.VISIBLE);
                    recycler_view_popular.setVisibility(View.GONE);
                } else if (list_recycler_mst_popular.isEmpty()) {
                    empty_popular.setVisibility(View.VISIBLE);
                    recycler_view_popular.setVisibility(View.GONE);
                } else {
                    empty_popular.setVisibility(View.GONE);
                    recycler_view_popular.setVisibility(View.VISIBLE);

                    adapter_mst_popular = new Adapter_most_popular_recycler(Guest_activity.this);
                    adapter_mst_popular.addAll(list_recycler_mst_popular);
                    /*adapter_mst_popular = new Adapter_recycler_mst_popular(Guest_activity.this, list_recycler_mst_popular, 0);
                    recycler_view_popular.setAdapter(adapter_mst_popular);*/
                    if (getResources().getBoolean(R.bool.Ads_check) == true) {
                        initRecyclerViewItems_most_popular();
                    } else {
                        recycler_view_popular.setAdapter(adapter_mst_popular);
                    }
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("Tag1", statusCode + "");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Tag1", statusCode + "");
            }
        });
    }


    private void initRecyclerViewItems_recents() {
        String[] testDevicesIds = new String[]{getString(R.string.testDeviceID), AdRequest.DEVICE_ID_EMULATOR};
        adapterWrapper = new AdmobExpressRecyclerAdapterWrapper(this, testDevicesIds) {
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent, int viewType) {
                NativeExpressAdView adView = adViewHolder.getAdView();
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT);
                CardView cardView = new CardView(Guest_activity.this);
                cardView.setLayoutParams(lp);
                TextView textView = new TextView(Guest_activity.this);
                textView.setLayoutParams(lp);
                textView.setText("Ad is loading...");
                textView.setTextColor(Color.RED);
                cardView.addView(textView);
                cardView.addView(adView);
                return cardView;
            }
        };
        adapterWrapper.setAdapter(adapter_recent);
        adapterWrapper.setLimitOfAds(10);
        adapterWrapper.setNoOfDataBetweenAds(10);
        adapterWrapper.setFirstAdIndex(4);
        recycler_view_recent.setAdapter(adapterWrapper); // setting an AdmobExpressRecyclerAdapterWrapper to a RecyclerView
    }


    private void initRecyclerViewItems_most_popular() {
        String[] testDevicesIds = new String[]{getString(R.string.testDeviceID), AdRequest.DEVICE_ID_EMULATOR};
        adapterWrapper = new AdmobExpressRecyclerAdapterWrapper(this, testDevicesIds) {
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent, int viewType) {
                NativeExpressAdView adView = adViewHolder.getAdView();
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT);
                CardView cardView = new CardView(Guest_activity.this);
                cardView.setLayoutParams(lp);
                TextView textView = new TextView(Guest_activity.this);
                textView.setLayoutParams(lp);
                textView.setText("Ad is loading...");
                textView.setTextColor(Color.RED);
                cardView.addView(textView);
                cardView.addView(adView);
                return cardView;
            }
        };
        adapterWrapper.setAdapter(adapter_mst_popular); //wrapping your adapter with a AdmobExpressRecyclerAdapterWrapper.
        adapterWrapper.setLimitOfAds(10);
        adapterWrapper.setNoOfDataBetweenAds(10);
        adapterWrapper.setFirstAdIndex(4);
        recycler_view_popular.setAdapter(adapterWrapper); // setting an AdmobExpressRecyclerAdapterWrapper to a RecyclerView
    }


    private void initRecyclerViewItems_most_likes() {
        String[] testDevicesIds = new String[]{getString(R.string.testDeviceID), AdRequest.DEVICE_ID_EMULATOR};
        adapterWrapper = new AdmobExpressRecyclerAdapterWrapper(this, testDevicesIds) {
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent, int viewType) {
                NativeExpressAdView adView = adViewHolder.getAdView();
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT);
                CardView cardView = new CardView(Guest_activity.this);
                cardView.setLayoutParams(lp);

                TextView textView = new TextView(Guest_activity.this);
                textView.setLayoutParams(lp);
                textView.setText("Ad is loading...");
                textView.setTextColor(Color.RED);
                cardView.addView(textView);
                cardView.addView(adView);
                return cardView;
            }
        };
        adapterWrapper.setAdapter(adapter_most_likes); //wrapping your adapter with a AdmobExpressRecyclerAdapterWrapper.
        adapterWrapper.setLimitOfAds(10);
        adapterWrapper.setNoOfDataBetweenAds(10);
        adapterWrapper.setFirstAdIndex(4);
        recycler_view_most_liked.setAdapter(adapterWrapper); // setting an AdmobExpressRecyclerAdapterWrapper to a RecyclerView
    }


    private void get_recent_recycler() {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "tracks.php?tracks=true", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                // Log.d("Tag1", statusCode + "");
                // Log.d("Response_Recent", String.valueOf(response));

                Explore_model_item model;
                try {
                    list_recycler_recent.clear();

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
                                model.Public = obj.getString("public");


                                if (!model.title.equalsIgnoreCase("")) {
                                    list_recycler_recent.add(model);


                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (list_recycler_recent == null) {
                    empty_recent.setVisibility(View.VISIBLE);
                    recycler_view_recent.setVisibility(View.GONE);
                } else if (list_recycler_recent.isEmpty()) {
                    empty_recent.setVisibility(View.VISIBLE);
                    recycler_view_recent.setVisibility(View.GONE);
                } else {
                    empty_recent.setVisibility(View.GONE);
                    recycler_view_recent.setVisibility(View.VISIBLE);

                    adapter_recent = new Adapter_recent_recycler(Guest_activity.this);
                    adapter_recent.addAll(list_recycler_recent);
                  /*  adapter_recent = new Adapter_recycler_recent(Guest_activity.this, list_recycler_recent, 0);
                    recycler_view_recent.setAdapter(adapter_recent);*/
                    if (getResources().getBoolean(R.bool.Ads_check) == true) {
                        initRecyclerViewItems_recents();
                    } else {
                        recycler_view_recent.setAdapter(adapter_recent);
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

    private void get_Most_Likes_recycler() {


        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        client.get(Station_Util.URL + "most_like.php", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // Log.d("Tag1", statusCode + "");
                // Log.d("ResponseMOST_LIKES", String.valueOf(response));

                Explore_model_item model;
                try {
                    list_recycler_most_likes.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("Most Likes");
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
                                    list_recycler_most_likes.add(model);
                                }

                              /*  if (checkFavItem(model)) {
                                    db.insertLikes(model.id, "true");
                                } else {
                                    db.insertLikes(model.id, "false");
                                }*/
                            }
                        } else {
                            Log.d("NoLikes", "NoLikes");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (list_recycler_most_likes == null) {
                    empty_most_liked.setVisibility(View.VISIBLE);
                    recycler_view_most_liked.setVisibility(View.GONE);
                } else if (list_recycler_most_likes.isEmpty()) {
                    empty_most_liked.setVisibility(View.VISIBLE);
                    recycler_view_most_liked.setVisibility(View.GONE);
                } else {
                    empty_most_liked.setVisibility(View.GONE);
                    recycler_view_most_liked.setVisibility(View.VISIBLE);
                    adapter_most_likes = new Adapter_most_liked_recycler(Guest_activity.this);
                    adapter_most_likes.addAll(list_recycler_most_likes);
                  /*  adapter_most_likes = new Adapter_recycler_most_liked(Guest_activity.this, list_recycler_most_likes, 1);
                    recycler_view_most_liked.setAdapter(adapter_most_likes);*/
                    if (getResources().getBoolean(R.bool.Ads_check) == true) {
                        initRecyclerViewItems_most_likes();
                    } else {
                        recycler_view_most_liked.setAdapter(adapter_most_likes);
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

        });
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    public void initNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        disableNavigationViewScrollbars(navigationView);
        View hView = navigationView.getHeaderView(0);
        circleImageView = (CircleImageView) hView.findViewById(R.id.profile_image);
        Iv_cover = (ImageView) hView.findViewById(R.id.Iv_cover);
        txt_likes_count = (TextView) hView.findViewById(R.id.txt_likes_count);
        txt_count_tracks = (TextView) hView.findViewById(R.id.txt_count_tracks);

        view = (TextView) navigationView.getMenu().findItem(R.id.notification).getActionView();
        view_two = (TextView) navigationView.getMenu().findItem(R.id.friends_activity).getActionView();

        if (str_userimage.equalsIgnoreCase("") | str_userimage.isEmpty() | str_cover_image.isEmpty() | str_cover_image.equalsIgnoreCase("")) {

            circleImageView.setImageResource(R.drawable.camera_icontwo);
        } else {
        }
        txt_username = (TextView) hView.findViewById(R.id.name);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.home:
                        //home
                        Intent i_e = new Intent(Guest_activity.this, Guest_activity.class);
                        startActivity(i_e);
                        finish();

                        drawerLayout.closeDrawers();
                        break;

                    case R.id.stream:

                        popUp_GUEST();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.upload_track:

                        popUp_GUEST();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.downloads:

                        popUp_GUEST();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.explore:

                        Intent i_explore = new Intent(Guest_activity.this, Explore.class);
                        startActivity(i_explore);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.profile:
                        popUp_GUEST();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.my_tracks:
                        try {
                            popUp_GUEST();
                            drawerLayout.closeDrawers();
                        } catch (IllegalArgumentException e) {
                            Log.e("Exception", "" + e);
                        }
                        break;

                    case R.id.likes:
                        popUp_GUEST();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.search:
                        //search
                        popUp_GUEST();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.message:

                        popUp_GUEST();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        popUp_GUEST();
                        drawerLayout.closeDrawers();
                        //return true;
                        break;

                    case R.id.playlist:
                        popUp_GUEST();
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.friends_activity:
                        popUp_GUEST();
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.notification:
                        popUp_GUEST();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.player:
                        Explore_model_item itemListRadio = MusicPlayer_Prefrence.getInstance(Guest_activity.this).getSave(Guest_activity.this);
                        // || adapter_data.size() == 0 || Likes_activity.fav_adapter_data.size() == 0
                        if (itemListRadio != null) {
                            if (Player.message == true) {
                                Intent e = new Intent(Guest_activity.this, Player.class);
                                e.putExtra("from", "drawer_player");
                                RadiophonyService.initialize(Guest_activity.this, itemListRadio, 1);

                                if (Station_Util.Song_image != null) {
                                    try {
                                        Transformation transformation = new RoundedTransformationBuilder()
                                                .borderColor(Color.BLACK)
                                                .cornerRadiusDp(5)
                                                .oval(false)
                                                .build();
                                        Picasso.with(Guest_activity.this).load(Station_Util.Song_image).fit()
                                                .transform(transformation).into(Iv_track_image);
                                    } catch (IllegalArgumentException a) {
                                        Log.e("Picasso path empty", String.valueOf(a));
                                    }
                                }
                                startActivity(e);
                            } else {
                                Intent e = new Intent(Guest_activity.this, Player.class);
                                e.putExtra("from", "drawer_player");
                                RadiophonyService.initialize(Guest_activity.this, itemListRadio, 1);
                                play(true);
                            /*    frame_footer.setVisibility(View.VISIBLE);
                                Tv_footer_track_name.setText(Station_Util.Song_name);
                                Tv_footer_track_name.setSelected(true);
                                Tv_footer_tag_name.setText(Station_Util.tag);
                                Tv_footer_tag_name.setSelected(true);

                                if (Station_Util.Song_image != null) {
                                    try {
                                        Transformation transformation = new RoundedTransformationBuilder()
                                                .borderColor(Color.BLACK)
                                                .cornerRadiusDp(5)
                                                .oval(false)
                                                .build();
                                        Picasso.with(Guest_activity.this).load(Station_Util.Song_image).fit()
                                                .transform(transformation).into(Iv_track_image);
                                    } catch (IllegalArgumentException a) {
                                        Log.e("Picasso path empty", String.valueOf(a));
                                    }
                                }*/
                                startActivity(e);
                            }
                        } else {
                            Toast.makeText(Guest_activity.this, "Play any track first", Toast.LENGTH_SHORT).show();
                        }
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.more_apps:
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.more_apps))));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.more_apps))));
                        }
                        drawerLayout.closeDrawers();
                        break;

                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        // return true;
                        break;

                }
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(Guest_activity.this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }

        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    public void play(boolean play) {
        if (!play) {
            stopService(new Intent(Guest_activity.this, RadiophonyService.class));
        } else {
            startService(new Intent(Guest_activity.this, RadiophonyService.class));
        }
    }

    private void initdeclare() {
        toolbar = (Toolbar) findViewById(R.id.toolbar123);
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(0xffffffff);
        frame_footer = (FrameLayout) findViewById(R.id.frame_footer);
        Tv_footer_track_name = (TextView) findViewById(R.id.Tv_footer_track_name);
        Tv_footer_tag_name = (TextView) findViewById(R.id.Tv_footer_tag_name);
        Iv_play_pause = (ImageView) findViewById(R.id.Iv_play_pause);
        Iv_track_image = (ImageView) findViewById(R.id.Iv_track_image);

        Iv_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service = RadiophonyService.getInstance();
                if (Player.message == true) {
                    service.pause();
                    Player.message = false;
                    //Player.mVuMeterView.pause();
                    //frame_footer.setVisibility(View.GONE);
                    startService(new Intent(Guest_activity.this, Notification_Serviceplay.class));
                }
            }
        });

        Iv_track_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Explore_model_item itemListRadio = MusicPlayer_Prefrence.getInstance(Guest_activity.this).getSave(Guest_activity.this);
                // || adapter_data.size() == 0 || Likes_activity.fav_adapter_data.size() == 0
                if (itemListRadio == null) {
                    Toast.makeText(Guest_activity.this, "Play any track first", Toast.LENGTH_SHORT).show();
                } else {
                    if (Player.message == true) {
                        Intent e = new Intent(Guest_activity.this, Player.class);
                        e.putExtra("from", "drawer_player");
                        RadiophonyService.initialize(Guest_activity.this, itemListRadio, 1);
                        boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
                   /*     frame_footer.setVisibility(View.VISIBLE);
                        Tv_footer_track_name.setText(Station_Util.Song_name);
                        Tv_footer_track_name.setSelected(true);
                        Tv_footer_tag_name.setText(Station_Util.tag);
                        Tv_footer_tag_name.setSelected(true);

                        if (Station_Util.Song_image != null) {
                            try {
                                Transformation transformation = new RoundedTransformationBuilder()
                                        .borderColor(Color.BLACK)
                                        .cornerRadiusDp(5)
                                        .oval(false)
                                        .build();
                                Picasso.with(Guest_activity.this).load(Station_Util.Song_image).fit()
                                        .transform(transformation).into(Iv_track_image);
                            } catch (IllegalArgumentException a) {
                                Log.e("Picasso path empty", String.valueOf(a));
                            }
                        }*/
                        startActivity(e);
                    } else {
                        Intent e = new Intent(Guest_activity.this, Player.class);
                        e.putExtra("from", "drawer_player");
                        RadiophonyService.initialize(Guest_activity.this, itemListRadio, 1);
                        play(true);
                     /*   frame_footer.setVisibility(View.VISIBLE);
                        Tv_footer_track_name.setText(Station_Util.Song_name);
                        Tv_footer_track_name.setSelected(true);
                        Tv_footer_tag_name.setText(Station_Util.tag);
                        Tv_footer_tag_name.setSelected(true);

                        if (Station_Util.Song_image != null) {
                            try {
                                Transformation transformation = new RoundedTransformationBuilder()
                                        .borderColor(Color.BLACK)
                                        .cornerRadiusDp(5)
                                        .oval(false)
                                        .build();
                                Picasso.with(Guest_activity.this).load(Station_Util.Song_image).fit()
                                        .transform(transformation).into(Iv_track_image);
                            } catch (IllegalArgumentException a) {
                                Log.e("Picasso path empty", String.valueOf(a));
                            }
                        }*/

                        startActivity(e);
                    }
                }
            }
        });


        recycler_view_most_liked = (RecyclerView) findViewById(R.id.recycler_view_most_liked);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_view_most_liked.setLayoutManager(layoutManager1);

        recycler_view_recent = (RecyclerView) findViewById(R.id.recycler_view_recent);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_view_recent.setLayoutManager(layoutManager2);

        recycler_view_popular = (RecyclerView) findViewById(R.id.recycler_view_popular);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_view_popular.setLayoutManager(layoutManager3);


        empty_recent = (TextView) findViewById(R.id.empty_recent);
        empty_most_liked = (TextView) findViewById(R.id.empty_most_liked);
        empty_popular = (TextView) findViewById(R.id.empty_popular);

        txt_username_profile = (TextView) findViewById(R.id.txt_username_profile);
        txt_city_country = (TextView) findViewById(R.id.txt_city_country);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        // circularProgressBar = (CircularProgressBar) findViewById(R.id.progress);
        Iv_about = (ImageView) findViewById(R.id.Iv_about);
        //IB_menu_items = (ImageButton) findViewById(R.id.IB_menu_items);
        // IB_menu_items.setOnClickListener(this);
        Iv_about.setOnClickListener(this);
        Iv_about.setOnLongClickListener(this);

        adapter_data = new ArrayList<>();

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
        if (v == Iv_about) {
            Intent i = new Intent(Guest_activity.this, About_app.class);
            startActivity(i);
        } /*else if (v == IB_menu_items) {
            if (drawerLayout.isDrawerOpen(Gravity.START)) {
                drawerLayout.closeDrawers();
            } else {
                drawerLayout.openDrawer(Gravity.START);
            }
        }*/
    }

    private void initiatePopupWindow1() {
        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) Guest_activity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup, (ViewGroup) findViewById(R.id.popup));

            RelativeLayout mainlayout = (RelativeLayout) layout.findViewById(R.id.popup);
            mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //pwindo_logout.dismiss();
                }
            });
            LinearLayout lin_no = (LinearLayout) layout.findViewById(R.id.nolayout);
            lin_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                }
            });
            LinearLayout lin_yes = (LinearLayout) layout.findViewById(R.id.yeslayout);
            lin_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


             /*
                    if (RadiophonyService.exoPlayer != null) {
                        RadiophonyService.exoPlayer.stop();
                    }
                    MusicPlayer_Prefrence.getInstance(Guest_activity.this).clear();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                    Intent iq = new Intent(Guest_activity.this, Login.class);
                    iq.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(iq);
                    Toast.makeText(Guest_activity.this, "Log Out Successfully!:)", Toast.LENGTH_LONG).show();
                    finish();

                    */

                    if (RadiophonyService.exoPlayer != null) {
                        RadiophonyService.exoPlayer.stop();
                    }

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    pwindo.dismiss();
                    finish();
                }
            });
            pwindo = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void popUp_GUEST() {
        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) Guest_activity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_guest, (ViewGroup) findViewById(R.id.popup_guest));

            RelativeLayout mainlayout = (RelativeLayout) layout.findViewById(R.id.popup_guest);
            mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //pwindo_logout.dismiss();
                }
            });
            LinearLayout lin_no = (LinearLayout) layout.findViewById(R.id.nolayout);
            lin_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                }
            });
            LinearLayout lin_yes = (LinearLayout) layout.findViewById(R.id.yeslayout);
            lin_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences preferences = getSharedPreferences("logincheck", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    if (RadiophonyService.exoPlayer != null) {
                        RadiophonyService.exoPlayer.stop();
                    }
                    stopService(new Intent(Guest_activity.this, RadiophonyService.class));
                    MusicPlayer_Prefrence.getInstance(Guest_activity.this).clear();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                    if (pwindo != null && pwindo.isShowing()) {
                        pwindo.dismiss();
                    }
                    Intent iq = new Intent(Guest_activity.this, Login.class);
                    iq.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(iq);
                    finish();

                }
            });
            pwindo = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        initiatePopupWindow1();
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(this, v.getContentDescription(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reciever);
        unregisterReceiver(receiver1);

        if (RadiophonyService.exoPlayer != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
        }

    }

    public class Data_Reciever1 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            service = RadiophonyService.getInstance();

            if (Player.message == true) {
               // Player.Iv_btn_PlayPause.setImageResource(R.drawable.ic_play_button);
                service.pause();
                //frame_footer.setVisibility(View.GONE);
                startService(new Intent(Guest_activity.this, Notification_Serviceplay.class));
                Player.message = false;

            } else if (Player.message == false) {

              //  Player.Iv_btn_PlayPause.setImageResource(R.drawable.ic_pause_button);
                service.start();
                //frame_footer.setVisibility(View.VISIBLE);
                startService(new Intent(Guest_activity.this, Notification_Serviceplay.class));
                Player.message = true;
            }
            sendBroadcast(new Intent(Player.RECIEVER_NOTI_PLAYPAUSE1));
        }
    }

    public class Data_Reciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("-----player reciever-----");

            get_mst_popular_recycler();
            //get_mst_popular_recycler_volly();
            get_Most_Likes_recycler();
            get_recent_recycler();

            if (adapter_most_likes != null) {
                adapter_most_likes.notifyDataSetChanged();
            }
            if (adapter_mst_popular != null) {
                adapter_mst_popular.notifyDataSetChanged();
            }
            if (adapter_recent != null) {
                adapter_recent.notifyDataSetChanged();
            }
        }
    }

}
