package com.example.logprogram;

import static com.example.logprogram.Constant.CHINESE_FLAG;
import static com.example.logprogram.Constant.ENGLISH_FLAG;
import static com.example.logprogram.Constant.c_resignationFailure;
import static com.example.logprogram.Constant.c_resignationSuccess;
import static com.example.logprogram.Constant.e_resignationFailure;
import static com.example.logprogram.Constant.e_resignationSuccess;
import static com.example.logprogram.Constant.resignation_sucess;

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

public class Resignation implements Runnable {

    String url;
    String email;
    String password;
    Context context;
    int language;
    
    //constructor
    public Resignation(String url, String email, String password, Context context, int language){
        this.url          = url;
        this.email  	  = email;
        this.password  	  = password;
        this.context      = context;
        this.language     = language;
    }    
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
        /* Create a new HttpClient and Post Header */
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost     = new HttpPost(url);

        try {
            // Add data to the array list 
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            //put the entity on the httpPost 
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));            
            // Execute HTTP Post Request
            HttpResponse httpResponse = httpClient.execute(httpPost);
            // Read the content stream
            String resultString = EntityUtils.toString(httpResponse.getEntity());
            
            /*check if the response code is 200 ok */
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            	Log.d("httppost", "resignation success: " + resultString);
            	resignation_sucess = true;
            }else{
            	Log.d("httppost", "resignation failure: " + resultString);
            	resignation_sucess = false;
            }
            
          //toast message for resignation successful or failure
        	Looper.prepare();
        	switch(language)
        	{
        		case CHINESE_FLAG:
        			if(resignation_sucess)
        				Toast.makeText(context, c_resignationSuccess, Toast.LENGTH_LONG).show();
        			else
        				Toast.makeText(context, c_resignationFailure, Toast.LENGTH_LONG).show();
        			break;
        		case ENGLISH_FLAG:
        			if(resignation_sucess)
        				Toast.makeText(context, e_resignationSuccess, Toast.LENGTH_LONG).show();
        			else
        				Toast.makeText(context, e_resignationFailure, Toast.LENGTH_LONG).show();
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
