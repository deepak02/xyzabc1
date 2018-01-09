package com.sekhontech.singering.Activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.CROP_IMAGE.CropImage;
import com.sekhontech.singering.CROP_IMAGE.InternalStorageContentProvider;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.FilePath;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.Utilities.Utility;
import com.sekhontech.singering.circleMenu.CircleActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ST_004 on 04-01-2017.
 */

public class Track_Upload extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    public static final String TAG = "General_Setting";
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    private static final int PICK_FILE_REQUEST1 = 4;
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "MyRecordedSongs";
    private static String KEY_SUCCESS = "success";
    public int count = 0;
    public DonutProgress donut_progress;
    public boolean check_record;
    String image, title, tag, description, trackid;
    CircleImageView track_image;
    Button btn_chooseImage, btn_save_general, btn_choosetrack, btn_record_start, btn_record_stop;
    EditText edttxt_title, edttxt_description, edttxt_tag;
    RequestParams requestParams;
    String uid;
    Bitmap bitmap;
    String image_path;
    String encodedImage;
    byte[] imageBytes;
    String upLoadServerUri = null;
    TextView messageText;
    // ProgressDialog dialog;
    File sourceFile;
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
    Toolbar toolbar;
    FrameLayout progress_area;
    int progress = 0;
    File file;
    boolean disableEvent;
    GoogleProgressBar googleProgressBar;
    private File mFileTemp;
    private boolean check = false;
    private String str_path = null;
    private String selectedFilePath;
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4,
            MediaRecorder.OutputFormat.THREE_GPP};
    private String[] permissions = {"android.permission.CAMERA"};
    private String[] getPermissionsread = {"android.permission.READ_EXTERNAL_STORAGE"};
    private String[] permissions1 = {"android.permission.RECORD_AUDIO"};
    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Station_Util.logString("Error: " + what + ", " + extra);
        }
    };
    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Station_Util.logString("Warning: " + what + ", " + extra);
        }
    };

    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        desiredFormat.setTimeZone(TimeZone.getDefault());
        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            //  Log.e("DATE IN MILLIS", String.valueOf(dateInMillis));
            return dateInMillis;
        } catch (ParseException e) {
            // Log.d("Exception date. " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_track_upload);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }

        initdeclare();
        disableEvent = false;

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    TEMP_PHOTO_FILE_NAME);
            System.out.println("---path 3v---" + mFileTemp.getPath());

            str_path = mFileTemp.getPath();

        } else {
            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
            System.out.println("---path 3---" + mFileTemp.getPath());

            str_path = mFileTemp.getPath();
        }
        upLoadServerUri = Station_Util.URL + "upload_track.php";

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void initdeclare() {
        googleProgressBar = (GoogleProgressBar) findViewById(R.id.googleProgressBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Upload Track");
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        progress_area = (FrameLayout) findViewById(R.id.progress_area);

        btn_record_start = (Button) findViewById(R.id.btn_record_start);
        btn_record_stop = (Button) findViewById(R.id.btn_record_stop);
        track_image = (CircleImageView) findViewById(R.id.track_image);
        messageText = (TextView) findViewById(R.id.messageText);
        btn_choosetrack = (Button) findViewById(R.id.btn_choosetrack);
        btn_chooseImage = (Button) findViewById(R.id.btn_chooseImage);
        edttxt_title = (EditText) findViewById(R.id.edttxt_title);
        edttxt_description = (EditText) findViewById(R.id.edttxt_description);
        edttxt_tag = (EditText) findViewById(R.id.edttxt_tag);
        btn_save_general = (Button) findViewById(R.id.btn_save_general);
        btn_chooseImage.setOnClickListener(this);
        btn_save_general.setOnClickListener(this);
        btn_choosetrack.setOnClickListener(this);
        btn_record_stop.setOnClickListener(this);
        btn_record_start.setOnClickListener(this);

        btn_record_stop.setAlpha(0.5f);
        btn_record_stop.setClickable(false);
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioEncodingBitRate(128000);
        recorder.setAudioSamplingRate(44100);
        recorder.setOutputFile(getFilename());
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    private String getFilename() {
        count++;
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        Date createdTime = new Date();
        int seconds = c.get(Calendar.DATE);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);


        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String currentDateTimeString = sdf.format(d);

        File folder = new File(Environment.getExternalStorageDirectory() +
                "/" + getResources().getString(R.string.app_name));
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            file = new File(folder, AUDIO_RECORDER_FOLDER);
            file.mkdir();
        }

        selectedFilePath = file.getAbsolutePath() + "/" + "Recording_" + n + ".mp3";
        Log.e("recorded_media_path", selectedFilePath);
        messageText.setText(selectedFilePath);
        return (file.getAbsolutePath() + "/" + "Recording_" + n + ".mp3");
    }

    @Override
    public void onClick(View v) {
        if (v == btn_chooseImage) {
            camera();
        } else if (v == btn_record_start) {

            btn_record_start.setAlpha(0.5f);
            btn_record_start.setClickable(false);
            btn_record_stop.setClickable(true);
            btn_record_stop.setAlpha(1f);
            startRecording();
            disableEvent = true;
        } else if (v == btn_record_stop) {

            btn_record_stop.setAlpha(0.5f);
            btn_record_stop.setClickable(false);
            btn_record_start.setClickable(true);
            btn_record_start.setAlpha(1f);
            try {
                stopRecording();
            } catch (IllegalStateException e) {
                Log.e("Exception", "" + e);
            }
            disableEvent = false;
        } else if (v == btn_choosetrack) {
            showFileChooser();
        } else if (v == btn_save_general) {
            if (edttxt_title.getText().toString().trim().length() == 0) {
                Utility.showAlert(this, "Alert", "Please enter title", null, "Ok");
            } else if (edttxt_description.getText().toString().trim().length() == 0) {
                Utility.showAlert(this, "Alert", "Please enter Description", null, "Ok");
            } else if (edttxt_tag.getText().toString().trim().length() == 0) {
                Utility.showAlert(this, "Alert", "Please enter tag", null, "Ok");
            } else if (image_path == null || image_path.toString().trim().length() == 0) {
                Utility.showAlert(this, "Alert", "Please select image", null, "Ok");
            } else if (selectedFilePath != null && !selectedFilePath.equals("")) {

               /* dialog = new ProgressDialog(Track_Upload.this);
                dialog.setMessage("Uploading file...");
                dialog.setIndeterminate(true);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCancelable(false);
                dialog.show();
                dialog.setProgress(0);*/
                progress_area.setVisibility(View.VISIBLE);

                title = edttxt_title.getText().toString();
                description = edttxt_description.getText().toString();
                tag = edttxt_tag.getText().toString();
                uid = getSharedPreferences("logincheck", MODE_PRIVATE).getString("uid", "");
                disableEvent = true;
                sourceFile = new File(selectedFilePath);

                if (!sourceFile.isFile()) {
                    // dialog.dismiss();
                    progress_area.setVisibility(View.GONE);
                    disableEvent = false;


                    Log.e("uploadFile", "Source File not exist :"
                            + selectedFilePath + "");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            messageText.setText("Source File not exist :"
                                    + selectedFilePath + "");
                        }
                    });
                } else {
                    try {
                        uploadFile(selectedFilePath, title, description, tag, image_path, uid);
                        //donut_progress.setProgress(0);
                        //   progress_area.setVisibility(View.VISIBLE); // Showing the stylish material progressbar
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void uploadFile(final String sourceFileUri, String title, String description, String tag, String image_path1, String userid) throws IOException {

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        String current_date = dateFormatter.format(today);
        final long current_time = getDateInMillis(current_date);

        File sourceFile = new File(sourceFileUri);
        //   String fileName = sourceFileUri.substring(sourceFileUri.lastIndexOf('/') + 1, sourceFileUri.length());
       /* int file_size = Integer.parseInt(String.valueOf(sourceFile.length()/1024));
        Log.e("totalSize_test", String.valueOf(file_size));*/
        double totalSize = (double) sourceFile.length();
        Log.e("totalSize", String.valueOf(totalSize));
     /*
      String hrSize = "";
     double m = totalSize / 1024.0;
        double g = totalSize / 1048576.0;
        double t = totalSize / 1073741824.0;

        DecimalFormat dec = new DecimalFormat("0.00");
        if (t > 1) {
            hrSize = dec.format(t).concat("TB");
            Log.e("FILE_", hrSize);
        } else if (g > 1) {
            hrSize = dec.format(g).concat("GB");
            Log.e("FILE_", hrSize);
        } else if (m > 1) {
            hrSize = dec.format(m).concat("MB");
            Log.e("FILE_", hrSize);
        } else {
            hrSize = dec.format(totalSize).concat("KB");
            Log.e("FILE_", hrSize);
        }
*/
        String hrSize = "";
        double m = totalSize / 1024.0;
        DecimalFormat dec = new DecimalFormat("0.00");
        if (m > 1) {
            hrSize = dec.format(m).concat("MB");
            Log.e("FILE_", hrSize);
        } else {
            hrSize = dec.format(totalSize).concat("KB");
            Log.e("FILE_", hrSize);
        }

        FileInputStream fileInputStream = new FileInputStream(sourceFile);
        bytesAvailable = fileInputStream.available();
        bufferSize = Math.max(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];
        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        progress += bytesRead;
        while (bytesRead > 0) {
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            progress += bytesRead;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        requestParams = new RequestParams();
        requestParams.add("uid", userid);
        requestParams.add("title", title);
        requestParams.add("description", description);
        requestParams.add("tag", tag);
        requestParams.add("image", image_path1);

/*
        requestParams.add("file_name", current_time + ".mp3");
*/
        requestParams.put("uploadtrack", new ByteArrayInputStream(buffer), current_time + ".mp3");

        client.post(this, Station_Util.URL + "push.php?request=uploadtrack", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);

                Log.e("Totalsize", String.valueOf(totalSize));

                int totProgress = (int) (((float) bytesWritten * 100) / totalSize);
                Log.i("Progress::::", "" + totProgress);
                if (totProgress > 0) {
                    //dialog.setProgress(totProgress);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // Log.d("Tag", statusCode + "");
                // Log.d("Response", String.valueOf(response));

                try {
                    if (response.getString(KEY_SUCCESS) != null) {
                        String res = response.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {

                            edttxt_title.setText("");
                            edttxt_description.setText("");
                            edttxt_tag.setText("");
                            selectedFilePath = "";
                            messageText.setText("No file chosen");
                            track_image.setImageBitmap(null);
                            progress_area.setVisibility(View.GONE);
                            disableEvent = false;
                            Toast.makeText(getApplicationContext(), "Track uploaded successfully", Toast.LENGTH_SHORT).show();
                            onBackPressed();

                            //  dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Track not uploaded", Toast.LENGTH_SHORT).show();
                            progress_area.setVisibility(View.GONE);
                            disableEvent = false;
                            //  dialog.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress_area.setVisibility(View.GONE);
                disableEvent = false;
                //dialog.dismiss();
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST1);
    }

    private void camera() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.YourDialogStyle)
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
                                mImageCaptureUri = FileProvider.getUriForFile(Track_Upload.this, getResources().getString(R.string.provider), mFileTemp);
                            } else {
                                mImageCaptureUri = Uri.fromFile(mFileTemp);
                            }
                            /* FileProvider.getUriForFile(Track_Upload.this,"com.sekhontech.singering.provider" + ".provider", mFileTemp);*/
                          /*  Uri.fromFile(mFileTemp);*/
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
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                try {
                    InputStream inputStream = getContentResolver().openInputStream(
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
                track_image.setImageBitmap(bitmap);
                image_path = getStringImage(bitmap);
                System.out.println("---path 3---" + path);
                break;
            case PICK_FILE_REQUEST1:

                if (data == null) {
                    //no data present
                    return;
                }
                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this, selectedFileUri);
                Log.i(TAG, "Selected File Path:" + selectedFilePath);
                messageText.setText(selectedFilePath);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startCropImage() {
        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.ASPECT_X, 3);
        intent.putExtra(CropImage.ASPECT_Y, 3);

        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.e("encodedImage", encodedImage);
        return encodedImage;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 1);
            }
        } else {
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "CAMERA permission has already been granted. Displaying camera preview.");
            // showCameraPreview();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(getPermissionsread, 1);
            }
        } else {
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "Read External storage permission has already been granted. Displaying camera preview.");
            // showCameraPreview();
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions1, 1);
            }
        } else {
            Log.i(TAG, "Record permission has already been granted.");
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (disableEvent == false) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    sendBroadcast(new Intent(CircleActivity.RECIEVER_DATA));
                    Log.e("check_times_hit", "check");
                }
            }, 1500); //time in millis
            // super.onBackPressed();
        } else {
            // do something
        }
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();

       /* if ((mDialog != null) && mDialog.isShowing())
            mDialog.dismiss();
        mDialog = null;*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}




/*    private void postdata() {

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
    }*/