package com.sekhontech.singering.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sekhontech.singering.Models.Search_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.circleMenu.CircleActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class My_Profile_Screen extends AppCompatActivity implements View.OnClickListener {
    public static String RECIEVER_DATA1 = "com.sekhontech.singering_app123";
    private static String KEY_SUCCESS = "success";
    boolean isImageFitToScreen;
    Button btn_follow;
    ImageView Iv_cover, Iv_website, Iv_facebook, Iv_google, Iv_twitter, Iv_youtube, Iv_soundcloud, Iv_myspace, Iv_lastfm, Iv_vimeo, Iv_tumblr;
    TextView txt_count_tracks, txt_likes_count, txt_username, txt_username_profile, txt_city_country, tv_edit_settings, tv_txt_person_detail;
    CircleImageView circleImageView;
    ImageButton Ib_backbtn;
    String str_profile_img, str_cover_img, str_username_profile, str_first_name, str_last_name, str_country, str_city;
    ImageButton Ib_profile_image_edit;
    Data_Reciever1 reciever;
    String str_facebook, str_website, str_twitter, str_google, str_youtube, str_soundcloud, str_myspace, str_lastfm, str_vimeo, str_tumblr, str_description;
    String uid;
    LinearLayout btn_tracks_list, btn_following_list, btn_likes_list, btn_follower_list;
    TextView txt_track_count, txt_following_count, txt_count_likes, txt_followers_count;
    View thumb1View;
    String finalImage;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_profile__screen);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        initDeclare();


        reciever = new Data_Reciever1();
        registerReceiver(reciever, new IntentFilter(RECIEVER_DATA1));


        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
        get_profile_data(uid);
        get_Following_person();
        get_Followers_person();

      /*  db = new Singering_Database(My_Profile_Screen.this);
        if (db.tableexist("followtable") == true) {
            db.deleteFollow();
        }*/


    }

    private void get_Following_person() {

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        client.get(Station_Util.URL + "follows.php?followingid=" + uid, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Search_model_item model;
                try {
                    My_following_activity.following_adapter_data.clear();

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

                                My_following_activity.following_adapter_data.add(model);

                                Log.e("count123", String.valueOf(array.length()));
                                txt_following_count.setText(String.valueOf(array.length()));

                            }
                        } else {
                            txt_following_count.setText("0");
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
        });
    }

    private void get_Followers_person() {
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "follows.php?followers=true&followerid=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String user_id, username, first_name, last_name, country, city, image;
                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("follower");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                txt_followers_count.setText(String.valueOf(array.length()));
                            }
                        } else {
                            txt_followers_count.setText("0");
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
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public boolean checkFavItem(Search_model_item checkfav) {
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
    }

    private void get_profile_data(String uid) {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        Log.e("str_user_id", uid);

        final ProgressDialog pDialog = new ProgressDialog(My_Profile_Screen.this);

        client.get(Station_Util.URL + "visitorpro.php?vid=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog.setMessage(getResources().getString(R.string.please_wait));
                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                String username = "", first_name = "", last_name = "", country = "", website = "", city = "", description = "", facebook = "",
                        twitter = "", gplus = "", youtube = "", vimeo = "", tumblr = "", soundcloud = "", myspace = "", lastfm = "", image = "", cover = "", totaltracks_count = "", follower_count = "", likes_count = "";
                Log.e("prnt", response.toString());
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    String obq = obj.getString("search");
                    JSONArray array = new JSONArray(obq);
                    JSONObject obj1 = array.getJSONObject(0);
                    username = obj1.getString("username");
                    first_name = obj1.getString("first_name");
                    last_name = obj1.getString("last_name");
                    country = obj1.getString("country");
                    website = obj1.getString("website");
                    city = obj1.getString("city");
                    description = obj1.getString("description");
                    facebook = obj1.getString("facebook");
                    twitter = obj1.getString("twitter");
                    gplus = obj1.getString("gplus");
                    youtube = obj1.getString("youtube");
                    vimeo = obj1.getString("vimeo");
                    tumblr = obj1.getString("tumblr");
                    soundcloud = obj1.getString("soundcloud");
                    myspace = obj1.getString("myspace");
                    lastfm = obj1.getString("lastfm");
                    image = obj1.getString("image");
                    cover = obj1.getString("cover");
                    totaltracks_count = obj1.getString("totaltracks");
                    follower_count = obj1.getString("follower");
                    likes_count = obj1.getString("likes");
                } catch (JSONException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
                finalImage = image;
                if (image != null) {
                    Picasso.with(getApplicationContext()).load(Station_Util.IMAGE_URL_AVATARS + image).into(circleImageView);
                }
                if (cover != null) {
                    Picasso.with(getApplicationContext()).load(Station_Util.IMAGE_URL_COVERS + cover).into(Iv_cover);
                }

                if (username != null) {
                    txt_username_profile.setText(username);
                }

                if (description.equalsIgnoreCase("")) {
                    tv_txt_person_detail.setVisibility(View.GONE);
                } else {
                    tv_txt_person_detail.setVisibility(View.VISIBLE);
                    tv_txt_person_detail.setText(description);
                }
                txt_count_tracks.setText(totaltracks_count);
                txt_likes_count.setText(likes_count);
                // btn_follower_list.setText(follower_count + " Followers");
                txt_count_likes.setText(likes_count);
                txt_track_count.setText(totaltracks_count);

                if (city.equalsIgnoreCase("")) {
                    txt_city_country.setText(country);
                    txt_city_country.setVisibility(View.VISIBLE);
                } else if (country.equalsIgnoreCase("")) {
                    txt_city_country.setText(city);
                    txt_city_country.setVisibility(View.VISIBLE);
                } else if (city.equalsIgnoreCase("") && country.equalsIgnoreCase("")) {
                    txt_city_country.setVisibility(View.GONE);
                } else {
                    txt_city_country.setVisibility(View.VISIBLE);
                    txt_city_country.setText(city + "," + country);
                }


                txt_username.setText(first_name + " " + last_name);


                if (facebook.equalsIgnoreCase("")) {
                    Iv_facebook.setVisibility(View.GONE);
                } else {
                    Iv_facebook.setVisibility(View.VISIBLE);
                    str_facebook = facebook;
                }

                if (gplus.equalsIgnoreCase("")) {
                    Iv_google.setVisibility(View.GONE);
                } else {
                    Iv_google.setVisibility(View.VISIBLE);
                    str_google = gplus;
                }

                if (twitter.equalsIgnoreCase("")) {
                    Iv_twitter.setVisibility(View.GONE);
                } else {
                    Iv_twitter.setVisibility(View.VISIBLE);
                    str_twitter = twitter;
                }

                if (youtube.equalsIgnoreCase("")) {
                    Iv_youtube.setVisibility(View.GONE);
                } else {
                    Iv_youtube.setVisibility(View.VISIBLE);
                    str_youtube = youtube;
                }

                if (soundcloud.equalsIgnoreCase("")) {
                    Iv_soundcloud.setVisibility(View.GONE);
                } else {
                    Iv_soundcloud.setVisibility(View.VISIBLE);
                    str_soundcloud = soundcloud;
                }

                if (myspace.equalsIgnoreCase("")) {
                    Iv_myspace.setVisibility(View.GONE);
                } else {
                    Iv_myspace.setVisibility(View.VISIBLE);
                    str_myspace = myspace;
                }

                if (lastfm.equalsIgnoreCase("")) {
                    Iv_lastfm.setVisibility(View.GONE);
                } else {
                    Iv_lastfm.setVisibility(View.VISIBLE);
                    str_lastfm = lastfm;
                }

                if (vimeo.equalsIgnoreCase("")) {
                    Iv_vimeo.setVisibility(View.GONE);
                } else {
                    Iv_vimeo.setVisibility(View.VISIBLE);
                    str_vimeo = vimeo;
                }

                if (tumblr.equalsIgnoreCase("")) {
                    Iv_tumblr.setVisibility(View.GONE);
                } else {
                    Iv_tumblr.setVisibility(View.VISIBLE);
                    str_tumblr = tumblr;
                }

                if (website.equalsIgnoreCase("")) {
                    Iv_website.setVisibility(View.GONE);
                } else {
                    Iv_website.setVisibility(View.VISIBLE);
                    str_website = website;
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
                Toast.makeText(My_Profile_Screen.this, "Error", Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(My_Profile_Screen.this, "Error", Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(My_Profile_Screen.this, "Error", Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }

    private void initDeclare() {
        Iv_website = (ImageView) findViewById(R.id.Iv_website);
        Iv_facebook = (ImageView) findViewById(R.id.Iv_facebook);
        Iv_google = (ImageView) findViewById(R.id.Iv_google);
        Iv_twitter = (ImageView) findViewById(R.id.Iv_twitter);
        Iv_youtube = (ImageView) findViewById(R.id.Iv_youtube);
        Iv_soundcloud = (ImageView) findViewById(R.id.Iv_soundcloud);
        Iv_myspace = (ImageView) findViewById(R.id.Iv_myspace);
        Iv_lastfm = (ImageView) findViewById(R.id.Iv_lastfm);
        Iv_vimeo = (ImageView) findViewById(R.id.Iv_vimeo);
        Iv_tumblr = (ImageView) findViewById(R.id.Iv_tumblr);
        btn_likes_list = (LinearLayout) findViewById(R.id.btn_likes_list);
        btn_follower_list = (LinearLayout) findViewById(R.id.btn_follower_list);
        btn_tracks_list = (LinearLayout) findViewById(R.id.btn_tracks_list);
        btn_following_list = (LinearLayout) findViewById(R.id.btn_following_list);
        txt_track_count = (TextView) findViewById(R.id.txt_track_count);
        txt_following_count = (TextView) findViewById(R.id.txt_following_count);
        txt_count_likes = (TextView) findViewById(R.id.txt_count_likes);
        tv_txt_person_detail = (TextView) findViewById(R.id.tv_txt_person_detail);
        txt_followers_count = (TextView) findViewById(R.id.txt_followers_count);

        tv_edit_settings = (TextView) findViewById(R.id.tv_edit_settings);
        Iv_cover = (ImageView) findViewById(R.id.Iv_cover);
        txt_username_profile = (TextView) findViewById(R.id.txt_username_profile);
        txt_city_country = (TextView) findViewById(R.id.txt_city_country);


        circleImageView = (CircleImageView) findViewById(R.id.profile_imagemy);
        thumb1View = findViewById(R.id.thumb_button_1);
        thumb1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!finalImage.isEmpty()) {
                    zoomImageFromThumb(thumb1View, Station_Util.IMAGE_URL_AVATARS + finalImage);
                }
            }
        });
        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);


        txt_likes_count = (TextView) findViewById(R.id.txt_likes_count);
        txt_username = (TextView) findViewById(R.id.txt_username);
        Ib_backbtn = (ImageButton) findViewById(R.id.Ib_backbtn);
        txt_count_tracks = (TextView) findViewById(R.id.txt_count_tracks);
        btn_follow = (Button) findViewById(R.id.btn_follow);
        Ib_profile_image_edit = (ImageButton) findViewById(R.id.Ib_profile_image_edit);
        if (btn_follow != null) {
            btn_follow.setOnClickListener(this);
        }
        circleImageView.setOnClickListener(this);
        Ib_backbtn.setOnClickListener(this);
        tv_edit_settings.setOnClickListener(this);
        Iv_facebook.setOnClickListener(this);
        Iv_google.setOnClickListener(this);
        Iv_twitter.setOnClickListener(this);
        Iv_youtube.setOnClickListener(this);
        Iv_soundcloud.setOnClickListener(this);
        Iv_myspace.setOnClickListener(this);
        Iv_lastfm.setOnClickListener(this);
        Iv_vimeo.setOnClickListener(this);
        Iv_tumblr.setOnClickListener(this);
        Iv_website.setOnClickListener(this);
        btn_likes_list.setOnClickListener(this);
        btn_tracks_list.setOnClickListener(this);
        btn_following_list.setOnClickListener(this);
        btn_follower_list.setOnClickListener(this);
    }

    private void zoomImageFromThumb(final View thumbView, String imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        Picasso.with(getApplicationContext()).load(imageResId).into(expandedImageView);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == Ib_backbtn) {
            onBackPressed();
        } else if (v == btn_tracks_list) {
            // uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
            Intent i = new Intent(My_Profile_Screen.this, My_Tracks.class);
            startActivity(i);
        } else if (v == btn_likes_list) {
            uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
            Intent i = new Intent(My_Profile_Screen.this, Profile_likes_added.class);
            i.putExtra("profile_user_id", uid);
            startActivity(i);
        } else if (v == btn_following_list) {
            Intent i = new Intent(My_Profile_Screen.this, My_following_activity.class);
            startActivity(i);
        } else if (v == btn_follower_list) {
            Intent i = new Intent(My_Profile_Screen.this, Followers.class);
            i.putExtra("profile_user_id", uid);
            startActivity(i);
        } else if (v == tv_edit_settings) {
            Intent i = new Intent(My_Profile_Screen.this, Settings.class);
            startActivity(i);
        } else if (v == Iv_facebook) {
            Uri uri = Uri.parse("https://www.facebook.com/" + str_facebook); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_google) {
            Uri uri = Uri.parse("https://plus.google.com/" + str_google); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_twitter) {
            Uri uri = Uri.parse("https://twitter.com/" + str_twitter); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_youtube) {
            Uri uri = Uri.parse("https://www.youtube.com/user/" + str_youtube); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_soundcloud) {
            Uri uri = Uri.parse("https://soundcloud.com/" + str_soundcloud); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_myspace) {
            Uri uri = Uri.parse("https://myspace.com/" + str_myspace); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_lastfm) {
            Uri uri = Uri.parse("http://www.last.fm/user/" + str_lastfm); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_vimeo) {
            Uri uri = Uri.parse("https://vimeo.com/" + str_vimeo); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_tumblr) {
            Uri uri = Uri.parse("http://" + str_tumblr + ".tumblr.com/"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (v == Iv_website) {
            if (str_website.contains("http://")) {
                Uri uri = Uri.parse(str_website); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else {
                str_website = "http://" + str_website;
                Uri uri = Uri.parse(str_website); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));
                Log.e("check_times_hit", "check");
            }
        }, 1500); //time in millis
        super.onBackPressed();
/*
        sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));
*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reciever);
    }

    public class Data_Reciever1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("-----player reciever-----");

            get_Following_person();
        }
    }
}
