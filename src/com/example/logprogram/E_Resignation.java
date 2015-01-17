package com.example.logprogram;

import static com.example.logprogram.Constant.ENGLISH_FLAG;
import static com.example.logprogram.Constant.e_unregister;
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


public class E_Resignation extends Activity{

	AlertDialog resignationDialog;
	private EditText mMail, mPassword;
	final String url = "http://localhost:8080/htdocs/resignation.php";	
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_resignation);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //initialize each reference
        mMail        	 = (EditText)findViewById(R.id.e_resignation_mail);
        mPassword    	 = (EditText)findViewById(R.id.e_resignation_pwd);
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
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	switch(action)
            	{
	            	case e_unregister:
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
	        	    		Thread sendthread = new Thread(new Resignation(url, mail, passwd, E_Resignation.this, ENGLISH_FLAG));
	        				sendthread.setDaemon(true);
	        				sendthread.start();
	        				sendthread.interrupt();
	        				//empty the text component
	        				mMail.setText(null);
	        				mPassword.setText(null);
	        				//back to login page
	        				Intent intent = new Intent(E_Resignation.this, E_Login.class);
	        				startActivity(intent);
	        	    	}else{
	        	    		Toast.makeText(getApplication(), "Please ensure you 've filled in email and password before resignation.", Toast.LENGTH_LONG).show();
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
		resignationDialog = getAlertDialog(e_unregister, 
        								"Account Resignation", 
        								"Please ensure you have filled in correct resignation information?\n" + "email:" + mail + "\npassword:" + passwd); 			
    	resignationDialog.show();
    }        
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }    
	
}
