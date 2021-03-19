package com.example.facerecognitionbasedentryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TypeOfUser extends AppCompatActivity {
    Button NormalUser,Admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_of_user);

        NormalUser=findViewById(R.id.login_as_user);
        Admin = findViewById(R.id.login_as_admin);

        NormalUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(TypeOfUser.this,LoginActivity.class);
                i.putExtra("message_user","normal_user");
                startActivity(i);
                finish();
            }
        });

        Admin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(TypeOfUser.this,LoginActivity.class);
                i.putExtra("message_user","admin_user");
                startActivity(i);
                finish();
            }
        });

    }
}