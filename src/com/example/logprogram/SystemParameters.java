package com.example.logprogram;

import java.util.Vector;

/**************  
 * Store all GLobal Variable
 ******************/

public class SystemParameters {
	
	public static long offset = 0L;								   //record the offset between system time and UTC time
	public static double C1   = 5.0;
	public static double C2   = 2.5;
	
	//for ReadMe.txt
	public static String VID=" ";
	public static String Collector = " ";
	public static String Memo = " ";
	public static String VehicleModel=" ";
	public static String PhoneModel=" ";
	public static String MountingMethod   = " ";
	public static String MountingLocation = " ";
	
	
	public static int GpsCount  = 0;								//compute the amount of GPS data
	public static int AccCount  = 0;								//compute the amount of Acc data
	public static int MagCount  = 0;								//compute the amount of Magnetic data
	public static int AuxCount  = 0;								//
	public static int AnomCount = 0;								//compute the amount of anomalies
	
	public static String StartLat = " ";							//start latitude
	public static String StartLng = " ";							//start longitude
	public static String EndLat   = " ";							//end latitude
	public static String EndLng   = " ";							//end longitude
	
	public static String SID =" "; 
	public static String StartDate =" ";							//start measuring date
	public static String Duration = " ";							//measuring duration
	public static String Mileage = " ";								//traveling distance
	
	//for Record Activity
	public static double SmallS1 = 0;
	public static double SmallS2 = 0;
	public static double SmallS3 = 0;
	public static double SmallS4 = 0;
	public static double SmallS5 = 0;
	public static double SmallS6 = 0;
	public static double SmallS7 = 0;
	public static double SmallS8 = 0;
	
	public static double[] smallSArray = new double [7];
	public static double[] bigSArray = new double [7];
	
	public static long eventTime = 0;
	public static double eventLat = 0;
	public static double eventLng = 0;
	public static float eventSpeed = 0;
	public static double eventAI = 0;
	public static double eventSigmaEvent =0;
	public static double eventSmallSigma =0;
	
	public static boolean isServiceRunning = false;					//check service is running or not
	public static int isOnRack = 1;									//1= have not test yet, 2= is on rack, 3=not on rack
	public static boolean isSetting = false;						//check if log information is set or not
	public static boolean isEnd = false;							//check if service is end or not
	
	public static Vector<Event> eventList = new Vector<Event>();	//record event, to show on map
	public static String filename = " ";
	
	public static int totalSecond = 0;
	public static boolean isOpen = false;							//to test if the sensing system is open
	
}
