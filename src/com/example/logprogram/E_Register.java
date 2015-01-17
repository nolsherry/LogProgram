package com.example.logprogram;

import static com.example.logprogram.Constant.e_register;
import static com.example.logprogram.Constant.ENGLISH_FLAG;
import java.sql.Timestamp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class E_Register extends Activity {

	private EditText e_mail;
	private EditText e_passwd;
	private EditText e_name;
	private EditText e_vehicle_type;
	private EditText e_mobile_type;
	final String url = "http://localhost:8080/htdocs/registration.php";	
	AlertDialog registerDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e_register);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //component initialization
        e_mail 		   = (EditText)findViewById(R.id.e_mailinput);
        e_passwd 	   = (EditText)findViewById(R.id.e_pwdinput);
        e_name 		   = (EditText)findViewById(R.id.e_login_name);
        e_vehicle_type = (EditText)findViewById(R.id.e_vehicle_type);
        e_mobile_type  = (EditText)findViewById(R.id.e_mobile_type); 
	}

	   private AlertDialog getAlertDialog(final int action, String title, String message){
	        //產生一個Builder物件
	        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        //設定Dialog的標題
	        builder.setTitle(title);
	        //設定Dialog的內容
	        builder.setMessage(message);
	        //設定Positive按鈕資料
	        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            @SuppressWarnings("deprecation")
				@Override
	            public void onClick(DialogInterface dialog, int which) {
	            	switch(action)
	            	{
		            	case e_register:
		            		String mail 		= e_mail.getText().toString();
		            		String passwd 		= e_passwd.getText().toString();
		            		String name 		= e_name.getText().toString();
		            		String vehicle_type = e_vehicle_type.getText().toString();
		            		String mobile_type 	= e_mobile_type.getText().toString();
		            		//get timestamp
		            		Timestamp timeTag = new Timestamp(System.currentTimeMillis()); 
		            		String timestamp = (timeTag.getYear()+1900) + "-" + 
		            						   (timeTag.getMonth()+1)   + "-" + 
		            						    timeTag.getDate()       + " " + 
		            						    timeTag.getHours()      + ":" + 
		            						    timeTag.getMinutes()    + ":" + 
		            						    timeTag.getSeconds();
		            		
		            		if(!mail.isEmpty() && !passwd.isEmpty() && !name.isEmpty() && !vehicle_type.isEmpty() && !mobile_type.isEmpty()){
		            			Thread sendthread = new Thread(new Registration(url, mail, passwd, name, vehicle_type, mobile_type, timestamp, E_Register.this, ENGLISH_FLAG));
		            			sendthread.setDaemon(true);
		            			sendthread.start();
		            			sendthread.interrupt();
		            			
		            			//make columns empty 
		            			e_mail.setText(null);
		            			e_passwd.setText(null);
		            			e_name.setText(null);
		            			e_vehicle_type.setText(null);
		            			e_mobile_type.setText(null);
		            			
		            			//back to login page
		            			startActivity(new Intent(getApplication(), E_Login.class));	
		            		}else{
		            			Toast.makeText(getApplication(), "Please fill in detailed personal information", Toast.LENGTH_SHORT).show();
		            		}	
		        			break;
		        		default:
		        			break;
	            	}
	            }
	        });
	        //設定Negative按鈕資料
	        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {

	            }
	        });
	        //利用Builder物件建立AlertDialog
	        return builder.create();
	    }			

	public void submitButton(View view){
		
		//show user information on the aler frame
		String mail 		= e_mail.getText().toString();
		String passwd 		= e_passwd.getText().toString();
		String name 		= e_name.getText().toString();
		String vehicle_type = e_vehicle_type.getText().toString();
		String mobile_type 	= e_mobile_type.getText().toString();
		
		if (Linkify.addLinks(e_mail.getText(), Linkify.EMAIL_ADDRESSES))
	    {  
			//initialize alert dialog frame
			registerDialog = getAlertDialog(e_register, 
        								    "Apply Account", 
        								    "Please ensure you have filled in correct persoal information?\n" + "email: " + mail + "\npassword: " + passwd + "\nname: " + name + "\nvehicle model: " + vehicle_type + "\nphone model: " + mobile_type); 
			registerDialog.show();
	   
	    }else{
	    	
	    	Toast.makeText(getApplication(), "Please fill in entire email account. ", Toast.LENGTH_SHORT).show();
	    }
		/*
		 * (1)send registration information to remote server
		 * (2)back to login page
		 * */
		/*
		String mail 		= e_mail.getText().toString();
		String passwd 		= e_passwd.getText().toString();
		String name 		= e_name.getText().toString();
		String vehicle_type = e_vehicle_type.getText().toString();
		String mobile_type 	= e_mobile_type.getText().toString();
		//get timestamp
		Timestamp timeTag = new Timestamp(System.currentTimeMillis()); 
		String timestamp = (timeTag.getYear()+1900) + "-" + 
						   (timeTag.getMonth()+1)   + "-" + 
						    timeTag.getDate()       + " " + 
						    timeTag.getHours()      + ":" + 
						    timeTag.getMinutes()    + ":" + 
						    timeTag.getSeconds();
		
		if(!mail.isEmpty() && !passwd.isEmpty() && !name.isEmpty() && !vehicle_type.isEmpty() && !mobile_type.isEmpty()){
			Thread sendthread = new Thread(new Registration(url, mail, passwd, name, vehicle_type, mobile_type, timestamp));
			sendthread.setDaemon(true);
			sendthread.start();
			sendthread.interrupt();
			
			//make columns empty 
			e_mail.setText(null);
			e_passwd.setText(null);
			e_name.setText(null);
			e_vehicle_type.setText(null);
			e_mobile_type.setText(null);
			
			//back to login page
			startActivity(new Intent(this, E_Login.class));	
		}else{
			Toast.makeText(this, "Please fill in detailed personal information", Toast.LENGTH_SHORT).show();
		}	
		*/
	}	
	
    //destroy the activity
    @Override
	protected void onDestroy(){
        super.onDestroy();
    }
    
    @Override
	protected void onStart(){
    	super.onStart();
    }
    
    @Override
	protected void onResume(){
    	super.onResume();
    }
    
    @Override
	protected void onPause(){
    	super.onPause();
    }
    
    @Override
	protected void onStop(){
    	super.onStop();
    }		
	
}
