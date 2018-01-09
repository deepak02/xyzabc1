package com.sekhontech.singering.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.sekhontech.singering.Models.Search_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ST_004 on 22-09-2016.
 */

public class Followers_adapter extends ArrayAdapter<Search_model_item> {
    Context mcontext;
    private List<Search_model_item> tracks = new ArrayList<>();
    private String TAG_TOP = "TOP";
    private LayoutInflater mInflater;
    ViewHolder viewHolder;
    public static boolean favourite = false;
    int count;


    public static class ViewHolder {
        TextView txt_first_last_name, txt_username, txt_city_country;
        ImageView Iv_search_image;
    }

    public Followers_adapter(Context context, ArrayList<Search_model_item> tracks,int count) {
        super(context, R.layout.follower_item_view, tracks);
        this.mcontext = context;
        this.tracks = tracks;
        this.count = count;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public Search_model_item getItem(int position) {
        return tracks.get(position);
    }

    public void setData(ArrayList<Search_model_item> temp_list) {
        this.tracks = temp_list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Search_model_item user1 = tracks.get(position);
        if (count == 0)
        {
        if (convertView == null || convertView.getTag().equals(TAG_TOP)) {
            viewHolder = new Followers_adapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.follower_item_view, parent, false);
            viewHolder.txt_first_last_name = (TextView) convertView.findViewById(R.id.txt_first_last_name);
            viewHolder.txt_username = (TextView) convertView.findViewById(R.id.txt_username);
            viewHolder.Iv_search_image = (ImageView) convertView.findViewById(R.id.Iv_search_image);
            viewHolder.txt_city_country = (TextView) convertView.findViewById(R.id.txt_city_country);
            viewHolder.Iv_search_image = (ImageView) convertView.findViewById(R.id.Iv_search_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Followers_adapter.ViewHolder) convertView.getTag();
        }
        String first_name = user1.getFirst_name();
        String last_name = user1.getLast_name();
        String username = user1.getUsername();
        String city = user1.getCity();
        String country = user1.getCountry();
        String image = user1.getImage();

        if (first_name.equalsIgnoreCase("")) {
            viewHolder.txt_first_last_name.setText(last_name);
        } else if (last_name.equalsIgnoreCase("")) {
            viewHolder.txt_first_last_name.setText(first_name);
        } else if (first_name.equalsIgnoreCase("") && last_name.equalsIgnoreCase("")) {
            viewHolder.txt_first_last_name.setVisibility(View.GONE);
        } else {
            viewHolder.txt_first_last_name.setText(first_name + " " + last_name);
        }


        if (username.equalsIgnoreCase("")) {
            viewHolder.txt_username.setVisibility(View.GONE);
        } else {
            viewHolder.txt_username.setText(username);
            viewHolder.txt_username.setVisibility(View.VISIBLE);
        }

        if (city.equalsIgnoreCase("")) {
            viewHolder.txt_city_country.setText(country);
        } else if (country.equalsIgnoreCase("")) {
            viewHolder.txt_city_country.setText(city);
        } else if (city.equalsIgnoreCase("") && country.equalsIgnoreCase("")) {
            viewHolder.txt_city_country.setVisibility(View.GONE);
        } else {
            viewHolder.txt_city_country.setVisibility(View.VISIBLE);
            viewHolder.txt_city_country.setText(city + "," + country);
        }

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .cornerRadiusDp(7)
                .oval(false)
                .build();
        Picasso.with(getContext()).load(Station_Util.IMAGE_URL_AVATARS + image).fit().transform(transformation).placeholder(R.drawable.mic).into(viewHolder.Iv_search_image);


    }else {

            if (convertView == null || convertView.getTag().equals(TAG_TOP)) {
                viewHolder = new Followers_adapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.layout_online_friends, parent, false);
                viewHolder.txt_first_last_name = (TextView) convertView.findViewById(R.id.txt_first_last_name);
                viewHolder.txt_username = (TextView) convertView.findViewById(R.id.txt_username);
                viewHolder.Iv_search_image = (ImageView) convertView.findViewById(R.id.Iv_search_image);
                viewHolder.txt_city_country = (TextView) convertView.findViewById(R.id.txt_city_country);
                viewHolder.Iv_search_image = (ImageView) convertView.findViewById(R.id.Iv_search_image);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (Followers_adapter.ViewHolder) convertView.getTag();
            }
            String first_name = user1.getFirst_name();
            String last_name = user1.getLast_name();
            String username = user1.getUsername();
            String city = user1.getCity();
            String country = user1.getCountry();
            String image = user1.getImage();

            if (first_name.equalsIgnoreCase("")) {
                viewHolder.txt_first_last_name.setText(last_name);
            } else if (last_name.equalsIgnoreCase("")) {
                viewHolder.txt_first_last_name.setText(first_name);
            } else if (first_name.equalsIgnoreCase("") && last_name.equalsIgnoreCase("")) {
                viewHolder.txt_first_last_name.setVisibility(View.GONE);
            } else {
                viewHolder.txt_first_last_name.setText(first_name + " " + last_name);
            }


            if (username.equalsIgnoreCase("")) {
                viewHolder.txt_username.setVisibility(View.GONE);
            } else {
                viewHolder.txt_username.setText(username);
                viewHolder.txt_username.setVisibility(View.VISIBLE);
            }

            if (city.equalsIgnoreCase("")) {
                viewHolder.txt_city_country.setText(country);
            } else if (country.equalsIgnoreCase("")) {
                viewHolder.txt_city_country.setText(city);
            } else if (city.equalsIgnoreCase("") && country.equalsIgnoreCase("")) {
                viewHolder.txt_city_country.setVisibility(View.GONE);
            } else {
                viewHolder.txt_city_country.setVisibility(View.VISIBLE);
                viewHolder.txt_city_country.setText(city + "," + country);
            }

            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .cornerRadiusDp(7)
                    .oval(false)
                    .build();
            Picasso.with(getContext()).load(Station_Util.IMAGE_URL_AVATARS + image).fit().transform(transformation).placeholder(R.drawable.mic).into(viewHolder.Iv_search_image);


        }

        return convertView;
    }
}