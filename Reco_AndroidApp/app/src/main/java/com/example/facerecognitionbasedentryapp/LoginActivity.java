package com.example.facerecognitionbasedentryapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button login;
    EditText editEmail,editPassword;
    ProgressDialog dialog;
    String LoginUrl = "https://frozen-wildwood-57358.herokuapp.com/api/face_recognition/login/";
    //String adminLogin  = "https://frozen-wildwood-57358.herokuapp.com/api/face_recognition/adminLogin/";;
    //String userLogin  = "https://frozen-wildwood-57358.herokuapp.com/api/face_recognition/userLogin/";;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        /*
        Intent intent = getIntent();
        String str = intent.getStringExtra("message_user");
        if(str.equals("normal_user")){
            LoginUrl = userLogin;
            //Toast.makeText(getApplicationContext(),"uer is normal",Toast.LENGTH_LONG).show();
        }else{
            LoginUrl = adminLogin;
            //Toast.makeText(getApplicationContext(),"url:"+LoginUrl,Toast.LENGTH_LONG).show();

        }

         */

        login = findViewById(R.id.button);
        editEmail= findViewById(R.id.username);
        editPassword = findViewById(R.id.editTextTextPassword);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String mEmail=editEmail.getText().toString().trim();
                String mPass = editPassword.getText().toString().trim();

                if (!mEmail.isEmpty() && !mPass.isEmpty()) {
                    Login(mEmail, mPass);
                } else if (!mEmail.isEmpty() && mPass.isEmpty()) {
                    displayDialog("Please insert password");
                } else if (mEmail.isEmpty() && !mPass.isEmpty()) {
                    displayDialog("Please insert Usename");
                } else {
                    displayDialog("Please insert Email and password");
                }
            }
        });


    }

    public void Login(final String email, final String password) {

        dialog = new ProgressDialog(LoginActivity.this, R.style.AlertDialog);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Logging In....");
        dialog.show();


        Log.d("Login", "onResponse: login clicked");
        final RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        JSONObject postparams = null;
        try {
            postparams = new JSONObject();
            postparams.put("email", email);
            postparams.put("password", password);
            //Toast.makeText(getApplicationContext(), "You eneterd login with " + email, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Log.d("Login", "Login: Error:" + e);
            Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            //onClickLogin.setEnabled(true);
        }

        //Toast.makeText(getApplicationContext(),"sending to url: "+LoginUrl,Toast.LENGTH_LONG).show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(LoginUrl, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(getApplicationContext(), "response is " + response.toString(), Toast.LENGTH_LONG).show();
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));

                            JSONObject data=jsonObject.getJSONObject("Data");

                            String token = data.getString("token");
                            String user_type = data.getString("userType");
                            /*
                            if(LoginUrl.equals(adminLogin))
                                user_type="admin";
                            else
                                user_type="normal_user";
                            */
                            SharedPreferences.Editor editor = getSharedPreferences("tokenFile", Context.MODE_PRIVATE).edit();

                            editor.putString("key", token);
                            editor.putString("TypeOfUser", user_type);
                            editor.apply();
                            Log.d("Login", "onResponse: key:" + token);
                            dialog.dismiss();
                            //displayDialog("Success");

                            if(user_type.equals("admin")){
                                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else{
                                Intent i = new Intent(getApplicationContext(),ViewRecords.class);
                                startActivity(i);
                                finish();

                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Exception occured while making post request", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            Log.d("Login", "onResponse: error in post catch block: " + e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "error is " + error.getMessage(), Toast.LENGTH_LONG).show();
                        if (error instanceof NoConnectionError || error instanceof TimeoutError){
                            //Toast.makeText(getApplicationContext(), "Check your internet connection!", Toast.LENGTH_LONG).show();
                            displayDialog("Check your internet connection!");
                        }
                        else if (error instanceof ServerError) {
                            displayDialog("Invalid Username or password");
                        }
                        /*else if (error instanceof AuthFailureError) {
                            // Error indicating that there was an Authentication Failure while performing the request
                            Toast.makeText(getApplicationContext(), "This error is case2", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                            Toast.makeText(getApplicationContext(), "This error is case3", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //Indicates that there was network error while performing the request
                            Toast.makeText(getApplicationContext(), "This error is case4", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            // Indicates tReportFire.thishat the server response could not be parsed
                            Toast.makeText(getApplicationContext(), "This error is case5", Toast.LENGTH_LONG).show();
                        }

                         */
                        else {
                         displayDialog("An unknown error occurred.");
                            //Toast.makeText(getApplicationContext(), "An unknown error occurred.", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
        MyRequestQueue.add(jsonObjectRequest);
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

    }

    public final void displayDialog(String str){

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this,R.style.AlertDialog);
        builder.setMessage(str);
        androidx.appcompat.app.AlertDialog alertDialog=builder.create();
        alertDialog.show();
        alertDialog.getWindow().getWindowStyle();
    }

    public void noInternetDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this, R.style.noInternetDialog);
        builder.setMessage("Not connected to Internet");
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.CENTER;
        alertDialog.show();
    }

}
