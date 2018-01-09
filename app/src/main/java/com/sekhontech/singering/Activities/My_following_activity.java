package com.sekhontech.singering.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.sekhontech.singering.Adapters.Followers_adapter;
import com.sekhontech.singering.Models.Search_model_item;
import com.sekhontech.singering.R;

import java.util.ArrayList;

public class My_following_activity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listview_following;
    Toolbar toolbar_following;
    FrameLayout framelay_no_following;
    public static Followers_adapter following_list_Adapter;
    public static ArrayList<Search_model_item> following_adapter_data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_following);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        init_declare();


        if (following_adapter_data.isEmpty())
        {
            framelay_no_following.setVisibility(View.VISIBLE);
            listview_following.setVisibility(View.GONE);
        } else
        {
            listview_following.setVisibility(View.VISIBLE);
            framelay_no_following.setVisibility(View.GONE);
            following_list_Adapter = new Followers_adapter(getApplicationContext(), following_adapter_data,0);
            following_list_Adapter.setData(following_adapter_data);
            listview_following.setAdapter(following_list_Adapter);
            following_list_Adapter.notifyDataSetChanged();
        }


    }

    private void init_declare() {

        listview_following = (ListView) findViewById(R.id.listview_following);
        listview_following.setOnItemClickListener(this);
        framelay_no_following = (FrameLayout) findViewById(R.id.framelay_no_following);
        toolbar_following = (Toolbar) findViewById(R.id.toolbar_following);
        setSupportActionBar(toolbar_following);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.Following);
        toolbar_following.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = listview_following.getItemAtPosition(position);
        Search_model_item profile_detail = (Search_model_item) o;
        Intent i = new Intent(this, Profile_Screen_visit.class);
        i.putExtra("position", position);
        i.putExtra("profile_detail", profile_detail);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendBroadcast(new Intent(My_Profile_Screen.RECIEVER_DATA1));
    }
}
