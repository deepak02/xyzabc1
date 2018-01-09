package com.sekhontech.singering.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.Preferences.Notification_on_off_prefrence;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ST_004 on 26-04-2016.
 */
public class Notification_Settings extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    View view;
    SwitchCompat switchCompatLikes, switchCompatComments, switchCompatChat, switchCompatFriends, switchCompatEmailComments, switchCompatEmailLikes, switchCompatEmailFriends;
    RequestParams requestParams;
    String likes, comments, chat, friends, emailcomments, emaillike, emailfriend, uid;
    Button btn_submit_changes;

    public Notification_Settings() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification_settings, container, false);
        initdeclare();
        return view;
    }

    private void initdeclare() {
        btn_submit_changes = (Button) view.findViewById(R.id.btn_submit_changes);
        btn_submit_changes.setOnClickListener(this);

        switchCompatLikes = (SwitchCompat) view.findViewById(R.id
                .switch_Likes);
        switchCompatComments = (SwitchCompat) view.findViewById(R.id
                .switch_Comments);
        switchCompatChat = (SwitchCompat) view.findViewById(R.id
                .switch_Chat);
        switchCompatFriends = (SwitchCompat) view.findViewById(R.id
                .switch_Friends);
        switchCompatEmailComments = (SwitchCompat) view.findViewById(R.id
                .switch_EmailComments);
        switchCompatEmailLikes = (SwitchCompat) view.findViewById(R.id
                .switch_EmailLikes);
        switchCompatEmailFriends = (SwitchCompat) view.findViewById(R.id
                .switch_EmailFriends);
        switchCompatLikes.setSwitchPadding(40);
        switchCompatLikes.setOnCheckedChangeListener(this);
        switchCompatComments.setSwitchPadding(40);
        switchCompatComments.setOnCheckedChangeListener(this);
        switchCompatChat.setSwitchPadding(40);
        switchCompatChat.setOnCheckedChangeListener(this);
        switchCompatFriends.setSwitchPadding(40);
        switchCompatFriends.setOnCheckedChangeListener(this);
        switchCompatEmailComments.setSwitchPadding(40);
        switchCompatEmailComments.setOnCheckedChangeListener(this);
        switchCompatEmailLikes.setSwitchPadding(40);
        switchCompatEmailLikes.setOnCheckedChangeListener(this);
        switchCompatEmailFriends.setSwitchPadding(40);
        switchCompatEmailFriends.setOnCheckedChangeListener(this);

        String notificationl = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("notificationl", "");
        String notificationc = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("notificationc", "");
        String notificationd = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("notificationd", "");
        String notificationf = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("notificationf", "");
        String email_comment = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("email_comment", "");
        String email_like = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("email_like", "");
        String email_new_friend = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("email_new_friend", "");

        /*
        if (Notification_on_off_prefrence.getInstance(getActivity()).getCountLikes() == 1) {
            switchCompatLikes.setChecked(true);
            likes = "1";
        } else {
            switchCompatLikes.setChecked(false);
            likes = "0";
        }*/

        if (notificationl.equalsIgnoreCase("1")) {
            switchCompatLikes.setChecked(true);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountLikes(1);
            likes = "1";
        } else {
            switchCompatLikes.setChecked(false);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountLikes(0);
            likes = "0";
        }

        if (notificationc.equalsIgnoreCase("1")) {
            switchCompatComments.setChecked(true);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountCommment(1);
            comments = "1";
        } else {
            switchCompatComments.setChecked(false);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountCommment(0);
            comments = "0";
        }

        if (notificationd.equalsIgnoreCase("1")) {
            switchCompatChat.setChecked(true);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountChat(1);
            chat = "1";
        } else {
            switchCompatChat.setChecked(false);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountChat(0);
            chat = "0";
        }

        if (notificationf.equalsIgnoreCase("1")) {
            switchCompatFriends.setChecked(true);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountFriends(1);
            friends = "1";
        } else {
            switchCompatFriends.setChecked(false);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountFriends(0);
            friends = "0";
        }

        if (email_comment.equalsIgnoreCase("1")) {
            switchCompatEmailComments.setChecked(true);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountEmailComment(1);
            emailcomments = "1";
        } else {
            switchCompatEmailComments.setChecked(false);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountEmailComment(0);
            emailcomments = "0";
        }

        if (email_like.equalsIgnoreCase("1")) {
            switchCompatEmailLikes.setChecked(true);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountEmailLikes(1);
            emaillike = "1";
        } else {
            switchCompatEmailLikes.setChecked(false);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountEmailLikes(0);
            emaillike = "0";
        }

        if (email_new_friend.equalsIgnoreCase("1")) {
            switchCompatEmailFriends.setChecked(true);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountEmialFriends(1);
            emailfriend = "1";
        } else {
            switchCompatEmailFriends.setChecked(false);
            Notification_on_off_prefrence.getInstance(getActivity()).setCountEmialFriends(0);
            emailfriend = "0";
        }
    }

    private void hit_API() {
        uid = getActivity().getSharedPreferences("logincheck", getActivity().MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        requestParams.add("notificationl", likes);
        requestParams.add("notificationcmt", comments);
        requestParams.add("notificationc", chat);
        requestParams.add("notificationf", friends);
        requestParams.add("email_comment", emailcomments);
        requestParams.add("email_like", emaillike);
        requestParams.add("email_new_friend", emailfriend);
        requestParams.add("userid", uid);

        //Log.d("image", image);
        final ProgressDialog pDialog = new ProgressDialog(getActivity());

        client.post(getActivity(), Station_Util.URL + "on_off_notify.php", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Tag", statusCode + "");
                Log.d("Response", String.valueOf(response));
                pDialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDialog.dismiss();
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_Likes:

                if (isChecked) {
                    switchCompatLikes.setChecked(true);
                    // Toast.makeText(Settings.this, "En la notificación", Toast.LENGTH_SHORT).show();
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountLikes(1);
                    likes = "1";
                } else {
                    switchCompatLikes.setChecked(false);
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountLikes(0);
                    likes = "0";
                }
                break;
            case R.id.switch_Comments:
                if (isChecked) {
                    switchCompatComments.setChecked(true);
                    // Toast.makeText(Settings.this, "En la notificación", Toast.LENGTH_SHORT).show();
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountCommment(1);
                    comments = "1";
                } else {
                    switchCompatComments.setChecked(false);
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountCommment(0);
                    comments = "0";
                }
                break;
            case R.id.switch_Chat:
                if (isChecked) {
                    switchCompatChat.setChecked(true);
                    // Toast.makeText(Settings.this, "En la notificación", Toast.LENGTH_SHORT).show();
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountChat(1);
                    chat = "1";
                } else {
                    switchCompatChat.setChecked(false);
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountChat(0);
                    chat = "0";
                }
                break;
            case R.id.switch_Friends:

                if (isChecked) {
                    switchCompatFriends.setChecked(true);
                    // Toast.makeText(Settings.this, "En la notificación", Toast.LENGTH_SHORT).show();
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountFriends(1);
                    friends = "1";
                } else {
                    switchCompatFriends.setChecked(false);
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountFriends(0);
                    friends = "0";
                }

                break;
            case R.id.switch_EmailComments:

                if (isChecked) {
                    switchCompatEmailComments.setChecked(true);
                    // Toast.makeText(Settings.this, "En la notificación", Toast.LENGTH_SHORT).show();
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountEmailComment(1);
                    emailcomments = "1";
                } else {
                    switchCompatEmailComments.setChecked(false);
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountEmailComment(0);
                    emailcomments = "0";
                }

                break;
            case R.id.switch_EmailLikes:

                if (isChecked) {
                    switchCompatEmailLikes.setChecked(true);
                    // Toast.makeText(Settings.this, "En la notificación", Toast.LENGTH_SHORT).show();
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountEmailLikes(1);
                    emaillike = "1";
                } else {
                    switchCompatEmailLikes.setChecked(false);
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountEmailLikes(0);
                    emaillike = "0";
                }
                break;
            case R.id.switch_EmailFriends:

                if (isChecked) {
                    switchCompatEmailFriends.setChecked(true);
                    // Toast.makeText(Settings.this, "En la notificación", Toast.LENGTH_SHORT).show();
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountEmialFriends(1);
                    emailfriend = "1";
                } else {
                    switchCompatEmailFriends.setChecked(false);
                    Notification_on_off_prefrence.getInstance(getActivity()).setCountEmialFriends(0);
                    emailfriend = "0";
                }

                break;
        }

    }

    @Override
    public void onClick(View v) {
        if (v == btn_submit_changes) {
            hit_API();
        }
    }
}
