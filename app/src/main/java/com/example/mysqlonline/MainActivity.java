package com.example.mysqlonline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

     public static String Main_Link = "";

     public static String Local_UserKey , Local_UserName, Local_UserEmail, Local_UserAvatar;

     public static String UserKey , UserName, UserEmail, UserAvatar;

    private SharedPreferences shared_getData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shared_getData = getSharedPreferences("UserData", Context.MODE_PRIVATE);

       findViewById(R.id.BTN_Show_Data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, All_Users_list.class));
            }
        });


        findViewById(R.id.BTN_insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Registration.class));
            }
        });


        findViewById(R.id.BTN_LogIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LogIn.class));
            }
        });


//// ...
//
//// Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url ="";
//
//// Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        Toast.makeText(getApplicationContext(), "Response is: "+ response.substring(0,500), Toast.LENGTH_SHORT).show();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "That didn't work!", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//// Add the request to the RequestQueue.
//        queue.add(stringRequest);

// ...
    }

    @Override
    protected void onResume() {
        super.onResume();

        Local_UserKey = shared_getData.getString("Local_UserKey", "").trim();
        Local_UserName = shared_getData.getString("Local_UserName", "").trim();
        Local_UserEmail = shared_getData.getString("Local_Email", "").trim();
        Local_UserAvatar = shared_getData.getString("Local_UserAvatar", "").trim();


    }
}
