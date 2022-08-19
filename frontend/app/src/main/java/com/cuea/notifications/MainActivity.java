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
            //if logged in check usertype
            User user = sessionManager.getUser();
            String usertype= user.getUsertype();
            //launch respective menu
            if(usertype=="student"){
                //launch student activity
                finish();
                Intent intent = new Intent(this,StudentActivity.class);
                startActivity(intent);
            }else if(usertype=="lecturer"){
                //launch lect activity
            };
        }else{
            //start sign in activity
            finish(); //finish mainactivity
            //start singup
            Intent intent = new Intent(this,SignupActivity.class);
            startActivity(intent);
        }

    }
}