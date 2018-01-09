/*
package com.sekhontech.singering.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.sekhontech.singering.Adapters.Followers_adapter;
import com.sekhontech.singering.Models.Search_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.circleMenu.CircleActivity;

public class Message_Person_list extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listview_message;
    Toolbar toolbar_message;
    FrameLayout framelay_no_message;
   // public static Followers_adapter following_list_Adapter;
    //public static ArrayList<Search_model_item> following_adapter_data = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message__person_list);


        init_declare();


        if (My_following_activity.following_adapter_data.isEmpty())
        {
            framelay_no_message.setVisibility(View.VISIBLE);
            listview_message.setVisibility(View.GONE);
        } else
        {
            listview_message.setVisibility(View.VISIBLE);
            framelay_no_message.setVisibility(View.GONE);
            My_following_activity.following_list_Adapter = new Followers_adapter(getApplicationContext(), My_following_activity.following_adapter_data);
            My_following_activity.following_list_Adapter.setData(My_following_activity.following_adapter_data);
            listview_message.setAdapter(My_following_activity.following_list_Adapter);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_help:
                Intent i=new Intent(Message_Person_list.this,Help_message.class);
                startActivity(i);
                break;
            default:
                break;
        }

        return true;
    }

    private void init_declare() {

        listview_message = (ListView) findViewById(R.id.listview_message);
        listview_message.setOnItemClickListener(this);
        framelay_no_message = (FrameLayout) findViewById(R.id.framelay_no_message);
        toolbar_message = (Toolbar) findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Messages");
        toolbar_message.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Object o = listview_message.getItemAtPosition(position);
        Search_model_item profile_detail = (Search_model_item) o;

        Intent i = new Intent(Message_Person_list.this, Chat_Screen.class);
        i.putExtra("position", position);
        i.putExtra("from","message_activity");
        i.putExtra("profile_detail", profile_detail);
        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));
    }
}
*/
