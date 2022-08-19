package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class VerifyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        //get the firstname from intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("FirstName");
        //place it on screen

    }
}