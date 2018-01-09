package com.sekhontech.singering.Fragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.CROP_IMAGE.CropImage;
import com.sekhontech.singering.CROP_IMAGE.InternalStorageContentProvider;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.Utilities.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ST_004 on 20-12-2016.
 */

public class Track_General_edit_Fragment extends Fragment implements View.OnClickListener {
    View view;
    String image, title, tag, description, trackid;
    CircleImageView track_image;
    Button btn_chooseImage, btn_save_general;
    EditText edttxt_title, edttxt_description, edttxt_tag;
    RequestParams requestParams;
    String uid;
    private static String KEY_SUCCESS = "success";
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    private File mFileTemp;
    public static final String TAG = "General_Setting";
    Bitmap bitmap;
    private boolean check = false;
    String image_path;
    String encodedImage;
    byte[] imageBytes;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    private String str_path = null;


    public Track_General_edit_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_general_edit, container, false);

        initdeclare(view);

        get_pref_values();
        set_values_from_pref();


        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    TEMP_PHOTO_FILE_NAME);
            System.out.println("---path 3v---" + mFileTemp.getPath());

            str_path = mFileTemp.getPath();

        } else {
            mFileTemp = new File(getActivity().getFilesDir(), TEMP_PHOTO_FILE_NAME);
            System.out.println("---path 3---" + mFileTemp.getPath());

            str_path = mFileTemp.getPath();

        }


        return view;
    }


    private void get_pref_values() {
        image = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("art", "");
        title = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("title", "");
        tag = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("tag", "");
        description = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("description", "");
        trackid = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("trackid", "");


    }

    private void set_values_from_pref() {

        if (title != null) {
            edttxt_title.setText(title);
        } else {
            edttxt_title.setText("");
        }

        if (description != null) {
            edttxt_description.setText(description);
        } else {
            edttxt_description.setText("");
        }

        if (tag != null) {
            edttxt_tag.setText(tag);
        } else {
            edttxt_tag.setText(tag);
        }

        if (image != null) {
            Picasso.with(getActivity()).load(Station_Util.IMAGE_URL_MEDIA + image).fit().into(track_image);
        } else {

        }

    }


    private void initdeclare(View view) {
        track_image = (CircleImageView) view.findViewById(R.id.track_image);
        btn_chooseImage = (Button) view.findViewById(R.id.btn_chooseImage);
        edttxt_title = (EditText) view.findViewById(R.id.edttxt_title);
        edttxt_description = (EditText) view.findViewById(R.id.edttxt_description);
        edttxt_tag = (EditText) view.findViewById(R.id.edttxt_tag);
        btn_save_general = (Button) view.findViewById(R.id.btn_save_general);

        btn_chooseImage.setOnClickListener(this);
        btn_save_general.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v == btn_chooseImage) {
            camera();
        } else if (v == btn_save_general) {
            if (edttxt_title.getText().toString().trim().length() == 0) {
                Utility.showAlert(getActivity(), "Alert", "Please enter title", null, "Ok");
            } else if (edttxt_description.getText().toString().trim().length() == 0) {
                Utility.showAlert(getActivity(), "Alert",
                        "Please enter Description", null, "Ok");
            } else if (edttxt_tag.getText().toString().trim().length() == 0) {
                Utility.showAlert(getActivity(), "Alert",
                        "Please enter tag", null, "Ok");
            } else {
                title = edttxt_title.getText().toString();
                description = edttxt_description.getText().toString();
                tag = edttxt_tag.getText().toString();

                postdata();
            }

        }
    }

    private void postdata() {
        if (image_path==null)
        {
            image_path="";
        }
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        uid = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("uid", "");
        requestParams = new RequestParams();
        requestParams.add("id", trackid);
        requestParams.add("title", title);
        requestParams.add("description", description);
        requestParams.add("tags", tag);
        requestParams.add("userpic", image_path);

        final ProgressDialog pDialog = new ProgressDialog(getActivity());

        client.post(getActivity(), Station_Util.URL + "track_general.php", requestParams, new JsonHttpResponseHandler() {
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
                            edit.putString("description", description);
                            edit.putString("title", title);
                            edit.putString("tag", tag);
                            edit.putString("art", image_path);
                            edit.putString("trackid", trackid);
                            edit.apply();

                            Toast.makeText(getActivity(), "Successfully updated...", Toast.LENGTH_SHORT).show();
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getActivity(), "Nothing Changed", Toast.LENGTH_SHORT).show();
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
                //  Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDialog.dismiss();
                //    Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }

    private void camera() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.YourDialogStyle)
                .setIcon(R.drawable.ic_add_photo).setTitle("Add Photo!");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    try {
                        Uri mImageCaptureUri = null;
                        String state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                                mImageCaptureUri = FileProvider.getUriForFile(getActivity(),getResources().getString(R.string.provider), mFileTemp);
                            }else {
                                mImageCaptureUri =Uri.fromFile(mFileTemp);
                            }
                        } else {
                            mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
                        }
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                mImageCaptureUri);
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
                    } catch (ActivityNotFoundException e) {

                        Log.d(TAG, "cannot take picture", e);
                    }
                } else if (items[item].equals("Choose from Library")) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != getActivity().RESULT_OK) {

            return;
        }
        switch (requestCode) {

            case REQUEST_CODE_GALLERY:

                try {

                    InputStream inputStream = getActivity().getContentResolver().openInputStream(
                            data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(
                            mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();

                    startCropImage();

                } catch (Exception e) {

                    Log.e(TAG, "Error while creating temp file", e);
                }

                break;
            case REQUEST_CODE_TAKE_PICTURE:

                startCropImage();
                break;
            case REQUEST_CODE_CROP_IMAGE:

                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path == null) {

                    return;
                }
                bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());

                //  if (check == true) {
                track_image.setImageBitmap(bitmap);
                // } else {
                // Iv_cover_pic.setImageBitmap(bitmap);
                // }

                    image_path = getStringImage(bitmap);



                //mImageView.setImageBitmap(bitmap);

                System.out.println("---path 3---" + path);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startCropImage() {
        Intent intent = new Intent(getActivity(), CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 3);
        intent.putExtra(CropImage.ASPECT_Y, 3);

        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.e("encodedImage", encodedImage);
        return encodedImage;
    }


}