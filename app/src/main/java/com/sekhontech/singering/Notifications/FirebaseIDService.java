package com.sekhontech.singering.Notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.Utilities.Station_Util;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ST_004 on 22-02-2017.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";
    String uid;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String token) {

        uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");

        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("type", "Android");
        params.put("uid", uid);
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        SharedPreferences pref = getSharedPreferences("logincheck", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("token", token);
        edit.apply();

        client.post(this,Station_Util.URL+"push.php?request=tokenrefresh", params, new AsyncHttpResponseHandler() {
            @Override
            public void setUsePoolThread(boolean pool) {
                super.setUsePoolThread(true);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("Message_Register", "DONE");
                System.out.println("----Message_Register----");
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FirebaseIDService.this);
                sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                sharedPreferences.edit().putString(QuickstartPreferences.REGISTRATION_ID, token).apply();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

}
