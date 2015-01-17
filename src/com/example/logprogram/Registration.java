package com.example.logprogram;

import static com.example.logprogram.Constant.register_sucess;
import static com.example.logprogram.Constant.CHINESE_FLAG;
import static com.example.logprogram.Constant.ENGLISH_FLAG;
import static com.example.logprogram.Constant.c_registerSuccess;
import static com.example.logprogram.Constant.c_registerFailure;
import static com.example.logprogram.Constant.e_registerSuccess;
import static com.example.logprogram.Constant.e_registerFailure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class Registration implements Runnable {

    String url;
    String email;
    String password;
    String name;
    String vehicle_type;
    String mobile_type;
    String timeStamp;
    Context context;
    int language;
    
    //constructor
    public Registration(String url, String email, String password, String name, String vehicle_type, String mobile_type,String timeStamp, Context context, int language){
        this.url          = url;
        this.email  	  = email;
        this.password  	  = password;
        this.name 		  = name;
        this.vehicle_type = vehicle_type;
        this.mobile_type  = mobile_type;
        this.timeStamp 	  = timeStamp;
        this.context      = context;
        this.language     = language;
    }
    
    @Override
    public void run() {
        /* Create a new HttpClient and Post Header */
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost     = new HttpPost(url);  
        
        try {
            // Add data to the array list 
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("vehicle_type", vehicle_type));
            nameValuePairs.add(new BasicNameValuePair("mobile_type", mobile_type));
            nameValuePairs.add(new BasicNameValuePair("timestamp", timeStamp));
            
            //put the entity on the httpPost 
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));   
            
            // Execute HTTP Post Request
            HttpResponse httpResponse = httpClient.execute(httpPost);
            
            // Read the content stream
            String resultString = EntityUtils.toString(httpResponse.getEntity());
            
            /*check if the response code is 200 ok */
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            	Log.d("httppost", "registration success: " + resultString);
            	register_sucess = true;
            	Log.d("register flag", String.valueOf(register_sucess));
            }else{
            	Log.d("httppost", "registration fails: " + resultString);
            	register_sucess = false;
            	Log.d("register flag", String.valueOf(register_sucess));
            }
            
            //toast message for registration successful or failure
        	Looper.prepare();
        	switch(language)
        	{
        		case CHINESE_FLAG:
        			if(register_sucess)
        				Toast.makeText(context, c_registerSuccess, Toast.LENGTH_LONG).show();
        			else
        				Toast.makeText(context, c_registerFailure, Toast.LENGTH_LONG).show();
        			break;
        		case ENGLISH_FLAG:
        			if(register_sucess)
        				Toast.makeText(context, e_registerSuccess, Toast.LENGTH_LONG).show();
        			else
        				Toast.makeText(context, e_registerFailure, Toast.LENGTH_LONG).show();
        			break;
        		default:
        			break;
        	}
            Looper.loop();
        
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}