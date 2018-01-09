package com.sekhontech.singering.circleMenu;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.sekhontech.singering.Activities.About_app;
import com.sekhontech.singering.Activities.Explore;
import com.sekhontech.singering.Activities.Friends_activity;
import com.sekhontech.singering.Activities.Likes_activity;
import com.sekhontech.singering.Activities.Login;
import com.sekhontech.singering.Activities.Message_Activity;
import com.sekhontech.singering.Activities.My_Downloads;
import com.sekhontech.singering.Activities.My_Profile_Screen;
import com.sekhontech.singering.Activities.My_Tracks;
import com.sekhontech.singering.Activities.My_following_activity;
import com.sekhontech.singering.Activities.Notifications;
import com.sekhontech.singering.Activities.Player;
import com.sekhontech.singering.Activities.Playlist_activity;
import com.sekhontech.singering.Activities.Search_userprofl;
import com.sekhontech.singering.Activities.Settings;
import com.sekhontech.singering.Activities.Stream_activity;
import com.sekhontech.singering.Activities.Track_Upload;
import com.sekhontech.singering.Adapters.Adapter_favorite_recycler;
import com.sekhontech.singering.Adapters.Adapter_most_liked_recycler;
import com.sekhontech.singering.Adapters.Adapter_most_popular_recycler;
import com.sekhontech.singering.Adapters.Adapter_recent_recycler;
import com.sekhontech.singering.Adapters.Adapter_recent_users_recycler;
import com.sekhontech.singering.Models.Explore_model_item;
import com.sekhontech.singering.Models.Notification_model_item;
import com.sekhontech.singering.Models.Recent_Activity_model_item;
import com.sekhontech.singering.Models.Search_model_item;
import com.sekhontech.singering.Notifications.Notification_Serviceplay;
import com.sekhontech.singering.Preferences.MusicPlayer_Prefrence;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Blur;
import com.sekhontech.singering.Utilities.Singering_Database;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.admobadapter.expressads.AdmobExpressRecyclerAdapterWrapper;
import com.sekhontech.singering.admobadapter.expressads.NativeExpressAdViewHolder;
import com.sekhontech.singering.listener.OnLoadMoreListener;
import com.sekhontech.singering.service.RadiophonyService;
import com.sekhontech.singering.service.Test_code;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class CircleActivity extends AppCompatActivity implements View.OnLongClickListener {

    public static final String RECIEVER_NOTI_PLAYPAUSE = "Player";
    public static final String TAG = "General_Setting";
    public static String RECIEVER_DATA = "com.sekhontech.singering_app";
    public static ArrayList<Explore_model_item> adapter_data = new ArrayList<>();
    public static ArrayList<Explore_model_item> list_recycler_fav = new ArrayList<Explore_model_item>();
    public static ArrayList<Explore_model_item> list_recycler_most_likes = new ArrayList<Explore_model_item>();
    public static ArrayList<Explore_model_item> list_recycler_recent = new ArrayList<Explore_model_item>();
    public static ArrayList<Explore_model_item> list_recycler_mst_popular = new ArrayList<Explore_model_item>();
    public static ArrayList<Search_model_item> list_recycler_recent_users = new ArrayList<Search_model_item>();
    public static FrameLayout frame_footer;
    public static ImageView Iv_track_image;
    public static ImageView Iv_play_pause;
    public static TextView Tv_footer_track_name, Tv_footer_tag_name;
    public static RecyclerView mRecyclerView_fav, recycler_view_most_liked, recycler_view_recent, recycler_view_popular, recycler_view_recent_user;
    static String noti_count;
    private static String KEY_SUCCESS = "success";
    String uid;
    CircularProgressBar circularProgressBar;
    Data_Reciever reciever;
    PopupWindow pwindo_menu;
    PopupWindow pwindo;
    Data_Reciever1 receiver1;
    RadiophonyService service;
    Singering_Database db;
    SharedPreferences sharedpref;
    CircleImageView circleImageView;
    String str_userimage, str_username, str_cover_image, totaltracks_count = "", follower_count = "", likes_count = "";
    ImageView Iv_cover;
    int counter;
    TextView view_two;
    NestedScrollView nestedscroll_view;
    ImageView settings, notification;
    FrameLayout frame_notification;
    TextView notification_count;
    int mutedColor = R.attr.colorPrimary;
    AdmobExpressRecyclerAdapterWrapper adapterWrapper;
    String check;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TextView txt_username, txt_count_tracks, txt_likes_count, empty_fav, empty_most_liked, empty_recent, empty_popular, empty_recent_user;
    private Adapter_favorite_recycler adapter_fav;
    private Adapter_most_liked_recycler adapter_most_likes;
    private Adapter_recent_recycler adapter_recent;
    private Adapter_recent_recycler1 adapter_recent1;
    private Adapter_favorite_recycler1 adapter_favorite_recycler1;
    private Adapter_most_popular_recycler adapter_mst_popular;
    private Adapter_recent_users_recycler adapter_recent_users;
    private String[] mItemTexts = new String[]{"", "", "", "", "", "", ""};
    private int[] mItemImgs = new int[]{
            R.drawable.playlist_icon, R.drawable.ic_explore, R.drawable.friends_icon,
            R.drawable.message_icon, R.drawable.thumb_up_button,
            R.drawable.stream_icon, R.drawable.settings
    };
    private String[] permissions = {"android.permission.CAMERA"};
    private String[] getPermissionsread = {"android.permission.READ_EXTERNAL_STORAGE"};
    private String[] permissions1 = {"android.permission.RECORD_AUDIO"};

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 1);
            }
        } else {
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG, "CAMERA permission has already been granted. Displaying camera preview.");
            // showCameraPreview();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(getPermissionsread, 1);
            }
        } else {
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG, "Read External storage permission has already been granted. Displaying camera preview.");
            // showCameraPreview();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions1, 1);
            }
        } else {
            Log.i(TAG, "Record permission has already been granted.");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(CircleActivity.this);

        setContentView(R.layout.activity_main02);
        initdeclare();
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        sharedpref = getSharedPreferences("logincheck", MODE_PRIVATE);
        db = new Singering_Database(CircleActivity.this);

        str_userimage = sharedpref.getString("image", "");
        str_cover_image = sharedpref.getString("cover", "");
        str_username = sharedpref.getString("username", "");
        reciever = new Data_Reciever();
        registerReceiver(reciever, new IntentFilter(RECIEVER_DATA));

        receiver1 = new Data_Reciever1();
        registerReceiver(receiver1, new IntentFilter(RECIEVER_NOTI_PLAYPAUSE));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar.setTitleTextColor(0xffffffff);

        check = getSharedPreferences("logincheck", MODE_PRIVATE).getString("check_one_time", "false");
        Test_code.initialize(check);
        startService(new Intent(CircleActivity.this, Test_code.class));

        initNavigationDrawer();
        initCollapsingToolbar();
        GetData();
        get_favorite_recycler1();
        get_recent_recycler1();
        get_mst_popular_recycler();
        get_Most_Likes_recycler();
        get_recent_users();

        set_ONLINE();
        getNotification_Data();
        //getFriends_activity_data();


        //get_favorite_recycler();


    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_info, menu);

       /* searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));*/
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_info:
                Intent i = new Intent(CircleActivity.this, About_app.class);
                startActivity(i);
                break;

            case R.id.action_search:
                Intent intent = new Intent(CircleActivity.this, Explore.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    private void initdeclare() {
        nestedscroll_view = (NestedScrollView) findViewById(R.id.nestedscroll_view);
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
                    frame_footer.setVisibility(View.GONE);
                    startService(new Intent(CircleActivity.this, Notification_Serviceplay.class));
                }
            }
        });

        Iv_track_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Explore_model_item itemListRadio = MusicPlayer_Prefrence.getInstance(CircleActivity.this).getSave(CircleActivity.this);
                if (itemListRadio != null) {
                    if (Player.message == true) {
                        Intent e = new Intent(CircleActivity.this, Player.class);
                        e.putExtra("from", "drawer_player");
                        RadiophonyService.initialize(CircleActivity.this, itemListRadio, 1);
                        frame_footer.setVisibility(View.VISIBLE);
                        CircleActivity.Tv_footer_track_name.setText(Station_Util.Song_name);
                        CircleActivity.Tv_footer_track_name.setSelected(true);
                        CircleActivity.Tv_footer_tag_name.setText(Station_Util.tag);
                        CircleActivity.Tv_footer_tag_name.setSelected(true);

                        if (Station_Util.Song_image != null) {
                            try {
                                Transformation transformation = new RoundedTransformationBuilder()
                                        .borderColor(Color.BLACK)
                                        .cornerRadiusDp(5)
                                        .oval(false)
                                        .build();
                                Picasso.with(CircleActivity.this).load(Station_Util.Song_image).fit()
                                        .transform(transformation).into(CircleActivity.Iv_track_image);
                            } catch (IllegalArgumentException a) {
                                Log.e("Picasso path empty", String.valueOf(a));
                            }
                        } else {
                            CircleActivity.Iv_track_image.setImageResource(R.drawable.mic_icon);
                        }
                        startActivity(e);
                    } else {
                        Intent e = new Intent(CircleActivity.this, Player.class);
                        e.putExtra("from", "drawer_player");
                        RadiophonyService.initialize(CircleActivity.this, itemListRadio, 1);
                        play(true);
                        frame_footer.setVisibility(View.VISIBLE);
                        CircleActivity.Tv_footer_track_name.setText(Station_Util.Song_name);
                        CircleActivity.Tv_footer_track_name.setSelected(true);
                        CircleActivity.Tv_footer_tag_name.setText(Station_Util.tag);
                        CircleActivity.Tv_footer_tag_name.setSelected(true);

                        if (Station_Util.Song_image != null) {
                            try {
                                Transformation transformation = new RoundedTransformationBuilder()
                                        .borderColor(Color.BLACK)
                                        .cornerRadiusDp(5)
                                        .oval(false)
                                        .build();
                                Picasso.with(CircleActivity.this).load(Station_Util.Song_image).fit()
                                        .transform(transformation).into(CircleActivity.Iv_track_image);
                            } catch (IllegalArgumentException a) {
                                Log.e("Picasso path empty", String.valueOf(a));
                            }
                        } else {
                            CircleActivity.Iv_track_image.setImageResource(R.drawable.mic_icon);
                        }
                        startActivity(e);
                    }
                } else {
                    Toast.makeText(CircleActivity.this, "Play any track first", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mRecyclerView_fav = (RecyclerView) findViewById(R.id.mRecyclerView_fav);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView_fav.setLayoutManager(layoutManager);
        mRecyclerView_fav.setNestedScrollingEnabled(false);


        recycler_view_most_liked = (RecyclerView) findViewById(R.id.recycler_view_most_liked);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_view_most_liked.setLayoutManager(layoutManager1);
        recycler_view_most_liked.setNestedScrollingEnabled(false);


        recycler_view_recent = (RecyclerView) findViewById(R.id.recycler_view_recent);
        final LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_view_recent.setLayoutManager(layoutManager2);
        recycler_view_recent.setNestedScrollingEnabled(false);

        recycler_view_popular = (RecyclerView) findViewById(R.id.recycler_view_popular);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_view_popular.setLayoutManager(layoutManager3);
        recycler_view_popular.setNestedScrollingEnabled(false);


        recycler_view_recent_user = (RecyclerView) findViewById(R.id.recycler_view_recent_user);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_view_recent_user.setLayoutManager(layoutManager4);
        recycler_view_recent_user.setNestedScrollingEnabled(false);


        empty_fav = (TextView) findViewById(R.id.empty_fav);
        empty_recent = (TextView) findViewById(R.id.empty_recent);
        empty_most_liked = (TextView) findViewById(R.id.empty_most_liked);
        empty_popular = (TextView) findViewById(R.id.empty_popular);
        empty_recent_user = (TextView) findViewById(R.id.empty_recent_user);

        circularProgressBar = (CircularProgressBar) findViewById(R.id.progress);

        adapter_data = new ArrayList<>();
    }


    private void initCollapsingToolbar() {


        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.background_home);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                mutedColor = palette.getMutedColor(R.color.colorPrimary);
                collapsingToolbar.setContentScrimColor(mutedColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.black_trans80);
            }
        });

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void getFriends_activity_data() {

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "recent_activity.php?idu=" + uid, new JsonHttpResponseHandler() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                //  Log.e("FRIEND_ACTVTY_RESPONSE", String.valueOf(response));
                Recent_Activity_model_item model;
                try {
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
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void getNotification_Data() {
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        client.get(Station_Util.URL + "notifications.php?idu=" + uid, new JsonHttpResponseHandler() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // Log.e("NOTIRESPONSE", String.valueOf(response));
                Notification_model_item model;
                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("notification");
                            for (int i = 0; i < array.length(); i++) {
                                model = new Notification_model_item();
                                JSONObject obj = array.getJSONObject(i);
                                model.count = obj.getString("count");

                                if (model.count.equalsIgnoreCase("0")) {
                                    notification_count.setVisibility(View.GONE);
                                    //view.setText("");
                                    //view.setVisibility(View.GONE);
                                } else {
                                    counter = Integer.parseInt(model.count);
                                    notification_count.setVisibility(View.VISIBLE);
                                    notification_count.setText(model.count);
                                    setMenuCounter(counter);
                                    //  view.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setMenuCounter(@IdRes int count) {

        //view.setTextColor(Color.LTGRAY);
        // view.setGravity(Gravity.CENTER);
        //  view.setBackgroundResource(R.drawable.noti_back_color);
        // view.setPadding(16, 5, 16, 5);

        // FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        //  llp.setMargins(0, 25, 0, 0);
        //  view.setLayoutParams(llp);
        // view.setlay
        //view.setMargins(30, 20, 30, 0);
        //view.setGravity(Gravity.CENTER);
        //  view.setText(count > 0 ? String.valueOf(count) : null);
    }

    private void get_recent_users() {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "recent_user.php", new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // Log.d("Tag12345", statusCode + "");
                //Log.d("Response_image", String.valueOf(response));
                try {
                    list_recycler_recent_users.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("users");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String user_id = obj.getString("idu");
                                String first_name = obj.getString("first_name");
                                String last_name = obj.getString("last_name");
                                String username = obj.getString("username");
                                String image = obj.getString("image");
                                String country = obj.getString("country");
                                String city = obj.getString("city");
                                list_recycler_recent_users.add(new Search_model_item(user_id, username, first_name, last_name, country, city, image));
                            }
                        }
                    } else {
                        Toast.makeText(CircleActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (list_recycler_recent_users == null) {
                    empty_recent_user.setVisibility(View.VISIBLE);
                    recycler_view_recent_user.setVisibility(View.GONE);
                } else if (list_recycler_recent_users.isEmpty()) {
                    empty_recent_user.setVisibility(View.VISIBLE);
                    recycler_view_recent_user.setVisibility(View.GONE);
                } else {
                    empty_recent_user.setVisibility(View.GONE);
                    recycler_view_recent_user.setVisibility(View.VISIBLE);
                    adapter_recent_users = new Adapter_recent_users_recycler(CircleActivity.this);
                    adapter_recent_users.addAll(list_recycler_recent_users);
                    adapter_recent_users.notifyDataSetChanged();
                    //recycler_view_recent_user.setAdapter(adapter_recent_users);
                    if (getResources().getBoolean(R.bool.Ads_check) == true) {
                        initRecyclerViewItems_recent_users();
                    } else {
                        recycler_view_recent_user.setAdapter(adapter_recent_users);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(CircleActivity.this, "Slow internet connection", Toast.LENGTH_LONG).show();
            }

        });
    }

    private void get_mst_popular_recycler() {


        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "most_popular.php", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                //  Log.d("Tag1", statusCode + "");
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
                    adapter_mst_popular = new Adapter_most_popular_recycler(CircleActivity.this);
                    adapter_mst_popular.addAll(list_recycler_mst_popular);
                    adapter_mst_popular.notifyDataSetChanged();
                    // recycler_view_popular.setAdapter(adapter_mst_popular);
                    if (getResources().getBoolean(R.bool.Ads_check) == true) {
                        initRecyclerViewItems_most_popular();
                    } else {
                        recycler_view_popular.setAdapter(adapter_mst_popular);
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
                    adapter_recent = new Adapter_recent_recycler(CircleActivity.this);
                    adapter_recent.addAll(list_recycler_recent);
                    adapter_recent.notifyDataSetChanged();
                    // adapter.notifyDataSetChanged();
                    // recycler_view_recent.setAdapter(adapter_recent);
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


    private void get_recent_recycler1() {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        client.get(Station_Util.URL + "tracks.php?tracks=true", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Explore_model_item model;
                try {
                    list_recycler_recent.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("tracks");
                            for (int i = 0; i < 5; i++) {
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
                            Log.e("list_recycler_recent", "" + list_recycler_recent.size());
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

                    adapter_recent1 = new Adapter_recent_recycler1();
              /* adapter_recent1.notifyDataSetChanged();*/

                /*if (getResources().getBoolean(R.bool.Ads_check) == true) {
                    initRecyclerViewItems_recents();
                } else {
                    recycler_view_recent.setAdapter(adapter_recent1);
                }*/
                    recycler_view_recent.setAdapter(adapter_recent1);

                    adapter_recent1.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            Log.e("haint", "Load More");
                            list_recycler_recent.add(null);
                            adapter_recent1.notifyItemInserted(list_recycler_recent.size() - 1);

                            Log.e("list_recycler_recent1", "" + list_recycler_recent.size());
                        }
                    });
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

    private void get_favorite_recycler1() {

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        client.get(Station_Util.URL + "likes.php?usid=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // Log.d("Tag1", statusCode + "");
                //// Log.d("Response1", String.valueOf(response));

                Explore_model_item model;
                try {
                    list_recycler_fav.clear();
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("like");
                            for (int i = 0; i < 5; i++) {
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
                                    list_recycler_fav.add(model);
                                }
                                // db.insertLikes(model.id, "true");
                            }
                        } else {
                            Log.d("NoLikes", "NoLikes");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (list_recycler_fav == null) {
                    empty_fav.setVisibility(View.VISIBLE);
                    mRecyclerView_fav.setVisibility(View.GONE);
                } else if (list_recycler_fav.isEmpty()) {
                    empty_fav.setVisibility(View.VISIBLE);
                    mRecyclerView_fav.setVisibility(View.GONE);
                } else {
         /*       if (list_recycler_fav == null) {
                    empty_fav.setVisibility(View.VISIBLE);
                    mRecyclerView_fav.setVisibility(View.GONE);
                } else if (list_recycler_fav.isEmpty()) {
                    empty_fav.setVisibility(View.VISIBLE);
                    mRecyclerView_fav.setVisibility(View.GONE);
                } else {

                }


                else if (list_recycler_fav.size() <= 10) {
                    adapter_favorite_recycler1 = new Adapter_favorite_recycler1();
                    mRecyclerView_fav.setAdapter(adapter_favorite_recycler1);
                } else if (list_recycler_fav.size() > 10) {*/


                    empty_fav.setVisibility(View.GONE);
                    mRecyclerView_fav.setVisibility(View.VISIBLE);
                    adapter_favorite_recycler1 = new Adapter_favorite_recycler1();
                    mRecyclerView_fav.setAdapter(adapter_favorite_recycler1);


                    adapter_favorite_recycler1.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            Log.e("haint", "Load More");
                            list_recycler_fav.add(null);
                            adapter_favorite_recycler1.notifyItemInserted(list_recycler_fav.size() - 1);
                            //Load more data for reyclerview

                        }
                    });
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

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void initRecyclerViewItems_recents() {
        String[] testDevicesIds = new String[]{getString(R.string.testDeviceID), AdRequest.DEVICE_ID_EMULATOR};
        adapterWrapper = new AdmobExpressRecyclerAdapterWrapper(this, testDevicesIds) {
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent, int viewType) {
                NativeExpressAdView adView = adViewHolder.getAdView();
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT);
                CardView cardView = new CardView(CircleActivity.this);
                cardView.setLayoutParams(lp);
                TextView textView = new TextView(CircleActivity.this);
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
        adapterWrapper.setFirstAdIndex(3);
        recycler_view_recent.setAdapter(adapterWrapper); // setting an AdmobExpressRecyclerAdapterWrapper to a RecyclerView
    }

    private void initRecyclerViewItems_favorite() {
        String[] testDevicesIds = new String[]{getString(R.string.testDeviceID), AdRequest.DEVICE_ID_EMULATOR};
        adapterWrapper = new AdmobExpressRecyclerAdapterWrapper(this, testDevicesIds) {
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent, int viewType) {
                NativeExpressAdView adView = adViewHolder.getAdView();
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT);
                CardView cardView = new CardView(CircleActivity.this);
                cardView.setLayoutParams(lp);
                TextView textView = new TextView(CircleActivity.this);
                textView.setLayoutParams(lp);
                textView.setText("Ad is loading...");
                textView.setTextColor(Color.RED);
                cardView.addView(textView);
                cardView.addView(adView);
                return cardView;
            }
        };
        adapterWrapper.setAdapter(adapter_fav); //wrapping your adapter with a AdmobExpressRecyclerAdapterWrapper.
        adapterWrapper.setLimitOfAds(10);
        adapterWrapper.setNoOfDataBetweenAds(10);
        adapterWrapper.setFirstAdIndex(3);
        mRecyclerView_fav.setAdapter(adapterWrapper); // setting an AdmobExpressRecyclerAdapterWrapper to a RecyclerView
    }

    private void initRecyclerViewItems_most_popular() {
        String[] testDevicesIds = new String[]{getString(R.string.testDeviceID), AdRequest.DEVICE_ID_EMULATOR};
        adapterWrapper = new AdmobExpressRecyclerAdapterWrapper(this, testDevicesIds) {
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent, int viewType) {
                NativeExpressAdView adView = adViewHolder.getAdView();
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT);
                CardView cardView = new CardView(CircleActivity.this);
                cardView.setLayoutParams(lp);
                TextView textView = new TextView(CircleActivity.this);
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
        adapterWrapper.setFirstAdIndex(3);
        recycler_view_popular.setAdapter(adapterWrapper); // setting an AdmobExpressRecyclerAdapterWrapper to a RecyclerView
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initRecyclerViewItems_most_likes() {
        String[] testDevicesIds = new String[]{getString(R.string.testDeviceID), AdRequest.DEVICE_ID_EMULATOR};
        adapterWrapper = new AdmobExpressRecyclerAdapterWrapper(this, testDevicesIds) {
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent, int viewType) {
                NativeExpressAdView adView = adViewHolder.getAdView();
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT);
                CardView cardView = new CardView(CircleActivity.this);
                cardView.setLayoutParams(lp);

                TextView textView = new TextView(CircleActivity.this);
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
        adapterWrapper.setFirstAdIndex(3);
        recycler_view_most_liked.setAdapter(adapterWrapper); // setting an AdmobExpressRecyclerAdapterWrapper to a RecyclerView
    }

    private void initRecyclerViewItems_recent_users() {
        String[] testDevicesIds = new String[]{getString(R.string.testDeviceID), AdRequest.DEVICE_ID_EMULATOR};
        adapterWrapper = new AdmobExpressRecyclerAdapterWrapper(this, testDevicesIds) {
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent, int viewType) {
                NativeExpressAdView adView = adViewHolder.getAdView();
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT);
                CardView cardView = new CardView(CircleActivity.this);
                cardView.setLayoutParams(lp);
                TextView textView = new TextView(CircleActivity.this);
                textView.setLayoutParams(lp);
                textView.setText("Ad is loading...");
                textView.setTextColor(Color.RED);
                cardView.addView(textView);
                cardView.addView(adView);
                return cardView;
            }
        };
        adapterWrapper.setAdapter(adapter_recent_users);
        adapterWrapper.setLimitOfAds(10);
        adapterWrapper.setNoOfDataBetweenAds(10);
        adapterWrapper.setFirstAdIndex(3);
        recycler_view_recent_user.setAdapter(adapterWrapper);
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
                    adapter_most_likes = new Adapter_most_liked_recycler(CircleActivity.this);
                    adapter_most_likes.addAll(list_recycler_most_likes);
                    adapter_most_likes.notifyDataSetChanged();
                    //recycler_view_most_liked.setAdapter(adapter_most_likes);
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

    private void get_favorite_recycler() {
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "likes.php?usid=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // Log.d("Tag1", statusCode + "");
                //// Log.d("Response1", String.valueOf(response));

                Explore_model_item model;
                try {
                    list_recycler_fav.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("like");
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
                                    list_recycler_fav.add(model);
                                }
                                // db.insertLikes(model.id, "true");
                            }
                        } else {
                            Log.d("NoLikes", "NoLikes");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (list_recycler_fav == null) {
                    empty_fav.setVisibility(View.VISIBLE);
                    mRecyclerView_fav.setVisibility(View.GONE);
                } else if (list_recycler_fav.isEmpty()) {
                    empty_fav.setVisibility(View.VISIBLE);
                    mRecyclerView_fav.setVisibility(View.GONE);
                } else {
                    empty_fav.setVisibility(View.GONE);
                    mRecyclerView_fav.setVisibility(View.VISIBLE);
                    adapter_fav = new Adapter_favorite_recycler(CircleActivity.this);
                    adapter_fav.addAll(list_recycler_fav);
                    adapter_fav.notifyDataSetChanged();
                    if (getResources().getBoolean(R.bool.Ads_check) == true) {
                        initRecyclerViewItems_favorite();
                    } else {
                        mRecyclerView_fav.setAdapter(adapter_fav);
                    }
                    // mRecyclerView_fav.setAdapter(adapter_fav);
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (adapter_fav != null) {
            adapter_fav.notifyDataSetChanged();
        }
    }

    public void initNavigationDrawer() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        disableNavigationViewScrollbars(navigationView);
        View hView = navigationView.getHeaderView(0);
        circleImageView = (CircleImageView) hView.findViewById(R.id.profile_image);
        Iv_cover = (ImageView) hView.findViewById(R.id.Iv_cover);
        txt_likes_count = (TextView) hView.findViewById(R.id.txt_likes_count);
        txt_count_tracks = (TextView) hView.findViewById(R.id.txt_count_tracks);
        settings = (ImageView) hView.findViewById(R.id.settings);
        notification = (ImageView) hView.findViewById(R.id.notification);
        notification_count = (TextView) hView.findViewById(R.id.notification_count);
        frame_notification = (FrameLayout) hView.findViewById(R.id.frame_notification);
        frame_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_noti = new Intent(CircleActivity.this, Notifications.class);
                startActivity(i_noti);
                drawerLayout.closeDrawers();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_set = new Intent(CircleActivity.this, Settings.class);
                startActivity(i_set);
                drawerLayout.closeDrawers();
            }
        });

        // view = (TextView) navigationView.getMenu().findItem(R.id.notification).getActionView();
        view_two = (TextView) navigationView.getMenu().findItem(R.id.friends_activity).getActionView();

        if (str_userimage.equalsIgnoreCase("") | str_userimage.isEmpty() | str_cover_image.isEmpty() | str_cover_image.equalsIgnoreCase("")) {

            circleImageView.setImageResource(R.drawable.camera_icontwo);
        } else {
            Picasso.with(CircleActivity.this)
                    .load(Station_Util.IMAGE_URL_AVATARS + str_userimage)
                    .placeholder(R.drawable.camera_icontwo)
                    .into(circleImageView);

            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                Picasso.with(CircleActivity.this).load(Station_Util.IMAGE_URL_COVERS + str_cover_image).transform(new Blur(CircleActivity.this, 25))
                        .placeholder(R.drawable.camera_icontwo)
                        .into(Iv_cover);
                Log.e("CHECK VERSION", "GREATER");
            } else {
                Picasso.with(CircleActivity.this).load(Station_Util.IMAGE_URL_COVERS + str_cover_image)
                        .placeholder(R.drawable.camera_icontwo)
                        .into(Iv_cover);
                Log.e("CHECK VERSION", "SMALLER");
            }

        }
        txt_username = (TextView) hView.findViewById(R.id.name);
        txt_username.setText(str_username);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {

                    case R.id.home:
                        //home
                        Intent i_e = new Intent(CircleActivity.this, CircleActivity.class);
                        startActivity(i_e);
                        finish();

                        drawerLayout.closeDrawers();
                        break;

                    case R.id.stream:

                        Intent i_stream = new Intent(CircleActivity.this, Stream_activity.class);
                        startActivity(i_stream);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.upload_track:

                        Intent i_upload = new Intent(CircleActivity.this, Track_Upload.class);
                        startActivity(i_upload);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.downloads:

                        Intent download = new Intent(CircleActivity.this, My_Downloads.class);
                        startActivity(download);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.explore:

                        Intent i_explore = new Intent(CircleActivity.this, Explore.class);
                        startActivity(i_explore);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.profile:
                        Intent i_profile = new Intent(CircleActivity.this, My_Profile_Screen.class);
                        startActivity(i_profile);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.my_tracks:
                        // uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
                        Intent i = new Intent(CircleActivity.this, My_Tracks.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.likes:
                        Intent i_likes = new Intent(CircleActivity.this, Likes_activity.class);
                        startActivity(i_likes);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.search:
                        //search
                        Intent i_setw = new Intent(CircleActivity.this, Search_userprofl.class);
                        startActivity(i_setw);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.message:

                        Intent i_message = new Intent(CircleActivity.this, Message_Activity.class);
                        startActivity(i_message);
                        drawerLayout.closeDrawers();
                        break;
                 /*   case R.id.settings:
                        Intent i_set = new Intent(CircleActivity.this, Settings.class);
                        startActivity(i_set);
                        drawerLayout.closeDrawers();
                        //return true;
                        break;*/

                    case R.id.playlist:
                        Intent i_playlist = new Intent(CircleActivity.this, Playlist_activity.class);
                        startActivity(i_playlist);
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.friends_activity:
                        Intent i_noti2 = new Intent(CircleActivity.this, Friends_activity.class);
                        startActivity(i_noti2);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.player:
                        Explore_model_item itemListRadio = MusicPlayer_Prefrence.getInstance(CircleActivity.this).getSave(CircleActivity.this);
                        if (itemListRadio != null) {
                            if (Player.message == true) {
                                Intent e = new Intent(CircleActivity.this, Player.class);
                                e.putExtra("from", "drawer_player");
                                RadiophonyService.initialize(CircleActivity.this, itemListRadio, 1);
                                frame_footer.setVisibility(View.VISIBLE);
                                CircleActivity.Tv_footer_track_name.setText(Station_Util.Song_name);
                                CircleActivity.Tv_footer_track_name.setSelected(true);
                                CircleActivity.Tv_footer_tag_name.setText(Station_Util.tag);
                                CircleActivity.Tv_footer_tag_name.setSelected(true);

                                if (Station_Util.Song_image != null) {
                                    try {
                                        Transformation transformation = new RoundedTransformationBuilder()
                                                .borderColor(Color.BLACK)
                                                .cornerRadiusDp(5)
                                                .oval(false)
                                                .build();
                                        Picasso.with(CircleActivity.this).load(Station_Util.Song_image).fit()
                                                .transform(transformation).into(CircleActivity.Iv_track_image);
                                    } catch (IllegalArgumentException a) {
                                        Log.e("Picasso path empty", String.valueOf(a));
                                    }
                                } else {
                                    CircleActivity.Iv_track_image.setImageResource(R.drawable.mic_icon);
                                }
                                //play(true);
                                startActivity(e);
                            } else {
                                Intent e = new Intent(CircleActivity.this, Player.class);
                                e.putExtra("from", "drawer_player");
                                RadiophonyService.initialize(CircleActivity.this, itemListRadio, 1);
                                play(true);
                                frame_footer.setVisibility(View.VISIBLE);
                                CircleActivity.Tv_footer_track_name.setText(Station_Util.Song_name);
                                CircleActivity.Tv_footer_track_name.setSelected(true);
                                CircleActivity.Tv_footer_tag_name.setText(Station_Util.tag);
                                CircleActivity.Tv_footer_tag_name.setSelected(true);

                                if (Station_Util.Song_image != null) {
                                    try {
                                        Transformation transformation = new RoundedTransformationBuilder()
                                                .borderColor(Color.BLACK)
                                                .cornerRadiusDp(5)
                                                .oval(false)
                                                .build();
                                        Picasso.with(CircleActivity.this).load(Station_Util.Song_image).fit()
                                                .transform(transformation).into(CircleActivity.Iv_track_image);
                                    } catch (IllegalArgumentException a) {
                                        Log.e("Picasso path empty", String.valueOf(a));
                                    }
                                } else {
                                    CircleActivity.Iv_track_image.setImageResource(R.drawable.mic_icon);
                                }
                                startActivity(e);
                            }
                        } else {
                            Toast.makeText(CircleActivity.this, "Play any track first", Toast.LENGTH_SHORT).show();
                        }
                        //return true;
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

                    case R.id.rate_app:
                        final String appPackageNam1e = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageNam1e)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageNam1e)));
                        }
                        break;
                    case R.id.log_out:

                        set_LOGOUT();
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


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(CircleActivity.this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
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

    private void set_LOGOUT() {
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
        String token = getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("token", "");
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);


        final ProgressDialog pDialog = new ProgressDialog(CircleActivity.this);

        client.get(Station_Util.URL + "logout.php?idu=" + uid + "&token=" + token, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.setIndeterminate(true);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.e("LOG OUT SUCCESSFUL", String.valueOf(response));
                LoginManager.getInstance().logOut();
                pDialog.dismiss();
                SharedPreferences preferences = getSharedPreferences("logincheck", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                if (RadiophonyService.exoPlayer != null) {
                    RadiophonyService.exoPlayer.stop();
                }
                stopService(new Intent(CircleActivity.this, Test_code.class));

                MusicPlayer_Prefrence.getInstance(CircleActivity.this).clear();
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1);
                Intent iq = new Intent(CircleActivity.this, Login.class);
                iq.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(iq);
                Toast.makeText(CircleActivity.this, "Log Out Successfully!:)", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(CircleActivity.this, "Error Check Connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void GetData() {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
        Log.e("str_user_id", uid);
        final ProgressDialog pDialog = new ProgressDialog(CircleActivity.this);

        client.get(Station_Util.URL + "visitorpro.php?vid=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                //   pDialog.setMessage("Please wait...");
                //  pDialog.setCancelable(true);
                //  pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                String username = "", first_name = "", last_name = "", country = "", website = "", city = "", description = "", facebook = "",
                        twitter = "", gplus = "", youtube = "", vimeo = "", tumblr = "", soundcloud = "", myspace = "", lastfm = "", image = "", cover = "";
                //  Log.e("prnt", response.toString());
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    String obq = obj.getString("search");
                    JSONArray array = new JSONArray(obq);
                    JSONObject obj1 = array.getJSONObject(0);
                    String img = obj1.getString("image");
                    username = obj1.getString("username");
                    first_name = obj1.getString("first_name");
                    last_name = obj1.getString("last_name");
                    country = obj1.getString("country");
                    website = obj1.getString("website");
                    String email = obj1.getString("email");
                    city = obj1.getString("city");
                    description = obj1.getString("description");
                    String private_status = obj1.getString("private");
                    String chat_status = obj1.getString("offline");

                    String profile_img = obj1.getString("image");
                    String cover_img = obj1.getString("cover");

                    String google = obj1.getString("gplus");

                    facebook = obj1.getString("facebook");
                    twitter = obj1.getString("twitter");
                    gplus = obj1.getString("gplus");
                    youtube = obj1.getString("youtube");
                    vimeo = obj1.getString("vimeo");
                    tumblr = obj1.getString("tumblr");
                    soundcloud = obj1.getString("soundcloud");
                    myspace = obj1.getString("myspace");
                    lastfm = obj1.getString("lastfm");
                    totaltracks_count = obj1.getString("totaltracks");
                    follower_count = obj1.getString("follower");
                    likes_count = obj1.getString("likes");
                    String notificationl = obj1.getString("notificationl");
                    String notificationc = obj1.getString("notificationc");
                    String notificationd = obj1.getString("notificationd");
                    String notificationf = obj1.getString("notificationf");
                    String email_comment = obj1.getString("email_comment");
                    String email_like = obj1.getString("email_like");
                    String email_new_friend = obj1.getString("email_new_friend");


                    SharedPreferences pref = getSharedPreferences("logincheck", MODE_PRIVATE);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("city", city);
                    //edit.putString("uid", uid);
                    edit.putString("country", country);
                    edit.putString("username", username);
                    edit.putString("image", img);
                    edit.putString("email", email);
                    edit.putString("first_name", first_name);
                    edit.putString("last_name", last_name);
                    edit.putString("website", website);
                    edit.putString("description", description);
                    edit.putString("private_status", private_status);
                    edit.putString("chat_status", chat_status);
                    edit.putString("profile_img", profile_img);
                    edit.putString("cover_img", cover_img);
                    edit.putString("facebook", facebook);
                    edit.putString("twitter", twitter);
                    edit.putString("google", google);
                    edit.putString("youtube", youtube);
                    edit.putString("soundcloud", soundcloud);
                    edit.putString("myspace", myspace);
                    edit.putString("lastfm", lastfm);
                    edit.putString("vimeo", vimeo);
                    edit.putString("tumblr", tumblr);
                    edit.putString("total_track_count", totaltracks_count);
                    edit.putString("follower_count", follower_count);
                    edit.putString("likes_count", likes_count);
                    edit.putString("notificationl", notificationl);
                    edit.putString("notificationc", notificationc);
                    edit.putString("notificationd", notificationd);
                    edit.putString("notificationf", notificationf);
                    edit.putString("email_comment", email_comment);
                    edit.putString("email_like", email_like);
                    edit.putString("email_new_friend", email_new_friend);
                    edit.apply();

                    txt_count_tracks.setText(totaltracks_count);
                    txt_likes_count.setText(likes_count);


                    str_userimage = profile_img;
                    str_cover_image = cover_img;

                    Picasso.with(CircleActivity.this)
                            .load(Station_Util.IMAGE_URL_AVATARS + str_userimage)
                            .placeholder(R.drawable.camera_icontwo)
                            .into(circleImageView);

                    Picasso.with(CircleActivity.this).load(Station_Util.IMAGE_URL_COVERS + str_cover_image).transform(new Blur(CircleActivity.this, 25))
                            .placeholder(R.drawable.camera_icontwo)
                            .into(Iv_cover);


                    circularProgressBar.progressiveStop();
                    circularProgressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //  Toast.makeText(CircleActivity.this, "Slow internet connection", Toast.LENGTH_LONG).show();
                //   pDialog.dismiss();
            }


            @Override
            public void onFinish() {
                super.onFinish();
                //  pDialog.dismiss();
                circularProgressBar.progressiveStop();
                circularProgressBar.setVisibility(View.GONE);
            }

        });
    }

    private void get_Following_person() {

        if (db.tableexist("followtable") == true) {
            db.deleteFollow();
        }

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        // final ProgressDialog pDialog = new ProgressDialog(My_Profile_Screen.this);

        client.get(Station_Util.URL + "follows.php?followingid=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // pDialog.setMessage("Please wait...");
                // pDialog.setCancelable(true);
                // pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String user_id, username, first_name, last_name, country, city, image;
                //  Log.d("Tag", statusCode + "");
                //  Log.d("Response121", String.valueOf(response));

                Search_model_item model;
                try {
                    My_following_activity.following_adapter_data.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            My_following_activity.following_adapter_data.clear();

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

                                My_following_activity.following_adapter_data.add(model);

                                db.insertFollow(model.user_id, "true");
                              /*  String check = db.get_Follow(model.user_id);
                                if (check.equalsIgnoreCase("true")) {

                                } else {
                                    db.insertFollow(model.user_id, "true");
                                }*/

                               /* } else {
                                    db.insertFollow(model.user_id, "false");
                                }*/
                            }
                        } else {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //pDialog.dismiss();

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

    private void getFavourites() {

        if (db.tableexist("liketable") == true) {
            db.deleteLikes();
        }
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "likes.php?usid=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("like");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String id = obj.getString("id");


                                // db.insertLikes(id, "true");


                           /*     String check = db.get_like(id);
                                if (check.equalsIgnoreCase("true")) {

                                } else {
                                    db.insertLikes(id, "true");
                                }*/

                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void play(boolean play) {
        if (!play) {
            stopService(new Intent(CircleActivity.this, RadiophonyService.class));
        } else {
            startService(new Intent(CircleActivity.this, RadiophonyService.class));
        }
    }

    private void initiatePopupWindowEXIT() {
        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) CircleActivity.this
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
                    if (RadiophonyService.exoPlayer != null) {
                        RadiophonyService.exoPlayer.stop();
                    }
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    if (pwindo != null && pwindo.isShowing()) {
                        pwindo.dismiss();
                    }
                    finish();
                    // set_OFFLINE();
                }
            });
            pwindo = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void set_OFFLINE() {
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        // final ProgressDialog pDialog = new ProgressDialog(CircleActivity.this);

        client.get(Station_Util.URL + "logout.php?idu=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("OFFLINE USER", String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(CircleActivity.this, "Error Check Connection", Toast.LENGTH_LONG).show();
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

    private void set_ONLINE() {
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        // final ProgressDialog pDialog = new ProgressDialog(CircleActivity.this);

        client.get(Station_Util.URL + "login_session.php?idu=" + uid, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("ONLINE USER", String.valueOf(response));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //  Toast.makeText(CircleActivity.this, "Error Check Connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        initiatePopupWindowEXIT();
    }


 /*   public boolean checkFavItem1(Search_model_item checkfav) {
        boolean check = false;
        //   List<Inicio_station> recents = RadioUtil.arr_recentstations;

        List<Search_model_item> favs = My_following_activity.following_adapter_data;
        if (favs != null) {
            for (Search_model_item station_data : favs) {
                if (station_data.equals(checkfav)) {
                    check = true;
                    System.out.println("-----follow check true---" + check);
                    break;
                }
            }
        }
        System.out.println("-----follow check false---" + check);
        return check;
    }*/

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(this, v.getContentDescription(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reciever);
        unregisterReceiver(receiver1);
        stopService(new Intent(CircleActivity.this, Test_code.class));

        if (RadiophonyService.exoPlayer != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public ImageView thumbnail;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.title);

            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public CircularProgressBar progressBar;
        public Button btn_loadmore;
        public TextView txt_loading;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            btn_loadmore = (Button) itemView.findViewById(R.id.btn_loadmore);
            progressBar = (CircularProgressBar) itemView.findViewById(R.id.progressBar1);
            txt_loading = (TextView) itemView.findViewById(R.id.txt_loading);
        }
    }

    private class Adapter_recent_recycler1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        private OnLoadMoreListener mOnLoadMoreListener;
        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;

        public Adapter_recent_recycler1() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recycler_view_recent.getLayoutManager();
            recycler_view_recent.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                            notifyDataSetChanged();
                        }
                        isLoading = true;
                    }
                }
            });
        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        public void removeItem(int position) {
            list_recycler_recent.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public int getItemViewType(int position) {
            return list_recycler_recent.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(CircleActivity.this).inflate(R.layout.listcategory_row, parent, false);
                return new UserViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(CircleActivity.this).inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof UserViewHolder) {
                final Explore_model_item user = list_recycler_recent.get(position);
                UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.tvName.setText(user.getTitle());
                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.BLACK)
                        .cornerRadiusDp(3)
                        .oval(false)
                        .build();
                Picasso.with(CircleActivity.this).load(Station_Util.IMAGE_THUMBNAIL + user.getArt()).transform(transformation)
                        .placeholder(R.drawable.adele)
                        .into(userViewHolder.thumbnail);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // int position = mContext.getAdapterPosition();
                        if (RadiophonyService.exoPlayer != null) {
                            RadiophonyService.exoPlayer.stop();
                            stopService(new Intent(CircleActivity.this, RadiophonyService.class));
                        }
                        MusicPlayer_Prefrence.getInstance(CircleActivity.this).save(CircleActivity.this, user);
                        MusicPlayer_Prefrence.getInstance(CircleActivity.this).save_position(position);

                        startService(new Intent(CircleActivity.this, RadiophonyService.class));
                        RadiophonyService.initialize(CircleActivity.this, user, 1);
                        play(true);
                        Intent i = new Intent(CircleActivity.this, Player.class);
                        i.putExtra("position", position);
                        i.putExtra("from", "recent_recycler");
                        i.putExtra("track_detail", user);
                        startActivity(i);
                    }
                });

            } else if (holder instanceof LoadingViewHolder) {
                final LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.btn_loadmore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                        loadingViewHolder.txt_loading.setVisibility(View.VISIBLE);
                        //Load more data for reyclerview
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("haint", "Load More 2");

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
                                            JSONArray array = response.getJSONArray("tracks");
                                            //Remove loading item
                                            list_recycler_recent.remove(list_recycler_recent.size() - 1);
                                            adapter_recent1.notifyItemRemoved(list_recycler_recent.size());
                                            //Load data
                                            final int index = list_recycler_recent.size();
                                            final int end = index + 5;
                                            for (int i = index; i < end; i++) {
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
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        loadingViewHolder.progressBar.setVisibility(View.GONE);
                                        loadingViewHolder.txt_loading.setVisibility(View.GONE);
                                        adapter_recent1.notifyDataSetChanged();
                                        adapter_recent1.setLoaded();
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
                        }, 3000);

                    }
                });
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return list_recycler_recent == null ? 0 : list_recycler_recent.size();
        }

        public void setLoaded() {
            isLoading = false;
        }
    }

    private class Adapter_favorite_recycler1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 3;
        private int lastVisibleItem, totalItemCount;

        public Adapter_favorite_recycler1() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView_fav.getLayoutManager();
            mRecyclerView_fav.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                            notifyDataSetChanged();
                        }
                        isLoading = true;
                    }
                }
            });
        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        public void removeItem(int position) {
            list_recycler_fav.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public int getItemViewType(int position) {
            return list_recycler_fav.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(CircleActivity.this).inflate(R.layout.listcategory_row, parent, false);
                return new UserViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(CircleActivity.this).inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof UserViewHolder) {
                final Explore_model_item user = list_recycler_fav.get(position);
                UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.tvName.setText(user.getTitle());
                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.BLACK)
                        .cornerRadiusDp(3)
                        .oval(false)
                        .build();
                Picasso.with(CircleActivity.this).load(Station_Util.IMAGE_THUMBNAIL + user.getArt()).transform(transformation)
                        .placeholder(R.drawable.adele)
                        .into(userViewHolder.thumbnail);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // int position = mContext.getAdapterPosition();
                        if (RadiophonyService.exoPlayer != null) {
                            RadiophonyService.exoPlayer.stop();
                            stopService(new Intent(CircleActivity.this, RadiophonyService.class));
                        }
                        MusicPlayer_Prefrence.getInstance(CircleActivity.this).save(CircleActivity.this, user);
                        MusicPlayer_Prefrence.getInstance(CircleActivity.this).save_position(position);

                        startService(new Intent(CircleActivity.this, RadiophonyService.class));
                        RadiophonyService.initialize(CircleActivity.this, user, 1);
                        play(true);
                        Intent i = new Intent(CircleActivity.this, Player.class);
                        i.putExtra("position", position);
                        i.putExtra("from", "recycler_fav_list");
                        i.putExtra("track_detail", user);
                        startActivity(i);
                    }
                });

            } else if (holder instanceof LoadingViewHolder) {
                final LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
                loadingViewHolder.btn_loadmore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                        loadingViewHolder.txt_loading.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("haint", "Load More 2");

                                AsyncHttpClient client = new AsyncHttpClient();
                                Station_Util.Https_code(client);
                                client.get(Station_Util.URL + "likes.php?usid=" + uid, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        // Log.d("Tag1", statusCode + "");
                                        // Log.d("Response_Recent", String.valueOf(response));
                                        Explore_model_item model;
                                        try {
                                            JSONArray array = response.getJSONArray("like");
                                            //Remove loading item
                                            list_recycler_fav.remove(list_recycler_fav.size() - 1);
                                            adapter_favorite_recycler1.notifyItemRemoved(list_recycler_fav.size());
                                            //Load data
                                            final int index = list_recycler_fav.size();
                                            final int end = index + 5;
                                            for (int i = index; i < end; i++) {

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
                                                    list_recycler_fav.add(model);
                                                }
                                                // db.insertLikes(model.id, "true");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        loadingViewHolder.progressBar.setVisibility(View.GONE);
                                        loadingViewHolder.txt_loading.setVisibility(View.GONE);
                                        adapter_favorite_recycler1.notifyDataSetChanged();
                                        adapter_favorite_recycler1.setLoaded();
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
                        }, 3000);


                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return list_recycler_fav == null ? 0 : list_recycler_fav.size();
        }

        public void setLoaded() {
            isLoading = false;
        }
    }

    public class Data_Reciever1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            service = RadiophonyService.getInstance();

            if (Player.message == true) {
                service.pause();
                frame_footer.setVisibility(View.GONE);
                startService(new Intent(CircleActivity.this, Notification_Serviceplay.class));
                Player.message = false;
            } else if (Player.message == false) {
                service.start();
                frame_footer.setVisibility(View.VISIBLE);
                CircleActivity.Tv_footer_track_name.setText(Station_Util.Song_name);
                CircleActivity.Tv_footer_track_name.setSelected(true);
                CircleActivity.Tv_footer_tag_name.setText(Station_Util.tag);
                CircleActivity.Tv_footer_tag_name.setSelected(true);

                boolean check_from_downloads = getSharedPreferences("logincheck", MODE_PRIVATE).getBoolean("check_from_downloads", false);
                if (check_from_downloads == true) {
                    Iv_track_image.setImageResource(R.drawable.mic_icon);
                } else {
                    if (Station_Util.Song_image != null) {
                        try {
                            Transformation transformation = new RoundedTransformationBuilder()
                                    .borderColor(Color.BLACK)
                                    .cornerRadiusDp(5)
                                    .oval(false)
                                    .build();
                            Picasso.with(CircleActivity.this).load(Station_Util.Song_image).fit()
                                    .transform(transformation).into(Iv_track_image);
                        } catch (IllegalArgumentException a) {
                            Log.e("Picasso path empty", String.valueOf(a));
                        }
                    } else if (Station_Util.Song_image.equalsIgnoreCase("")) {
                        Iv_track_image.setImageResource(R.drawable.mic_icon);
                    } else {
                        Iv_track_image.setImageResource(R.drawable.mic_icon);
                    }
                }

                startService(new Intent(CircleActivity.this, Notification_Serviceplay.class));
                Player.message = true;
            }
            sendBroadcast(new Intent(Player.RECIEVER_NOTI_PLAYPAUSE1));
        }
    }

    public class Data_Reciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("-----player reciever-----");
            if (intent.getAction().equals(RECIEVER_DATA)) {

                if (RadiophonyService.exoPlayer != null) {
                    if (Player.message) {
                        frame_footer.setVisibility(View.VISIBLE);
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
                                Picasso.with(CircleActivity.this).load(Station_Util.Song_image).fit()
                                        .transform(transformation).into(Iv_track_image);
                            } catch (IllegalArgumentException a) {
                                Log.e("Picasso path empty", String.valueOf(a));
                            }
                        } else {
                            Iv_track_image.setImageResource(R.drawable.mic_icon);
                        }
                        startService(new Intent(CircleActivity.this, Notification_Serviceplay.class));
                    } else if (!Player.message) {
                        frame_footer.setVisibility(View.GONE);
                        startService(new Intent(CircleActivity.this, Notification_Serviceplay.class));
                    }
                } else {
                    frame_footer.setVisibility(View.GONE);
                }
            }
        }
    }

}

