package com.example.logprogram;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class C_Artificial_Return extends Activity {
	
	private EditText userName;
	private EditText location;
	private EditText systime;
	private Button   snapShot, handin;
	private Spinner  event;
	private final static int CAMERA = 66;
	private final static int PHOTO = 99; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Εγ₯ά€Ά­±
		setContentView(R.layout.c_artificial_return);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        snapShot = (Button)findViewById(R.id.c_snapshot); 
        snapShot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				c_snapshot();
			}
		});
        
        
        
           
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
    
    public void c_hand_in(View v){ 
    	
    }
    
    public void c_snapshot(){
    	Intent intent = new Intent();
    	intent.setClass(C_Artificial_Return.this, C_Camera.class);
    	startActivity(intent);
    	C_Artificial_Return.this.finish();
    }
	
    
	
   
}
