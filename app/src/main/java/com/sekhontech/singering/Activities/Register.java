package com.sekhontech.singering.Activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sekhontech.singering.R;
import com.sekhontech.singering.Utilities.ExifUtil;
import com.sekhontech.singering.Utilities.Station_Util;
import com.sekhontech.singering.Utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private static String KEY_SUCCESS = "success";
    private static String RESPONSE = "registration";
    private static String KEY_ERROR = "error";
    final int PICK_IMAGE_REQUEST = 0;
    final int CLICKED_IMAGE = 1;
    final int CROP_PIC = 2;
    TextView tv_signin;
    ScrollView scrollview;
    EditText edttxt_username, edttxt_emailid, edttxt_password, edttxt_confirm_password;
    Button btn_register;
    CircleImageView profile_circle_image;
    RequestParams requestParams;
    String Edittxt_username, Edittxt_email, Edittxt_password, Edittxt_confirm_password;
    Bitmap bitmap;
    String image;
    String mCurrentPhotoPath;
    String encodedImage;
    byte[] imageBytes;
    File photoFile;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black_translucent));
        }
        initdeclare();
    }

    private void initdeclare() {
        scrollview = (ScrollView) findViewById(R.id.scrollone);
        btn_register = (Button) findViewById(R.id.btn_register);
        profile_circle_image = (CircleImageView) findViewById(R.id.profile_circle_image);
        edttxt_username = (EditText) findViewById(R.id.edttxt_username);
        edttxt_emailid = (EditText) findViewById(R.id.edttxt_emailid);
        edttxt_password = (EditText) findViewById(R.id.edttxt_password);
        edttxt_confirm_password = (EditText) findViewById(R.id.edttxt_confirm_password);
        tv_signin = (TextView) findViewById(R.id.tv_signin);
        tv_signin.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        profile_circle_image.setOnClickListener(this);
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
    public void onClick(View v) {
        if (v == tv_signin) {
            Intent i = new Intent(Register.this, Login.class);
//         i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
            finish();
        } else if (v == btn_register) {
            if (edttxt_username.getText().toString().trim().length() == 0) {
                Utility.showAlert(Register.this, "Alert", "Please enter Username", null, "Ok");
            } else if (edttxt_username.getText().toString().length() < 3) {
                edttxt_username.setError("Username should be minimum 6 characters");
            } else if (edttxt_emailid.getText().toString().trim().length() == 0) {
                Utility.showAlert(Register.this, "Alert",
                        "Please enter Email", null, "Ok");
            } else if (edttxt_password.getText().toString().trim().length() == 0) {
                Utility.showAlert(Register.this, "Alert",
                        "Please enter Password", null, "Ok");
            } else if (edttxt_confirm_password.getText().toString().trim().length() == 0) {
                Utility.showAlert(Register.this, "Alert",
                        "Re-enter Password", null, "Ok");
            } else {

/*
                string.matches("[a-zA-Z.? ]*")     That will evaluate to true if every character in the
                string is either a lowercase letter a-z, an uppercase letter A-Z
               ,a period, a question mark, or a space.
*/

                Edittxt_username = edttxt_username.getText().toString();
                Pattern p = Pattern.compile("[^a-zA-Z0-9]");
                boolean hasSpecialChar = p.matcher(Edittxt_username).find();
                if (hasSpecialChar) {
                    Snackbar snackbar = Snackbar.make(scrollview, "Username consist from Letters and Numbers only", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Edittxt_email = edttxt_emailid.getText().toString();
                    Edittxt_password = edttxt_password.getText().toString();
                    Edittxt_confirm_password = edttxt_confirm_password.getText().toString();

                    final String email = edttxt_emailid.getText().toString();
                    final String pass = edttxt_password.getText().toString();

                    if (!isValidEmail(email)) {
                        edttxt_emailid.setError("Invalid Email");
                    } else if (!isValidPassword(pass)) {
                        edttxt_password.setError("Password too short");
                    } else if (Edittxt_password.equals(Edittxt_confirm_password)) {
                        postdata();
                    } else {
                        Snackbar snackbar = Snackbar.make(scrollview, "Password match failed", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }
        } else if (v == profile_circle_image) {
            takepicture();
        }
    }

    //////////////////////////////////////////////////////////// VALIDATION OF EMAIL AND PASSWORD >>>>>>>>>>
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 6) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    ////////////////////////////////////////////////////////////////////////////////////////

    private void takepicture() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this, R.style.YourDialogStyle)
                .setIcon(R.drawable.ic_add_photo).setTitle("Add Photo!");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            ex.printStackTrace();
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            startActivityForResult(takePictureIntent, CLICKED_IMAGE);
                        }

                    }
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_IMAGE_REQUEST);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        Log.e("Getpath", "Cool" + mCurrentPhotoPath);
        return image;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CLICKED_IMAGE && resultCode == RESULT_OK) {
            performCrop(Uri.fromFile(photoFile));
        } else if (requestCode == CROP_PIC && resultCode == RESULT_OK) {
            setPic();
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                image = getStringImage(bitmap);
                profile_circle_image.setImageBitmap(bitmap);
                Log.e("uploadFile", "data:" + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "You haven't picked Image",
                    Toast.LENGTH_LONG).show();
        }

    }

    public int getSpecialCharacterCount(String s) {
        if (s == null || s.trim().isEmpty()) {
            System.out.println("Incorrect format of string");
            return 0;
        }
        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(s);
        // boolean b = m.matches();
        boolean b = m.find();
        if (b == true)
            System.out.println("There is a special character in my string ");
        else
            System.out.println("There is no special char.");
        return 0;
    }

    private void performCrop(Uri picUri) {

        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 2);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);

            if (cropIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    cropIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    // start the activity - we handle returning in onActivityResult
                    startActivityForResult(cropIntent, CROP_PIC);
                }
            }
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        //  int targetW = profile_circle_image.getWidth();
        // int targetH = profile_circle_image.getHeight();


        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, photoW, photoH);

        bmOptions.inJustDecodeBounds = false;
        //   bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        bitmap = ExifUtil.rotateBitmap(mCurrentPhotoPath, bitmap);
        image = getStringImage(bitmap);
        profile_circle_image.setImageBitmap(bitmap);
    }


    private int calculateInSampleSize(BitmapFactory.Options bmOptions, int photoW, int photoH) {
        //BitmapFactory.Options bmOptions, int reqWidth, int reqHeight)
        {
            // Raw height and width of image
            final int height = bmOptions.outHeight;
            final int width = bmOptions.outWidth;
            int inSampleSize = 1;

            if (height > photoH || width > photoW) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > photoH
                        && (halfWidth / inSampleSize) > photoW) {
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }
    }

    private void postdata() {
        AsyncHttpClient client = new AsyncHttpClient();
        Station_Util.Https_code(client);
        requestParams = new RequestParams();
        requestParams.add("username", Edittxt_username);
        requestParams.add("email", Edittxt_email);
        requestParams.add("pass", Edittxt_password);
        requestParams.add("user_image", image);
        //Log.d("image", image);
        final ProgressDialog pDialog = new ProgressDialog(Register.this);

        client.post(this, Station_Util.URL + "registration.php", requestParams, new JsonHttpResponseHandler() {
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
                            Toast.makeText(Register.this, "Registered Successfully!:)", Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                            Intent i = new Intent(Register.this, Login.class);
                            // profile_circle_image.buildDrawingCache();
                            // Bitmap bit = profile_circle_image.getDrawingCache();
                            // i.putExtra("BitmapImage", bit);
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
                            Log.d("message", mes);
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
                Toast.makeText(Register.this, "Error in Registering", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pDialog.dismiss();
                Toast.makeText(Register.this, "Error in Registering", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDialog.dismiss();
                Toast.makeText(Register.this, "Error in Registering", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
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
