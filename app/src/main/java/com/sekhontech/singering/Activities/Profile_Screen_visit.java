package com.sekhontech.singering.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.Models.Search_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Singering_Database;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.circleMenu.CircleActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Screen_visit extends AppCompatActivity implements View.OnClickListener {
    public static boolean follow_check = false;
    private static String KEY_SUCCESS = "success";
    ImageButton btn_follow;
    ImageView Iv_cover, Iv_website, Iv_facebook, Iv_google, Iv_twitter, Iv_youtube, Iv_soundcloud, Iv_myspace, Iv_lastfm, Iv_vimeo, Iv_tumblr;
    TextView tv_count_tracks, txt_likes_count, txt_username, txt_username_profile, txt_city_country, tv_edit_settings, txt_about, tv_txt_person_detail;
    CircleImageView circleImageView;
    ImageButton Ib_backbtn;
    String str_user_id;
    ImageButton Ib_profile_image_edit;
    int position;
    Search_model_item model;
    RequestParams requestParams;
    String my_user_id, uid;
    String str_facebook, str_website, str_twitter, str_google, str_youtube, str_soundcloud, str_myspace, str_lastfm, str_vimeo, str_tumblr, str_description;
    Singering_Database db;

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

        setContentView(R.layout.activity_profile__screen_visit);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        init_declare();
        db = new Singering_Database(Profile_Screen_visit.this);                      ////////////DATABASE

        intent_data_recieve();

       /* if (db.tableexist("followtable") == true) {
            db.deleteFollow();
        }*/

    }

    private void intent_data_recieve() {
        if (getIntent().hasExtra("profile_detail")) {
            model = (Search_model_item) getIntent().getSerializableExtra("profile_detail");

            position = getIntent().getIntExtra("position", 0);
            str_user_id = model.getUser_id();
            Log.e("Profile_visit_ID", str_user_id);
        }
        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        if (str_user_id.equalsIgnoreCase(uid)) {
            btn_follow.setVisibility(View.GONE);
        }

        get_profile_data(str_user_id);
        get_Following_person_userprofile(str_user_id);


        String check1 = db.get_Follow(str_user_id);
        if (check1.equalsIgnoreCase("true")) {
            follow_check = true;
            btn_follow.setImageResource(R.drawable.remove_user);
        } else if (check1.equalsIgnoreCase("false")) {
            follow_check = false;
            btn_follow.setImageResource(R.drawable.add_user1);
        }
    }

    private void get_profile_data(String str_user_id) {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        // Log.e("str_user_id", str_user_id);

        final ProgressDialog pDialog = new ProgressDialog(Profile_Screen_visit.this);

        client.get(Station_Util.URL + "visitorpro.php?vid=" + str_user_id, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog.setMessage("Please wait...");
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Picasso.with(getApplicationContext()).load(Station_Util.IMAGE_URL_AVATARS + image).into(circleImageView);
                Picasso.with(getApplicationContext()).load(Station_Util.IMAGE_URL_COVERS + cover).into(Iv_cover);
                finalImage = image;

                txt_username_profile.setText(username);

                if (description.equalsIgnoreCase("")) {
                    txt_about.setVisibility(View.GONE);
                    tv_txt_person_detail.setVisibility(View.GONE);
                } else {
                    tv_txt_person_detail.setVisibility(View.VISIBLE);
                    txt_about.setVisibility(View.VISIBLE);
                    tv_txt_person_detail.setText(description);
                }
                tv_count_tracks.setText(totaltracks_count);
                txt_likes_count.setText(likes_count);
                txt_followers_count.setText(follower_count);
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
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(Profile_Screen_visit.this, "Error", Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(Profile_Screen_visit.this, "Error", Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }
        });

    }


    private void get_Following_person_userprofile(String str_user_id) {


        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        client.get(Station_Util.URL + "follows.php?followingid=" + str_user_id, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("following");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
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

    private void init_declare() {
        // btn_follower_list = (Button) findViewById(R.id.btn_follower_list);
        // btn_likes_list = (Button) findViewById(R.id.btn_likes_list);
        // btn_tracks_list = (Button) findViewById(R.id.btn_tracks_list);
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

        btn_follower_list = (LinearLayout) findViewById(R.id.btn_follower_list);
        btn_tracks_list = (LinearLayout) findViewById(R.id.btn_tracks_list);
        btn_following_list = (LinearLayout) findViewById(R.id.btn_following_list);
        btn_likes_list = (LinearLayout) findViewById(R.id.btn_likes_list);
        txt_track_count = (TextView) findViewById(R.id.txt_track_count);
        txt_following_count = (TextView) findViewById(R.id.txt_following_count);
        txt_count_likes = (TextView) findViewById(R.id.txt_count_likes);
        txt_followers_count = (TextView) findViewById(R.id.txt_followers_count);


        tv_txt_person_detail = (TextView) findViewById(R.id.tv_txt_person_detail);
        txt_about = (TextView) findViewById(R.id.txt_about);

        tv_edit_settings = (TextView) findViewById(R.id.tv_edit_settings);
        Iv_cover = (ImageView) findViewById(R.id.Iv_cover);
        txt_username_profile = (TextView) findViewById(R.id.txt_username_profile);
        txt_city_country = (TextView) findViewById(R.id.txt_city_country);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);

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
        tv_count_tracks = (TextView) findViewById(R.id.tv_count_tracks);
        btn_follow = (ImageButton) findViewById(R.id.btn_follow);
        Ib_profile_image_edit = (ImageButton) findViewById(R.id.Ib_profile_image_edit);

        btn_follow.setOnClickListener(this);
        tv_edit_settings.setVisibility(View.GONE);
        circleImageView.setOnClickListener(this);
        Ib_backbtn.setOnClickListener(this);
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
        btn_follower_list.setOnClickListener(this);
        btn_likes_list.setOnClickListener(this);
        btn_tracks_list.setOnClickListener(this);
        btn_following_list.setOnClickListener(this);
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
    }

    @Override
    public void onClick(View v) {
        if (v == Ib_backbtn) {
            onBackPressed();
        } else if (v == btn_follow) {
            if (follow_check == true) {
                set_Unfollow();
            } else if (follow_check == false) {
                set_follow();
            }
        } else if (v == btn_follower_list) {
            Intent i = new Intent(Profile_Screen_visit.this, Followers.class);
            i.putExtra("profile_user_id", str_user_id);
            startActivity(i);
        } else if (v == btn_following_list) {
            Intent i = new Intent(Profile_Screen_visit.this, Following.class);
            i.putExtra("profile_user_id", str_user_id);
            startActivity(i);
        } else if (v == btn_tracks_list) {
            Intent i = new Intent(Profile_Screen_visit.this, Profile_Tracks_added.class);
            i.putExtra("profile_user_id", str_user_id);
            startActivity(i);
        } else if (v == btn_likes_list) {
            Intent i = new Intent(Profile_Screen_visit.this, Profile_likes_added.class);
            i.putExtra("profile_user_id", str_user_id);
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

    private void set_follow() {

        my_user_id = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        requestParams.add("userid", my_user_id);
        requestParams.add("friendid", str_user_id);

        Log.e("check_values", "my_id=" + my_user_id + " , " + str_user_id);

        client.get(Station_Util.URL + "push.php?request=addfriend", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("Tag", statusCode + "");
                Log.d("Response", String.valueOf(response));

                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            btn_follow.setImageResource(R.drawable.remove_user);
                            follow_check = true;

                            db.insertFollow(str_user_id, "true");


                            // get_Following_person();

                            // db.updateLikes(id, "true");

                            //   int num = Integer.parseInt(str_likes) + 1;
                            //  str_likes = String.valueOf(num);
                            //setLikesNum();

                            // model.setCheck(true);

                            //getFavourites();
                        } else {
                            // btn_follow.setImageResource(R.drawable.add_user1);
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

    private void set_Unfollow() {

        my_user_id = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        requestParams.add("uid", my_user_id);
        requestParams.add("requestid", str_user_id);

        Log.e("check_values", "my_id=" + my_user_id + " , " + str_user_id);

        client.get(Station_Util.URL + "follows.php?unfollow=yes", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("Tag", statusCode + "");
                Log.d("Response", String.valueOf(response));

                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            btn_follow.setImageResource(R.drawable.add_user1);
                            follow_check = false;


                            db.insertFollow(str_user_id, "false");


                            //  get_Following_person();
                            // db.updateLikes(id, "true");

                            //   int num = Integer.parseInt(str_likes) + 1;
                            //  str_likes = String.valueOf(num);
                            //setLikesNum();

                            // model.setCheck(true);

                            //getFavourites();
                        } else {
                            // btn_follow.setImageResource(R.drawable.add_user1);
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
}
