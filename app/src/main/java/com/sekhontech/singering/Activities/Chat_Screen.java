package com.sekhontech.singering.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.Adapters.Message_Adapter;
import com.sekhontech.singering.Models.Message_model_item;
import com.sekhontech.singering.Models.Search_model_item;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.Utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class Chat_Screen extends AppCompatActivity implements View.OnClickListener {
    String from = "";
    Toolbar toolbar_chat;
    FrameLayout framelay_no_chat;
    ListView listview_chat;
    Search_model_item model;
    int position;
    String uid, user_id;
    private static String KEY_SUCCESS = "success";
    RequestParams requestParams;
    public static ArrayList<Message_model_item> array_list_message = new ArrayList<>();
    public static Message_Adapter message_Adapter;
    EditText Edtxt_write_message;
    ImageView Iv_send_message;
    String str_txt_message, str_username;
    private Timer autoUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__screen);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }


        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        init_declare();


        if (getIntent().hasExtra("from")) {
            from = getIntent().getStringExtra("from");
        }
        intent_data_recieve();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_item_two, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_delete:
                createAndShowAlertDialog(uid, user_id);
                break;
            default:
                break;
        }
        return true;
    }

    private void createAndShowAlertDialog(final String uid, final String user_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Chat_Screen.this);
        builder.setTitle(R.string.app_name).setMessage("All of your messages will be removed ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                Chat_Delete(uid, user_id);
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

    private void Chat_Delete(String uid, String user_id) {


        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        requestParams.add("from", uid);
        requestParams.add("to", user_id);
        //Log.d("image", image);

        client.post(this, Station_Util.URL + "del-message.php", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            Toast.makeText(Chat_Screen.this, "Message Deleted Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Chat_Screen.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //pDialog.dismiss();
                //  Toast.makeText(Chat_Screen.this, "Error", Toast.LENGTH_LONG).show();
            }

        });


    }


    private void intent_data_recieve() {
        if (getIntent().hasExtra("profile_detail")) {
            model = (Search_model_item) getIntent().getSerializableExtra("profile_detail");

            position = getIntent().getIntExtra("position", 0);

            str_username = model.getUsername();
            user_id = model.getUser_id();
        }

        toolbar_chat = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(str_username);
        toolbar_chat.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        Call_API(user_id, uid);

    }

    private void Call_API(String user_id, String uid) {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        requestParams.add("from", uid);
        requestParams.add("to", user_id);
        //Log.d("image", image);
        final ProgressDialog pDialog = new ProgressDialog(Chat_Screen.this);

        client.post(this, Station_Util.URL + "chat.php", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
              /*  pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();*/
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Tag", statusCode + "");
                Log.d("Response_to_chat", String.valueOf(response));

                array_list_message.clear();
                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {

                            String id, from, to, message, read, time;
                            try {
                                JSONArray array = response.getJSONArray("chat");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    id = obj.getString("id");
                                    from = obj.getString("from");
                                    to = obj.getString("to");
                                    message = obj.getString("message");
                                    read = obj.getString("read");
                                    time = obj.getString("time");

                                    array_list_message.add(new Message_model_item(id, from, to, message, read, time));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //  pDialog.dismiss();

                        } else {
                            //   pDialog.dismiss();
                        }

                        if (array_list_message.isEmpty()) {
                            framelay_no_chat.setVisibility(View.VISIBLE);
                            listview_chat.setVisibility(View.GONE);
                        } else {
                            listview_chat.setVisibility(View.VISIBLE);
                            framelay_no_chat.setVisibility(View.GONE);
                            message_Adapter = new Message_Adapter(Chat_Screen.this, array_list_message);
                            message_Adapter.setData(array_list_message);
                            listview_chat.setAdapter(message_Adapter);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //pDialog.dismiss();
                //  Toast.makeText(Chat_Screen.this, "Error", Toast.LENGTH_LONG).show();
            }

        });


    }

    private void init_declare() {

        Edtxt_write_message = (EditText) findViewById(R.id.Edtxt_write_message);
        Iv_send_message = (ImageView) findViewById(R.id.Iv_send_message);
        Iv_send_message.setOnClickListener(this);

        framelay_no_chat = (FrameLayout) findViewById(R.id.framelay_no_chat);
        listview_chat = (ListView) findViewById(R.id.listview_chat);


    }

    @Override
    public void onClick(View v) {
        if (v == Iv_send_message) {
            if (Edtxt_write_message.getText().toString().trim().length() == 0) {
                Utility.showAlert(Chat_Screen.this, "Alert", "field empty", null, "Ok");
            } else {
                str_txt_message = Edtxt_write_message.getText().toString();
                Run_API(user_id, uid, str_txt_message);
            }
        }
    }

    private void Run_API(final String user_id, final String uid, String str_txt_message) {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        requestParams.add("from", uid);
        requestParams.add("to", user_id);
        requestParams.add("message", str_txt_message);
        //Log.d("image", image);

        client.post(this,Station_Util.URL+"push.php?request=chat&type=chat", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Tag", statusCode + "");
                Log.d("Respo_to_sent_message", String.valueOf(response));

                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            Edtxt_write_message.setText("");
                            Call_API(user_id, uid);

                        } else {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(Chat_Screen.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Call_API(user_id, uid);
                    }
                });
            }
        }, 0, 8000); // updates each 8 secs

    }

    @Override
    public void onPause() {
        autoUpdate.cancel();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        autoUpdate.cancel();
    }
}
