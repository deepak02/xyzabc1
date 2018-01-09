package com.sekhontech.singering.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.Utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ST_004 on 27-04-2016.
 */
public class Password_Settings extends Fragment implements View.OnClickListener {
    View view;
    EditText edtxt_current_pass, edtxt_new_pass, edtxt_repeat_pass;
    Button btn_update_pass;
    ScrollView scrollview;
    String str_current_pass, str_new_pass, str_repeat_pass, uid;
    RequestParams requestParams;


    public Password_Settings() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_password_settings, container, false);
        init_declare(view);

        return view;
    }

    private void init_declare(View view) {
        scrollview = (ScrollView) view.findViewById(R.id.scroll123);
        edtxt_current_pass = (EditText) view.findViewById(R.id.edtxt_current_pass);
        edtxt_new_pass = (EditText) view.findViewById(R.id.edtxt_new_pass);
        edtxt_repeat_pass = (EditText) view.findViewById(R.id.edtxt_repeat_pass);
        btn_update_pass = (Button) view.findViewById(R.id.btn_update_pass);
        btn_update_pass.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == btn_update_pass) {
            if (edtxt_current_pass.getText().toString().trim().length() == 0) {
                Utility.showAlert(getActivity(), "Alert", "Current Password", null, "Ok");
            } else if (edtxt_new_pass.getText().toString().trim().length() == 0) {
                Utility.showAlert(getActivity(), "Alert", "New Password", null, "Ok");
            } else if (edtxt_repeat_pass.getText().toString().trim().length() == 0) {
                Utility.showAlert(getActivity(), "Alert", "Repeat Password", null, "Ok");
            } else if (edtxt_new_pass.getText().toString().length() < 6) {
                edtxt_new_pass.setError("Username should be minimum 6 characters");
            } else {
                str_current_pass = edtxt_current_pass.getText().toString();
                str_new_pass = edtxt_new_pass.getText().toString();
                str_repeat_pass = edtxt_repeat_pass.getText().toString();

                final String pass = edtxt_new_pass.getText().toString();
                if (!isValidPassword(pass)) {
                    edtxt_new_pass.setError("Password too short");
                } else if (str_new_pass.equals(str_repeat_pass)) {
                    postdata();
                } else {
                    Snackbar snackbar = Snackbar.make(scrollview, "Password match failed", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }
    }

    private void postdata() {

        uid = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        requestParams = new RequestParams();
        requestParams.add("us_id", uid);
        requestParams.add("currentpass", str_current_pass);
        requestParams.add("newpass", str_new_pass);


        final ProgressDialog pDialog = new ProgressDialog(getActivity());

        client.post(getActivity(), Station_Util.URL + "profilesetting.php?passwordchange=yes", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // Log.d("Tag12345", statusCode + "");
                Log.d("Response_image", String.valueOf(response));
                pDialog.dismiss();
                try {
                    JSONObject ob = new JSONObject(String.valueOf(response));
                    String get = ob.getString("newpass");
                    JSONArray array = new JSONArray(get);
                    JSONObject obj1 = array.getJSONObject(0);

                    String message = obj1.getString("message");
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

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

            @Override
            public void onFinish() {
                super.onFinish();
                pDialog.dismiss();
            }
        });


    }


    //////////////////////////////////////////////////////////// VALIDATION OF EMAIL AND PASSWORD >>>>>>>>>>
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 6) {
            return true;
        } else {
            return false;
        }
    }


}
