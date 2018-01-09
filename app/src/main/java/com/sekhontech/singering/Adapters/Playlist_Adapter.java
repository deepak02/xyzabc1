package com.sekhontech.singering.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sekhontech.singering.Models.Playlist_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ST_004 on 25-10-2016.
 */

public class Playlist_Adapter extends ArrayAdapter<Playlist_model_item> {
   // Context mcontext;
    private List<Playlist_model_item> item = new ArrayList<>();
    private String TAG_TOP = "TOP";
    private LayoutInflater mInflater;
    ViewHolder viewHolder;
    public static boolean favourite = false;
    int count;
    PopupWindow pwindo;
    Activity mActivityContext = null;

    public static class ViewHolder {
        TextView txtview_playlistname, tv_track_count, txt_username, txt_city_country;
        ImageView iv_playlist_img,IV_delete_playlist;
    }

    public Playlist_Adapter(Activity context, ArrayList<Playlist_model_item> item, int count) {
        super(context, R.layout.playlist_item_show, item);
     //   this.mcontext = context;
        this.mActivityContext=context;
        this.count = count;
        this.item = item;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public Playlist_model_item getItem(int position) {
        return item.get(position);
    }

    public void setData(ArrayList<Playlist_model_item> temp_list) {
        this.item = temp_list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Playlist_model_item user1 = item.get(position);
        if (count == 0) {
            if (convertView == null || convertView.getTag().equals(TAG_TOP)) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.playlist_item_show, parent, false);
                viewHolder.txtview_playlistname = (TextView) convertView.findViewById(R.id.txtview_playlistname);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String name = user1.getName();
            if (name != null) {
                viewHolder.txtview_playlistname.setText(name);
            } else {
            }

        } else if (count == 1) {
            if (convertView == null || convertView.getTag().equals(TAG_TOP)) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.playlist_item_show_two, parent, false);

                viewHolder.txtview_playlistname = (TextView) convertView.findViewById(R.id.txtview_playlistname);
                viewHolder.tv_track_count = (TextView) convertView.findViewById(R.id.tv_track_count);
                viewHolder.iv_playlist_img = (ImageView) convertView.findViewById(R.id.iv_playlist_img);
                viewHolder.IV_delete_playlist=(ImageView)convertView.findViewById(R.id.IV_delete_playlist);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String name = user1.getName();
            String track_count = user1.getTotal_tracks();
            String image = user1.getImage();

            if (track_count != null) {
                viewHolder.tv_track_count.setText(track_count + " Tracks");
            } else {
            }
            if (name != null) {
                viewHolder.txtview_playlistname.setText(name);
            } else {
            }

            if (image != null) {
                Picasso.with(mActivityContext).load(Station_Util.IMAGE_URL_MEDIA + image).fit().into(viewHolder.iv_playlist_img);
            } else {

            }

            viewHolder.IV_delete_playlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String playlistID=user1.getId();
                   // initiatePopupWindow1();
                    createAndShowAlertDialog(playlistID,position);
                   // playlist_Delete(playlistID);
                }
            });

        }

        return convertView;
    }

    private void createAndShowAlertDialog(final String idu, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivityContext);
        builder.setTitle(R.string.app_name).setMessage("Do you want to delete the playlist ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                playlist_Delete(idu,position);
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

    private void playlist_Delete(String playlistID, final int pos) {


        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        client.get(Station_Util.URL+"playlist.php?playlistRemove=true&playlistid=" + playlistID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Tag1", statusCode + "");
                Log.d("Resp_playlist_Delete", String.valueOf(response));

                item.remove(pos);
                notifyDataSetChanged();

               // notifyDataSetChanged();
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
