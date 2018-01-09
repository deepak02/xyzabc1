package com.sekhontech.singering.Utilities;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.MySSLSocketFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ST_004 on 04-07-2016.
 */
public class Station_Util {

    public static String Song_name = "";
    public static String Song_image = "";
    public static String tag = "";
    public static String check_category = "";
    public static String playlist_ID = "";
    public static String profile_user_id = "";
    public static int audiosessionid;

    public static String URL = "http://ec2-54-226-104-223.compute-1.amazonaws.com/myapi/";
    public static String IMAGE_URL_MEDIA = "http://ec2-54-226-104-223.compute-1.amazonaws.com/uploads/media/";
    public static String IMAGE_URL_COVERS = "http://ec2-54-226-104-223.compute-1.amazonaws.com/uploads/covers/";
    public static String IMAGE_URL_AVATARS = "http://ec2-54-226-104-223.compute-1.amazonaws.com/uploads/avatars/";
    public static String TRACK_PATH = "http://ec2-54-226-104-223.compute-1.amazonaws.com/uploads/tracks/";
    public static String IMAGE_THUMBNAIL ="http://ec2-54-226-104-223.compute-1.amazonaws.com/thumb.php?&t=m&w=112&h=112&src=";
    private static final String APP_TAG = "AudioRecorder";

    public static int logString(String message) {
        return Log.i(APP_TAG, message);
    }

    public static void Https_code(AsyncHttpClient client) {
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client.setSSLSocketFactory(sf);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static String Time_Zone() {
        DateFormat dateFormatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatter1.setLenient(false);
        Date today1 = new Date();
        dateFormatter1.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
        String current_date1 = dateFormatter1.format(today1);
        //System.out.println("Date_Time_LIVE : " + current_date1);
        return current_date1;
    }

}
