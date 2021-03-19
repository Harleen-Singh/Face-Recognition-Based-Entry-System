package com.example.facerecognitionbasedentryapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
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

public class RegisterUser extends AppCompatActivity {
    ImageButton back;
    TextView name, email, password;
    Button register;
    String registerUser = "https://frozen-wildwood-57358.herokuapp.com/api/face_recognition/register/";
    String mytoken;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user);

        final SharedPreferences sp = getSharedPreferences("tokenFile", Context.MODE_PRIVATE);
        if (sp.contains("key")) {
            mytoken = sp.getString("key", "");
        }

        back = findViewById(R.id.imageView2);
        register = findViewById(R.id.register_user);
        name = findViewById(R.id.enter1);
        email = findViewById(R.id.enter2);
        password = findViewById(R.id.enter3);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterUser.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName = name.getText().toString().trim();
                String mEmail = email.getText().toString().trim();
                String mPass = password.getText().toString().trim();

                if (!mEmail.isEmpty() && !mPass.isEmpty() && !mName.isEmpty()) {
                    Register(mName, mEmail, mPass);
                } else {
                    displayDialog("Please enter complete details");
                }
            }
        });

    }


    public void Register(String name, String email, String password) {
        dialog = new ProgressDialog(RegisterUser.this, R.style.AlertDialog);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait....");
        dialog.show();

        final RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        JSONObject postparams = null;
        try {
            postparams = new JSONObject();
            postparams.put("name", name);
            postparams.put("email", email);
            postparams.put("password", password);
            postparams.put("phoneNo","1234567890");
            //Toast.makeText(getApplicationContext(), "You eneterd login with " + email, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Log.d("Login", "Login: Error:" + e);
            Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            //onClickLogin.setEnabled(true);
        }

        //Toast.makeText(getApplicationContext(), "sending data" + postparams.toString(), Toast.LENGTH_LONG).show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(registerUser, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(getApplicationContext(), "response is " + response.toString(), Toast.LENGTH_LONG).show();
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));

                            JSONObject data = jsonObject.getJSONObject("Data");
                            String userId = data.get("userId").toString();
                            dialog.dismiss();
                            displayDialog("Successfully Registered with id "+userId);


                            Intent i = new Intent(RegisterUser.this,ImageSubmit.class);
                            i.putExtra("userid",userId);
                            startActivity(i);



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
                        if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                            //Toast.makeText(getApplicationContext(), "Check your internet connection!", Toast.LENGTH_LONG).show();
                            displayDialog("Check your internet connection!");
                        } else if (error instanceof ServerError) {
                            displayDialog("Invalid input...cannot register");
                        }
                        else if (error instanceof AuthFailureError) {
                            // Error indicating that there was an Authentication Failure while performing the request
                            Toast.makeText(getApplicationContext(), "This error is case2", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                            Toast.makeText(getApplicationContext(), "This error is case3", Toast.LENGTH_LONG).show();
                        }
                        /*else if (error instanceof NetworkError) {
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
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                //map.put("Authorization", "Token " + "4a4ae26d6df615268d641f8efc7dd27dd27f2c308b29");
                // map.put("Authorization", "Token " + "dbf8a13129cc5369437f1631939c19b45cdbcbb5");
                map.put("Authorization", "Token " + mytoken);
                return map;
            }
        };
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

    private void displayDialog(String str) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(RegisterUser.this, R.style.AlertDialog);
        builder.setMessage(str);
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().getWindowStyle();
    }


}
