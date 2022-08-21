package com.cuea.notifications;

//create a class to handle requests using java.neturl

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
            //write to input stream if sucess
            InputStream inputStream = httpURLConnection.getInputStream();
            if(httpURLConnection.getResponseCode()==200){
                response = InputstreamToString(inputStream);
            }
            else if(httpURLConnection.getResponseCode()==400){
                response="notfound";
            }
            else if(httpURLConnection.getResponseCode()==403){
                response="unauthorized";
            }
            //close the connection
            httpURLConnection.disconnect();
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
            //write stream
            outputStream.write(body.toString().getBytes());
            outputStream.close();//close


            //check response code
            if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                //if ok continue
                InputStream is = httpURLConnection.getInputStream();
                //convert to string
                response = InputstreamToString(is);
            }
            else if(httpURLConnection.getResponseCode()==403){
                response="unauthorized";

            }
            else if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_NO_CONTENT){
                response="notfound";
            }
            //close connection
            httpURLConnection.disconnect();


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
