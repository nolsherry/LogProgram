package com.example.logprogram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BumpRecord extends Activity implements SensorEventListener{
	//GPS manager
	private LocationManager mgr = null;
	private String bestProvider = null;
	
	//accelerometer & magnetic
	private SensorManager accSensorManager, magSensorManager;
	
	//small sigma
	private TextView tv_SmallS1;
	private TextView tv_SmallS2;
	private TextView tv_SmallS3;
	private TextView tv_SmallS4;	
	private TextView tv_SmallS5;
	private TextView tv_SmallS6;
	private TextView tv_SmallS7;
	
	//current status
	private TextView cs_title;
	private TextView cs_Time;
	private TextView cs_Speed;
	private TextView cs_Latitude;
	private TextView cs_Longitude;
	private TextView cs_onRack;
	
	//last bumping
	private TextView lb_title;
	private TextView lb_Time;
	private TextView lb_Speed;
	private TextView lb_Latitude;
	private TextView lb_Longitude;
	private TextView lb_BI;
	
	//last bumping layout
	private TableLayout last_bumping, current_status;
	
	//log button
	private Button logStartButton, logStopButton;
	
	//object for writing file
	private FileOutputStream outputStream;
	
	//log system start time
	private Long systemStartTime;
	
	//count down handler
	private Handler handler = new Handler();
	private VCE vce ;
	private int now = 0; 
	
	//gps information
	private double lat;
	private double lng;
	private float speed;
	private long time;
	private double height;
	private float bearing;
	private float HDOP;
	private float VDOP;
	private float PDOP;
	private int numOfSec;
	private long numOfSeclong;
	
	//flag
	private boolean isInitialize       = false;
	private boolean isClick            = false; //check if the button is pressed
	private boolean isFirstGps         = true;	//check if gps is first turned on
	private boolean isGpsDataReady     = false; //check if gps data is right for collection
	private boolean isReadFile         = false;	//not read = false, read = true, read and run one by one once sensor change
	private boolean isReadFileFunction = false;	//read data and run all in VCE once = true
	private boolean isFirstRead        = true;
	
	//file writer
	private LogFileWriter GpsWriter;
	private LogFileWriter MagWriter;
	private LogFileWriter ReadmeWriter;
	
	//for read input use
	private BufferedReader br;
	private BufferedReader brGPS;
	private int lastSecond		 = 0;
	private long lastSecondlong  = 0;
	private long checkSec		 = 0;
	private boolean isSecondRead = false;
	
	/* Menu */
	protected static final int MENU_SETTING = Menu.FIRST; 
	protected static final int MENU_EXIT    = Menu.FIRST + 1;
	
	//for count sampling rate
	private int totalSecond     = 0;
	private float totalDistance = 0;
	private double eventCountp  = 0;
    private int eventCountDown  = 1;
    
	long previousTimestamp  = 0;
	long previousTimestampM = 0;
	
	int num = 0;
	
	//color change flag and timer 
	private boolean colorChangeFlag = false;
	private int tmpCount = 0;
	
	//time difference for calibration
	private long TIME_DIFFERENCE = 28800000;
	
	//check if detect the first event
	private boolean firstEventFlag = false;
/*	
 	//-------------------------------------For MSRA Demo----------------------------------------------
	//count-down timer
	CountDownTimer waitTimer;
	
	//store read csv file
	private String line = "";
	
	//variable for storing data
	private String time, speed, latitude, longitude, eventLatitude, eventLongitude, eventBI, eventFlag; 
	private String sigma1, sigma2, sigma3, sigma4, sigma5, sigma6, sigma7;
	
	//color change flag and timer 
	private boolean colorChangeFlag = false;
	private int tmpCount = 0;
	
	private long startTime, endTime, elapsedTime, currStamp, start, end, period;
	private float updateBase = 1000, updateInterval = 1000;
	private long baseTime = 1408115479;
	
	private boolean firstData = true;
	Time current_time= new Time();
	//-------------------------------------For MSRA Demo----------------------------------------------
*/	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bump_record);
		
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
		//initialize system parameters
		initializeSystemParams();
       
		//create cache path
		makeCacheDir();
		
        //initialize components
        initializeUiComponent();
	}
	
	/*initialize system parameter*/
    private void initializeSystemParams() {
		SystemParameters.GpsCount  = 0;
		SystemParameters.AuxCount  = 0;
		SystemParameters.AccCount  = 0;
		SystemParameters.MagCount  = 0;
		SystemParameters.AnomCount = 0;
		
		SystemParameters.StartLat = "";
		SystemParameters.StartLng = "";
		SystemParameters.EndLat   = "";
		SystemParameters.EndLng   = "";
		
		SystemParameters.StartDate = " ";
		SystemParameters.Duration  = " ";
		SystemParameters.Mileage   = "";
		
		SystemParameters.isServiceRunning = false;
		SystemParameters.isOnRack         = 1;
		SystemParameters.isSetting        = false;		
	}

    /*//initialize UI components*/
	private void initializeUiComponent() {
    	//small sigma
    	tv_SmallS1 = (TextView)findViewById(R.id.S1);
    	tv_SmallS2 = (TextView)findViewById(R.id.S2);
    	tv_SmallS3 = (TextView)findViewById(R.id.S3);
    	tv_SmallS4 = (TextView)findViewById(R.id.S4);
    	tv_SmallS5 = (TextView)findViewById(R.id.S5);
    	tv_SmallS6 = (TextView)findViewById(R.id.S6);
    	tv_SmallS7 = (TextView)findViewById(R.id.S7);
    	
    	//current status
    	cs_title     = (TextView)findViewById(R.id.current_status);
    	cs_Time 	 = (TextView)findViewById(R.id.cs_time);
    	cs_Speed 	 = (TextView)findViewById(R.id.cs_speed);
    	cs_Latitude  = (TextView)findViewById(R.id.cs_latitude);
    	cs_Longitude = (TextView)findViewById(R.id.cs_longitude);
    	cs_onRack    = (TextView)findViewById(R.id.cs_onRack);
    	
    	//last bumping
    	lb_title	 = (TextView)findViewById(R.id.lastBumping);
    	lb_Time 	 = (TextView)findViewById(R.id.lb_time);
    	lb_Speed 	 = (TextView)findViewById(R.id.lb_speed);
    	lb_Latitude  = (TextView)findViewById(R.id.lb_latitude);
    	lb_Longitude = (TextView)findViewById(R.id.lb_longitude);
    	lb_BI		 = (TextView)findViewById(R.id.lb_bi);
    	
    	//last bumping layout
    	last_bumping   = (TableLayout)findViewById(R.id.last_bumping);
    	current_status = (TableLayout)findViewById(R.id.currentStatus);
    	
    	//log button
    	logStartButton = (Button)findViewById(R.id.log_start_button);
    	logStopButton  = (Button)findViewById(R.id.log_stop_button);
	}
    
	/*Setting Information will save in this file*/
	private void makeCacheDir(){
		//obtain the state of external storage
		String state = Environment.getExternalStorageState();
		//check the state
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		   Log.d("ExternalStorageState", "read/write is enabled - MSR_Demo.java");
		   
		   //Returns the current state of the storage device that provides the given path.
		   String path = Environment.getExternalStorageDirectory().getPath();
		   File dir, file;	    
		   String fileName = "cache.log";
		    
		   //Constructs a new file using the specified path.
		   //dir = new File(path + "/NOL/BumpsOnRoads/cache");
		   dir = new File(path + "/NOL/BumpsOnRoads/cache", fileName);
		   //load parameters(Collector, VID, PhoneModel, VehicleModel, MountingMethod, MountingLocation, C1, C1, Memo)
		   try {
			   loadParam(dir);
		   } catch (IOException e2) {
			   e2.printStackTrace();
		   }
		   //check if the new file is created
		   if (!dir.exists()){ 
			   Log.d("cacheFile", "PATH NOT CREATED");
			   //create the file directory
			   file = new File(path + "/NOL/BumpsOnRoads/cache", fileName);
			   Log.d("cacheFile", "FILE CREATED: " + file.getAbsolutePath());
			   //C1 = 5.0, C2 = 2.5
			   String outputString = " / / / / / /5.0/2.5/ ";
			   //return a file object to outputStream
			   try {
				   outputStream = new FileOutputStream(file);
			   } catch (FileNotFoundException e1) {
				   e1.printStackTrace();
			   }
			   //write parameters into log file
			   try {
				   outputStream.write(outputString.getBytes());
			   } catch (IOException e) {
				   e.printStackTrace();
			   }
			   //load parameters into SystemParameters
			   try {
				loadParam(file);
			   } catch (IOException e) {
				   e.printStackTrace();
			   }
		   }else{
			   Log.d("cacheFile", "FILE EXISTS: " + dir.getAbsolutePath());
		   }
		   
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			Log.d("ExternalStorageState", "read-only / write disabled - MSR_Demo.java");
		} else {
			Log.d("ExternalStorageState", "read/write is disabled - MSR_Demo.java");
		}	
	}
	
	/*loading previous saved Setting Information*/
	private void loadParam(File file) throws IOException{
		//check if the file does exits in externalstorage
		if(!file.exists()){ 
			Log.d("cacheFile", "loadInformation(): file doesn't exist - MSR_Demo.java"); 
		}else{
			Log.d("cacheFile", "loadInformation(): file does exists - MSR_Demo.java"); 
			BufferedReader br = new BufferedReader(new FileReader(file));
			//set up system parameters
			String Line = br.readLine();
			StringTokenizer token 			  = new StringTokenizer(Line, "/");
			SystemParameters.Collector 		  = token.nextToken().toString();
			SystemParameters.VID 			  = token.nextToken().toString();
			SystemParameters.PhoneModel 	  = token.nextToken().toString();
			SystemParameters.VehicleModel     = token.nextToken().toString();
			SystemParameters.MountingMethod   = token.nextToken().toString();
			SystemParameters.MountingLocation = token.nextToken().toString();
			SystemParameters.C1				  = Double.valueOf(token.nextToken().toString()).doubleValue();
			SystemParameters.C2				  = Double.valueOf(token.nextToken().toString()).doubleValue();
			SystemParameters.Memo			  = token.nextToken().toString();			
			br.close();
		}
	}
	
	/*unused function*/
    private void readfile(){
    	File sdcard = Environment.getExternalStorageDirectory();
		
		File demoFile  = new File(sdcard,"gpsEvent.csv");
	
		try {
			br  = new BufferedReader(new FileReader(demoFile));			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private String getDate(long timeStamp){
        SimpleDateFormat objFormatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        objFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        Calendar objCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        objCalendar.setTimeInMillis(timeStamp);//edit
        String result = objFormatter.format(objCalendar.getTime());
        //AM/PM recognition
        long dayRestStamp = timeStamp % 86400000;
        if(dayRestStamp >= 43200000)
        	result += " PM";
        else
        	result += " AM";
        objCalendar.clear();
        return result;         
    }
    
	//destroy the activity
    @Override
	protected void onDestroy(){
		super.onDestroy();
		//remove location manager function
		mgr.removeUpdates(locationlistener);
		mgr.removeNmeaListener(NmeaListener);
		mgr.removeGpsStatusListener(GPSstatusListener);
		//Unregisters a listener for the sensors with which it is registered.
		accSensorManager.unregisterListener(this);
		magSensorManager.unregisterListener(this);
    }
    
    @Override
	protected void onStart(){
    	super.onStart();
    	
    }
    
    @Override
	protected void onResume(){
    	super.onResume();
		/************************** startGPS ***********************************/

		//Getting LocationManager object from System Service LOCATION_SERVICE
		mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		//Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();
        //set up a finer location accuracy requirement
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //set up a low power requirement. 
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //Getting the name of the best provider
		bestProvider = mgr.getBestProvider(criteria, true);
		//Getting Current Location
		if (bestProvider != null)
			mgr.getLastKnownLocation(bestProvider);
		//process location data when updated
		mgr.requestLocationUpdates(bestProvider, 0, 0, locationlistener); 
		//Adds a GPS status listener.
		mgr.addGpsStatusListener(GPSstatusListener);
		//Adds an NMEA listener.
		mgr.addNmeaListener(NmeaListener);
		
		/************************** start sensor ******************************/
	
		//accelerometer
		accSensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
		accSensorManager.registerListener(	this, 
										  	accSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
										  	SensorManager.SENSOR_DELAY_FASTEST);
		
		//magnetic
		magSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);		
		magSensorManager.registerListener(	this, 
											magSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
											SensorManager.SENSOR_DELAY_FASTEST);
		
		//Returns the unique device ID, for example, the IMEI for GSM and the MEID 
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		SystemParameters.SID = telephonyManager.getDeviceId();    	
    	
		/************************** read input *********************************/
		/*
		if(!isReadFile){
			//Returns the current state of the primary "external" storage device
			File sdcard = Environment.getExternalStorageDirectory();
			//Get the text file
			File file    = new File(sdcard,"AccFile.csv");
			File fileGPS = new File(sdcard,"GPSFile.csv");
			//assign a bufferReader object to the file pointer
			try {
				br    = new BufferedReader(new FileReader(file));
				brGPS = new BufferedReader(new FileReader(fileGPS));
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}		*/
		
/*    	
        //create a count-down timer object and configure the cycle
        waitTimer = new CountDownTimer(1000, (long) updateBase) {
		    @Override
		    //Callback fired on regular interval.
			public void onTick(long milliilFinished) {}
		    	
			@Override
			//Callback fired when the time is up
			public void onFinish() {
				try {
					
					//adjust fraction digits
					NumberFormat nf = NumberFormat.getInstance();
					nf.setMaximumFractionDigits(3); 
					nf.setMinimumFractionDigits(3);					
					
					if(!firstData){
						//currStamp = (Long.valueOf(data[0]) - baseTime)*1000;
						endTime = System.currentTimeMillis();
						end = System.currentTimeMillis();
						elapsedTime = endTime - startTime;
						period = end - start;
						//if(currStamp > elapsedTime){ Thread.sleep(currStamp - elapsedTime); }
						if(elapsedTime > 1000){ 
							updateBase = updateInterval + (period - 1000); 
						}else{
							updateBase = updateInterval;
						}
						Log.d("time", "system time:" + nf.format(elapsedTime/1000) + " currtime:" + currStamp + " peroid: " + period + " updateInterval:" + nf.format(1000/updateBase));	
					}										
					
					//color-changing duration
					if(colorChangeFlag) tmpCount++;
					if(tmpCount == 1){
						//set colorChangeFlag to the default value 
						colorChangeFlag = false;
						//set last bumping layout background to default value
						last_bumping.setBackgroundColor(Color.BLACK);
						//set last bumping layout background border to default value
						last_bumping.setBackgroundResource(drawable.back_border);
						//set timer to default value
						tmpCount = 0;
						//reset text color
						lb_title.setTextColor(Color.WHITE);
						lb_Time.setTextColor(Color.WHITE);
						lb_Time.setTextColor(Color.WHITE);	
						lb_Speed.setTextColor(Color.WHITE);
						lb_Latitude.setTextColor(Color.WHITE);
						lb_Longitude.setTextColor(Color.WHITE);	
						lb_BI.setTextColor(Color.WHITE);
					}
					
					if((line = br.readLine()) != null) {
						
						if(firstData){
							startTime = System.currentTimeMillis();
							firstData = false;
						}
						start = System.currentTimeMillis();
						// use comma as separator
						String[] data = line.split(",");

						//assign data to variables
						//time           = timestamp;
						time           = data[15];
						speed          = data[1];
						latitude 	   = data[2];
						longitude	   = data[3];
						eventFlag 	   = data[4];
						eventLatitude  = data[5];
						eventLongitude = data[6];
						eventBI        = data[7];
						sigma1         = nf.format(Double.valueOf(data[8]));
						sigma2         = nf.format(Double.valueOf(data[9]));
						sigma3         = nf.format(Double.valueOf(data[10]));
						sigma4         = nf.format(Double.valueOf(data[11]));
						sigma5         = nf.format(Double.valueOf(data[12]));
						sigma6         = nf.format(Double.valueOf(data[13]));
						sigma7         = nf.format(Double.valueOf(data[14]));
						//show on the UI	
						//small sigma
						tv_SmallS1.setText(sigma1);
						tv_SmallS2.setText(sigma2);
						tv_SmallS3.setText(sigma3);
						tv_SmallS4.setText(sigma4);
						tv_SmallS5.setText(sigma5);
						tv_SmallS6.setText(sigma6);
						tv_SmallS7.setText(sigma7);
						
						//current status
						cs_Time.setText("Time:         " + time + " pm");
						cs_Speed.setText("Speed:       " + speed + " km/h");
						cs_Latitude.setText("Latitude:    " + latitude);
						cs_Longitude.setText("Longitude: " + longitude);
						
						currStamp = (Long.valueOf(data[0]) - baseTime)*1000;
						
						//last bumping
						if(eventFlag.equals("1")){
							//color-change flag set to true
							colorChangeFlag = true;
							//set last bumping layout background to red
							last_bumping.setBackgroundColor(getResources().getColor(R.color.grass_green));
							//set text				
							lb_Time.setText("Time:         " + time + " pm");					
							lb_Speed.setText("Speed:       " + speed + " km/h");		
							lb_Latitude.setText("Latitude:    " + eventLatitude);		
							lb_Longitude.setText("Longitude: " + eventLongitude);	
							lb_BI.setText("BI:               " + eventBI);					
							//change text color
							lb_title.setTextColor(Color.BLACK);
							lb_Time.setTextColor(Color.BLACK);
							lb_Time.setTextColor(Color.BLACK);	
							lb_Speed.setTextColor(Color.BLACK);
							lb_Latitude.setTextColor(Color.BLACK);
							lb_Longitude.setTextColor(Color.BLACK);	
							lb_BI.setTextColor(Color.BLACK);
						}
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					if(line == null){
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
		    	//restart the count down timer
		    	this.start();
			}
		}.start();*/
    }
    
	//initialize those 6 file writer
	private void newFileWriter(){
		GpsWriter 	   = new LogFileWriter(this,"GPSFile.csv",1);
		MagWriter 	   = new LogFileWriter(this,"MagFile.csv",3);
		ReadmeWriter   = new LogFileWriter(this,"ReadMe.txt",6);
		vce 		   = new VCE(this);           
		vce.AccWriter  = new LogFileWriter(this,"AccFile.csv",2);
		vce.AuxWriter  = new LogFileWriter(this,"AuxFile.csv",4);
		vce.AnomWriter = new LogFileWriter(this,"AnomFile.csv",5);	
	}
    
	/*process object traveling information when updates*/
	private final LocationListener locationlistener = new LocationListener(){
		@Override
		public void onLocationChanged(Location location){
			//Get the latitude, in degrees.
			lat = location.getLatitude();
			//Get the longitude, in degrees.
			lng = location.getLongitude();	
			//Get the speed if it is available, in meters/second over ground. And transform the speed unit to kilometers/hour
			speed =(float) (location.getSpeed()*3.6);
			//Return the UTC time of this fix, in milliseconds since January 1, 1970.
			time = location.getTime() + TIME_DIFFERENCE;	
			//set up GPS data flag
			isGpsDataReady = true;
			//check if the button is pressed
			if(isClick){
				//check if the GPS is first switched on
				if(isFirstGps){
					//compute the offset between system time and UTC time
					SystemParameters.offset = System.currentTimeMillis() + TIME_DIFFERENCE - time;
					Log.d("time_calibration", "current timestamp: " + String.valueOf(System.currentTimeMillis()) + "\nstart timestamp: " + String.valueOf(time) + "\noffset: " + String.valueOf(SystemParameters.offset));
					//NumberFormat helps you to format and parse numbers for any locale.
					NumberFormat nf_loc = NumberFormat.getInstance();
					//Sets the maximum number of fraction digits that are printed when formatting.
					nf_loc.setMaximumFractionDigits(6); 
					//Sets the minimum number of fraction digits that are printed when formatting.
					nf_loc.setMinimumFractionDigits(6);
					//set lat,lng in 6 digit after decimal point, like 120.XXXXXX , 24.XXXXXX
					String latS = nf_loc.format(lat);
					String lngS = nf_loc.format(lng);
					
					//NumberFormat helps you to format and parse numbers for any locale.
					NumberFormat nf_speed = NumberFormat.getInstance();
					//Sets the maximum number of fraction digits that are printed when formatting.
					nf_speed.setMaximumFractionDigits( 0 ); 
					nf_speed.setMinimumFractionDigits(0);	
					String speedS = nf_speed.format(speed);
					
					//show traveling information on the UI				
					String curTime = getDate(time);
					cs_Time.setText("Time:         " + curTime);
					cs_Speed.setText("Speed:       " + speedS + " km/h");
					cs_Latitude.setText("Latitude:    " + latS);
					cs_Longitude.setText("Longitude: " + lngS);	
					
					//mark the starting location
					SystemParameters.StartLat = latS;
					SystemParameters.StartLng = lngS;
					//disable the GPS flag
					isFirstGps = false;
				}

				lat     = location.getLatitude();
				lng     = location.getLongitude();
				speed   = (float) (location.getSpeed()*3.6);
				time    = location.getTime() + TIME_DIFFERENCE;
				height  = location.getAltitude();
				bearing = location.getBearing();	
				Log.d("gps time", getDate(time)); 
				
				NumberFormat nf_loc = NumberFormat.getInstance();
				nf_loc.setMaximumFractionDigits(6); 
				nf_loc.setMinimumFractionDigits(6);
				
				String latS = nf_loc.format(lat);
				String lngS = nf_loc.format(lng);
				
				NumberFormat nf_speed = NumberFormat.getInstance();
				nf_speed.setMaximumFractionDigits( 0 ); 
				nf_speed.setMinimumFractionDigits(0);	
				String speedS = nf_speed.format(speed);

				//show traveling information on the UI
				String curTime = getDate(time);
				cs_Time.setText("Time:         " + curTime);
				cs_Speed.setText("Speed:       " + speedS + " km/h");
				cs_Latitude.setText("Latitude:    " + latS);
				cs_Longitude.setText("Longitude: " + lngS);	

				try {
					GpsWriter.writeGpsFile(time, lat, lng, speed, height, bearing, HDOP, VDOP, PDOP);
					//accumulate the GPS data
					SystemParameters.GpsCount++;
				} catch (IOException e) {
					e.printStackTrace();
				}  
			}
		}
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub		
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub		
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub	
		}
	
	};
	
	/*process object status information when updates*/
	private final GpsStatus.Listener GPSstatusListener = new GpsStatus.Listener() {

		@Override
		public void onGpsStatusChanged(int event) {
			// TODO Auto-generated method stub
			
		} 
		
	};
	
	/*receive the format information of NMEA0831 and get the detailed information like coordinates, valid satellite amount, speed...etc*/
	private final GpsStatus.NmeaListener NmeaListener = new GpsStatus.NmeaListener(){
		
		@Override
		public void onNmeaReceived(long timestamp, String nmea) {
			
			if (nmea == null)
				return;
			//Searches in this string for the first index of the specified string.
			int searchGSA = nmea.indexOf("GPGSA");
			if (searchGSA != -1)
			{
				Log.d("GPS-NMEA", nmea);				
				/* 	GPS DOP and active satellites
					eg1. $GPGSA,A,3,,,,,,16,18,,22,24,,,3.6,2.1,2.2*3C
					eg2. $GPGSA,A,3,19,28,14,18,27,22,31,39,,,,,1.7,1.0,1.3*35
					
					
					1    = Mode:
					       M=Manual, forced to operate in 2D or 3D
					       A=Automatic, 3D/2D
					2    = Mode:
					       1=Fix not available
					       2=2D
					       3=3D
					3-14 = IDs of SVs used in position fix (null for unused fields)
					15   = PDOP
					16   = HDOP
					17   = VDOP
				 */ 
				String[] nmeaData = nmea.split(",");
				String   PDOPtemp = nmeaData[15];		//dilution of precision
				String   HDOPtemp = nmeaData[16];		//Horizontal dilution of precision
				String   VDOPtemp = nmeaData[17];		//Vertical dilution of precision
				
				if(PDOPtemp.equals(""))
					PDOP = 0;
				else
					PDOP = Float.parseFloat(PDOPtemp);
				
				if(HDOPtemp.equals(""))
					HDOP = 0;
				else
					HDOP = Float.parseFloat(HDOPtemp);
				
				String[] vdoptemp = VDOPtemp.split("\\*");
				
				if(vdoptemp[0].equals(""))
					VDOP = 0;
				else
					VDOP = Float.parseFloat(vdoptemp[0]);	
				
				Log.d("GPS-NMEA","PDOP: " + PDOP + "\nHDOP: " + HDOP + "\nVDOP: " + VDOP);
			}
			return;	
		}
		
	};
	
	/*providing you with a reference to the Sensor object that changed and the new accuracy of the sensor*/
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	/*providing you with a SensorEvent object. A SensorEvent object contains information about the new sensor data*/
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			//variable to store the 3-axis acceleration data and timestamp
			double gx = 0, gy = 0, gz = 0;
			long timestamp = 0;
			
			if(!isReadFile){
				gx = event.values[0]; //sensor Gx
				gy = event.values[1]; //sensor Gy
				gz = event.values[2]; //sensor Gz	
				
				//timestamp = event.timestamp - SystemParameters.offset
				//transform nanosecond to millisecond
				timestamp = (System.currentTimeMillis() - SystemParameters.offset) + TIME_DIFFERENCE;
				Log.d("acc timestamp: ", getDate(timestamp));
			}

/*			if(!isReadFile && !isReadFileFunction){
				String strLine;
				StringTokenizer st = null;
			   	try {
			   		if(isFirstRead){
			   			
						String strGPSLine = "";
						StringTokenizer stGPS = null;

						strGPSLine = brGPS.readLine();
					    stGPS = new StringTokenizer(strGPSLine, ",");
					    	
					    time    = Long.parseLong(stGPS.nextToken());
					    lat     = Double.parseDouble(stGPS.nextToken());
					    lng     = Double.parseDouble(stGPS.nextToken());
					    height  = Double.parseDouble(stGPS.nextToken());
					    speed   = Float.parseFloat(stGPS.nextToken());
					    bearing = Float.parseFloat(stGPS.nextToken());
					    
					    numOfSeclong = time/1000;
					    lastSecondlong = numOfSeclong;

			   		}
			   		
			    	strLine = br.readLine();
			    	st = new StringTokenizer(strLine, ",");

	    			timestamp = Long.parseLong(st.nextToken()); //timestamp
	    			gx 		  = Double.parseDouble(st.nextToken());
	    			gy		  = Double.parseDouble(st.nextToken());
	    			gz		  = Double.parseDouble(st.nextToken());
	    			double gv = Double.parseDouble(st.nextToken());
	    			long tmpNumOfSeclong = timestamp/1000; //#sec   			
	    			
			    	if(timestamp/1000 == checkSec){
			    		now++;
			    		Log.d("MapActivity","now: "+now );

			    		if(tmpNumOfSeclong >=numOfSeclong){
			    			Log.d("MapActivity","here" );
			    			
			    			if(isSecondRead){
			    				String strGPSLine;
			    				StringTokenizer stGPS = null;
			    				strGPSLine = brGPS.readLine();
			    				stGPS = new StringTokenizer(strGPSLine, ",");
					    	
			    				time=Long.parseLong(stGPS.nextToken());
			    				lat = Double.parseDouble(stGPS.nextToken());
			    				lng = Double.parseDouble(stGPS.nextToken());
							    height = Double.parseDouble(stGPS.nextToken());
							    speed = Float.parseFloat(stGPS.nextToken());
							    bearing = Float.parseFloat(stGPS.nextToken());	    				
			    				numOfSeclong = time/1000;
			    				
				    			if(lastSecond == numOfSec){
				    				String strGPSLine1;
				    				StringTokenizer stGPS1 = null;
				    				strGPSLine1 = brGPS.readLine();
				    				stGPS1 = new StringTokenizer(strGPSLine1, ",");
						    	
				    				time=Long.parseLong(stGPS1.nextToken());
				    				lat = Double.parseDouble(stGPS1.nextToken());
				    				lng = Double.parseDouble(stGPS1.nextToken());
								    height = Double.parseDouble(stGPS1.nextToken());
								    speed = Float.parseFloat(stGPS1.nextToken());
								    bearing = Float.parseFloat(stGPS1.nextToken());	
				    				numOfSeclong = time/1000;

				    			}
				    			
				    			lastSecondlong = numOfSeclong;
			    			}
	
			    			isGpsDataReady = true;
					    }
					    
					    checkSec = timestamp/1000 + 1; 
	
			    	}
	    			
	    			if(isFirstRead){
	    				checkSec = timestamp/1000 + 1;
			   			isFirstRead = false;
	    			}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // skip first line

			}
*/
			if(isClick && isGpsDataReady && !isReadFileFunction){
				try {
					if(isInitialize){
						//Log.d("MapActivity", "VCE start now");
						if(previousTimestamp != timestamp)
							vce.VCExtractWithSpeed(gx, gy, gz, lat, lng, speed, timestamp, height, bearing); 
						previousTimestamp = timestamp;
					}
					else{ // after OnRack test is true

						if(previousTimestamp != timestamp)
							isInitialize = vce.initializationWithSpeed(gx, gy, gz, lat, lng, speed, timestamp, height, bearing);
						previousTimestamp = timestamp;
						isSecondRead = true;						
						
						if(SystemParameters.isOnRack == 2){
							//tv_onracktestresult.setText("True");
							cs_onRack.setText("On Rack:    True");
						}
						else if(SystemParameters.isOnRack == 3){
							cs_onRack.setText("On Rack:    False");
						}
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
		if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
			//long timestamp = (event.timestamp/1000000) - SystemParameters.offset;
			long timestamp = (System.currentTimeMillis() - SystemParameters.offset) + TIME_DIFFERENCE;
			float Mx = event.values[1];
			float My = event.values[2];
			float Mz = event.values[0];
			Log.d("mag time", getDate(timestamp));
			
			try {
				if(isClick){
					if(previousTimestampM != timestamp)
						MagWriter.writeMagFile(timestamp, Mx, My, Mz);
					previousTimestampM = timestamp;
					SystemParameters.MagCount++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	/*work description to be performed on a runnable object*/
	private Runnable updateTimer = new Runnable() {

		@Override
		public void run() {
			//compute the passed time in millisecond
			Long spentTime = System.currentTimeMillis() - systemStartTime;
			//compute the passed minutes
			Long minius = (spentTime/1000)/60;
			//compute the passed seconds
			Long seconds = (spentTime/1000) % 60;
			//compute the passed hours
			Long hour = minius / 60 ;
			
			String hourS    = "00".substring(0, 2 - String.valueOf(hour).length())    + String.valueOf(hour);
			String miniusS  = "00".substring(0, 2 - String.valueOf(minius).length())  + String.valueOf(minius);
			String secondS  = "00".substring(0, 2 - String.valueOf(seconds).length()) + String.valueOf(seconds);
				
			SystemParameters.Duration = hourS + ":" + miniusS + ":" + secondS;
			handler.postDelayed(this, 1000);
			
			//mark the passed time and traveling distance every second
			now++;
			
			//SystemParameters.totalMinute = totalMinute;
			totalSecond  = (int) (spentTime/1000);
			totalDistance += (speed/3.6);
			
			setSmallSigma();
			setBumpingEvent();
		}
	};
	
	/*transfer to Setting Activity*/
	private void gotoSettingActivity(){
		Intent intent = new Intent(this, SettingEndActivity.class);
		startActivity(intent);
		//finish this activity
		finish();
	}
	
    /*activate log start button*/
    public void logStart(View view){
		//check if he service is running
		if(!SystemParameters.isServiceRunning){
			//set service flag to true
			SystemParameters.isServiceRunning = true;
			//allocate FileWriter object
			newFileWriter();
			//read all data and run it once
			if(isReadFileFunction && isReadFile){ readfile(); }
			//print service start log message
			Toast.makeText(getBaseContext(), "Log Service is Start", Toast.LENGTH_SHORT).show();
			//record start time
			systemStartTime = System.currentTimeMillis();
			//設定定時要執行的方法
			//handler.removeCallbacks(updateTimer);
			//Causes the Runnable object to be added to the message queue, to be run after the specified amount of time elapses.
			handler.postDelayed(updateTimer, 1000);
			//re-configure isClick flag
			isClick = true;
			//reset button text and color
			//logButton.setText("Finish");
			//logButton.setTextColor(Color.BLACK);
			//logButton.setBackgroundColor(Color.GREEN);
			
			//set time
		    Time t        = new Time();
		    t.setToNow();
		    String year   = String.valueOf(t.year);
		    String month  = String.valueOf(t.month+1);
		    String day    = String.valueOf(t.monthDay);
		    String hour   = String.valueOf(t.hour);
		    String minute = String.valueOf(t.minute);
		    String second = String.valueOf(t.second);

		    //YYYYMMDD-HHMMSS			    
		    
		    month  = "00".substring(0, 2 - month.length())  + month;
		    day    = "00".substring(0, 2 - day.length())    + day;
		    hour   = "00".substring(0, 2 - hour.length())   + hour;
		    minute = "00".substring(0, 2 - minute.length()) + minute;
		    second = "00".substring(0, 2 - second.length()) + second;
		    
		    String date = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
		    
		    //configure system starting date
			SystemParameters.StartDate = date;
		}
    }
    
    /*activate log stop button*/
    public void logStop(View view){
    	if(SystemParameters.isServiceRunning){
			//configure system parameters
			SystemParameters.isServiceRunning = false;
			SystemParameters.isEnd = true;
			//output service stop log
			Toast.makeText(getBaseContext(), "Log Service is Stop", Toast.LENGTH_SHORT).show();
			//Remove any pending posts of Runnable object that are in the message queue.
			handler.removeCallbacks(updateTimer);
			//configure isClick flag to false
			isClick = false;	
			//to format and parse numbers for any locale
			NumberFormat nf_loc = NumberFormat.getInstance();
			//Returns the maximum number of integer digits that are printed when formatting.
			nf_loc.setMaximumFractionDigits(6); 
			//Returns the minimum number of fraction digits that are printed when formatting.
			nf_loc.setMinimumFractionDigits(6);	
			//format the location precision locale
			String latS = nf_loc.format(lat);
			String lngS = nf_loc.format(lng);
			//mark the ending location
			SystemParameters.EndLat = latS;
			SystemParameters.EndLng = lngS;
			//compute the distance from start to end
			totalDistance = totalDistance/1000;
			NumberFormat nf_distance = NumberFormat.getInstance();
			nf_distance.setMaximumFractionDigits(3); 
			String totalDistanceS = nf_distance.format(totalDistance);
			//record the traveling distance
			SystemParameters.Mileage = totalDistanceS;
			SystemParameters.totalSecond = totalSecond;
			//transfer to Setting Activity
			gotoSettingActivity(); 	
    	}   	
    }
    
	/*format the bumping event information*/
	private void setBumpingEvent(){
/*		//configure bumping event for AI, Event Sigma, Small Sigma
		NumberFormat nf1 = NumberFormat.getInstance();
		nf1.setMaximumFractionDigits(2); 
		nf1.setMinimumFractionDigits(2);	
		//String event_AI         = nf1.format(SystemParameters.eventAI);
		//String event_Sigma      = nf1.format(SystemParameters.eventSigmaEvent);
		//String event_SmallSigma = nf1.format(SystemParameters.eventSmallSigma);
		
		//configure bumping event for event speed 
		NumberFormat nf2 = NumberFormat.getInstance();
		nf2.setMaximumFractionDigits(0); 
		nf2.setMinimumFractionDigits(0);	
		String speedS = nf2.format(SystemParameters.eventSpeed);
		
		//configure bumping event for event location
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(6); 
		nf.setMinimumFractionDigits(6);	
		String latS = nf.format(SystemParameters.eventLat);
		String lngS = nf.format(SystemParameters.eventLng);*/
		//set last bumping event background to black
		last_bumping.setBackgroundColor(Color.BLACK);
		
		//remain text			
		if(!firstEventFlag){
			lb_Time.setText("Time: ");					
			lb_Speed.setText("Speed: ");		
			lb_Latitude.setText("Latitude: ");		
			lb_Longitude.setText("Longitude: ");	
			lb_BI.setText("BI: ");	
		}	
		
		//remain text color
		lb_title.setTextColor(Color.WHITE);
		lb_Time.setTextColor(Color.WHITE);
		lb_Time.setTextColor(Color.WHITE);	
		lb_Speed.setTextColor(Color.WHITE);
		lb_Latitude.setTextColor(Color.WHITE);
		lb_Longitude.setTextColor(Color.WHITE);	
		lb_BI.setTextColor(Color.WHITE);

		checkBumpingEventChange();
	}

	/*check the bumping event information change*/
	private void checkBumpingEventChange(){
		eventCountDown--;
		
		//initial
		if(eventCountDown == 0){
			//set last bumping layout background color to default value
			last_bumping.setBackgroundColor(Color.BLACK);
			//set last bumping layout background border to default value
			last_bumping.setBackgroundResource(R.drawable.back_border);
			//reset text color
			lb_title.setTextColor(Color.WHITE);
			lb_Time.setTextColor(Color.WHITE);
			lb_Time.setTextColor(Color.WHITE);	
			lb_Speed.setTextColor(Color.WHITE);
			lb_Latitude.setTextColor(Color.WHITE);
			lb_Longitude.setTextColor(Color.WHITE);	
			lb_BI.setTextColor(Color.WHITE);
			eventCountDown = 1;
		}
		
		//check
		if(eventCountp != SystemParameters.AnomCount){
			//configure bumping event for AI, Event Sigma, Small Sigma
			NumberFormat nf1 = NumberFormat.getInstance();
			nf1.setMaximumFractionDigits(2); 
			nf1.setMinimumFractionDigits(2);	
			String event_AI         = nf1.format(SystemParameters.eventAI);
			
			//configure bumping event for event speed 
			NumberFormat nf2 = NumberFormat.getInstance();
			nf2.setMaximumFractionDigits(0); 
			nf2.setMinimumFractionDigits(0);	
			String speedS = nf2.format(SystemParameters.eventSpeed);
			
			//configure bumping event for event location
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(6); 
			nf.setMinimumFractionDigits(6);	
			String latS = nf.format(SystemParameters.eventLat);
			String lngS = nf.format(SystemParameters.eventLng);
			
			//set last bumping layout background to green
			last_bumping.setBackgroundColor(getResources().getColor(R.color.grass_green));
			//set text				
			String eventTime = getDate(time);
			lb_Time.setText("Time:         " + eventTime);					
			lb_Speed.setText("Speed:       " + speedS + " km/h");		
			lb_Latitude.setText("Latitude:    " + latS);		
			lb_Longitude.setText("Longitude: " + lngS);	
			lb_BI.setText("BI:               " + event_AI);					
			//change text color
			lb_title.setTextColor(Color.BLACK);
			lb_Time.setTextColor(Color.BLACK);
			lb_Time.setTextColor(Color.BLACK);	
			lb_Speed.setTextColor(Color.BLACK);
			lb_Latitude.setTextColor(Color.BLACK);
			lb_Longitude.setTextColor(Color.BLACK);	
			lb_BI.setTextColor(Color.BLACK);		
			
			//set to true because first event has been detected
			firstEventFlag = true;
		}
		
		//record
		eventCountp = SystemParameters.AnomCount;
	}
	
	/*format the small sigma information*/
	private void setSmallSigma(){
		//configure bumping event for Small Sigma array
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3); 
		nf.setMinimumFractionDigits(3);	
		
		String SmallS1S = nf.format(SystemParameters.smallSArray[0]);
		String SmallS2S = nf.format(SystemParameters.smallSArray[1]);
		String SmallS3S = nf.format(SystemParameters.smallSArray[2]);
		String SmallS4S = nf.format(SystemParameters.smallSArray[3]);
		String SmallS5S = nf.format(SystemParameters.smallSArray[4]);
		String SmallS6S = nf.format(SystemParameters.smallSArray[5]);
		String SmallS7S = nf.format(SystemParameters.smallSArray[6]);

		//show small sigma information on the screen
		tv_SmallS1.setText(String.valueOf(SmallS1S));
		tv_SmallS2.setText(String.valueOf(SmallS2S));
		tv_SmallS3.setText(String.valueOf(SmallS3S));
		tv_SmallS4.setText(String.valueOf(SmallS4S));
		tv_SmallS5.setText(String.valueOf(SmallS5S));
		tv_SmallS6.setText(String.valueOf(SmallS6S));
		tv_SmallS7.setText(String.valueOf(SmallS7S));
		
		//check the small sigma information change
		checkSmallSigmaChange();
	}

	/*check the small sigma information change*/
    private void checkSmallSigmaChange(){
    	//initial
    	tv_SmallS1.setBackgroundColor(Color.BLACK); tv_SmallS1.setTextColor(Color.WHITE);
    	tv_SmallS2.setBackgroundColor(Color.BLACK); tv_SmallS2.setTextColor(Color.WHITE);
    	tv_SmallS3.setBackgroundColor(Color.BLACK); tv_SmallS3.setTextColor(Color.WHITE);
    	tv_SmallS4.setBackgroundColor(Color.BLACK); tv_SmallS4.setTextColor(Color.WHITE);
    	tv_SmallS5.setBackgroundColor(Color.BLACK); tv_SmallS5.setTextColor(Color.WHITE);
    	tv_SmallS6.setBackgroundColor(Color.BLACK); tv_SmallS6.setTextColor(Color.WHITE);
    	tv_SmallS7.setBackgroundColor(Color.BLACK); tv_SmallS7.setTextColor(Color.WHITE);	
    	
    	//V = 20j - 10 j = 1,2,3,...,7
	    if (speed >=10 && speed < 30) {
	    	tv_SmallS1.setBackgroundColor(Color.CYAN);
	    	tv_SmallS1.setTextColor(Color.BLACK);
	    } else if (speed >=30 && speed < 50) {
	    	tv_SmallS2.setBackgroundColor(Color.CYAN);
	    	tv_SmallS2.setTextColor(Color.BLACK);
	    } else if (speed >=50 && speed < 70) {
	    	tv_SmallS3.setBackgroundColor(Color.CYAN);
	    	tv_SmallS3.setTextColor(Color.BLACK);
	    } else if (speed >=70 && speed < 90) {
	    	tv_SmallS4.setBackgroundColor(Color.CYAN);
	    	tv_SmallS4.setTextColor(Color.BLACK);
	    } else if (speed >=90 && speed < 110) {
	    	tv_SmallS5.setBackgroundColor(Color.CYAN);
	    	tv_SmallS5.setTextColor(Color.BLACK);
	    } else if (speed >=110 && speed < 130) {
	    	tv_SmallS6.setBackgroundColor(Color.CYAN);
	    	tv_SmallS6.setTextColor(Color.BLACK);
	    } else if (speed >= 130) {
	    	tv_SmallS7.setBackgroundColor(Color.CYAN);
	    	tv_SmallS7.setTextColor(Color.BLACK);
	    }

    }
    
	/*go to SettingActivity*/
	private void gotoSetting()
	{
		startActivity(new Intent().setClass(this, SettingActivity.class));
	}
    
	/*add option menu to the upper right corner*/
	@Override
	
	public boolean onCreateOptionsMenu(Menu menu)
	{		
		
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MENU_SETTING, 0, R.string.action_settings);
		menu.add(Menu.NONE, MENU_EXIT, 0, R.string.action_exit);	
		return true;
	}

	/*specify the function as long as the option is pressed*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			/*case MENU_INTRODUCTION:
				gotoIntroduction();
				break;*/
			case MENU_SETTING:
				gotoSetting();
				break;
			case MENU_EXIT:
				finish();
				break;
			default:		
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*pause the activity*/
    @Override
	protected void onPause(){
    	super.onPause();
    }
    
    /*stop the activity*/
    @Override
	protected void onStop(){
    	super.onStop();
    }	
}
