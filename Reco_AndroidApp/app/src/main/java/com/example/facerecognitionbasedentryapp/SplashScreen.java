package com.example.facerecognitionbasedentryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences preferences = getSharedPreferences("tokenFile", Context.MODE_PRIVATE);
                String typeofuser = preferences.getString("TypeOfUser","");
                Intent i;
                if(typeofuser.isEmpty()){
                    i = new Intent(SplashScreen.this ,LoginActivity.class);
                }else if(typeofuser.equals("admin")) {
                    i = new Intent(SplashScreen.this, MainActivity.class);
                }else{
                    i = new Intent(SplashScreen.this, ViewRecords.class);
                }


                //Intent i = new Intent(getApplicationContext(),ClickImage.class);
                startActivity(i);
                finish();


            }
        }, 2000);
    }
}
