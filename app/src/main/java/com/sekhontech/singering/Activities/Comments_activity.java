package com.sekhontech.singering.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.Adapters.Comment_Adapter;
import com.sekhontech.singering.Models.Comment_model_item;
import com.sekhontech.singering.Models.Explore_model_item;
import com.sekhontech.singering.Models.Notification_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.Utilities.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class Comments_activity extends AppCompatActivity implements View.OnClickListener {
    public static ArrayList<Explore_model_item> single_track_list1 = new ArrayList<>();
    public static ArrayList<Comment_model_item> list_comment = new ArrayList<>();
    private static String KEY_SUCCESS = "success";
    public RelativeLayout progressBarHolder;
    Toolbar toolbar;
    LinearLayout linear_listener_count, linear_comments_count, linear_likes_count, linear_count_downloads;
    TextView txt_username, txt_song_title, txt_song_tag, txt_uploaded_time;
    TextView txt_count_downloads, txt_count_likes, txt_count_comments, txt_count_listener, txt_no_comments;
    ListView list_view_comments;
    EditText edttxt_text_comment;
    Button btn_send_comment;
    ProgressBar progressBarInitial;
    String uid, trackid, userid;
    CircleImageView track_image;
    RequestParams requestParams;
    Comment_Adapter adapter_comment;
    String str_comment_txt;

    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            Log.e("DATE IN MILLIS", String.valueOf(dateInMillis));
            return dateInMillis;
        } catch (ParseException e) {
            // Log.d("Exception date. " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        init_declare();

        if (getIntent().hasExtra("trackid")) {
            trackid = getIntent().getStringExtra("trackid");
            userid = getIntent().getStringExtra("userid");
            geta_all_data(trackid, userid);
            show_all_comments(trackid);
        }
        if (getIntent().hasExtra("item_detail")) {
            Notification_model_item model = (Notification_model_item) getIntent().getSerializableExtra("item_detail");
            int position = getIntent().getIntExtra("position", 0);
            trackid = model.getParent();
            userid = model.getTo();
            geta_all_data(trackid, userid);
            show_all_comments(trackid);
        }
    }

    private void show_all_comments(String trackids) {

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        final ProgressDialog pDialog1 = new ProgressDialog(Comments_activity.this);

        client.get(Station_Util.URL + "track_comments.php?tid=" + trackids, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog1.setMessage("Please wait...");
                pDialog1.setIndeterminate(false);
                pDialog1.setCancelable(true);
                pDialog1.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("ALL COMMENTS", String.valueOf(response));
                Comment_model_item model;
                try {
                    list_comment.clear();
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            JSONArray array = response.getJSONArray("comments");
                            for (int i = 0; i < array.length(); i++) {
                                model = new Comment_model_item();
                                JSONObject obj = array.getJSONObject(i);
                                model.total_comments = obj.getString("total_comments");
                                model.cid = obj.getString("cid");
                                model.uid = obj.getString("uid");
                                model.tid = obj.getString("tid");
                                model.message = obj.getString("message");
                                model.time = obj.getString("time");
                                model.username = obj.getString("username");
                                model.image = obj.getString("image");

                                list_comment.add(model);

                                if (pDialog1 != null && pDialog1.isShowing()) {
                                    pDialog1.dismiss();
                                }
                                txt_count_comments.setText(model.total_comments);
                            }
                        } else {
                            if (pDialog1 != null && pDialog1.isShowing()) {
                                pDialog1.dismiss();
                            }
                            txt_count_comments.setText("0");
                            Log.d("NoComments", "Nocomments");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (list_comment.isEmpty()) {
                    progressBarHolder.setVisibility(View.GONE);
                    txt_no_comments.setVisibility(View.VISIBLE);
                    //frame_no_noti.setVisibility(View.VISIBLE);
                    list_view_comments.setVisibility(View.GONE);
                } else {
                    progressBarHolder.setVisibility(View.GONE);
                    txt_no_comments.setVisibility(View.GONE);
                    list_view_comments.setVisibility(View.VISIBLE);
                    adapter_comment = new Comment_Adapter(Comments_activity.this, list_comment);
                    adapter_comment.setData(list_comment);
                    list_view_comments.setAdapter(adapter_comment);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void init_declare() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.Comments);
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        linear_count_downloads = (LinearLayout) findViewById(R.id.linear_count_downloads);
        linear_likes_count = (LinearLayout) findViewById(R.id.linear_likes_count);
        linear_comments_count = (LinearLayout) findViewById(R.id.linear_comments_count);
        linear_listener_count = (LinearLayout) findViewById(R.id.linear_listener_count);
        txt_username = (TextView) findViewById(R.id.txt_username);
        txt_song_title = (TextView) findViewById(R.id.txt_song_title);
        txt_song_tag = (TextView) findViewById(R.id.txt_song_tag);
        txt_uploaded_time = (TextView) findViewById(R.id.txt_uploaded_time);
        list_view_comments = (ListView) findViewById(R.id.list_view_comments);
        edttxt_text_comment = (EditText) findViewById(R.id.edttxt_text_comment);
        btn_send_comment = (Button) findViewById(R.id.btn_send_comment);
        progressBarHolder = (RelativeLayout) findViewById(R.id.progressBarHolder);
        progressBarInitial = (ProgressBar) findViewById(R.id.progressBarInitial);
        txt_count_downloads = (TextView) findViewById(R.id.txt_count_downloads);
        txt_count_likes = (TextView) findViewById(R.id.txt_count_likes);
        txt_count_comments = (TextView) findViewById(R.id.txt_count_comments);
        txt_count_listener = (TextView) findViewById(R.id.txt_count_listener);
        track_image = (CircleImageView) findViewById(R.id.track_image);
        txt_no_comments = (TextView) findViewById(R.id.txt_no_comments);
        btn_send_comment.setOnClickListener(this);
        txt_song_tag.setSelected(true);
        list_view_comments.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void geta_all_data(String trackid1, String userid1) {

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        //  final ProgressDialog pDialog = new ProgressDialog(Comments_activity.this);

        client.get(Station_Util.URL + "single_track.php?trackid=" + trackid1 + "&uid=" + userid1, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                //     pDialog.setMessage("Please wait...");
                //    pDialog.setCancelable(true);
                //   pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("SINGLE TRACK INFO", String.valueOf(response));
                Explore_model_item model;
                try {
                    single_track_list1.clear();

                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            JSONObject ob = new JSONObject(response.toString());
                            JSONArray array = ob.getJSONArray("tracks");
                            model = new Explore_model_item();

                            JSONObject obj = array.getJSONObject(0);
                            model.id = obj.getString("id");
                            model.uid = obj.getString("uid");
                            model.title = obj.getString("title");
                            model.description = obj.getString("description");
                            String username = obj.getString("username");
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
                                single_track_list1.add(model);
                            }
                            // pDialog.dismiss();
                            String date = model.time;
                            long api_date = getDateInMillis(date);


                            String current_date1 = Station_Util.Time_Zone();
                            long current_time1 = getDateInMillis(current_date1);
                            Log.e("time CURRENT", String.valueOf(current_time1));

                            txt_uploaded_time.setText(DateUtils.getRelativeTimeSpanString(api_date, current_time1, DateUtils.SECOND_IN_MILLIS));


                            txt_song_title.setText(model.title);
                            txt_song_tag.setText(model.tag);

                            txt_count_likes.setText(model.likes);
                            txt_count_downloads.setText(model.downloads);
                            txt_count_listener.setText(model.views);
                            if (model.first_name.equalsIgnoreCase("") || model.last_name.equalsIgnoreCase("")) {
                                txt_username.setText(username);
                            } else {
                                txt_username.setText(model.first_name + " " + model.last_name);
                            }

                            Picasso.with(Comments_activity.this).load(Station_Util.IMAGE_URL_MEDIA + model.art).fit().into(track_image);

                        } else {
                            //   pDialog.dismiss();
                            onBackPressed();
                            Toast.makeText(Comments_activity.this, "No info Available", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        if (v == btn_send_comment) {
            if (edttxt_text_comment.getText().toString().trim().length() == 0) {
                Utility.showAlert(Comments_activity.this, "Alert", "Add Comment", null, "Ok");
            } else {
                str_comment_txt = edttxt_text_comment.getText().toString();
                get_comment_api(str_comment_txt);
            }
        }
    }

    private void get_comment_api(String comment) {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        uid = getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("uid", "");

        requestParams = new RequestParams();
        requestParams.add("uid", uid);   ///id of person who uploaded track
        requestParams.add("tid", trackid);
        requestParams.add("message", comment);

        final ProgressDialog pDialog = new ProgressDialog(Comments_activity.this);

        client.post(Comments_activity.this, Station_Util.URL + "push.php?request=getrequest&type=comments", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog.setMessage("wait...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Tag", statusCode + "");
                Log.d("Response", String.valueOf(response));

                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                            edttxt_text_comment.setText("");
                            show_all_comments(trackid);
                            // Toast.makeText(Comments_activity.this,"Successfully updated...",Toast.LENGTH_SHORT).show();
                        } else {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                            Toast.makeText(Comments_activity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
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
}
