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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
 * Created by ST_004 on 20-12-2016.
 */

public class Track_Permission_Fragment extends Fragment implements View.OnClickListener {
    View view;
    Spinner spinner_visibility, spinner_allow_download;
    Button btn_save_permission;
    String visibility, allow_download,trackid;
    RequestParams requestParams;
    String uid;
    private static String KEY_SUCCESS = "success";

    public Track_Permission_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_track_permission, container, false);

        initdeclare(view);

        get_pref_values();
        set_values_from_pref();


        return view;
    }


    private void get_pref_values() {
        visibility = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("public", "");
        allow_download = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("download", "");
        trackid = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("trackid", "");

    }

    private void set_values_from_pref() {

        if (visibility.equalsIgnoreCase("0")) {
            spinner_visibility.setSelection(0);
        } else {
            spinner_visibility.setSelection(1);
        }


        if (allow_download.equalsIgnoreCase("0")) {
            spinner_allow_download.setSelection(0);
        } else {
            spinner_allow_download.setSelection(1);
        }


    }


    private void initdeclare(View view) {

        spinner_visibility = (Spinner) view.findViewById(R.id.spinner_visibility);
        spinner_allow_download = (Spinner) view.findViewById(R.id.spinner_allow_download);
        btn_save_permission = (Button) view.findViewById(R.id.btn_save_permission);
        btn_save_permission.setOnClickListener(this);

        spinner_visibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String imc_met = spinner_visibility.getSelectedItem().toString();
                //    Toast.makeText(getActivity(),imc_met,Toast.LENGTH_SHORT).show();
                if (imc_met.equalsIgnoreCase("Private")) {
                    visibility = "0";
                } else if (imc_met.equalsIgnoreCase("Public")) {
                    visibility = "1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_allow_download.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String imc_met = spinner_allow_download.getSelectedItem().toString();
                //   Toast.makeText(getActivity(),imc_met,Toast.LENGTH_SHORT).show();
                if (imc_met.equalsIgnoreCase("Off")) {
                    allow_download = "0";
                } else if (imc_met.equalsIgnoreCase("On")) {
                    allow_download = "1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.visibility_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_visibility.setAdapter(adapter);


        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(),
                R.array.allow_download, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_allow_download.setAdapter(adapter1);


    }

    @Override
    public void onClick(View v) {
        if (v == btn_save_permission) {
            postdata();
        }
    }

    private void postdata() {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        uid = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("uid", "");

        requestParams = new RequestParams();
        requestParams.add("trackid", trackid);
        requestParams.add("visibility", visibility);
        requestParams.add("download",allow_download);

        final ProgressDialog pDialog = new ProgressDialog(getActivity());

        client.post(getActivity(), Station_Util.URL+"track_permission.php?uid="+uid+"&permission=true", requestParams, new JsonHttpResponseHandler() {
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

                            SharedPreferences pref = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("download", visibility);
                            edit.putString("public", allow_download);
                            edit.apply();

                            Toast.makeText(getActivity(),"Successfully updated...",Toast.LENGTH_SHORT).show();
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getActivity(),"Nothing Changed", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Slow Internet Connection", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
             //   Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDialog.dismiss();
             //   Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }


}
