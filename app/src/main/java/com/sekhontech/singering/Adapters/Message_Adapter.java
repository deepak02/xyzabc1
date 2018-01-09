package com.sekhontech.singering.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sekhontech.singering.Models.Message_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ST_004 on 23-11-2016.
 */

public class Message_Adapter extends ArrayAdapter<Message_model_item> {
    Context mcontext;
    public List<Message_model_item> message = new ArrayList<>();
    public String TAG_TOP = "TOP";
    private LayoutInflater mInflater;
    ViewHolder viewHolder;
    public static boolean favourite = false;
    String uid;

    public static class ViewHolder {
        TextView txt_to_user_message, txt_my_message,txt_time_to_user_message,txt_time_my_message;
        LinearLayout linear_to, linear_from;
    }

    public Message_Adapter(Context context, ArrayList<Message_model_item> message) {
        super(context, R.layout.chat_show_item, message);
        this.mcontext = context;
        this.message = message;
        this.mInflater = LayoutInflater.from(context);
        uid = context.getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

    }

    @Override
    public Message_model_item getItem(int position) {
        return message.get(position);
    }

    public void setData(ArrayList<Message_model_item> temp_list) {
        this.message = temp_list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Message_model_item user1 = message.get(position);

        if (convertView == null || convertView.getTag().equals(TAG_TOP)) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.chat_show_item, parent, false);
            viewHolder.txt_time_to_user_message=(TextView)convertView.findViewById(R.id.txt_time_to_user_message);
            viewHolder.txt_time_my_message=(TextView)convertView.findViewById(R.id.txt_time_my_message);
            viewHolder.txt_to_user_message = (TextView) convertView.findViewById(R.id.txt_to_user_message);
            viewHolder.txt_my_message = (TextView) convertView.findViewById(R.id.txt_my_message);
            viewHolder.linear_from = (LinearLayout) convertView.findViewById(R.id.linear_from);
            viewHolder.linear_to = (LinearLayout) convertView.findViewById(R.id.linear_to);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String message = user1.getMessage();
        String time = user1.getTime();








        String current_date1 = Station_Util.Time_Zone();
        long current_time1 = getDateInMillis(current_date1);
        long api_date = getDateInMillis(time);


       /* long final_time = current_time1 - api_date;

        long seconds = TimeUnit.MILLISECONDS.toSeconds(final_time);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(final_time);
        long hours = TimeUnit.MILLISECONDS.toHours(final_time);
        long days = TimeUnit.MILLISECONDS.toDays(final_time);
*/

        if (user1.getFrom().equalsIgnoreCase(uid)) {

            viewHolder.txt_my_message.setText(message);
            viewHolder.linear_to.setVisibility(View.GONE);
            viewHolder.linear_from.setVisibility(View.VISIBLE);

            viewHolder.txt_time_my_message.setText(DateUtils.getRelativeTimeSpanString(api_date, current_time1, DateUtils.SECOND_IN_MILLIS));

        } else {
            viewHolder.txt_to_user_message.setText(message);
            viewHolder.linear_to.setVisibility(View.VISIBLE);
            viewHolder.linear_from.setVisibility(View.GONE);

            viewHolder.txt_time_to_user_message.setText(DateUtils.getRelativeTimeSpanString(api_date, current_time1, DateUtils.SECOND_IN_MILLIS));

        }

        return convertView;
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

}


           /* if (seconds < 60) {
                System.out.println(seconds + " seconds ago");
              viewHolder.txt_time_my_message.setText(seconds + " seconds ago");
            } else if (minutes < 60) {
                System.out.println(minutes + " minutes ago");
             viewHolder.txt_time_my_message.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                System.out.println(hours + " hours ago");
              viewHolder.txt_time_my_message.setText(hours + " hours ago");
            } else {
                System.out.println(days + " days ago");
               viewHolder.txt_time_my_message.setText(days + " days ago");
            }*/



  /* if (seconds < 60) {
                System.out.println(seconds + " seconds ago");
                viewHolder.txt_time_to_user_message.setText(seconds + " seconds ago");
            } else if (minutes < 60) {
                System.out.println(minutes + " minutes ago");
                viewHolder.txt_time_to_user_message.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                System.out.println(hours + " hours ago");
                viewHolder.txt_time_to_user_message.setText(hours + " hours ago");
            } else {
                System.out.println(days + " days ago");
                viewHolder.txt_time_to_user_message.setText(days + " days ago");
            }*/