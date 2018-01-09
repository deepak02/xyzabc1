package com.sekhontech.singering.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.sekhontech.singering.R;

public class First extends AppCompatActivity implements View.OnClickListener {
Button btn_explore,btn_settings,btn_stream,btn_friends,btn_message,btn_notification,btn_playlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        initDeclare();
    }

    private void initDeclare()
    {
        btn_friends=(Button)findViewById(R.id.btn_friends);
        btn_explore=(Button)findViewById(R.id.btn_explore);
        btn_settings=(Button)findViewById(R.id.btn_settings);
        btn_stream=(Button)findViewById(R.id.btn_stream);
        btn_message=(Button)findViewById(R.id.btn_message);
        btn_notification=(Button)findViewById(R.id.btn_notification);
        btn_playlist=(Button)findViewById(R.id.btn_playlist);
        btn_friends.setOnClickListener(this);
        btn_stream.setOnClickListener(this);
        btn_settings.setOnClickListener(this);
        btn_explore.setOnClickListener(this);
        btn_message.setOnClickListener(this);
        btn_notification.setOnClickListener(this);
        btn_playlist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btn_explore)
        {
            btn_explore.setBackgroundDrawable(getResources().getDrawable(R.drawable.backcolor));
            btn_settings.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_stream.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_friends.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_notification.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_playlist.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_message.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
          /*  Intent i=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);*/

        }
        else if (v==btn_settings)
        {
            btn_explore.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_settings.setBackgroundDrawable(getResources().getDrawable(R.drawable.backcolor));
            btn_stream.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_friends.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_notification.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_playlist.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_message.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));

            Intent i = new Intent(getApplicationContext(), Settings.class);
            startActivity(i);
        }
        else if (v==btn_stream)
        {
            btn_explore.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_settings.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_stream.setBackgroundDrawable(getResources().getDrawable(R.drawable.backcolor));
            btn_friends.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_notification.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_playlist.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_message.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));

            Intent i = new Intent(getApplicationContext(), My_Profile_Screen.class);
            startActivity(i);
        }
        else if (v==btn_friends)
        {
            btn_explore.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_settings.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_stream.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_friends.setBackgroundDrawable(getResources().getDrawable(R.drawable.backcolor));
            btn_notification.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_playlist.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_message.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));


            Intent i = new Intent(getApplicationContext(), Player.class);
            startActivity(i);
        }
        else if (v==btn_notification)
        {
            btn_explore.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_settings.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_stream.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_friends.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_notification.setBackgroundDrawable(getResources().getDrawable(R.drawable.backcolor));
            btn_playlist.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_message.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));

            Intent i = new Intent(getApplicationContext(), Player.class);
            startActivity(i);
        }
        else if (v==btn_playlist)
        {
            btn_explore.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_settings.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_stream.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_friends.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_notification.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_playlist.setBackgroundDrawable(getResources().getDrawable(R.drawable.backcolor));
            btn_message.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));

            Intent i = new Intent(getApplicationContext(), Player.class);
            startActivity(i);
        }else if (v==btn_message)
        {
            btn_explore.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_settings.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_stream.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_friends.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_notification.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_playlist.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corner));
            btn_message.setBackgroundDrawable(getResources().getDrawable(R.drawable.backcolor));

            Intent i = new Intent(getApplicationContext(), Player.class);
            startActivity(i);
        }

    }

}
