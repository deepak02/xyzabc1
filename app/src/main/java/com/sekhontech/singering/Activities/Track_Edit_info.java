package com.sekhontech.singering.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sekhontech.singering.Adapters.ViewPagerAdapter;
import com.sekhontech.singering.Fragments.Track_General_edit_Fragment;
import com.sekhontech.singering.Fragments.Track_Permission_Fragment;
import com.sekhontech.singering.R;

public class Track_Edit_info extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track__edit_info);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        initDeclare();
    }

    private void initDeclare() {
        toolbar = (Toolbar) findViewById(R.id.tabanim_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new Track_General_edit_Fragment(), "General");
        adapter.addFrag(new Track_Permission_Fragment(), "Permission Setting");
        //adapter.addFrag(new Blocked_Users(), "Blocked Users");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()) {
            case 0:
                //Toast.makeText(Settings.this,"General Settings",Toast.LENGTH_SHORT).show();
                break;
            case 1:
                //Toast.makeText(Settings.this,"Profile_Setting",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                //Toast.makeText(Settings.this,"Three",Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));
        Intent i=new Intent(Track_Edit_info.this,My_Tracks.class);
        startActivity(i);
        finish();

    }
}
