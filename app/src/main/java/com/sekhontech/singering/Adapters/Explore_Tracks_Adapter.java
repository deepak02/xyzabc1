package com.sekhontech.singering.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.sekhontech.singering.Activities.Track_Edit_info;
import com.sekhontech.singering.Models.Explore_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ST_004 on 19-08-2016.
 */
public class Explore_Tracks_Adapter extends ArrayAdapter<Explore_model_item> {
    Context mcontext;
    private List<Explore_model_item> tracks = new ArrayList<>();
    private LayoutInflater inflater;
    ViewHolder viewHolder;
    private int count;
    private String uid;
    private List<Explore_model_item> temp = new ArrayList<>();
    public static ArrayList<Explore_model_item> single_track_list = new ArrayList<>();
    private static String KEY_SUCCESS = "success";
    private boolean FROM_DOWNLOAD = false;
    private Explore_model_item user1;

    private static class ViewHolder {
        TextView txt_track_name, text_name;
        ImageView iv_track_image, Iv_track_delete, Iv_delete_item, Iv_track_edit;
        LinearLayout linearlayout_artist;
    }

    public Explore_Tracks_Adapter(Context context, ArrayList<Explore_model_item> track123, int count, boolean FROM_DOWNLOAD) {
        super(context, R.layout.explore_view_item, track123);
        this.mcontext = context;
        this.count = count;
        this.tracks = track123;
        temp.addAll(tracks);
        this.FROM_DOWNLOAD = FROM_DOWNLOAD;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public Explore_model_item getItem(int position) {
        return tracks.get(position);
    }

    public void setData(ArrayList<Explore_model_item> temp_list) {
        this.tracks = temp_list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        user1 = tracks.get(position);
        try {
            String TAG_TOP = "TOP";
            if (count == 0) {
                if (convertView == null || convertView.getTag().equals(TAG_TOP)) {
                    viewHolder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.explore_view_item, parent, false);
                    viewHolder.txt_track_name = (TextView) convertView.findViewById(R.id.txt_track_name);
                    viewHolder.iv_track_image = (ImageView) convertView.findViewById(R.id.iv_track_image);
                    viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                String title = user1.getTitle();
                String img = user1.getArt();
                String name = user1.getFirst_name() + " " + user1.getLast_name();
                String image = user1.getImageART();

                if (title.equalsIgnoreCase("")) {
                    viewHolder.linearlayout_artist.setVisibility(View.GONE);
                } else {
                    viewHolder.txt_track_name.setText(title);
                    viewHolder.text_name.setText(name);

                    Transformation transformation = new RoundedTransformationBuilder()
                            .borderColor(Color.BLACK)
                            .cornerRadiusDp(7)
                            .oval(false)
                            .build();
                    Picasso.with(getContext()).load(Station_Util.IMAGE_URL_MEDIA + img).fit().transform(transformation).placeholder(R.drawable.mic).into(viewHolder.iv_track_image);
                }
            } else if (count == 1) {
                if (convertView == null || convertView.getTag().equals(TAG_TOP)) {
                    viewHolder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.explore_view_item_two, parent, false);
                    viewHolder.txt_track_name = (TextView) convertView.findViewById(R.id.txt_track_name);
                    viewHolder.iv_track_image = (ImageView) convertView.findViewById(R.id.iv_track_image);
                    viewHolder.Iv_delete_item = (ImageView) convertView.findViewById(R.id.Iv_delete_item);
                    viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                String title = user1.getTitle();
                String img = user1.getArt();
                String name = user1.getFirst_name() + " " + user1.getLast_name();

                if (title.equalsIgnoreCase("")) {
                    viewHolder.linearlayout_artist.setVisibility(View.GONE);
                } else {
                    viewHolder.txt_track_name.setText(title);
                    viewHolder.text_name.setText(name);

                    if (FROM_DOWNLOAD == true) {

                        // Bitmap bitmap=  StringToBitMap(image);
                        //  Drawable myDrawable = mcontext.getDrawable(R.drawable.mic_icon);
                        viewHolder.iv_track_image.setImageResource(R.drawable.mic_icon);

                        viewHolder.Iv_delete_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialogForDelete(position);
                            }
                        });
                    } else {
                        Transformation transformation = new RoundedTransformationBuilder()
                                .borderColor(Color.BLACK)
                                .cornerRadiusDp(7)
                                .oval(false)
                                .build();
                        Picasso.with(getContext()).load(Station_Util.IMAGE_URL_MEDIA + img).fit().transform(transformation).placeholder(R.drawable.mic).into(viewHolder.iv_track_image);

                        viewHolder.Iv_delete_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //  Playlist_model_item item=new Playlist_model_item();
                                String str_track_id = user1.getId();
                                String str_playlist_id = Station_Util.playlist_ID;

                                createAndShowAlertDialog(str_track_id, str_playlist_id, position);
                            }
                        });
                    }
                }

            /*String check = db.get_like(user1.getId());
            if (check.equalsIgnoreCase("true")) {
                viewHolder.Iv_fav_item.setImageResource(R.drawable.ic_favorite_fill);
            } else if (check.equalsIgnoreCase("false")) {
                viewHolder.Iv_fav_item.setImageResource(R.drawable.ic_favorite_empty);
            }*/
            } else if (count == 2) {
                if (convertView == null || convertView.getTag().equals(TAG_TOP)) {
                    viewHolder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.explore_view_item_three, parent, false);
                    viewHolder.txt_track_name = (TextView) convertView.findViewById(R.id.txt_track_name);
                    viewHolder.iv_track_image = (ImageView) convertView.findViewById(R.id.iv_track_image);
                    viewHolder.Iv_track_edit = (ImageView) convertView.findViewById(R.id.Iv_track_edit);
                    viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
                    viewHolder.Iv_track_delete = (ImageView) convertView.findViewById(R.id.Iv_track_delete);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                String title = user1.getTitle();
                String img = user1.getArt();
                String name = user1.getFirst_name() + " " + user1.getLast_name();
                final String track_id = user1.getId();

                if (title.equalsIgnoreCase("")) {
                    viewHolder.linearlayout_artist.setVisibility(View.GONE);
                } else {
                    viewHolder.txt_track_name.setText(title);
                    viewHolder.text_name.setText(name);

                    Transformation transformation = new RoundedTransformationBuilder()
                            .borderColor(Color.BLACK)
                            .cornerRadiusDp(7)
                            .oval(false)
                            .build();
                    Picasso.with(getContext()).load(Station_Util.IMAGE_URL_MEDIA + img).fit().transform(transformation).placeholder(R.drawable.mic).into(viewHolder.iv_track_image);
                }


                viewHolder.Iv_track_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  Playlist_model_item item=new Playlist_model_item();
                        String str_track_id = user1.getId();
                        String str_playlist_id = Station_Util.playlist_ID;
                        uid = mcontext.getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
                        set_track_data(str_track_id, uid);
                    }
                });

                viewHolder.Iv_track_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uid = mcontext.getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
                        createAndShowAlertDialog_delete(track_id, uid, position);
                    }
                });
            } else if (count == 3) {
                if (convertView == null || convertView.getTag().equals(TAG_TOP)) {
                    viewHolder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.explore_view_item, parent, false);
                    viewHolder.txt_track_name = (TextView) convertView.findViewById(R.id.txt_track_name);
                    viewHolder.iv_track_image = (ImageView) convertView.findViewById(R.id.iv_track_image);
                    // viewHolder.Iv_fav_item = (ImageView) convertView.findViewById(R.id.Iv_fav_item);
                    viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                String title = user1.getTitle();
                String img = user1.getArt();
                String name = user1.getFirst_name() + " " + user1.getLast_name();

                if (title.equalsIgnoreCase("")) {
                    viewHolder.linearlayout_artist.setVisibility(View.GONE);
                } else {
                    viewHolder.txt_track_name.setText(title);
                    viewHolder.text_name.setText(name);

                    Transformation transformation = new RoundedTransformationBuilder()
                            .borderColor(Color.BLACK)
                            .cornerRadiusDp(7)
                            .oval(false)
                            .build();
                    Picasso.with(getContext()).load(Station_Util.IMAGE_URL_MEDIA + img).fit().transform(transformation).placeholder(R.drawable.mic).into(viewHolder.iv_track_image);
                }

         /*   String check = db.get_like(user1.getId());
            if (check.equalsIgnoreCase("true")) {
                viewHolder.Iv_fav_item.setImageResource(R.drawable.ic_favorite_fill);
            } else if (check.equalsIgnoreCase("false")) {
                viewHolder.Iv_fav_item.setImageResource(R.drawable.ic_favorite_empty);
            }*/
            }
        } catch (NullPointerException e) {
            Log.e("Null_CHeck", "" + e);
        }


        return convertView;
    }

    private void set_track_data(final String track_id, String uid) {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        final ProgressDialog pDialog = new ProgressDialog(mcontext);

        client.get(Station_Util.URL + "single_track.php?trackid=" + track_id + "&uid=" + uid, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("SINGLE TRACK INFO", String.valueOf(response));
                Explore_model_item model;
                try {
                    single_track_list.clear();

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
                                single_track_list.add(model);
                            }

                            SharedPreferences pref = mcontext.getSharedPreferences("logincheck", MODE_PRIVATE);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("download", model.download);
                            edit.putString("description", model.description);
                            edit.putString("title", model.title);
                            edit.putString("tag", model.tag);
                            edit.putString("public", model.Public);
                            edit.putString("art", model.art);
                            edit.putString("trackid", track_id);
                            edit.apply();

                            pDialog.dismiss();

                            Intent i = new Intent(mcontext, Track_Edit_info.class);
                            mcontext.startActivity(i);
                            ((Activity) mcontext).finish();
                            //  }
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(mcontext, "Try Again", Toast.LENGTH_SHORT).show();
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

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    private void showDialogForDelete(final int pos) {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(mcontext);
        // Setting Dialog Title
        alertDialog2.setTitle("Confirm Delete...");
        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure, do you want delete this file?");
        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        File mFile = new File(tracks.get(pos).getFiledownloaded_path());
                        if (mFile.isFile()) {
                            mFile.delete();
                        }
                        tracks.remove(pos);
                        notifyDataSetChanged();
                    }
                });
        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog

                        dialog.cancel();
                    }
                });

        // Showing Alert Dialog
        alertDialog2.show();
    }

    private void createAndShowAlertDialog_delete(final String track_id, final String uid, final int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle(R.string.app_name).setMessage("Do you want to delete this track ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                track_Delete(track_id, uid, position);
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


    private void createAndShowAlertDialog(final String str_track_id, final String str_playlist_id, final int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle(R.string.app_name).setMessage("Do you want to delete this track ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                playlist_track_Delete(str_track_id, str_playlist_id, position);
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


    private void track_Delete(String track_id, String uid, final int pos) {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        RequestParams requestParams = new RequestParams();
        requestParams.add("tid", track_id);
        //requestParams.add("uid", uid);

        client.post(mcontext, Station_Util.URL + "track_delete.php", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Tag1", statusCode + "");
                Log.d("track_Delete", String.valueOf(response));

                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            tracks.remove(pos);
                            notifyDataSetChanged();
                            Toast.makeText(mcontext, mcontext.getResources().getString(R.string.track_deleted), Toast.LENGTH_SHORT).show();
                        } else {
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
        });

    }


    private void playlist_track_Delete(String str_track_id, String str_playlistID, final int pos) {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        client.get(Station_Util.URL + "playlist.php?trackRemove=true&trackid=" + str_track_id + "&listid=" + str_playlistID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Tag1", statusCode + "");
                Log.d("Re_playlist_track_Dele", String.valueOf(response));


                tracks.remove(pos);
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


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        tracks.clear();
        if (charText.length() == 0) {
            tracks.addAll(temp);

        } else {
            for (Explore_model_item postDetail : temp) {
                if (charText.length() != 0 && postDetail.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    tracks.add(postDetail);
                } else if (charText.length() != 0 && postDetail.getFirst_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                    tracks.add(postDetail);
                }
            }
        }
        notifyDataSetChanged();
    }

}

/*    public boolean checkFavItem(Explore_model_item checkfav) {
        boolean check = false;
        //   List<Inicio_station> recents = RadioUtil.arr_recentstations;

        List<Explore_model_item> favs = Player.fav_adapter_data;
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

