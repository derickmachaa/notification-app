package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
            ///if student go to student menu
            if(usertype.equals("student")){
                Intent intent = new Intent(this,StudentActivity.class);
                startActivity(intent);
                finish(); //finish current activity
            }
            else if(usertype.equals("lecturer")){
                Intent intent = new Intent(this,LecActivity.class);
                startActivity(intent);
                finish();
            }else if(usertype.equals("admin")){
                Intent intent = new Intent(this,AdminActivity.class);
                startActivity(intent);
                finish();//
            }
        }else{
            //start sign in activity
            //start singup
            Intent intent = new Intent(this,SignupActivity.class);
            startActivity(intent);
            finish(); //finish mainactivity

        }

    }
}