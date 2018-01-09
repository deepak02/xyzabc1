package com.sekhontech.singering.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.Adapters.CountriesListAdapter;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.Utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;


public class GeneralSetting extends Fragment implements View.OnClickListener {
    View view;
    TextView txtvw_country;
    EditText edttxt_first_name, edttxt_last_name, edttxt_email, edttxt_city, edttxt_website, edttxt_description;
    String first_name, last_name, country, city, website, description, private_status, chat_status, email;
    String get_f_name, get_l_name, get_email, get_city, get_website, get_profile_status, get_description,get_country;
    Spinner profile_spinner, chat_spinner;
    PopupWindow pwindo_menu;
    String[] recourseList;
    Button btn_general_set;
    RequestParams requestParams;
    private static String KEY_SUCCESS = "success";
    String uid;
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";


    public GeneralSetting() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_general_setting, container, false);

        initdeclare(view);

        get_pref_values();
        set_values_from_pref();



        return view;
    }




    private void set_values_from_pref() {
        if (first_name.equalsIgnoreCase("")) {

        } else {
            edttxt_first_name.setText(first_name);
        }
        if (last_name.equalsIgnoreCase("")) {

        } else {
            edttxt_last_name.setText(last_name);
        }
        if (country.equalsIgnoreCase("")) {

        } else {
            txtvw_country.setText(country);
        }
        if (city.equalsIgnoreCase("")) {

        } else {
            edttxt_city.setText(city);
        }
        if (website.equalsIgnoreCase("")) {

        } else {
            edttxt_website.setText(website);
        }
        if (description.equalsIgnoreCase("")) {

        } else {
            edttxt_description.setText(description);
        }
        if (email.equalsIgnoreCase("")) {

        } else {
            edttxt_email.setText(email);
        }
        if (private_status.equalsIgnoreCase("0")) {
            profile_spinner.setSelection(0);
        } else {
            profile_spinner.setSelection(1);
        }
        if (chat_status.equalsIgnoreCase("0")) {
            chat_spinner.setSelection(1);
        } else {
            chat_spinner.setSelection(0);
        }
    }

    private void get_pref_values() {
        first_name = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("first_name", "");
        last_name = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("last_name", "");
        country = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("country", "");
        city = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("city", "");
        website = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("website", "");
        email = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("email", "");
        description = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("description", "");
        private_status = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("private_status", "");
        chat_status = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("chat_status", "");
    }

    private void initdeclare(View view) {
        btn_general_set = (Button) view.findViewById(R.id.btn_general_set);
        btn_general_set.setOnClickListener(this);
        edttxt_first_name = (EditText) view.findViewById(R.id.edttxt_first_name);
        edttxt_last_name = (EditText) view.findViewById(R.id.edttxt_last_name);
        edttxt_email = (EditText) view.findViewById(R.id.edttxt_email);
        txtvw_country = (TextView) view.findViewById(R.id.txtvw_country);
        txtvw_country.setOnClickListener(this);
        edttxt_city = (EditText) view.findViewById(R.id.edttxt_city);
        edttxt_website = (EditText) view.findViewById(R.id.edttxt_website);
        edttxt_description = (EditText) view.findViewById(R.id.edttxt_description);
        profile_spinner = (Spinner) view.findViewById(R.id.profile_spinner1);
        chat_spinner = (Spinner) view.findViewById(R.id.chat_spinner1);

        profile_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String imc_met1 = profile_spinner.getSelectedItem().toString();
                //   Toast.makeText(getActivity(),imc_met1,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        chat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String imc_met = chat_spinner.getSelectedItem().toString();
                if (imc_met.equalsIgnoreCase("Online(when available)"))
                {
                    chat_status="0";
                }else if (imc_met.equalsIgnoreCase("Always Offline"))
                {
                   chat_status="1";
                }

               //  Toast.makeText(getActivity(),imc_met,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.chat_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chat_spinner.setAdapter(adapter);


        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(),
                R.array.profile_status, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        profile_spinner.setAdapter(adapter1);

    }

    @Override
    public void onClick(View v) {
        if (v == txtvw_country) {
            initiatePopupWindow(view);
        } else if (v == btn_general_set) {
            get_f_name = edttxt_first_name.getText().toString();
            get_l_name = edttxt_last_name.getText().toString();
            get_email = edttxt_email.getText().toString();
            get_city = edttxt_city.getText().toString();
            get_website = edttxt_website.getText().toString();
            get_profile_status = profile_spinner.getSelectedItem().toString();
            get_description = edttxt_description.getText().toString();
            get_country=txtvw_country.getText().toString();
            if (get_website.toString().isEmpty())
            {
                postdata();
            }else{
                get_website = edttxt_website.getText().toString();
               Pattern p = Pattern.compile(URL_REGEX);
                Matcher m = p.matcher(get_website);//replace with string to compare
                if(m.find()) {
                    postdata();
                    System.out.println("String contains URL");
                }else {
                    Utility.showAlert(getActivity(), "Alert","Please enter a valid URL format.", null, "Ok");
                    System.out.println("String not contains URL");
                }
            }
 /*  boolean url= Patterns.WEB_URL.matcher(get_website).matches();
                if (url==true)
                {

                    postdata();
                }else {

                }*/

        }
    }

    private void postdata() {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        requestParams.add("fname", get_f_name);
        requestParams.add("lname", get_l_name);
        requestParams.add("email", get_email);
        requestParams.add("country",get_country);
        requestParams.add("city", get_city);
        requestParams.add("website", get_website);
        requestParams.add("profile", get_profile_status);
        requestParams.add("description", get_description);
        requestParams.add("offline",chat_status);

        uid = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("uid", "");


        //Log.d("image", image);
        final ProgressDialog pDialog = new ProgressDialog(getActivity());

        client.post(getActivity(), Station_Util.URL+"profilesetting.php?profilemodify=true&u_ids=" + uid, requestParams, new JsonHttpResponseHandler() {
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
                            edit.putString("city", get_city);
                            edit.putString("country", get_country);
                            edit.putString("email",get_email);
                            edit.putString("first_name", get_f_name);
                            edit.putString("last_name", get_l_name);
                            edit.putString("website", get_website);
                            edit.putString("description", get_description);
                            edit.putString("private_status",get_profile_status);
                            edit.putString("chat_status",chat_status);
                            edit.apply();

                            Toast.makeText(getActivity(),"Successfully updated...",Toast.LENGTH_SHORT).show();
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


    private void initiatePopupWindow(View view) {

        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.pop_up_country,
                    (ViewGroup) view.findViewById(R.id.menupopup1));

            recourseList = getActivity().getResources().getStringArray(R.array.CountryCodes);
            ListView list_view_country = (ListView) layout.findViewById(R.id.list_view_country);
            list_view_country.setAdapter(new CountriesListAdapter(getActivity(), recourseList));
            list_view_country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String[] g = recourseList[position].split(",");
                    txtvw_country.setText(GetCountryZipCode(g[1]).trim());
                    pwindo_menu.dismiss();
                }
            });


            RelativeLayout mainlayout = (RelativeLayout) layout.findViewById(R.id.menupopup1);
            mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //   pwindo_menu.dismiss();
                }
            });

            LinearLayout lin_exit = (LinearLayout) layout.findViewById(R.id.linear_exit);
            lin_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtvw_country.setText("");
                    pwindo_menu.dismiss();
                }
            });
            pwindo_menu = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
            pwindo_menu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));////custom dialog Box
            pwindo_menu.showAtLocation(layout, Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String GetCountryZipCode(String ssid) {
        Locale loc = new Locale("", ssid);
        return loc.getDisplayCountry().trim();
    }


}
