package com.example.logprogram;

import static com.example.logprogram.Constant.c_unregister;
import static com.example.logprogram.Constant.CHINESE_FLAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class C_Resignation extends Activity{

	AlertDialog resignationDialog;
	private EditText mMail, mPassword;
	final String url = "http://nol.cs.nctu.edu.tw/~Sherry/resignation.php";	
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_resignation);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //initialize each reference
        mMail        	 = (EditText)findViewById(R.id.c_resignation_mail);
        mPassword    	 = (EditText)findViewById(R.id.c_resignation_pwd);
        //initialize alert dialog frame
        resignationDialog = getAlertDialog(c_unregister, "���P�b��", "�нT�{��T�O�_��g���T? ��Ƥ@�g���P���i�A�_��C");         
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
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	switch(action)
            	{
	            	case c_unregister:
	        	    	if(!mMail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()){
	        	    		/*
	        	    		 * (1)an alert-dialog frame jumps out, check if user ensure to send out the information 
	        	    		 * (2)send (mail,password) to remote server
	        	    		 * (3)check if the user does exist
	        	    		 * (4)unregister the user
	        	    		 * */
	        	    		
	        	    		String mail 		= mMail.getText().toString();
	        	    		String passwd 		= mPassword.getText().toString();
	        	    		//send out un-registration information
	        	    		Thread sendthread = new Thread(new Resignation(url, mail, passwd, C_Resignation.this, CHINESE_FLAG));
	        				sendthread.setDaemon(true);
	        				sendthread.start();
	        				sendthread.interrupt();
	        				//empty the text component
	        				mMail.setText(null);
	        				mPassword.setText(null);
	        				//back to login page
	        				Intent intent = new Intent(C_Resignation.this, C_Login.class);
	        				startActivity(intent);
	        	    	}else{
	        	    		Toast.makeText(getApplication(), "���P�b���e�A�Х��T�{�H�c�K�X�w��g����", Toast.LENGTH_LONG).show();
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
    
    public void unregisterButton(View view){
    	//show user information on the alert frame
		String mail 		= mMail.getText().toString();
		String passwd 		= mPassword.getText().toString();
    	
		//initialize alert dialog frame
		resignationDialog = getAlertDialog(c_unregister, 
        								"���P�b��", 
        								"�нT�{���P��T�O�_��g���T?\n" + "�H�c:" + mail + "\npassword:" + passwd); 			
    	resignationDialog.show();
    }    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }    
    
}
