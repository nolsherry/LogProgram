package com.example.logprogram;

import java.util.ArrayList;
import android.app.Activity;
//import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;

public class C_Welcome extends Activity{

	//private CountDownTimer waitTimer;
	private ImageButton skip;
	private ViewPager page;
	ArrayList<View> viewList;
	boolean mIsBound;
	Messenger mService = null;
	final Messenger mMessenger = new Messenger(new IncomingHandler());
    
	//deal with messages from the service
	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case NetworkChecking.REGISTER:
	        	
	        	break;
	        case NetworkChecking.UNREGISTER:
	        	
	        	break;
	        case NetworkChecking.CHINESE_INTERFACE:
	            
	            break;
	        case NetworkChecking.ENGLISH_INTERFACE:
	            
	            break;
	        default:
	            super.handleMessage(msg);
	        }
	    }
	}	
	
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
		public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, NetworkChecking.CHINESE_INTERFACE);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
            //Toast.makeText(getApplicationContext(), "Service Attached.", Toast.LENGTH_SHORT).show();
        }

        @Override
		public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            //Toast.makeText(getApplicationContext(), "Service Disconnected.", Toast.LENGTH_SHORT).show();
        }
    };    
    
    public void doBindService() {
    	Intent bindIntent = new Intent(getApplication(), NetworkChecking.class);
        bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        //Toast.makeText(this, "Bind Service.", Toast.LENGTH_SHORT).show();
    }
    
    public  void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, NetworkChecking.UNREGISTER);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            //Toast.makeText(getApplicationContext(), "Service Unbinding....", Toast.LENGTH_SHORT).show();
        }
    }    
    
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_welcome);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        viewList = new ArrayList<View>();
        LayoutInflater Inflater = getLayoutInflater();
        viewList.add(Inflater.inflate(R.layout.c_welcome1,null));
        viewList.add(Inflater.inflate(R.layout.c_welcome2,null)); 
        page = (ViewPager)findViewById(R.id.viewpager1);
	    page.setAdapter(new MyPagerAdapter());
	    page.setOnPageChangeListener(new MyPagerChangeListener());
	}
    
	 public void skipButton(){
	    	skip = (ImageButton)findViewById(R.id.imageButton1);
	    	skip.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v) {
		    // TODO Auto-generated method stub
		    Intent intent = new Intent();
		    //Log.d("imgbutton", "enter");
		    intent.setClass(C_Welcome.this,NetworkChecking.class);
		    startService(intent);
		    doBindService();
		   
		    
		    		  	  }	
	    		
	    	});
	    	
	    	
	    	
	    }
   
    
    //destroy the activity
    @Override
	protected void onDestroy(){
        super.onDestroy();
        doUnbindService();
    }
    
    @Override
	protected void onStart(){
    	super.onStart();
    }    
    
    @Override
	protected void onPause(){
    	super.onPause();
    	doUnbindService();
    	
    }
    
    @Override
	protected void onStop(){
    	super.onStop();
    	 doUnbindService();
        /*if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }*/    	
    }	
  public class MyPagerAdapter extends PagerAdapter{
	  
	@Override
    public Object instantiateItem(View v,int position){
    	((ViewPager)v).addView(viewList.get(position));
    	return viewList.get(position);
    	
    }	
	
	@Override
	public void destroyItem(View v,int position,Object arg2){
		
	 ((ViewPager)v).removeView(viewList.get(position));	
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View v, Object arg1) {
		// TODO Auto-generated method stub
		return v==arg1;
	}  
  }  
   
  public class MyPagerChangeListener implements OnPageChangeListener {

		@Override
		//當滑動狀態改變
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		//當目前頁面被滑動時用
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}
	  
		@Override
		//當新的頁面被選中
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub

		}

	}
    
  @Override
protected void onResume(){
  	super.onResume();
		
  	//waitTimer = new CountDownTimer(1000, 1000){
	//@Override
	//public void onFinish() {
		skipButton();
		//startService(new Intent(C_Welcome.this, NetworkChecking.class));
	    //doBindService();
	//}
		/*
	@Override
	public void onTick(long millisUntilFinished) {}
		
		}.start();*/	
  }   
}
