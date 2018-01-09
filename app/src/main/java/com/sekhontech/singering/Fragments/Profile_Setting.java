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
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.CROP_IMAGE.CropImage;
import com.sekhontech.singering.CROP_IMAGE.InternalStorageContentProvider;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.Station_Util;
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
 * Created by ST_004 on 26-04-2016.
 */
public class Profile_Setting extends Fragment implements View.OnClickListener {
    View view;
    int height = 190;
    boolean isImageFitToScreen;
    ImageView Iv_cover_pic;
    String str_profile_img, str_cover_img;
    Button btn_chooseProfile_Img, btn_set_profile_pic, btn_choosecover_Img, btn_set_cover_pic;
    CircleImageView profile_image;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    private File mFileTemp;
    private String str_path = null;
    private String str_status;
    public static final String TAG = "Profile_Setting";
    Bitmap bitmap;
    String encodedImage;
    byte[] imageBytes;
    RequestParams requestParams;
    String uid;
    String image_path;
    private boolean check = false;


    public Profile_Setting() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_setting, container, false);

        init_declare(view);
        get_shared_pref();
        set_shared_pref();


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

        str_status = "";

        return view;
    }


    private void get_shared_pref() {
        str_profile_img = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("profile_img", "");
        str_cover_img = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("cover_img", "");
    }

    private void set_shared_pref() {
        Picasso.with(getActivity()).load(Station_Util.IMAGE_URL_AVATARS + str_profile_img).into(profile_image);
        Picasso.with(getActivity()).load(Station_Util.IMAGE_URL_COVERS + str_cover_img).into(Iv_cover_pic);

    }

    private void init_declare(View view) {
        profile_image = (CircleImageView) view.findViewById(R.id.profile_image);
        Iv_cover_pic = (ImageView) view.findViewById(R.id.Iv_cover_pic);
        btn_chooseProfile_Img = (Button) view.findViewById(R.id.btn_chooseProfile_Img);
        btn_set_profile_pic = (Button) view.findViewById(R.id.btn_set_profile_pic);
        btn_choosecover_Img = (Button) view.findViewById(R.id.btn_choosecover_Img);
        btn_set_cover_pic = (Button) view.findViewById(R.id.btn_set_cover_pic);
        btn_set_cover_pic.setOnClickListener(this);
        btn_set_profile_pic.setOnClickListener(this);
        btn_choosecover_Img.setOnClickListener(this);
        btn_chooseProfile_Img.setOnClickListener(this);
        Iv_cover_pic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_chooseProfile_Img) {
            camera();
            check = true;
        } else if (v == btn_set_profile_pic) {
            set_pic_data(image_path);
        } else if (v == btn_choosecover_Img) {
            camera();
            check = false;
        } else if (v == btn_set_cover_pic) {
            set_pic_data1(image_path);
        }
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
                            } else {
                                mImageCaptureUri = Uri.fromFile(mFileTemp);
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

                if (check == true) {
                    profile_image.setImageBitmap(bitmap);
                    image_path = getStringImage(bitmap);
                } else if (check == false) {
                    Iv_cover_pic.setImageBitmap(bitmap);
                    image_path = getStringImage(bitmap);
                }

                //mImageView.setImageBitmap(bitmap);

                System.out.println("---path 3---" + path);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void set_pic_data(final String image_path) {

        uid = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        requestParams = new RequestParams();
        requestParams.add("user_id", uid);
        requestParams.add("userpic", image_path);


        final ProgressDialog pDialog = new ProgressDialog(getActivity());

        client.post(getActivity(), Station_Util.URL + "profilesetting.php?profileimage=true", requestParams, new JsonHttpResponseHandler() {
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
                try {
                    JSONObject ob = new JSONObject(String.valueOf(response));
                    String image = ob.getString("userimage");


                    SharedPreferences pref = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("profile_img", image);
                    edit.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                pDialog.dismiss();
                Toast.makeText(getActivity(), "Profile picture updated...", Toast.LENGTH_SHORT).show();
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


    private void set_pic_data1(String image_path) {

        uid = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE).getString("uid", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);

        requestParams = new RequestParams();
        requestParams.add("user_id", uid);
        requestParams.add("coverpic", image_path);

        final ProgressDialog pDialog = new ProgressDialog(getActivity());

        client.post(getActivity(), Station_Util.URL + "profilesetting.php?coverwall=true", requestParams, new JsonHttpResponseHandler() {
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
                try {
                    JSONObject ob = new JSONObject(String.valueOf(response));
                    String image = ob.getString("userimage");

                    SharedPreferences pref = getActivity().getSharedPreferences("logincheck", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("cover_img", image);
                    edit.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                pDialog.dismiss();
                Toast.makeText(getActivity(), "Cover picture updated...", Toast.LENGTH_SHORT).show();
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
