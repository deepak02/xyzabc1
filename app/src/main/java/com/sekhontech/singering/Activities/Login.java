package com.sekhontech.singering.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.Notifications.RegistrationIntentService;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.Utilities.Utility;
import com.sekhontech.singering.circleMenu.CircleActivity;
import com.sekhontech.singering.circleMenu.Guest_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.protocol.ClientContext;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;
import de.hdodenhof.circleimageview.CircleImageView;

public class Login extends AppCompatActivity implements View.OnClickListener {
    public static final String MyPREFERENCES = "MyPrefs";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static String TAG = "MainActivity";
    public static String path = "";
    private static String KEY_SUCCESS = "success";
    private static String RESPONSE = "userlogin";
    private static Context context;
    TextView tv_signup;
    EditText edttxt_username_login, edttxt_password_login;
    Button btn_signin, btn_forgotpassword, btn_skip_guest;
    Bitmap bitmap;
    boolean doubleBackToExitPressedOnce = false;
    ScrollView scrollview;
    int counter = 5;
    CircleImageView profile_image;
    RequestParams requestParams;
    String useremail, password;
    int value;
    SharedPreferences pref;
    Bundle bFacebookData;
    String str_pic, str_firstname, str_lastname, str_gender, str_email, str_location, id, str_city, str_birthday, str_country, imageurl;
    String str_profileimage, encodedImage;
    byte[] imageBytes;
    LoginButton loginButton;
    CallbackManager callbackManager;
    URL profile_pic;
    Uri imageuri;
    String username, firstname, lastname;
    Bitmap bitmap_pic = null;
    private boolean permissionToReadAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.CAMERA", "android.permission.RECORD_AUDIO"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(Login.this);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        pref = getSharedPreferences("logincheck", Context.MODE_PRIVATE);
        initDeclare();
        CreatePath();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 1);
        }

        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends", "user_location"));
        loginButton.registerCallback(callbackManager, callback);
    }

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            String accessToken = loginResult.getAccessToken().getToken();
            Log.i("accessToken", accessToken);

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.i("---LoginActivity----", response.toString());

                    // Get facebook data from login
                    bFacebookData = getFacebookData(object);
                    if (bFacebookData != null) {
                        str_pic = bFacebookData.getString("profile_pic");
                        str_firstname = bFacebookData.getString("first_name");
                        str_lastname = bFacebookData.getString("last_name");
                        str_gender = bFacebookData.getString("gender");
                        str_email = bFacebookData.getString("email");
                        str_location = bFacebookData.getString("location");
                        str_birthday = bFacebookData.getString("birthday");
                    }
                    System.out.println("----facebook profile first name---" + str_firstname);
                    System.out.println("----facebook profile image---" + str_pic);
                    System.out.println("----facebook profile last name---" + str_lastname);
                    System.out.println("----facebook profile gender---" + str_gender);
                    System.out.println("----facebook profile email---" + str_email);
                    System.out.println("----facebook profile location---" + str_location);
                    System.out.println("----facebook profile birthday---" + str_birthday);
                    if (str_location != null) {
                        String[] split = str_location.split(",");
                        str_city = split[0];
                        str_country = split[1];
                        str_city = str_city.trim();
                        str_country = str_country.trim();
                    } else {
                        str_location = "";
                    }
                    System.out.println("----facebook profile image---" + str_pic);
                    getBitmapFromURL(str_pic);
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email,gender, birthday"); // Parámetros que pedimos a facebook
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            System.out.println("onCancel");
        }

        @Override
        public void onError(FacebookException e) {
            Log.v("LoginActivity", "" + e);
        }
    };


    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();
        try {
            id = object.getString("id");
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=200");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private void getBitmapFromURL(String str_pic) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(str_pic, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] fileData) {
                bitmap_pic = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);
                str_profileimage = getStringImage(bitmap_pic);
                try {
                    Registration_fb(str_firstname + str_lastname, str_firstname, str_lastname, "", str_gender, str_email, str_birthday, str_profileimage);
                } catch(UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | IOException | KeyManagementException | CertificateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void Registration_fb(String s, String str_firstname, String str_lastname, String s1, String str_gender, String str_email, String str_birthday, String str_profileimage)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, CertificateException {
        String username_one = firstname + " " + lastname;

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        Log.d("USER_AGENT", new WebView(this).getSettings().getUserAgentString());
        requestParams = new RequestParams();
        requestParams.add("firstname", str_firstname);
        requestParams.add("lastname", str_lastname);
        requestParams.add("email", str_email);
        requestParams.add("user_image", str_profileimage);
        //requestParams.add("fb_avatar", String.valueOf(profile_pic));
        final ProgressDialog pDialog = new ProgressDialog(Login.this);

        client.post(this, Station_Util.URL + "fblogin.php", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Tag", statusCode + "");
                Log.d("ResponseFB", String.valueOf(response));
                String myresponse = String.valueOf(response);

                try {
                    if (myresponse.contains("registration")) {

                        String res = response.getString("registration");
                        JSONArray obj = new JSONArray(res);
                        JSONObject ob = obj.getJSONObject(0);
                        String uid = ob.getString("idu");
                        String avatar = ob.getString("image");
                        String user_mail = ob.getString("email");
                        String user_name = ob.getString("username");

                        SharedPreferences pref = getSharedPreferences("logincheck", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putString("login", "true");
                        edit.putString("check_one_time", "true");
                        edit.putString("image", avatar);
                        edit.putString("uid", uid);
                        edit.putString("username", user_name);

                        edit.putString("PROFILE_IMAGE_FB", String.valueOf(profile_pic));
                        edit.apply();

                        if (checkPlayServices()) {
                            // Start IntentService to register this application with GCM.
                            Intent intent = new Intent(Login.this, RegistrationIntentService.class);
                            intent.putExtra("uid", uid);
                            startService(intent);
                        }

                        pref = context.getSharedPreferences("guest_check", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit1 = pref.edit();
                        edit1.putBoolean("guest", false);
                        edit1.apply();
                        pDialog.dismiss();
                        Intent i = new Intent(Login.this, CircleActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();

                    } else {
                        pDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
                Toast.makeText(Login.this, "Try Again !!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
                Toast.makeText(Login.this, "Try Again !!", Toast.LENGTH_LONG).show();
            }
        });

    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                permissionToReadAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToWriteAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        // if (!permissionToReadAccepted) Login.super.finish();
        //if (!permissionToWriteAccepted) Login.super.finish();

    }

    public void CreatePath() {
        File folder = new File(Environment.getExternalStorageDirectory() +
                "/" + getResources().getString(R.string.app_name));
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            path = folder.getAbsolutePath() + "/";
        }
    }


    private void initDeclare() {
        context = this;
        btn_skip_guest = (Button) findViewById(R.id.btn_skip_guest);
        scrollview = (ScrollView) findViewById(R.id.scrolltwo);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        edttxt_username_login = (EditText) findViewById(R.id.edttxt_username_login);
        edttxt_password_login = (EditText) findViewById(R.id.edttxt_password_login);
        tv_signup = (TextView) findViewById(R.id.tv_signup);
        btn_signin = (Button) findViewById(R.id.btn_signin);
        btn_forgotpassword = (Button) findViewById(R.id.btn_forgotpassword);
        btn_signin.setOnClickListener(this);
        tv_signup.setOnClickListener(this);
        btn_forgotpassword.setOnClickListener(this);
        btn_skip_guest.setOnClickListener(this);

        if (getIntent().hasExtra("BitmapImage")) {
            bitmap = getIntent().getParcelableExtra("BitmapImage");
            profile_image.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == tv_signup) {
            Intent i = new Intent(Login.this, Register.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        } else if (v == btn_skip_guest) {
            Intent i = new Intent(Login.this, Guest_activity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pref = context.getSharedPreferences("guest_check", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putBoolean("guest", true);
            edit.apply();

            startActivity(i);
            finish();
        } else if (v == btn_signin) {
            if (!edttxt_username_login.getText().toString().equals("") && !edttxt_password_login.getText().toString().equals("")) {
                GetData();
            } else if (edttxt_username_login.getText().toString().trim().length() == 0) {
                Utility.showAlert(Login.this, "Alert", getResources().getString(R.string.Username_Email), null, "Ok");
            } else if (edttxt_password_login.getText().toString().trim().length() == 0) {
                Utility.showAlert(Login.this, "Alert", getResources().getString(R.string.pass_field_empty), null, "Ok");
            } else {
                Toast.makeText(getApplicationContext(), "Email and Password field are empty", Toast.LENGTH_SHORT).show();
            }
        } else if (v == btn_forgotpassword) {
            Intent i = new Intent(getBaseContext(), ForgotPassword.class);
            startActivity(i);
        }
    }


    private void GetData() {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        Log.d("COOKIE_STORE", client.toString());

        useremail = edttxt_username_login.getText().toString();
        password = edttxt_password_login.getText().toString();

        final ProgressDialog pDialog = new ProgressDialog(Login.this);

        client.get(Station_Util.URL + "loginapi.php?useremail=" + useremail + "&password=" + password, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog.setTitle("Contacting Servers");
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.setIndeterminate(true);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Tag", statusCode + "");

                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            JSONArray obj = response.getJSONArray("userlogin");
                            JSONObject ob = obj.getJSONObject(0);
                            String img = ob.getString("image");
                            String username = ob.getString("username");
                            String password = ob.getString("password");
                            String city = ob.getString("city");
                            String country = ob.getString("country");
                            String uid = ob.getString("idu");
                            String cover_img = ob.getString("cover");
                            Toast.makeText(Login.this, "Login Successfully! :)", Toast.LENGTH_LONG).show();
                            pDialog.dismiss();

                            pref = context.getSharedPreferences("logincheck", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("login", "true");
                            edit.putString("password", password);
                            edit.putString("username", username);
                            edit.putString("city", city);
                            edit.putString("country", country);
                            edit.putString("image", img);
                            edit.putString("cover", cover_img);
                            edit.putString("uid", uid);
                            edit.apply();

                            if (checkPlayServices()) {
                                // Start IntentService to register this application with GCM.
                                Intent intent = new Intent(Login.this, RegistrationIntentService.class);
                                intent.putExtra("uid", uid);
                                startService(intent);
                            }

                            pref = context.getSharedPreferences("guest_check", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit1 = pref.edit();
                            edit1.putBoolean("guest", false);
                            edit1.apply();

                            Intent i = new Intent(Login.this, CircleActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        } else {
                            pDialog.dismiss();
                            String mes = response.getString(RESPONSE);
                            if (mes.contains("[")) {
                                mes = mes.replace("[", "");
                                mes = mes.replace("]", "");
                            }
                            Snackbar snackbar = Snackbar.make(scrollview, mes, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            counter--;
                            /*Snackbar snack = Snackbar.make(scrollview,"Attempts Left :"+counter, Snackbar.LENGTH_LONG);
                            View view = snack.getView();
                            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                            params.gravity = Gravity.TOP;
                            view.setLayoutParams(params);
                            snack.show();*/
                            Toast.makeText(Login.this, getResources().getString(R.string.Attempts_Left) + counter, Toast.LENGTH_SHORT).show();
                            if (counter == 0) {
                                btn_signin.setEnabled(false);
                                btn_signin.setAlpha(0.3f);
                            }
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
                Toast.makeText(Login.this, "Error in Login", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
                Toast.makeText(Login.this, "Error in Login", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                pDialog.dismiss();
                Toast.makeText(Login.this, "Error in Login", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        Snackbar snackbar = Snackbar.make(scrollview, "Tap back again to exit", Snackbar.LENGTH_LONG);
        snackbar.show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
