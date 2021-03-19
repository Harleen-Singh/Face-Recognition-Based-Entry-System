package com.example.facerecognitionbasedentryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewRecords extends AppCompatActivity {
    ImageButton back;
    String token;
    private ArrayList<String> entry;
    ViewRecordAdapter recyclerViewAdapter;
    RecyclerView Rview;
    LinearLayoutManager layoutManager;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_records);

        entry = new ArrayList<>();
        Rview = findViewById(R.id.entry_recyclerview);
        back = findViewById(R.id.viewimageView2);
        TextView logout = findViewById(R.id.logout_user);

        Intent i= getIntent();
        id= i.getStringExtra("id");
        Toast.makeText(getApplicationContext(),"got id "+id,Toast.LENGTH_LONG).show();



        SharedPreferences preferences =getSharedPreferences("tokenFile", Context.MODE_PRIVATE);

        String userType = preferences.getString("TypeOfUser", "");
        //Toast.makeText(getApplicationContext(),"user is "+userType,Toast.LENGTH_LONG).show();


        if(!userType.equals("admin")) {
            back.setVisibility(View.GONE);
            logout.setVisibility(View.VISIBLE);
        }
        else{
            back.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
        }


        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    Intent i = new Intent(ViewRecords.this, UserList.class);
                    startActivity(i);
                    finish();
            }
        });


        String text_forgot = "Logout";
        SpannableString ss2 = new SpannableString(text_forgot);
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //
                //
                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("tokenFile", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                //Toast.makeText(getApplicationContext(),"Ypu clcik",Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
                finish();


            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                //ds.setColor(getResources(R.color.my_purple));
                ds.setColor(getResources().getColor(R.color.my_purple));
                ds.setUnderlineText(false);
            }
        };
        ss2.setSpan(clickableSpan2,0,6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        logout.setText(ss2);
        logout.setMovementMethod(LinkMovementMethod.getInstance());





        recyclerViewAdapter = new ViewRecordAdapter(getApplicationContext(),entry);
        Rview.setAdapter(recyclerViewAdapter);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        Rview.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(getApplicationContext(), layoutManager.getOrientation());
        Rview.addItemDecoration(divider);


        token = preferences.getString("key", "");
        Log.d("user list", "onCreateView: " + token);




        //getData(mUrl);
        //postdata("https://frozen-wildwood-57358.herokuapp.com/api/face_recognition/viewEntries/");//works for normal user

        postadmin("https://frozen-wildwood-57358.herokuapp.com/api/face_recognition/viewEntries/",id);

    }


    public void postdata(String mUrl){
        final RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        JSONObject postparams = null;

        postparams = new JSONObject();
        Log.d("post_data","Send data1");
            /*
            postparams.put("email", email);
            postparams.put("password", password);

             */
        //Toast.makeText(getApplicationContext(), "You eneterd login with " + email, Toast.LENGTH_LONG).show();

        //Toast.makeText(getApplicationContext(),"sending to url: "+LoginUrl,Toast.LENGTH_LONG).show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(mUrl, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(getApplicationContext(), "response is " + response.toString(), Toast.LENGTH_LONG).show();
                            Log.d("post_data","Send data2");
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));

                            JSONArray data = jsonObject.getJSONArray("Data");
                            if(data.length()== 0){
                                Log.d("user_list", "onResponse: length of array is 0");
                                //recyclerViewAdater.mShowShimmer = false;
                                Toast.makeText(getApplicationContext(),"List is empty",Toast.LENGTH_LONG).show();
                               recyclerViewAdapter.notifyDataSetChanged();
                            }


                            for(int i=0;i<data.length();i++){
                                JSONObject entryobject= data.getJSONObject(i);

                                try {
                                    String value = entryobject.getString("entryTime");
                                    //Toast.makeText(getApplicationContext(),value,Toast.LENGTH_LONG).show();
                                    entry.add(value);
                                    //entry.add(entryobject.getString("entryTime"));
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(),"Exception occured",Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }

                            }
                            Log.d("user list", "onResponse: " );
                            //recyclerViewAdater.mShowShimmer = false;
                            recyclerViewAdapter.notifyDataSetChanged();
                            //Toast.makeText(getApplicationContext(),"Notified",Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Exception occured while making post request", Toast.LENGTH_LONG).show();
                            //dialog.dismiss();
                            Log.d("Login", "onResponse: error in post catch block: " + e);
                        }
                        //recyclerViewAdapter.notifyDataSetChanged();

                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "error is " + error.getMessage(), Toast.LENGTH_LONG).show();
                        if (error instanceof NoConnectionError || error instanceof TimeoutError){
                            Toast.makeText(getApplicationContext(), "Check your internet connection!", Toast.LENGTH_LONG).show();
                            //displayDialog("Check your internet connection!");
                        }
                        else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
                            //displayDialog("Invalid Username or password");
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
                            //displayDialog("An unknown error occurred.");
                            Toast.makeText(getApplicationContext(), "An unknown error occurred.", Toast.LENGTH_SHORT).show();
                        }
                        //dialog.dismiss();
                    }
                })

        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> map = new HashMap<>();
            map.put("Authorization", "Token " + token);
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

    public void postadmin(String mUrl, String mId){
        //Toast.makeText(getApplicationContext(),"got id in function "+mId,Toast.LENGTH_LONG).show();
            final RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            JSONObject postparams = null;
            try {
                postparams = new JSONObject();
                Log.d("post_data", "Send data1");

                postparams.put("userId", mId);
                //Toast.makeText(getApplicationContext(),"posting values "+postparams.toString(),Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception occured while posting ", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        //

            //Toast.makeText(getApplicationContext(),"sending to url: "+LoginUrl,Toast.LENGTH_LONG).show();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(mUrl, postparams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //Toast.makeText(getApplicationContext(), "response is " + response.toString(), Toast.LENGTH_LONG).show();
                                Log.d("post_data","Send data2");
                                JSONObject jsonObject = new JSONObject(String.valueOf(response));
                                JSONArray data = jsonObject.getJSONArray("Data");
                                if(data.length()== 0){
                                    Log.d("user_list", "onResponse: length of array is 0");
                                    //recyclerViewAdater.mShowShimmer = false;
                                    Toast.makeText(getApplicationContext(),"List is empty",Toast.LENGTH_LONG).show();
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }


                                for(int i=0;i<data.length();i++){
                                    JSONObject entryobject= data.getJSONObject(i);

                                    try {
                                        String value = entryobject.getString("entryTime");
                                        //Toast.makeText(getApplicationContext(),value,Toast.LENGTH_LONG).show();
                                        entry.add(value);
                                        //entry.add(entryobject.getString("entryTime"));
                                    } catch (JSONException e) {
                                        Toast.makeText(getApplicationContext(),"Exception occured",Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }

                                }
                                Log.d("user list", "onResponse: " );
                                //recyclerViewAdater.mShowShimmer = false;
                                recyclerViewAdapter.notifyDataSetChanged();


                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Exception occured while making post request", Toast.LENGTH_LONG).show();
                                //dialog.dismiss();
                                Log.d("Login", "onResponse: error in post catch block: " + e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "error is " + error.getMessage(), Toast.LENGTH_LONG).show();
                            if (error instanceof NoConnectionError || error instanceof TimeoutError){
                                Toast.makeText(getApplicationContext(), "Check your internet connection!", Toast.LENGTH_LONG).show();
                                //displayDialog("Check your internet connection!");
                            }
                            else if (error instanceof ServerError) {
                                Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
                                //displayDialog("Invalid Username or password");
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
                                //displayDialog("An unknown error occurred.");
                                Toast.makeText(getApplicationContext(), "An unknown error occurred.", Toast.LENGTH_SHORT).show();
                            }
                            //dialog.dismiss();
                        }
                    })

            {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Authorization", "Token " + token);
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

    public void getData(String mUrl) {
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, mUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject rootObject = new JSONObject(String.valueOf(response));
                    Toast.makeText(getApplicationContext(),rootObject.toString(),Toast.LENGTH_LONG).show();

                    /*
                    Log.d("user list", "onResponse: " + username);

                    JSONArray resultsArray = rootObject.getJSONArray("Data");
                    if(resultsArray.length()== 0){
                        Log.d("user_list", "onResponse: length of array is 0");
                        //recyclerViewAdater.mShowShimmer = false;
                        Toast.makeText(getApplicationContext(),"List is empty",Toast.LENGTH_LONG).show();
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject singleObject = resultsArray.getJSONObject(i);
                        mUserId.add(singleObject.getString("userId").toUpperCase());
                        try {
                            username.add(singleObject.getString("userName").toUpperCase());
                        } catch (JSONException e) {
                            username.add("--");
                            e.printStackTrace();
                        }
                        try {
                            mPno.add(singleObject.getString("phoneNo").toUpperCase());
                        } catch (JSONException e) {
                            mPno.add("--");
                            e.printStackTrace();
                        }
                        try {
                            userinfo.add(singleObject.getString("userEmail"));
                        } catch (JSONException e) {
                            userinfo.add("--");
                            e.printStackTrace();
                        }

                    }
                    Log.d("user list", "onResponse: " + username);
                    //recyclerViewAdater.mShowShimmer = false;
                    recyclerViewAdapter.notifyDataSetChanged();
                    //recyclerViewAdapter.show_suggestions(username);
                    //spinner.setVisibility(View.GONE);

                    //recyclerViewAdater.mShowShimmer = false;

                    recyclerViewAdapter.notifyDataSetChanged();
                    //recyclerViewAdapter.show_suggestions(username);
                    spinner.setVisibility(View.GONE);

                     */

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "An exception occurred", Toast.LENGTH_LONG).show();
                    //spinner.setVisibility(View.GONE);
                    Log.d("user_list", "onResponse: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //spinner.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Error is "+error.getMessage(),Toast.LENGTH_LONG).show();
                if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Check Your Internt Connection Please!", Toast.LENGTH_SHORT).show();

                }
                else if (error instanceof AuthFailureError) {
                    // Error indicating that there was an Authentication Failure while performing the request
                    Toast.makeText(getApplicationContext(), "This error is case2", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    Toast.makeText(getApplicationContext(), "This error is server error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    Toast.makeText(getApplicationContext(), "This error is case4", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    Toast.makeText(getApplicationContext(), "This error is case5", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.e("user list", "onErrorResponse: " + error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Token " + token);
                return map;
            }
        };

        requestQueue.add(jsonObjectRequest);
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
}
