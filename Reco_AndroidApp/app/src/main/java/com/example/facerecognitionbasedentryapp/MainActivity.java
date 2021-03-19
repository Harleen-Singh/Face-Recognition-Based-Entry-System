package com.example.facerecognitionbasedentryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button registerNewUser, viewDetails;
    TextView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerNewUser = findViewById(R.id.register_new_user);
        viewDetails = findViewById(R.id.view_details);
        logout = findViewById(R.id.logout);

        registerNewUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this,RegisterUser.class);
                startActivity(i);
            }
        });

        viewDetails.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Intent i = new Intent(MainActivity.this,UserList.class);
                Intent i = new Intent(MainActivity.this,UserList.class);
                startActivity(i);
            }
        });

        //function for logout
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
                ds.setColor(getResources().getColor(R.color.my_purple));
                //ds.setColor(getColor(R.color.my_purple));
                ds.setUnderlineText(false);
            }
        };
        ss2.setSpan(clickableSpan2,0,6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        logout.setText(ss2);
        logout.setMovementMethod(LinkMovementMethod.getInstance());

    }
}