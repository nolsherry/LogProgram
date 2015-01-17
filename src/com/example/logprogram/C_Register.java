package com.example.logprogram;

import static com.example.logprogram.Constant.c_register;
import static com.example.logprogram.Constant.CHINESE_FLAG;

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





public class C_Register extends Activity {

	private EditText c_mail;
	private EditText c_passwd;
	private EditText c_name;
	private EditText c_vehicle_type;
	private EditText c_mobile_type;
	final String url = "http://nol.cs.nctu.edu.tw/~Sherry/registration.php";
	AlertDialog registerDialog;
	AlertDialog checkDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_register);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //component initialization
        c_mail 		   = (EditText)findViewById(R.id.c_mailinput);
        c_passwd 	   = (EditText)findViewById(R.id.c_pwdinput);
        c_name 		   = (EditText)findViewById(R.id.c_login_name);
        c_vehicle_type = (EditText)findViewById(R.id.c_vehicle_type);
        c_mobile_type  = (EditText)findViewById(R.id.c_mobile_type);
	}	
	
	   private AlertDialog getAlertDialog(final int action, String title, String message){
	        //���ͤ@��Builder����
	        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        //�]�wDialog�����D
	        builder.setTitle(title);
	        //�]�wDialog�����e
	        builder.setMessage(message);
	        //�]�wPositive���s���
	        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            @SuppressWarnings({ "deprecation" })
				@Override
	            public void onClick(DialogInterface dialog, int which) {
	            	switch(action)
	            	{
		            	case c_register:
		            		/*
		            		 * (1)send registration information to remote server
		            		 * (2)back to login page
		            		 * */
		            		String mail 		= c_mail.getText().toString();
		            		String passwd 		= c_passwd.getText().toString();
		            		String name 		= c_name.getText().toString();
		            		String vehicle_type = c_vehicle_type.getText().toString();
		            		String mobile_type 	= c_mobile_type.getText().toString();
		            		//get timestamp
		            		Timestamp timeTag = new Timestamp(System.currentTimeMillis()); 
		            	
							String timestamp = (timeTag.getYear()+1900) + "-" + 
		            						   (timeTag.getMonth()+1)   + "-" + 
		            						    timeTag.getDate()       + " " + 
		            						    timeTag.getHours()      + ":" + 
		            						    timeTag.getMinutes()    + ":" + 
		            						    timeTag.getSeconds();
		            		
		            		if(!mail.isEmpty() && !passwd.isEmpty() && !name.isEmpty() && !vehicle_type.isEmpty() && !mobile_type.isEmpty()){
		            			
		            			Thread sendthread = new Thread(new Registration(url, mail, passwd, name, vehicle_type, mobile_type, timestamp, C_Register.this, CHINESE_FLAG));
		            			sendthread.setDaemon(true);
		            			sendthread.start();
		            			sendthread.interrupt();
			
		            			//make columns empty
		            			c_mail.setText(null);
		            			c_passwd.setText(null);
		            			c_name.setText(null);
		            			c_vehicle_type.setText(null);
		            			c_mobile_type.setText(null);
		            			
		            			//back to login page
		            			startActivity(new Intent(getApplication(), C_Login.class));	
		            		}else{
		            			Toast.makeText(getApplication(), "�аȥ���J�����T", Toast.LENGTH_SHORT).show();
		            		}       			
		        			break;
		        		default:
		        			break;
	            	}
	            }
	        });
	        //�]�wNegative���s���
	        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {

	            }
	        });
	        //�Q��Builder����إ�AlertDialog
	        return builder.create();
	    }		
	
	/*public boolean validEmailString(String mail){
			  Pattern pattern = Patterns.EMAIL_ADDRESS;	
			  return pattern.matcher(mail).matches();	
				
			}*/
	public void submitButton(View view){
		
		//show user information on the alert frame
		String mail 		= c_mail.getText().toString();
		String passwd 		= c_passwd.getText().toString();
		String name 		= c_name.getText().toString();
		String vehicle_type = c_vehicle_type.getText().toString();
		String mobile_type 	= c_mobile_type.getText().toString();
		//check the email format
		if (Linkify.addLinks(c_mail.getText(), Linkify.EMAIL_ADDRESSES))
	    {  
		
		
			//initialize alert dialog frame
	    	registerDialog = getAlertDialog(c_register, 
        								    "���U�b��", 
        								    "�нT�{�ӤH��T�O�_��g���T?\n" + "�H�c:" + mail + "\npassword:" + passwd + "\n�m�W:" + name + "\n����:" + vehicle_type + "\n�������:" + mobile_type); 		
		                                    registerDialog.show();
		                                    
	    }else{
	    	
	    	Toast.makeText(getApplication(), "�аȥ���J���㪺�H�c", Toast.LENGTH_SHORT).show();
	    	
	    	
	    }
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
