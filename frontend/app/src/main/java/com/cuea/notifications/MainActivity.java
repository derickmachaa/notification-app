package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if the user is logged in or not
        SessionManager sessionManager = new SessionManager(this);
        if(sessionManager.checkLoggedin()){
            //if logged in to
        }else{
            //start sign in activity
            finish(); //finish mainactivity
            //start singup
            Intent intent = new Intent(this,SignupActivity.class);
            startActivity(intent);
        }

    }
}