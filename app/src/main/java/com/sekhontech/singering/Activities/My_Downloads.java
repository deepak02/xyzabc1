package com.sekhontech.singering.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.sekhontech.singering.Adapters.Explore_Tracks_Adapter;
import com.sekhontech.singering.Models.Explore_model_item;
import com.sekhontech.singering.Preferences.MusicPlayer_Prefrence;
import com.sekhontech.singering.R;
import com.sekhontech.singering.admobadapter.expressads.AdmobExpressAdapterWrapper;
import com.sekhontech.singering.admobadapter.expressads.NativeExpressAdViewHolder;
import com.sekhontech.singering.service.RadiophonyService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class My_Downloads extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static ArrayList<Explore_model_item> list_stations = new ArrayList<Explore_model_item>();
    public RelativeLayout pDialog;
    Toolbar toolbar;
    ProgressBar progressBarInitial;
    ListView listview_tracks;
    FrameLayout framelay_no_download;
    Timer timer;
    MyTimerTask myTimerTask;
    AdmobExpressAdapterWrapper adapterWrapper;
    private Explore_Tracks_Adapter list_Adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__downloads);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        initDeclare();
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 2000);

    }

    private void initDeclare() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.My_Downloads);
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        framelay_no_download = (FrameLayout) findViewById(R.id.framelay_no_download);
        pDialog = (RelativeLayout) findViewById(R.id.progressBarHolder);
        progressBarInitial = (ProgressBar) findViewById(R.id.progressBarInitial);
        listview_tracks = (ListView) findViewById(R.id.list_view_download);
        listview_tracks.setOnItemClickListener(this);

        list_Adapter = new Explore_Tracks_Adapter(this, list_stations, 1, true);
    }

    public void SetList() {
        list_stations.clear();

        // int count = 1;


        File directory = new File(Environment.getExternalStorageDirectory() +
                "/" + getResources().getString(R.string.app_name) + "/Downloads");

        Log.d("directory", directory.getName());
        //get all the files from a directory
        File[] fList = directory.listFiles();
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    Explore_model_item model = new Explore_model_item();
                    String file_path = file.getAbsolutePath();
                    String radio_name = "";
                    if (file_path.contains("*")) {
                        model.setTitle(file.getName());
                    } else {
                        model.setTitle(file.getName());
                    }


                 /*   /////FOR FETCHING IMAGE OF SONGS
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    byte[] rawArt;
                    Bitmap art = null;
                    BitmapFactory.Options bfo = new BitmapFactory.Options();
                    mmr.setDataSource(My_Downloads.this, Uri.fromFile(file));
                    rawArt = mmr.getEmbeddedPicture();
                    if (null != rawArt)
                        art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);*/


                    //    String image = BitMapToString(art);
                    model.setImageART("");
                    // model.setBitmap(art);


                    // model.setFirst_name(String.valueOf(file.length()));
                    model.setId("");
                    model.setUid("");
                    model.setArt("");
                    model.setDescription("");
                    model.setFirst_name("");
                    model.setLast_name("");
                    model.setTag("");
                    model.setArt("");
                    model.setBuy("");
                    model.setRecord("");
                    model.setRelease("");
                    model.setLicense("");
                    model.setSize("");
                    model.setDownload("");
                    model.setTime("");
                    model.setLikes("");
                    model.setDownloads("");
                    model.setViews("");

                    // model.setLast_name();
                    //   Uri uri = getAudioContentUri(mactivity, file);
                    String url = Uri.fromFile(file).toString();
                    URL Url = null;
                    try {
                        Url = new URL(url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    model.setName(Url.toString());
                    model.setFiledownloaded_path(file_path);

                    list_stations.add(model);
                    //count++;
                }
            }

            // count = 1;

        }

        if (list_stations == null) {
            framelay_no_download.setVisibility(View.VISIBLE);
            pDialog.setVisibility(View.GONE);
        } else if (list_stations.isEmpty()) {
            framelay_no_download.setVisibility(View.VISIBLE);
            pDialog.setVisibility(View.GONE);
        } else {
            pDialog.setVisibility(View.GONE);
            framelay_no_download.setVisibility(View.GONE);
            listview_tracks.setVisibility(View.VISIBLE);
            list_Adapter.setData(list_stations);
            if (getResources().getBoolean(R.bool.Ads_check) == true) {
                initListViewItems();
            } else {
                listview_tracks.setAdapter(list_Adapter);
            }
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Object o = listview_tracks.getItemAtPosition(position);
        try {
            Explore_model_item track_detail = (Explore_model_item) o;
            if (RadiophonyService.exoPlayer != null) {
                RadiophonyService.exoPlayer.stop();
                stopService(new Intent(My_Downloads.this, RadiophonyService.class));
            }
            MusicPlayer_Prefrence.getInstance(My_Downloads.this).save(My_Downloads.this, track_detail);
            MusicPlayer_Prefrence.getInstance(My_Downloads.this).save_position(position);

            startService(new Intent(My_Downloads.this, RadiophonyService.class));
            RadiophonyService.initialize(My_Downloads.this, track_detail, 1);
            play(true);

            Intent i = new Intent(My_Downloads.this, Player.class);
            i.putExtra("position", position);
            i.putExtra("from", "my_downloads");
            i.putExtra("track_detail", track_detail);
            startActivity(i);
        } catch (NullPointerException | ClassCastException e) {
            Log.e("Null_noti", "" + e);
        }
        //  decodeFile();
    }

    public void play(boolean play) {
        if (!play) {
            stopService(new Intent(My_Downloads.this, RadiophonyService.class));
        } else {
            startService(new Intent(My_Downloads.this, RadiophonyService.class));
        }
    }

    private void initListViewItems() {

        //creating your adapter, it could be a custom adapter as well
        //your test devices' ids
        String[] testDevicesIds = new String[]{getString(R.string.testDeviceID), AdRequest.DEVICE_ID_EMULATOR};
        //when you'll be ready for release please use another ctor with admobReleaseUnitId instead.
        adapterWrapper = new AdmobExpressAdapterWrapper(this, testDevicesIds) {
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent, int viewType) {
                NativeExpressAdView adView = adViewHolder.getAdView();
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                        AbsListView.LayoutParams.WRAP_CONTENT);
                RelativeLayout container = new RelativeLayout(My_Downloads.this);
                container.setLayoutParams(lp);
                container.setBackgroundColor(Color.BLACK);

                TextView textView = new TextView(My_Downloads.this);
                textView.setLayoutParams(lp);
                /*textView.setText("Ad is loading...");*/
                textView.setTextColor(Color.RED);
                container.addView(textView);
                //wrapping
                container.addView(adView);
                //return wrapper view
                return container;
            }
        };
        adapterWrapper.setAdapter(list_Adapter); //wrapping your adapter with a AdmobExpressAdapterWrapper.

        adapterWrapper.setLimitOfAds(10);

        adapterWrapper.setNoOfDataBetweenAds(10);

        adapterWrapper.setFirstAdIndex(3);

        listview_tracks.setAdapter(adapterWrapper); // setting an AdmobAdapterWrapper to a ListView

    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SetList();
                }
            });
        }
    }
}
