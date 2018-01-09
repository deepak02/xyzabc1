package com.sekhontech.singering.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.sekhontech.singering.Models.Comment_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ST_004 on 22-12-2016.
 */

public class Comment_Adapter extends ArrayAdapter<Comment_model_item> {
    Context mcontext;
    public List<Comment_model_item> comment = new ArrayList<>();
    public String TAG_TOP = "TOP";
    private LayoutInflater mInflater;
    ViewHolder viewHolder;
    public static boolean favourite = false;
    String uid;
    RequestParams requestParams;
    private static String KEY_SUCCESS = "success";


    public static class ViewHolder {
        TextView txtv_message, txtv_time, txtv_name;
        ImageView Iv_image;
        FrameLayout frame_commment_delete;
    }

    public Comment_Adapter(Context context, ArrayList<Comment_model_item> comment) {
        super(context, R.layout.comments_show_item, comment);
        this.mcontext = context;
        this.comment = comment;
        //   this.mInflater = LayoutInflater.from(context);
        // uid = context.getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

    }

    @Override
    public Comment_model_item getItem(int position) {
        return comment.get(position);
    }

    public void setData(ArrayList<Comment_model_item> temp_list) {
        this.comment = temp_list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return comment.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Comment_model_item user1 = comment.get(position);

        if (convertView == null || convertView.getTag().equals(TAG_TOP)) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.comments_show_item, parent, false);
            viewHolder.txtv_name = (TextView) convertView.findViewById(R.id.txtv_name);
            viewHolder.txtv_message = (TextView) convertView.findViewById(R.id.txtv_message);
            viewHolder.txtv_time = (TextView) convertView.findViewById(R.id.txtv_time);
            viewHolder.Iv_image = (ImageView) convertView.findViewById(R.id.Iv_image);
            viewHolder.frame_commment_delete = (FrameLayout) convertView.findViewById(R.id.frame_commment_delete);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String image = user1.getImage();
        String time = user1.getTime();
        String username = user1.getUsername();
        String message = user1.getMessage();
        String user_id = user1.getUid();
        final String tid=user1.getTid();
        final String cid=user1.getCid();

        String uid = mcontext.getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
        if (user_id.equals(uid)) {
            viewHolder.frame_commment_delete.setVisibility(View.VISIBLE);
        }else {
            viewHolder.frame_commment_delete.setVisibility(View.GONE);
        }
        if (image != null) {
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .cornerRadiusDp(7)
                    .oval(false)
                    .build();
            Picasso.with(getContext()).load(Station_Util.IMAGE_URL_AVATARS+ image).fit().transform(transformation).placeholder(R.drawable.mic).into(viewHolder.Iv_image);
        }
        if (username != null) {
            viewHolder.txtv_name.setText(username);
        }
        if (message != null) {
            viewHolder.txtv_message.setText(message);
        }

        System.out.println("Date_Time_FROM_API : " + time);

        String current_date1 = Station_Util.Time_Zone();
        System.out.println("Date_Time_LIVE : " + current_date1);

        long api_date = getDateInMillis(time);
        long current_time1 = getDateInMillis(current_date1);

        viewHolder.txtv_time.setText("(" + DateUtils.getRelativeTimeSpanString(api_date, current_time1, DateUtils.SECOND_IN_MILLIS) + ")");

        viewHolder.frame_commment_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndShowAlertDialog(cid,tid,position);
            }
        });

        return convertView;
    }


    private void createAndShowAlertDialog(final String cid, final String tid, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle(R.string.app_name).setMessage("Delete this Comment ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                get_comment_delete(cid,tid,position);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        desiredFormat.setTimeZone(TimeZone.getDefault());
        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            //  Log.e("DATE IN MILLIS", String.valueOf(dateInMillis));
            return dateInMillis;
        } catch (ParseException e) {
            // Log.d("Exception date. " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }



    private void get_comment_delete(String cid, String tid, final int position) {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        uid = mcontext.getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("uid", "");

        requestParams = new RequestParams();
        requestParams.add("uid", uid);
        requestParams.add("cid", cid);
        requestParams.add("tid", tid);

        final ProgressDialog pDialog = new ProgressDialog(mcontext);

        client.post(mcontext, Station_Util.URL + "del_comment.php", requestParams, new JsonHttpResponseHandler() {
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
                            comment.remove(position);
                            notifyDataSetChanged();
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
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
     /*   long final_time = current_time1 - api_date;

        Log.e("time FINAL", String.valueOf(final_time));

        long seconds = TimeUnit.MILLISECONDS.toSeconds(final_time);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(final_time);
        long hours = TimeUnit.MILLISECONDS.toHours(final_time);
        long days = TimeUnit.MILLISECONDS.toDays(final_time);

        if (seconds < 60) {
            System.out.println(seconds + " seconds ago");
            viewHolder.txtv_time.setText("( " + seconds + " seconds ago )");
        } else if (minutes < 60) {
            System.out.println(minutes + " minutes ago");
            viewHolder.txtv_time.setText("( " + minutes + " minutes ago )");
        } else if (hours < 24) {
            System.out.println(hours + " hours ago");
            viewHolder.txtv_time.setText("( " + hours + " hours ago )");
        } else {
            System.out.println(days + " days ago");
            viewHolder.txtv_time.setText("( " + days + " days ago )");
        }*/
