package com.cuea.notifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

//This class is the session manager to handle user sessions using shared preferences
public class SessionManager {
    private final Context mycontext;
    private User user;
    private static final String mypreference = "sessions";
    private static final String fname="FirstName";
    private static final String lname="LastName";
    private static final String token="Token";
    private static final String usrtyp="UserType";
    private static final String islec="IsLec";

    //constructor class
    public SessionManager(Context context){
    this.mycontext = context;
    }

    //function to store the user session
    public void storeSession(User user){
        //create preference instance with private mode access
        SharedPreferences sharedPreferences = mycontext.getSharedPreferences(mypreference,mycontext.MODE_PRIVATE);
        //create preference editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //put users
        editor.putString(fname, user.getFirstname());
        editor.putString(lname, user.getLastname());
        editor.putString(usrtyp,user.getUsertype());
        editor.putString(token,user.getToken());
        editor.putBoolean(islec,user.getIs_lec());
        //finally store them
        editor.commit();

    }
    //function to get the stored session
    public User getUser(){
        //String usertype = "";
        //create preference instance
        SharedPreferences sharedPreferences = mycontext.getSharedPreferences(mypreference,mycontext.MODE_PRIVATE);
        //getsessions
        String firstname = sharedPreferences.getString(fname,null);
        String lastname = sharedPreferences.getString(lname,null);
        String sessionkey = sharedPreferences.getString(token,null);
        String usertype = sharedPreferences.getString(usrtyp,null);
        Boolean is_lec = sharedPreferences.getBoolean(islec,false);

        //return a user object
        return new User(firstname,lastname,usertype,sessionkey,is_lec);

    }

    //function to check if user is logged in
    public boolean checkLoggedin(){
        SharedPreferences sharedPreferences = mycontext.getSharedPreferences(mypreference,mycontext.MODE_PRIVATE);
        //check if token in storage
        if((sharedPreferences.getString(token,null))!=null){
            return true;
        }else{
            return false;
        }
    }

    public void logout(){
        //this function will be used by all users to logout
        //build a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mycontext);
        builder.setMessage("Do you really want to logout?");
        //add a positive action
        builder.setPositiveButton(R.string.Accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //preference instance
                SharedPreferences sharedPreferences = mycontext.getSharedPreferences(mypreference,mycontext.MODE_PRIVATE);
                //create preference editor
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //clear the storage ares
                editor.clear();
                //commit
                editor.commit();
                //say good bye
                Toast.makeText(mycontext, "Good Bye", Toast.LENGTH_LONG).show();
                //redirect to main activity
                Intent intent = new Intent(mycontext,MainActivity.class);
                mycontext.startActivity(intent);
                //finish current activity
                ((Activity)mycontext).finish();

            }
        });

        //add a negative action
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        //show the dialog
        builder.show();
    }

}
