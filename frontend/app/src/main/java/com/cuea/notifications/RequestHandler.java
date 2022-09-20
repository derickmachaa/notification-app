package com.cuea.notifications;

//create a class to handle requests using java.neturl

import android.content.Context;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RequestHandler {


    //function to do get requests and return response
    public String GetRequest(String link,String rqtoken){
        //create string to hold response
        String response="";
        //net.url
        URL url;
        //create an instance of url...... catch exceptions
        try{
            url = new URL(link);
            //connection instance
            HttpsURLConnection Connection = (HttpsURLConnection) url.openConnection();
            //set required headers
            Connection.setRequestProperty("Authorization","Bearer "+rqtoken); //set Authorization
            //set input stream
            Connection.setDoInput(true);
            if(Connection.getResponseCode()>=100 && Connection.getResponseCode()<=399){
                //write to input stream if sucess
                InputStream inputStream = Connection.getInputStream();
                response = InputstreamToString(inputStream);
            }
            else{
                response = InputstreamToString(Connection.getErrorStream());
            }
            //close the connection
            Connection.disconnect();
        }catch (Exception e){
            response="Error";
            e.printStackTrace();
        }
    return response;
    }

    //function recieve json object and send it via post request
    public String PostRequest(String link,JSONObject body , String rqtoken){
        String response="";
        //create  url object
        URL url;
        try{
            //create instance
            url = new URL(link);
            //create and open connection
            HttpsURLConnection Connection = (HttpsURLConnection) url.openConnection();
            //set post method
            Connection.setRequestMethod("POST");
            //set headers
            Connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
            Connection.setRequestProperty("Authorization","Bearer "+rqtoken);
            //allow output streams
            Connection.setDoInput(true);
            Connection.setDoOutput(true);
            //stream
            OutputStream outputStream = Connection.getOutputStream();
            //write stream
            outputStream.write(body.toString().getBytes());
            outputStream.close();//close


            //check response code
            if(Connection.getResponseCode()>=100 && Connection.getResponseCode()<=399){
                //if ok continue
                InputStream is = Connection.getInputStream();
                //convert to string
                response = InputstreamToString(is);
            }
            else {
                response=InputstreamToString(Connection.getErrorStream());
            }
            //close connection
            Connection.disconnect();

        }catch(Exception e){
            e.printStackTrace();
            return "Error";
        }
        return response;
    }

    //function to convert input stream to string
    public String InputstreamToString(InputStream input){
        String string="";
        //create an instance
        InputStreamReader inputStreamReader = new InputStreamReader(input);
        //buffered
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        //String builder
        StringBuilder sb = new StringBuilder();
        try{
            while((string=bufferedReader.readLine())!=null){
                sb.append(string);
            }
        }catch (java.io.IOException e){//handle java.io.IOException
            e.printStackTrace();
        }
        return sb.toString();
    }
}
