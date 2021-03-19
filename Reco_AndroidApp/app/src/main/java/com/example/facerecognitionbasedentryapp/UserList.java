package com.example.facerecognitionbasedentryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserList  extends AppCompatActivity {
     UserListAdapter recyclerViewAdapter;
    RecyclerView Rview;

    /*
    private ArrayList<String> username;
    private ArrayList<String> userinfo;
    ArrayList<String> mUserId;
    private ArrayList<String> mPno;
    private String token;

     */

    ArrayList<String> username,userinfo,mUserId,mPno;
    private String token;
    ProgressBar spinner;
    LinearLayoutManager layoutManager;
    String mUrl = "https://frozen-wildwood-57358.herokuapp.com/api/face_recognition/listUsers";

    ImageButton back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        username = new ArrayList<>();
        userinfo = new ArrayList<>();
        mUserId = new ArrayList<>();
        mPno = new ArrayList<>();
        Rview = findViewById(R.id.user_list_recycler);

        back = findViewById(R.id.viewimageView2_user);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(UserList.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });



        recyclerViewAdapter = new UserListAdapter(getApplicationContext(), mUserId,username, userinfo,  mPno);
        Rview.setAdapter(recyclerViewAdapter);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        Rview.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(getApplicationContext(), layoutManager.getOrientation());
        Rview.addItemDecoration(divider);


        SharedPreferences preferences =getSharedPreferences("tokenFile", Context.MODE_PRIVATE);
        token = preferences.getString("key", "");
        Log.d("user list", "onCreateView: " + token);
        getData();
    }

    public void getData() {
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, mUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject rootObject = new JSONObject(String.valueOf(response));
                    //Toast.makeText(getApplicationContext(),rootObject.toString(),Toast.LENGTH_LONG).show();

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
                    /*
                    recyclerViewAdapter.notifyDataSetChanged();
                    //recyclerViewAdapter.show_suggestions(username);
                    spinner.setVisibility(View.GONE);

                     */

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "An exception occurred", Toast.LENGTH_LONG).show();
                    spinner.setVisibility(View.GONE);
                    Log.d("user_list", "onResponse: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spinner.setVisibility(View.GONE);
                if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Check Your Internt Connection Please!", Toast.LENGTH_SHORT).show();

                }
                /*
                else if (error instanceof AuthFailureError) {
                    // Error indicating that there was an Authentication Failure while performing the request
                    Toast.makeText(getActivity(), "This error is case2", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    Toast.makeText(getActivity(), "This error is server error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    Toast.makeText(getActivity(), "This error is case4", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    Toast.makeText(getActivity(), "This error is case5", Toast.LENGTH_LONG).show();
                }

                 */
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
        Rview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                /*
                int totalCount, pastItemCount, visibleItemCount;
                if (dy > 0) {
                    totalCount = layoutManager.getItemCount();
                    pastItemCount = layoutManager.findFirstVisibleItemPosition();
                    visibleItemCount = layoutManager.getChildCount();
                    if ((pastItemCount + visibleItemCount) >= totalCount) {
                        Log.d(TAG, "onScrolled: " + nextUrl);
                        if (!nextUrl.equals("null") && !isNextBusy)
                            getNextDdos();
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
            */
            }
        });
    }

}
