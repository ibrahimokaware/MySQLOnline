package com.example.mysqlonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Registration extends AppCompatActivity {

    private EditText ETXT_User_Name, ETXT_Email, ETXTpassword;

    Button BTN_Reg;
    private ImageView imageView_avatar;
    private String User_name = "", User_Email = "", User_Password = "";

    private Boolean Add_AVATAR = false;
    private ProgressDialog progressDialog;
    private SharedPreferences shared_Save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ETXT_User_Name = findViewById(R.id.ETXT_UserName);
        ETXT_Email = findViewById(R.id.ETXT_Email);
        ETXTpassword = findViewById(R.id.ETXT_Pass);
        BTN_Reg = findViewById(R.id.btn_Reg);
        imageView_avatar = findViewById(R.id.imageView_avatar);

        progressDialog = new ProgressDialog(this);
        shared_Save = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        BTN_Reg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HardwareIds")
            @Override
            public void onClick(View view) {
                permission_abrove();
            }
        });

    }

    public void permission_abrove() {
        //////////////////
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                return;
            }
        }
        Registration();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Registration();
        } else {
            permission_abrove();
        }
    }


    void Registration() {

        User_name = ETXT_User_Name.getText().toString().trim();
        User_Email = ETXT_Email.getText().toString().trim();
        User_Password = ETXTpassword.getText().toString().trim();


        if (TextUtils.isEmpty(User_name) || TextUtils.isEmpty(User_Email) || TextUtils.isEmpty(User_Password)) {
            Toast.makeText(this, "يجب تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();

        } else {


            if (!Add_AVATAR) {
                String msg = "<font color='#000000'>لا توجد صورة رمزية</font>";
                String title = "<font color='#000000'>صورة رمزية</font>";
                AlertDialog.Builder build = new AlertDialog.Builder(Registration.this);
                build.setTitle(Html.fromHtml(title))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage(Html.fromHtml(msg))
                        .setPositiveButton("إضافة صورة رمزية", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Add_Avatar();
                            }
                        })
                        .setNegativeButton("إغلاق", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Add_AVATAR = true;
                            }
                        }).show();

            } else {


                Bitmap Bimg = ((BitmapDrawable) imageView_avatar.getDrawable()).getBitmap();
                Bitmap bitmap = getCroppedBitmap(Bimg);
                Base64_Img_Compress_Reg compress = new Base64_Img_Compress_Reg(this);
                String ImgCode_Avatar = compress.Img_Compress(bitmap, 50);


                BTN_Reg.setEnabled(false);

                progressDialog.setMessage("انتظر ارسال البيانات");
                progressDialog.setCancelable(true);
                progressDialog.show();

                final RequestQueue requestQueue;

// Instantiate the cache
                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
                Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
                requestQueue = new RequestQueue(cache, network);

// Start the queue
                requestQueue.start();

                Response.Listener<String> responseLisener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonResponse = jsonArray.getJSONObject(0);
                            String success = jsonResponse.getString("success");
                            String UserKey = jsonResponse.getString("UserKey");

                            Toast.makeText(getApplicationContext(), success+"", Toast.LENGTH_SHORT).show();
                            Log.d("UserKey==========>",UserKey);

                            if (success.contains("Reg_OK")) {
                                Toast.makeText(Registration.this, "تم التسجيل بنجاح", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = shared_Save.edit();
                                editor.putString("Local_UserKey", UserKey.trim());
                                editor.putString("Local_UserName", User_name.trim());
                                editor.putString("Local_Email", User_Email.trim());
                                editor.putString("Local_PassWord", User_Password.trim());
                                editor.putString("Local_UserAvatar",  UserKey.trim()+".jpg");
                                editor.apply();

                                MainActivity.Local_UserKey = UserKey.trim();
                                MainActivity.Local_UserName = User_name.trim();
                                MainActivity.Local_UserAvatar = UserKey.trim()+".jpg";
                                MainActivity.Local_UserEmail = User_Email.trim();

                                MainActivity.UserAvatar = UserKey.trim()+".jpg";
                                MainActivity.UserKey = UserKey.trim();
                                MainActivity.UserName = User_name.trim();
                                MainActivity.UserEmail = User_Email.trim();

                                startActivity(new Intent(Registration.this, User_Profile.class));

                            } else if (success.contains("Error")) {
                                Toast.makeText(Registration.this, "عذرا حدث خطأ لم يتم إرسال البيانات", Toast.LENGTH_SHORT).show();

                                BTN_Reg.setEnabled(true);
                            }

                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            requestQueue.stop();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                Send_Data_Registration send_Data = new Send_Data_Registration(
                        User_name,
                        User_Email,
                        User_Password,
                        ImgCode_Avatar,
                        responseLisener);
                requestQueue.add(send_Data);
            }
        }
    }



    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public void imageView_avatar(View view) {
        Add_Avatar();
    }

    public void Add_Avatar() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {

            int colorYallow = Color.argb(255, 244, 171, 54);
            int colorRed = Color.argb(255, 255, 111, 0);
            int colorBlack = Color.argb(150, 0, 0, 0);
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setActivityTitle(getResources().getString(R.string.app_name))
                    .setAutoZoomEnabled(true)
                    .setBorderCornerColor(colorRed)
                    .setBackgroundColor(colorBlack)
                    .setBorderLineColor(colorYallow)
                    .setBorderLineThickness(2)
                    .setMaxCropResultSize(4000, 4000)
                    .setAllowCounterRotation(true)
                    .setAllowRotation(true)
                    .setAutoZoomEnabled(true)
                    .setFixAspectRatio(true)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageView_avatar.setImageURI(resultUri);
                Add_AVATAR = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


 //end
}

