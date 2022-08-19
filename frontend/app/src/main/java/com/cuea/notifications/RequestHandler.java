package com.cuea.notifications;

//create a class to handle requests using java.neturl

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

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
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //set required headers
            httpURLConnection.setRequestProperty("Authorization","Bearer "+rqtoken); //set Authorization
            //set input stream
            httpURLConnection.setDoInput(true);
            //write to input stream
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //write
            String line="";
            while((line=bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }
            response = stringBuilder.toString();
            //close the connection
            httpURLConnection.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
    return response;
    }

    //function to send post request
    public String PostRequest(String link, HashMap <String,String> body , String rqtoken){
        String response="";
        //create  url object
        URL url;
        try{
            //create instance
            url = new URL(link);
            //create and open connection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //set post method
            httpURLConnection.setRequestMethod("POST");
            //set headers
            httpURLConnection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Authorization","Bearer "+rqtoken);
            //allow output streams
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            //stream
            OutputStream outputStream = httpURLConnection.getOutputStream();
            //convert hashmap to stream
            JSONObject json = new JSONObject(body);
            //write stream
            outputStream.write(json.toString().getBytes());
            outputStream.close();//close

            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line="";
            while((line=bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }
            response = stringBuilder.toString();
            //close connection
            //httpURLConnection.disconnect();

        }catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }
}
