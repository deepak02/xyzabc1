package com.sekhontech.singering.Notifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.widget.RemoteViews;

import com.sekhontech.singering.Activities.Player;
import com.sekhontech.singering.Models.Explore_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.circleMenu.CircleActivity;
import com.sekhontech.singering.circleMenu.Guest_activity;
import com.sekhontech.singering.service.RadiophonyService;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;


/**
 * Created by ST_003 on 12-05-2016.
 */
public class Notification_Serviceplay extends IntentService {

    public static Explore_model_item data = new Explore_model_item();
    String title = "";
    String radio_name = "";
    String img_url = "";
    RemoteViews remoteViews;
    NotificationManager notificationmanager;
    NotificationCompat.Builder builder;
    Notification foregroundNote;
    public static Notification_Serviceplay service;
    int bigIconId = 0;
    String songname, tag_name, song_image;
    PendingIntent pendingLayoutIntent;

    public Notification_Serviceplay() {
        super("NotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        data = RadiophonyService.getInstance().getPlayingRadioStation();
        if (data != null) {
            title = data.getTitle();
            img_url = data.getArt();
        }
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }

    static public Notification_Serviceplay getInstance() {
        if (service == null) {
            service = new Notification_Serviceplay();
        }
        return service;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("----noti send-----");
                    CustomNotification();
                } catch (OutOfMemoryError e) {
                    System.out.println("---bitmap exception----" + e);
                } catch (Exception e) {
                    System.out.println("---Service exception----" + e);
                }
            }
        });

    }


    public void CustomNotification() {

        Notification foregroundNote;
        String text_radio_name, str_text;
        // Using RemoteViews to bind custom layouts into Notification
        final RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.player_noti_layout);


        boolean guest = getSharedPreferences("guest_check", MODE_PRIVATE).getBoolean("guest", false);
        if (guest == true) {
            Intent layout = new Intent(getApplicationContext(), Guest_activity.class);
            layout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingLayoutIntent = PendingIntent.getActivity(this, 0, layout, 0);
        } else {
            Intent layout = new Intent(getApplicationContext(), CircleActivity.class);
            layout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingLayoutIntent = PendingIntent.getActivity(this, 0, layout, 0);
        }

        //for playpause button click
        Intent playpauseButton = new Intent(CircleActivity.RECIEVER_NOTI_PLAYPAUSE);
        playpauseButton.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingPlayPauseIntent = PendingIntent.getBroadcast(this, 0, playpauseButton, 0);

        //  Intent layout = new Intent(getApplicationContext(),Player.class);
        //   layout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //  PendingIntent pendingLayoutIntent = PendingIntent.getActivity(this, 0, layout, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.mic_icon);

        if (Player.message) {
            remoteViews.setImageViewResource(R.id.iv_playpause, R.drawable.ic_pause_noti);
        } else {

           /* if (Station_Util.state_ended==true)
            {
                Player.message = true;
                Station_Util.state_ended=false;
            }else */
            remoteViews.setImageViewResource(R.id.iv_playpause, R.drawable.ic_play_noti);
        }


        if (Station_Util.Song_name != null) {
            if (Station_Util.Song_name.equalsIgnoreCase("Song Title Not Available")) {
                title = "";
            } else {
                title = Station_Util.Song_name;
            }
        } else {
            title = "song name";
        }

        remoteViews.setImageViewResource(R.id.iv_station_img, R.drawable.mic_icon);

        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_radiostationname, Station_Util.tag);
        remoteViews.setOnClickPendingIntent(R.id.iv_playpause, pendingPlayPauseIntent);

        final int bigIconId = R.id.iv_station_img;

        notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        // Set Icon

     /*   if (RadiophonyService.exoPlayer != null) {
            builder.setAutoCancel(false);
        } else {
            builder.setAutoCancel(true);
        }*/

        foregroundNote = builder.setSmallIcon(R.drawable.mic_icon)
                .setLargeIcon(largeIcon)
                // Set Ticker Message
                .setTicker(getResources().getString(R.string.app_name))
                // Dismiss Notification
                .setAutoCancel(true)
                .setOngoing(true)
                // Set RemoteViews into Notification
                .setContent(remoteViews)
                //  .setColor(NotificationCompat.COLOR_DEFAULT)
                .setPriority(Notification.DEFAULT_ALL)
                .build();

        remoteViews.setOnClickPendingIntent(R.id.linear_notif, pendingLayoutIntent);
        //remoteViews.setOnClickPendingIntent(R.id.linear_notif, pendingLayoutIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            foregroundNote.bigContentView = remoteViews;
        }

        notificationmanager.notify(1, foregroundNote);

        boolean check_from_downloads = getSharedPreferences("logincheck", MODE_PRIVATE).getBoolean("check_from_downloads", false);
        if (check_from_downloads == true) {
            if (Station_Util.Song_image != null) {
                Bitmap bitmap = StringToBitMap(Station_Util.Song_image);
                Picasso.with(Notification_Serviceplay.this).load(Station_Util.Song_image).resize(150, 150).memoryPolicy(MemoryPolicy.NO_CACHE).into(remoteViews, bigIconId, 1, foregroundNote);

            }
        } else {
            if (Station_Util.Song_image != null) {
                Picasso.with(Notification_Serviceplay.this).load(Station_Util.Song_image).resize(150, 150).memoryPolicy(MemoryPolicy.NO_CACHE).into(remoteViews, bigIconId, 1, foregroundNote);
            } else {
                Picasso.with(Notification_Serviceplay.this).load(Station_Util.Song_image).resize(150, 150).memoryPolicy(MemoryPolicy.NO_CACHE).into(remoteViews, bigIconId, 1, foregroundNote);
            }
        }



























/*

        String str_title, str_text;
        // Using RemoteViews to bind custom layouts into Notification
      remoteViews  = new RemoteViews(getPackageName(),
                R.layout.player_noti_layout);

        //  Constant.views =  remoteViews.clone();

        //for share button click
//        Intent shareButton = new Intent(MainActivity.RECIEVER_NOTI_SHARE);
//        shareButton.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingShareIntent = PendingIntent.getBroadcast(this, 0, shareButton, 0);

        //for playpause button click
        Intent playpauseButton = new Intent(Player.RECIEVER_NOTI_PLAYPAUSE);
        playpauseButton.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingPlayPauseIntent = PendingIntent.getBroadcast(this, 0, playpauseButton, 0);

        //for fav button click
//        Intent favButton = new Intent(MainActivity.RECIEVER_NOTI_FAV);
//        favButton.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingFavIntent = PendingIntent.getBroadcast(this, 0, favButton, 0);

        //for cross button click
//        Intent crossButton = new Intent(MainActivity.RECIEVER_NOTI_CROSS);
//        crossButton.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingCrossIntent = PendingIntent.getBroadcast(this, 0, crossButton, 0);
//        //for image click
//        Intent image = new Intent(MainActivity.RECIEVER_NOTI_IMAGE);
//        image.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingImageIntent = PendingIntent.getBroadcast(this, 0, image, 0);

        // Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.microphone_icon);

//        if(RadioUtil.radio_name!=null)
//        {
//            str_title = RadioUtil.radio_name;
//        }
//        else
//        {
//            str_title="radio name";
//        }
//        if(RadioUtil.song_name!=null) {
//            if(RadioUtil.song_name.equalsIgnoreCase("Song Title Not Available"))
//            {
//                str_text="";
//            }
//            else {
//                str_text=RadioUtil.song_name;
//            }
//
//        }
//        else
//        {
//            str_text="song name";
//        }
        *//*if (checkFavoriteItem(Player_Activity.fav_data)) {
            remoteViews.setImageViewResource(R.id.heartimg, R.drawable.heart_fill);
            RadioUtil.fav_tag="fill";
        } else {
            remoteViews.setImageViewResource(R.id.heartimg, R.drawable.heart_empty);
            RadioUtil.fav_tag="empty";
        }*//*

        if (Player.message) {
            remoteViews.setImageViewResource(R.id.iv_playpause, R.drawable.ic_pause_noti);
        } else {
            remoteViews.setImageViewResource(R.id.iv_playpause, R.drawable.ic_play_noti);
        }
      //  remoteViews.setImageViewResource(R.id.iv_station_img, R.drawable.cool_1);

       // Constant.NOTIFICATION_CREATED = true;


        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteViews.setTextViewText(R.id.tv_title,Station_Util.Song_name);
        remoteViews.setTextViewText(R.id.tv_radiostationname,Station_Util.tag);

     builder  = new NotificationCompat.Builder(this);
        // Set Icon
        foregroundNote = builder.setSmallIcon(R.drawable.stream_icon)
//                .setLargeIcon(R.drawable.cool_1)
                // Set Ticker Message
                .setTicker(getResources().getString(R.string.app_name))
                // Dismiss Notification
                .setAutoCancel(true)
                // .setOngoing(true)
                // Set RemoteViews into Notification
                .setContent(remoteViews)
                .setPriority(Notification.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigPictureStyle())

                .build();

//        remoteViews.setOnClickPendingIntent(R.id.shareimg, pendingShareIntent);
//        remoteViews.setOnClickPendingIntent(R.id.heartimg, pendingFavIntent);
        remoteViews.setOnClickPendingIntent(R.id.iv_playpause, pendingPlayPauseIntent);
//        remoteViews.setOnClickPendingIntent(R.id.crossimg, pendingCrossIntent);
//        remoteViews.setOnClickPendingIntent(R.id.radio_imageview, pendingImageIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            foregroundNote.bigContentView = remoteViews;
        }
        // Locate and set the Image into customnotificationtext.xml ImageViews


        // Create Notification Manager
        notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        // Build Notification with Notification Manager
        notificationmanager.notify(1, foregroundNote);

        //     final RemoteViews bigContentView = foregroundNote.bigContentView;
       bigIconId = R.id.iv_station_img;
        //AssetManager assetManager = getAssets();


        if(Station_Util.Song_image.equalsIgnoreCase(""))
        {
            Picasso.with(Notification_Serviceplay.this).load("http://singering.com/uploads/media/" +Station_Util.Song_image).into(remoteViews, bigIconId, 1, foregroundNote);
        }
        else
        {
            Picasso.with(Notification_Serviceplay.this).load("http://singering.com/uploads/media/" +Station_Util.Song_image).into(remoteViews, bigIconId, 1, foregroundNote);
        }*/
//        try {
//            is = assetManager.open(station.getImage_path());
//        } catch (IOException ea) {
//            ea.printStackTrace();
//        }
//        Bitmap bitmap = BitmapFactory.decodeStream(is);
//        iv_station.setImageBitmap(bitmap);
//        iv_background.setImageBitmap(bitmap);
//        //     Picasso.with(Player_Activity.this).load(RadioUtil.Radio_Image_URL + str_radioimg).into(contentView, iconId, 1, foregroundNote);
//
//        //  Picasso.with(Notification_Service.this).load(img).into(bigContentView, bigIconId, 1, foregroundNote);
//        Picasso.with(Notification_Service.this).load(Constant.IMAGE_URL).into(remoteViews, bigIconId, 1, foregroundNote);


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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


//    public void updateNotification() {
//
//        int api = Build.VERSION.SDK_INT;
//        data = RadiophonyService.getInstance().getPlayingRadioStation();
//        Log.d("updat", Constant.RADIO_STATION_NAME);
//
//        // update the icon
//        // remoteViews.setImageViewResource(R.id.notif_icon, R.drawable.icon_off2);
//        // update the title
//
//        if (Constant.views != null)
//        {
//            remoteViews = Constant.views;
//            Log.d("update", Constant.RADIO_STATION_NAME);
//             remoteViews.setTextViewText(R.id.tv_title, Constant.RADIO_STATION_TITLE);
//            // update the content
//            remoteViews.setTextViewText(R.id.tv_radiostationname, Constant.RADIO_STATION_NAME);
//
//            Picasso.with(Notification_Service.this).load(Constant.IMAGE_URL).placeholder(R.drawable.cool_1).into(remoteViews, bigIconId, 1,);
//            startForeground(1, foregroundNote);
//            // update the notification
//            if (api < Build.VERSION_CODES.HONEYCOMB) {
//                notificationmanager.notify(1, foregroundNote);
//            } else if (api >= Build.VERSION_CODES.HONEYCOMB) {
//                notificationmanager.notify(1, builder.build());
//
//            }
//
//        }
//    }

//    public boolean checkFavItem(Inicio_station checkfav) {
//        boolean check = false;
//        //   List<Inicio_station> recents = RadioUtil.arr_recentstations;
//
//        List<Inicio_station> favs = RadioUtil.arr_favourite;
//        if (favs != null) {
//            for (Inicio_station station_data : favs) {
//                if (station_data.equals(checkfav)) {
//                    check = true;
//                    System.out.println("-----fav check true---" + check);
//                    break;
//                }
//            }
//        }
//        System.out.println("-----fav check false---" + check);
//        return check;
//    }
//

}
