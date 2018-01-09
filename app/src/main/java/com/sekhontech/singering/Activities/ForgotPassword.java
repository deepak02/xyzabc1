package com.sekhontech.singering.Activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Dialog_Show;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.Utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {
    EditText edttxt_email_forgot;
    Button btn_send_mail;
    Toolbar toolbar;
    RequestParams requestParams;
    private static String KEY_SUCCESS = "success";
    private static String RESPONSE = "email_exist";
    ScrollView scrollview;
    String Edittxt_email;
    TextView txt_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        initDeclare();
    }

    private void initDeclare() {
        toolbar = (Toolbar) findViewById(R.id.toolbarwe);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(R.string.Forgot_Password);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        edttxt_email_forgot = (EditText) findViewById(R.id.edttxt_email_forgot);
        scrollview = (ScrollView) findViewById(R.id.scrollthree);
        btn_send_mail = (Button) findViewById(R.id.btn_send_mail);
        txt_help = (TextView) findViewById(R.id.txt_help);
        txt_help.setOnClickListener(this);
        btn_send_mail.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_send_mail) {
            if (edttxt_email_forgot.getText().toString().trim().length() == 0) {
                Utility.showAlert(ForgotPassword.this, "Alert", "Please enter E-mail Address", null, "Ok");
            } else {
                Edittxt_email = edttxt_email_forgot.getText().toString();
                if (!isValidEmail(Edittxt_email)) {
                    edttxt_email_forgot.setError("Invalid Email");
                } else {
                    GetData();
                }

            }
        } else if (v == txt_help) {
            Dialog_Show cdd = new Dialog_Show(ForgotPassword.this);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));////custom dialog Box
            cdd.show();
        }
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void GetData() {

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        requestParams = new RequestParams();
        requestParams.add("email", Edittxt_email);

        // useremail=edttxt_email_forgot.getText().toString();
        final ProgressDialog pDialog = new ProgressDialog(ForgotPassword.this);

        client.post(this, Station_Util.URL + "forgotpass.php", requestParams, new JsonHttpResponseHandler()

                // client.get(/*"*//*http://singering.com/myapi/forgotpass.php?email=*//*"*/""+useremail, new JsonHttpResponseHandler()
        {
            @Override
            public void onStart() {
                pDialog.setTitle("Contacting Servers");
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
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
                            String mes = response.getString(RESPONSE);
                            if (mes.contains("[")) {
                                mes = mes.replace("[", "");
                                mes = mes.replace("]", "");
                            }
                            Log.d("messageforgot", mes);
                            Toast.makeText(ForgotPassword.this, mes, Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            String mes = response.getString(RESPONSE);
                            if (mes.contains("[")) {
                                mes = mes.replace("[", "");
                                mes = mes.replace("]", "");
                            }
                            Snackbar snackbar = Snackbar.make(scrollview, mes, Snackbar.LENGTH_LONG);
                            snackbar.show();
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
                Toast.makeText(ForgotPassword.this, R.string.please_try_again, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
                Toast.makeText(ForgotPassword.this, R.string.please_try_again, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDialog.dismiss();
                Toast.makeText(ForgotPassword.this, R.string.please_try_again, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                pDialog.dismiss();
                Toast.makeText(ForgotPassword.this, R.string.please_try_again, Toast.LENGTH_LONG).show();
            }
        });


    }
}