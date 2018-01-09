package com.sekhontech.singering.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ST_004 on 26-04-2016.
 */
public class Social_Networks_Profile extends Fragment implements View.OnClickListener {
    View view;
    EditText edttxt_facebook, edttxt_twitter, edttxt_google, edttxt_youtube, edttxt_soundcloud, edttxt_myspace, edttxt_lastfm, edttxt_vimeo, edttxt_tumblr;
    String str_facebook, str_twitter, str_google, str_youtube, str_soundcloud, str_myspace, str_lastfm, str_vimeo, str_tumblr;
    Button btn_social_profile;
    String uid;
    String get_facebook, get_twitter, get_google, get_youtube, get_soundcloud, get_myspace, get_lastfm, get_vimeo, get_tumblr;
    RequestParams requestParams;
    private static String KEY_SUCCESS = "success";


    public Social_Networks_Profile() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_social_networks_profile, container, false);
        initdeclare(view);

        get_pref_values();
        set_values_from_pref();

        return view;

    }

    private void initdeclare(View view) {
        edttxt_facebook = (EditText) view.findViewById(R.id.edttxt_facebook);
        edttxt_twitter = (EditText) view.findViewById(R.id.edttxt_twitter);
        edttxt_google = (EditText) view.findViewById(R.id.edttxt_google);
        edttxt_youtube = (EditText) view.findViewById(R.id.edttxt_youtube);
        edttxt_soundcloud = (EditText) view.findViewById(R.id.edttxt_soundcloud);
        edttxt_myspace = (EditText) view.findViewById(R.id.edttxt_myspace);
        edttxt_lastfm = (EditText) view.findViewById(R.id.edttxt_lastfm);
        edttxt_vimeo = (EditText) view.findViewById(R.id.edttxt_vimeo);
        edttxt_tumblr = (EditText) view.findViewById(R.id.edttxt_tumblr);
        btn_social_profile = (Button) view.findViewById(R.id.btn_social_profile);
        btn_social_profile.setOnClickListener(this);
    }

    private void get_pref_values() {
        str_facebook = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("facebook", "");
        str_twitter = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("twitter", "");
        str_google = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("google", "");
        str_youtube = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("youtube", "");
        str_soundcloud = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("soundcloud", "");
        str_myspace = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("myspace", "");
        str_lastfm = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("lastfm", "");
        str_vimeo = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("vimeo", "");
        str_tumblr = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("tumblr", "");
    }

    private void set_values_from_pref() {
        if (str_facebook.equalsIgnoreCase("")) {

        } else {
            edttxt_facebook.setText(str_facebook);
        }
        if (str_twitter.equalsIgnoreCase("")) {

        } else {
            edttxt_twitter.setText(str_twitter);
        }
        if (str_google.equalsIgnoreCase("")) {

        } else {
            edttxt_google.setText(str_google);
        }
        if (str_youtube.equalsIgnoreCase("")) {

        } else {
            edttxt_youtube.setText(str_youtube);
        }
        if (str_soundcloud.equalsIgnoreCase("")) {

        } else {
            edttxt_soundcloud.setText(str_soundcloud);
        }
        if (str_myspace.equalsIgnoreCase("")) {

        } else {
            edttxt_myspace.setText(str_myspace);
        }
        if (str_lastfm.equalsIgnoreCase("")) {

        } else {
            edttxt_lastfm.setText(str_lastfm);
        }
        if (str_vimeo.equalsIgnoreCase("")) {

        } else {
            edttxt_vimeo.setText(str_vimeo);
        }
        if (str_tumblr.equalsIgnoreCase("")) {

        } else {
            edttxt_tumblr.setText(str_tumblr);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_social_profile) {
            postdata();
        }
    }

    private void postdata() {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        get_facebook = edttxt_facebook.getText().toString();
        get_twitter = edttxt_twitter.getText().toString();
        get_google = edttxt_google.getText().toString();
        get_youtube = edttxt_youtube.getText().toString();
        get_soundcloud = edttxt_soundcloud.getText().toString();
        get_myspace = edttxt_myspace.getText().toString();
        get_lastfm = edttxt_lastfm.getText().toString();
        get_vimeo = edttxt_vimeo.getText().toString();
        get_tumblr = edttxt_tumblr.getText().toString();


        requestParams = new RequestParams();
        requestParams.add("fb", get_facebook);
        requestParams.add("twitter", get_twitter);
        requestParams.add("gplus", get_google);
        requestParams.add("youtube", get_youtube);
        requestParams.add("soundcloud", get_soundcloud);
        requestParams.add("myspace", get_myspace);
        requestParams.add("lastfm", get_lastfm);
        requestParams.add("vimeo", get_vimeo);
        requestParams.add("tumblr", get_tumblr);


        uid = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("uid", "");


        //Log.d("image", image);
        final ProgressDialog pDialog = new ProgressDialog(getActivity());

        client.post(getActivity(), Station_Util.URL+"profilesetting.php?social=true&urid=" + uid, requestParams, new JsonHttpResponseHandler() {
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

                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);

                        if (Integer.parseInt(res) == 1) {
                            pDialog.dismiss();
                            //String mes = response.getString("message");
                            //Toast.makeText(getActivity(), mes, Toast.LENGTH_SHORT).show();


                            SharedPreferences pref = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("facebook", get_facebook);
                            edit.putString("twitter", get_twitter);
                            edit.putString("google", get_google);
                            edit.putString("youtube", get_youtube);
                            edit.putString("soundcloud", get_soundcloud);
                            edit.putString("myspace", get_myspace);
                            edit.putString("lastfm", get_lastfm);
                            edit.putString("vimeo", get_vimeo);
                            edit.putString("tumblr", get_tumblr);
                            edit.apply();

                            Toast.makeText(getActivity(), "Successfully updated...", Toast.LENGTH_SHORT).show();

                        } else {
                            pDialog.dismiss();
                            String mes = response.getString("");

                            if (mes.contains("[")) {
                                mes = mes.replace("[", "");
                                mes = mes.replace("]", "");
                            }
                            Toast.makeText(getActivity(), mes, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
}
