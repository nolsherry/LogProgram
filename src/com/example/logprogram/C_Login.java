package com.example.logprogram;

import static com.example.logprogram.Constant.CHINESE_FLAG;
import static com.example.logprogram.Constant.ENGLISH_FLAG;
import static com.example.logprogram.Constant.c_login;
import static com.example.logprogram.Constant.c_login_fail;

import java.util.ArrayList;
import java.util.List;

//import com.example.logprogram.SSLconnect;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;



public class C_Login<Identifiction> extends Activity {

	private CheckBox remLoginInfo;
	private EditText mMail, mPassword;
	int chinese = CHINESE_FLAG;
	int english = ENGLISH_FLAG;
	AlertDialog loginDialog;

    
    final String url = "http://nol.cs.nctu.edu.tw/~Sherry/registration.php";

	final String verifyUrl = "http://nol.cs.nctu.edu.tw/~Sherry/identification.php";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD)
		{
		    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		        .detectDiskReads()
		        .detectDiskWrites()
		        .detectNetwork()   // or .detectAll() for all detectable problems
		        .penaltyLog()
		        .build());
		}		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_login);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //initialize each reference
        remLoginInfo = (CheckBox)findViewById(R.id.c_check_logininfo);
        mMail        = (EditText)findViewById(R.id.c_login_mail);
        mPassword    = (EditText)findViewById(R.id.c_login_pwd);
        //SharedPreferencesr將mail和password儲存起來，每次進去時會直接讀取帳密資料，放入mMail和mPassword
        SharedPreferences remember = getPreferences(Context.MODE_PRIVATE);
        String mail   = remember.getString("email","");
        String passwd = remember.getString("passwd","");
        mMail.setText(mail);
        mPassword.setText(passwd);
       
        
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
            public void onClick(DialogInterface dialog, int which){
            	switch(action)
            	{
	            	case c_login:
	                	/*
	                	 * if(the id is registered)
	                	 * 		go to navigation page
	                	 * else
	                	 * 		go to registration page
	                 	 * */
	            		
	            		//check if the user does exist in the database
	                	String mail   = mMail.getText().toString();
	            		String passwd = mPassword.getText().toString(); 
	            		
	            		if(!mail.isEmpty() && !passwd.isEmpty()){
	            	
	                		// Building Parameters
	                        List<NameValuePair> Params = new ArrayList<NameValuePair>();
	                        Params.add(new BasicNameValuePair("email", mail));
	                        Params.add(new BasicNameValuePair("password", passwd));
	                        
	                        // getting account verification result
	                        String verifyRsult = LoginVerify.makeHttpRequest(verifyUrl, "POST", Params);     

	                        Log.d("login", verifyRsult);
	                        
	                        if(verifyRsult.equals("YES")){
	                        	
	                        	if(remLoginInfo.isChecked()){
	                        		SharedPreferences remdname=getPreferences(Context.MODE_PRIVATE);
	                        		SharedPreferences.Editor edit=remdname.edit();
	                        		edit.putString("email", mMail.getText().toString());
	                        		edit.putString("passwd", mPassword.getText().toString());
	                        		edit.commit();		
	                        	}
	                        	else
	                        	{
	                        	    SharedPreferences remdname=getPreferences(Context.MODE_PRIVATE);
	                        		SharedPreferences.Editor edit=remdname.edit();
	                        		edit.putString("email", "");
	                        		edit.putString("passwd", "");
	                        		edit.commit();
	                        		}
	                        	//enter network monitoring interface
			        	    	Intent intent = new Intent(C_Login.this, NetworkMonitoring.class);
			        	    	Bundle bundle = new Bundle();
			        	    	bundle.putInt("interface", chinese);
			        	    	intent.putExtras(bundle);
			        	    	startService(intent);	
			        	    	
	                        }else{
	            				Toast.makeText(getApplication(), c_login_fail, Toast.LENGTH_LONG).show();
	            			}	
	                        
	            		}else{
	            			Toast.makeText(C_Login.this, "登入前請確認已填寫信箱和密碼", Toast.LENGTH_LONG).show();
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
    
    public void facebookButton(View view){
    	
    }
    
    public void gmailButton(View view){
    	
    }    
	
    public void linkedinButton(View view){
    	
    }
    
    public void registerButton(View view){
    	startActivity(new Intent(this, C_Register.class));
    }
    
    public void enterNavigation(View view){
    	//show login information on the alert frame
    	String mail   = mMail.getText().toString();
		String passwd = mPassword.getText().toString();
		
    	//initialize alert dialog frame
        loginDialog   = getAlertDialog(c_login, "進入Bump Sensor", "請確認註冊資訊是否填寫正確?" + "\n信箱:" + mail + "\n密碼:" + passwd);    	
    	loginDialog.show();
    	
    }
	/*public boolean isValidEmailFormat()
	{
		
		EditText mMail = (EditText)findViewById(R.id.c_login_mail);	
	    if(mMail == null)
	    	return false;
		return android.util.Patterns.EMAIL_ADDRESS.matcher(mMail.getText().toString()).matches();
	}*/
   
   /* private CheckBox.OnCheckedChangeListener abc = 
        new CheckBox.OnCheckedChangeListener(){
    	  
    	public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
        {
    			
        }
    	
    };*/
}