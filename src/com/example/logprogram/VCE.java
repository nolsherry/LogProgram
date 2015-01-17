package com.example.logprogram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**************  
 * Extraction Algorithm 
 * *****************/

public class VCE {

    //in millisecond
    private int T0 = 30000;	
    private int T1 = 180000;//平均這個時間內的sensor值來算g0
    private int T2 = 500;
    
    //parameter for bumping detection
    private double C1 = 5.0;
    private double C2 = 2.5;

	private int maxb = 30;
      
    //timestamp
    private long checkT0;//T0秒時的timestamp
    private long checkT1;
    private long checkT2;
    private long checkSec;//每一秒的timestamp,每秒會去+1
    private long checkT3=0;//偵測到event之後，要空一段時間。這是下一次會開始偵測的時間點
  
    //record now is :
    private int s = 0; //第幾筆sensor data
    private int now;//第幾秒

    // For sliding window
    private int offset = 0;
    
    //the most important parameter
    private double smallS = 0;

    
	private double deltaGv = 0;
	
	//private double[] deltaGvArray = new double [8];
	private double[] smallSArray ;
	
	/**/
	private Vector<ArrayList<DeltaGv>> smallSigmaSet ;
	private Vector<Double> smallSVector;
	private int ssr = 10;
	private int BIN_NUMBER = 7;

    // For calculating g0
    private double [] g0CumulatedAcc = {0.0f,0.0f,0.0f};
    private SensorValues g0;
    private int g0count =0;//how many element in g0CumulatedAcc
    
    //for onRackTest
    private double g01=0;
    private double g02=0;
    private double g03=0;
    private boolean isOnRack=false;
    private double C = 0.2;
    private int g0now = 0;//第二次或以上的Onracktest的起始描述
    private int g0s = 0;//第二次或以上的onracktest的起始sensor筆數
    private boolean isOnRackTest = false;//是否做過onracktest了
	
	//for sE
    private double sE=-1f; //SmallSigmaEvent
	private double cumulatedsE = 0.0f;
	private double cumulatedSquaredsE = 0.0f;	
	private int eventSize = 0;
	int eventT = 0; //通過bumping detection1時是第幾秒
	long eventTimeStamp =0; //通過bumping detection1時的timestamp
	//private double pavementIndex = -1f;//Ai
	private Vector<VC> C2Buffer; // record data for bumping detection2

    //output
    private Vector<VC> vcs;//每一筆sensor資料會有一筆，�堶惘陶t度
    private Vector<SensorValues> gBuffer;//g sensor buffer, buffer size is roughly T1 * sampling rate, sliding window.
    private Vector<SecondRange> sr;//記錄每一秒是第幾筆sensor到第幾筆，等等

	//flag
    private boolean isInitialized = false;
	private boolean isFirstSecond = true;
	private boolean isT0 = false;
	private boolean isT1 = false;
	private boolean beginCheck = false;//begin check C2
	
	//log file writer
	public LogFileWriter AccWriter;
	public LogFileWriter AuxWriter;
	public LogFileWriter AnomWriter;
	
	double ratio=0;
	
	double previousLat;
	double previousLng;
	double nowLat;
	double nowLng;
	double previousHeight;
	double previousSmallS;
	double previousSE;
	long previousTimestamp;
	float previousSpeed;
	float previousBearing;
	double previousRatio;
	double previousDeltaGv;
	double previousPeakGv;
	double previousNorm ;
	
	
	long peakEventTimeStamp;
	double peakGv=0;
	double previouslat1=0;
	double previouslat2=0;
	double previouslng1=0;
	double previouslng2=0;
	
	int enterCount=1;
	private boolean eventFlag = false;
	private LinkedList<GPSdata> gpsbuffer ;
	
	Context context;
	Activity activity;
	
	int c1=0;

	public VCE(Context context) {
		this.context = context;
		this.gBuffer = new Vector<SensorValues>();
		this.vcs = new Vector<VC>();
		this.sr = new Vector<SecondRange>();

		this.C2Buffer = new Vector<VC>();
		this.C1 = SystemParameters.C1;
		this.C2 = SystemParameters.C2;
		
		this.smallSigmaSet = new Vector<ArrayList<DeltaGv>>(BIN_NUMBER);
		this.smallSArray = new double [BIN_NUMBER];
		this.gpsbuffer = new LinkedList<GPSdata>();

		for(int i=0;i<BIN_NUMBER;i++){
			smallSArray[i] = 0;
			smallSigmaSet.add(new ArrayList<DeltaGv>());
		}
	
	}
      
	  public boolean initializationWithSpeed(double gX, double gY, double gZ, double lat, double lng, float speed, 
	    		long timeStamp,double height, float bearing) throws IOException {
	
	    	boolean return_result = false;
	    	if (isFirstSecond) {
	    		isFirstSecond = false;
	    		
	    		if(isOnRackTest){//如果已經做過一次onracktest了
	    			now  = g0now;
	    			s = g0s;
	    			checkT0 = timeStamp/1000 + T0/1000 ;
	    			checkSec = timeStamp/1000 + 1;
	    			checkT1 = timeStamp/1000 + T1/1000 ;
	    			sr.add(new SecondRange(now,timeStamp));
	    			sr.get(now).setFrom(s);
	    			sr.get(now).setSpeed(speed);
	    		}
	    		else{	    		
	    			now = 0;
	    			s=0;
	    			checkT0 = timeStamp/1000 + T0/1000 ;//initialize結束時間
	    			checkSec = timeStamp/1000 + 1;//下一秒的時間
	    			checkT1 = timeStamp/1000 + T1/1000 ;//用來算g0的buffer滿了的時間
	    			sr.add(new SecondRange(now,timeStamp));
	    			sr.get(now).setFrom(s);//記錄這一秒是從sensor第幾筆開始
	    			sr.get(now).setSpeed(speed);
	    		}
	    	} 
	    	if (timeStamp/1000 == checkSec) {//每秒進來一次
	    		addGPSbuffer(timeStamp,lat,lng,height);
	    		Log.d("initialize", "now:"+ now);
	    		//one second
	    		sr.get(now).setTo(s-1);//記錄這一秒到sensor的第幾筆
	    		now++;
	    		sr.add(new SecondRange(now,timeStamp));
	    		sr.get(now).setFrom(s);
	    		sr.get(now).setSpeed(speed);
	    		checkSec = timeStamp/1000 + 1;

	    	}    			
	    	if (timeStamp/1000 == checkT0) { //若已到了T0秒，也就是initial的時間結束了
	    		onRackTest();	    		
	    		if(isOnRack){
	    			
	        		calculateG0(g0count);//平均這T0秒內的所有x,y,z計算g0
	        		
	        		//計算每一秒的deltaGV
	        		for (int i=0+g0now; i<(T0/1000)+g0now; i++){
	        		    int from = sr.get(i).getFrom();
	        		    int to = sr.get(i).getTo();
	        		    calculateVCWithSpeed(from, to,i); //this will calculate deltaGv
	        		    sr.get(i).setdeltaGv(deltaGv);
	        		}

	        		//計算每一個速度區間的small sigma
	        		for(int i=0;i<smallSigmaSet.size();i++){
	        			if(smallSigmaSet.get(i).size() >= ssr)
	        				calculateSmallS(i);
	        		}
	
	        		for (int i=0+g0now; i<(T0/1000)+g0now; i++){
	        			long timeStamp1 = sr.get(i).getTime();
	        			float tmpSpeed = sr.get(i).getSpeed();
	        			double deltagv = sr.get(i).getdeltaGv();

	        			double gxBar = g0.getXvalue();
	        			double gyBar = g0.getYvalue();
	        			double gzBar = g0.getZvalue();
	        			double gzero = g0.getNorm();	        		
	        			
	        			for(int j=0;j<BIN_NUMBER;j++){
	        				SystemParameters.smallSArray[j] = smallSArray[j];//為了給record activity看得到即時的small sigma
	        			}
	        			
	        		    AuxWriter.writeAuxFile(timeStamp1,bearing,gxBar, gyBar, gzBar,smallSArray,deltagv ,tmpSpeed);
	        		    SystemParameters.AuxCount++;
	        		}  
	        		
	        		////// after initial了
	        		addGBuffer(gX,gY,gZ, timeStamp, now, speed);
	    			accumulatedACCforG0(gX,gY,gZ);
	        		isT0 = true;    		
	        		s++;
	    		}
	    		/*
	    		 * ADD reinitialize g0CumulatedAcc when onracktest is false
	    		 */
	    		 
	    	} 
	    	else{//每一筆sensor值會進來這裡
	    			addGBuffer(gX,gY,gZ, timeStamp, now, speed);
	    			accumulatedACCforG0(gX,gY,gZ);
	    			s++;
	    	}
	    	if (isT0) {
	    		isInitialized = true;
	    		return_result = true;
	    	}
	    	
			return return_result;
	 
	    }
   
    public void addGPSbuffer(long timestamp, double lat, double lng,double height){
    	GPSdata gpsobject = new GPSdata();
    	gpsobject.setTimestamp(timestamp);
    	gpsobject.setLat(lat);
    	gpsobject.setLng(lng);
    	gpsobject.setHeight(height);
    	gpsbuffer.add(gpsobject);
    	while(gpsbuffer.size()>10)
    		gpsbuffer.removeFirst();
    	//Log.d("VCE", "finish	"+ now);
    	
    }
    
    public void VCExtractWithSpeed(double gX, double gY, double gZ, double lat, double lng, float speed, long timeStamp,
    								double height, float bearing) throws IOException {

    	if (timeStamp/1000 == checkSec) { //每秒進去一次
    		Log.d("s","timestamp after:" + timeStamp);
    		addGPSbuffer(timeStamp,lat,lng,height);
    		
        	if(enterCount == 1){
        		previouslat1 = lat; previouslng1 = lng; previouslat2 = lat; previouslng2 = lng;
        		Log.d("count","enter!");
        	}
        	
        	if(eventFlag){
        		writeEvent(lat,lng,height);
        	}
        	
    		//one second
    		
    		int index, from, to;
    		long timeStampSec;
    		float tmpSpeed;
    				
    		checkSec = timeStamp/1000 + 1;    				
    				
    		if (timeStamp/1000 == checkT1)
    			isT1 = true;
    					
    		if (isT1) 
    			index = T1/1000-1;
    		else 
    			index = now;
    	
    		from = sr.get(index).getFrom();
    		timeStampSec = sr.get(index).getTime();
    		tmpSpeed = sr.get(index).getSpeed();
    		sr.get(index).setTo(s-1);
    		to = sr.get(index).getTo();

    		calculateVCWithSpeed(from, to, index);
    				
    		calculateG0(g0count);
		
    		sr.get(index).setdeltaGv(deltaGv);

    		for(int i=0;i<smallSigmaSet.size();i++){
    			if(smallSigmaSet.get(i).size() >= ssr)
    				calculateSmallS(i);
    		}
 	
    		//3. write new statistic information in this second
    		//writeVCSecWithSpeed(now, from, to, timeStampSec, tmpSpeed);
    		//boolean isstable = sr.get(index).getStableStatus();
    		//double SSE1 = sr.get(index).getSmallSigmaEvent();
    		//double SSE2 = sr.get(index).getSmallSigmaEvent2();
			double gxBar = g0.getXvalue();
			double gyBar = g0.getYvalue();
			double gzBar = g0.getZvalue();
			double gzero = g0.getNorm();
			previousNorm = gzero;
			
			for(int j=0;j<BIN_NUMBER;j++){
				SystemParameters.smallSArray[j] = smallSArray[j];
			}
			
			double temps=0;
  	    	if (speed>=10 && speed < 30) {
  	    		temps = smallSArray[0];
	    	} else if (speed >=30 && speed < 50) {
	    		temps = smallSArray[1];
	    	} else if (speed >=50 && speed < 70) {
	    		temps = smallSArray[2];
	    	} else if (speed >=70 && speed < 90) {
	    		temps = smallSArray[3];
	    	} else if (speed >=90 && speed < 110) {
	    		temps = smallSArray[4];
	    	} else if (speed >=110 && speed < 130) {
	    		temps = smallSArray[5];
	    	} else if (speed >=130){
	    		temps = smallSArray[6];
	    	} 
			
			AuxWriter.writeAuxFile(timeStampSec,bearing,gxBar, gyBar, gzBar,smallSArray, deltaGv,tmpSpeed);
   		 	SystemParameters.AuxCount++;
   		 	
		 		long temp = timeStamp/1000 - (checkT3+2);
		 		//Log.d("!!!!","inside :"+now + " CHECK :"+ temp);
    
   		 	if((timeStamp/1000) > (checkT3+2)){

   		 		if (bumpingDetection1(deltaGv,tmpSpeed)) {
   		 			c1++;
   		 			Log.d("c1",c1+"");
   		 			//System.out.println(now+", "+" ,"+deltaGv+" ,"+g0.getNorm()+" ,"+Math.abs(deltaGv-g0.getNorm())+", "+(1.2*bigS));
   		 			if (!beginCheck) {  
   		 				//find the biggest gv's timestamp
   		 				checkT2 = timeStamp + T2; //in millisecond
   		 				beginCheck = true;
   		 				eventT = now;
   		 				eventTimeStamp = timeStamp;
   		 				previousLat = lat;
   		 				previousLng = lng;
   		 			}
   		 		}	
   		 		else{
        		C2Buffer.clear();
   		 		}
   		 	}

    		//update sr 
    		now++;
    		index = now;
    		//check if the time is > T1
    		if (isT1) {    					
    			removeElementsWithSpeed();    
    			index = T1/1000 -1;
    		}    				
    				
    		sr.add(new SecondRange(now,timeStamp));    				
    		sr.get(index).setFrom(s);
    		sr.get(index).setSpeed(speed);

        	if(enterCount == 1){
        		previouslat1= lat; previouslng1 = lng;
        		previouslat2 = lat; previouslng2 = lng;
        	}
        	if(enterCount >= 2){
        		previouslat2 = previouslat1; previouslng2 = previouslng1;
        		previouslat1 = lat; previouslng1 = lng;
        		//Log.d("previous",previouslat2+" " +previouslng2 + " " +lat + " " +lng);
        	}
        	
        	enterCount++;

    	}
    	// end of one second
	
    	if (beginCheck) {
    		if (timeStamp < checkT2) {
    			C2Buffer.add(new VC(0,timeStamp,now,gX,gY,gZ,speed));
    		}
    		else{
       			beginCheck = false;
       			calculateSE_pre(speed);
       			System.out.println(eventSize);
    			calculateSE(eventSize);
    			int l = now;
    			if (isT1) {
    				l = T1/1000-1;
    			}
    			//System.out.println(sE);
    			sr.get(l).setSmallSigmaEvent(sE);

      	    	if (speed>=10 && speed < 30) {
      	    		sr.get(l).setSmallSigmaEvent2(sE/smallSArray[0]);
    	    	} else if (speed >=30 && speed < 50) {
    	    		sr.get(l).setSmallSigmaEvent2(sE/smallSArray[1]);
    	    	} else if (speed >=50 && speed < 70) {
    	    		sr.get(l).setSmallSigmaEvent2(sE/smallSArray[2]);
    	    	} else if (speed >=70 && speed < 90) {
    	    		sr.get(l).setSmallSigmaEvent2(sE/smallSArray[3]);
    	    	} else if (speed >=90 && speed < 110) {
    	    		sr.get(l).setSmallSigmaEvent2(sE/smallSArray[4]);
    	    	} else if (speed >=110 && speed < 130) {
    	    		sr.get(l).setSmallSigmaEvent2(sE/smallSArray[5]);
    	    	} else if (speed >=130){
    	    		sr.get(l).setSmallSigmaEvent2(sE/smallSArray[6]);
    	    	} 	

      	    	boolean flag = (eventTimeStamp/1000) > ((checkT3+3));
      	    	
      	    	if(bumpingDetection2(speed) && flag){
       			//if(bumpingDetection2(speed) ){
    				//writeEventWithSpeed(eventT, eventTimeStamp,lat,lng,speed,hit,k);
    				
    				double tempsmallS = 0;
    				double tempbigS =0;
  
    				checkT3 =(long) (peakEventTimeStamp/1000 + 2 + 4.5/speed);
  
          	    	if (speed>=10 && speed < 30) {
          	    		tempsmallS = smallSArray[0];
        	    	} else if (speed >=30 && speed < 50) {
        	    		tempsmallS = smallSArray[1];
        	    	} else if (speed >=50 && speed < 70) {
        	    		tempsmallS = smallSArray[2];
        	    	} else if (speed >=70 && speed < 90) {
        	    		tempsmallS = smallSArray[3];
        	    	} else if (speed >=90 && speed < 110) {
        	    		tempsmallS = smallSArray[4];
        	    	} else if (speed >=110 && speed < 130) {
        	    		tempsmallS = smallSArray[5];
        	    	} else if (speed >=130){
        	    		tempsmallS = smallSArray[6];
        	    	} 	

          	    	//Log.d("testing",eventTimeStamp+" "+previousLat +" "+previousLng+" " +timeStamp+" "+lat+" " +lng);
          	    	
          	    	//previousLat = previouslat2; previousLng = previouslng2;
          	    	//nowLat = previouslat1; nowLng = previouslng1;
          	    	previousTimestamp = peakEventTimeStamp;
          	    	previousSpeed = speed;
          	    	previousSE = sE; previousSmallS = tempsmallS;
          	    	previousHeight = height; previousBearing = bearing;
          	    	previousRatio = ratio;
          	    	previousDeltaGv = deltaGv;
          	    	previousPeakGv = peakGv;
          	    	
          	    	eventFlag = true;
   
    			}
    			
       			eventSize=0;
    			resetSe(); 			
    		}
		
    	}

		addGBuffer(gX,gY,gZ, timeStamp, now, speed);
		VC tempVC = new VC(0,timeStamp,now,gX,gY,gZ,speed);
		C2Buffer.add(tempVC);
		s++;

    }
    
    private void writeEvent(double lat, double lng,double height) throws IOException{

	    	GPSdata gpsobject = new GPSdata();
	    	lat = gpsobject.getExactLat(gpsbuffer, peakEventTimeStamp, ratio);
	    	lng = gpsobject.getExactLng(gpsbuffer, peakEventTimeStamp, ratio);
	    	height = gpsobject.getExactHeight(gpsbuffer, peakEventTimeStamp, ratio);

	    	AnomWriter.writeAnomFile(previousTimestamp, lat, lng, (previousSpeed), previousSE, previousSmallS, previousSE/previousSmallS, height, previousBearing);
	    			//previousDeltaGv,previousDeltaGv/previousSmallS,(previousPeakGv-previousNorm)/previousSmallS);
			//AnomWriter.writeAnomFile(peakEventTimeStamp, lat, lng, (float)(speed), sE, tempsmallS, sE/tempsmallS, height, bearing,deltaGv,deltaGv/tempsmallS,deltaGv/tempbigS);
			SystemParameters.AnomCount++;
			SystemParameters.eventLat = lat;
			SystemParameters.eventLng = lng;
			SystemParameters.eventTime = previousTimestamp ;
			SystemParameters.eventSpeed = (previousSpeed);
			SystemParameters.eventAI = previousSE/previousSmallS;
			SystemParameters.eventSigmaEvent = previousSE;
			SystemParameters.eventSmallSigma = previousSmallS;
			
    		eventFlag = false;
    }
    
    public void onRackTest(){
    	
    	isOnRackTest = true;
    	isFirstSecond = true;
    	
    	g01 =0; g02=0; g03=0;
    	double g01x =0; double g01y =0; double g01z =0;
    	double g02x =0; double g02y =0; double g02z =0;
    	double g03x =0; double g03y =0; double g03z =0;
    	int g01count =0; int g02count =0; int g03count =0;
    	
   		for (int i=0+g0now; i<(T0/1000/3)+g0now; i++){
		    int from = sr.get(i).getFrom();
		    int to = sr.get(i).getTo();
		    
	    	for (int j=from-offset; j<=to-offset; j++) {
	    		SensorValues sV = gBuffer.get(j);
	    		VC vc = vcs.get(j);
	    		g01x += vc.getGX(); g01y += vc.getGY(); g01z += vc.getGZ();
	    		g01count++;
	    	}  
		}
   		
   		for (int i=(T0/1000/3)+g0now; i<((T0/1000/3)*2)+g0now; i++){
		    int from = sr.get(i).getFrom();
		    int to = sr.get(i).getTo();
 	    
	    	for (int j=from-offset; j<=to-offset; j++) {
	    		SensorValues sV = gBuffer.get(j);
	    		VC vc = vcs.get(j);
	    		g02x += vc.getGX(); g02y += vc.getGY(); g02z += vc.getGZ();
	    		g02count++;
	    	}  
		}
   		
   		for (int i=((T0/1000/3)*2)+g0now; i<(T0/1000)+g0now; i++){
		    int from = sr.get(i).getFrom();
		    int to = sr.get(i).getTo();

	    	for (int j=from-offset; j<=to-offset; j++) {
	    		SensorValues sV = gBuffer.get(j);
	    		VC vc = vcs.get(j);
	    		g03x += vc.getGX(); g03y += vc.getGY(); g03z += vc.getGZ();
	    		g03count++;
	    	}  
		}
   		
   		g01x = g01x/g01count; g01y = g01y/g01count; g01z = g01z/g01count;
   		g02x = g02x/g02count; g02y = g02y/g02count; g02z = g02z/g02count;
   		g03x = g03x/g03count; g03y = g03y/g03count; g03z = g03z/g03count;
   		
   		g01 = Math.sqrt(g01x*g01x+g01y*g01y+g01z*g01z);
   		g02 = Math.sqrt(g02x*g02x+g02y*g02y+g02z*g02z);
   		g03 = Math.sqrt(g03x*g03x+g03y*g03y+g03z*g03z);
   		
   		Log.d("VCE", "g01-g02= "+ (g01-g02) + "g01-g03= "+ (g01-g03) + "g02-g03= "+ (g02-g03));
   		
   		if((Math.abs(g01-g02) < C) && (Math.abs(g01-g03) < C) && (Math.abs(g02-g03) < C)){//isonrack
   			isOnRack = true;
   			Toast.makeText(this.context, "onRackTest: TRUE", Toast.LENGTH_LONG).show();
   			SystemParameters.isOnRack = 2;
   		}
   		else{ //not onrack
   			g0now = now;//記錄下一次onracktest的起始描述
   			g0s = s;//記錄下一次onracktest的起始sensor第幾筆
   			Toast.makeText(this.context, "onRackTest: FALSE", Toast.LENGTH_LONG).show();
   			SystemParameters.isOnRack = 3;
   		}
    	
    }
  
    public boolean bumpingDetection1(double deltaGv,float speed) {
    	
    	double smallS = 0;
    	if (speed>=10 && speed < 30) {
    		smallS = smallSArray[0];
    	} else if (speed >=30 && speed < 50) {
    		smallS = smallSArray[1];
    	} else if (speed >=50 && speed < 70) {
    		smallS = smallSArray[2];
    	} else if (speed >=70 && speed < 90) {
    		smallS = smallSArray[3];
    	} else if (speed >=90 && speed < 110) {
    		smallS = smallSArray[4];
    	} else if (speed >=110 && speed < 130) {
    		smallS = smallSArray[5];
    	} else if (speed >=130){
    		smallS = smallSArray[6];
    	} 	

	return deltaGv > SystemParameters.C1*smallS;

	}
    
    public boolean bumpingDetection2(float speed){
    	double C2 = SystemParameters.C2;

	    	
	    	if (speed >=10 && speed < 30) {
	    		if((sE > C2 * smallSArray[0]) && smallSArray[0]>0)
	    			return true;
	    	} else if (speed >=30 && speed < 50) {
	    		if((sE > C2 * smallSArray[1]) && smallSArray[1]>0)
	    			return true;
	    	} else if (speed >=50 && speed < 70) {
	    		if((sE > C2 * smallSArray[2]) && smallSArray[2]>0)
	    			return true;
	    	} else if (speed >=70 && speed < 90) {
	    		if((sE > C2 * smallSArray[3]) && smallSArray[3]>0)
	    			return true;
	    	} else if (speed >=90 &&  speed < 110) {
	    		if((sE > C2 * smallSArray[4]) && smallSArray[4]>0)
	    			return true;
	    	} else if (speed >=110 && speed < 130) {
	    		if((sE > C2 * smallSArray[5]) && smallSArray[5]>0)
	    			return true;
	    	} else if (speed >=130){
	    		if((sE > C2 * smallSArray[6]) && smallSArray[6]>0)
	    			return true;
	    	} 
    	
	    	return false;
    }

	public void addGBuffer(double gX, double gY, double gZ, long timeStamp, int numOfSec, float speed) {
    	SensorValues sV= new SensorValues(gX,gY,gZ);
		gBuffer.add(sV);
		vcs.add(new VC(s,timeStamp,numOfSec,gX,gY,gZ,speed));
    }

    public void calculateVCWithSpeed(int from, int to,int now) throws IOException {
    	double bigGv = 0, smallGv = 0;
    	deltaGv =0;
    	float speed =0;
    	boolean isStable;
    	DeltaGv deltaGvObject = new DeltaGv();
    	
    	//1. 找出deltaGv
    	for (int i=from-offset; i<=to-offset; i++) {
    		SensorValues sV = gBuffer.get(i);
    		VC vc = vcs.get(i);
    		double gV = SensorValues.projection(sV, g0).getNorm();
    		speed = vc.getSpeed();
    		vc.setGv(gV);
    		
    		deltaGvObject.setnumOfSec(now);
    		deltaGvObject.addGvList(gV);
    		
    		//writeVC(vc);
    		AccWriter.writeAccFile(vc);   		
    		SystemParameters.AccCount++;
    		
    		if(i == (from-offset)){
    			smallGv = gV;
    			bigGv = gV;
    		}
    		else{
    			if(gV > bigGv)
    				bigGv = gV;
    			else if(gV < smallGv)
    				smallGv = gV;
    		}

    	}  	
    	
    	deltaGvObject.getDeltaGv();
    	deltaGv = bigGv - smallGv;
    	
    	if(bigGv == smallGv && now == 30)
    		deltaGv = Math.abs(bigGv - 8.2);
 	
    	//2. 把deltaGV加到對應的speed的set�堶�   ，用來計算各個速度區間的 smallSigma
  	    	
  	    	if (speed >=10 && speed < 30) {
  	    		smallSigmaSet.elementAt(0).add(deltaGvObject);
	    	} else if (speed >=30 && speed < 50) {
  	    		smallSigmaSet.elementAt(1).add(deltaGvObject);
	    	} else if (speed >=50 && speed < 70) {
  	    		smallSigmaSet.elementAt(2).add(deltaGvObject);
	    	} else if (speed >=70 && speed < 90) {
  	    		smallSigmaSet.get(3).add(deltaGvObject);
	    	} else if (speed >=90 && speed < 110) {
  	    		smallSigmaSet.get(4).add(deltaGvObject);
	    	} else if (speed >=110 && speed < 130) {
  	    		smallSigmaSet.get(5).add(deltaGvObject);
	    	} else if (speed >= 130 ) {
  	    		smallSigmaSet.get(6).add(deltaGvObject);
	    	} 
  		
    	//3. after initialized 過後 ， 把所有的 x,y,z都加到G0的set

    		if (isInitialized) {	
    			for (int i=from-offset; i<=to-offset; i++){
    	    		SensorValues sV = gBuffer.get(i);
    	    		accumulatedACCforG0(sV.getXvalue(),sV.getYvalue(),sV.getZvalue());
    			}
    		}

    }
    
    public void accumulatedACCforG0(double gX, double gY, double gZ) {
    	g0CumulatedAcc[0]+=gX;
		g0CumulatedAcc[1]+=gY;
		g0CumulatedAcc[2]+=gZ;
		g0count++;
    }

    public void accumulatedACCforSE(double gV) {
    	cumulatedsE += gV;
		cumulatedSquaredsE += gV * gV;
    }
    
    public void resetSe() {
    	cumulatedsE = 0;
    	cumulatedSquaredsE = 0;
    	sE = -1f;
    }
    
    public void calculateG0(int s) {
    	g0 = new SensorValues(g0CumulatedAcc[0]/s, g0CumulatedAcc[1]/s, g0CumulatedAcc[2]/s);
    //	System.out.println("calculate g0 " + s);
    }

    public void calculateSmallS(int index) {
    	double avg=0,total=0;
    	DeltaGv deltaGvObject = new DeltaGv();

    	while(smallSigmaSet.get(index).size()>maxb)
    		smallSigmaSet.get(index).remove(0);
    	//計算後放在array裡面
    	smallSArray[index] = deltaGvObject.getSmallSigma(smallSigmaSet.get(index),index);

    }

    public void calculateSE(int s) {
    	sE = Math.sqrt((cumulatedSquaredsE - (cumulatedsE*cumulatedsE)/s)/(s-1));
    }
    
    public void calculateSE_pre(float speed) throws IOException{
    	Double bigGv=0.0;
    	Long bigTimestamp = null;
    	Long endTimeStamp =null;
    	VC tempVc ;
    	for(int i=0;i<C2Buffer.size();i++){
    		tempVc = C2Buffer.get(i);
			Double gV = SensorValues.projection(new SensorValues(tempVc.getGX(),tempVc.getGY(),tempVc.getGZ()), g0).getNorm();
			tempVc.setGv(gV);
    		if(i==0){
    			bigGv = tempVc.getGV();
    			bigTimestamp = tempVc.getTime();
    		}
    		else if(tempVc.getGV() > bigGv){
    			bigGv = tempVc.getGV();
    			bigTimestamp = tempVc.getTime();
    		}
    	}
    	peakGv = bigGv;
    	endTimeStamp = bigTimestamp+T2;

    	ratio = (double) (bigTimestamp - ((bigTimestamp/1000)*1000))/1000;
	
    	peakEventTimeStamp = bigTimestamp;

    	VC tempVc2;
    	for(int i=0;i<C2Buffer.size();i++){
    		tempVc2 = C2Buffer.get(i);
    		Double gV = SensorValues.projection(new SensorValues(tempVc2.getGX(),tempVc2.getGY(),tempVc2.getGZ()), g0).getNorm();
			tempVc2.setGv(gV);
    		if(tempVc2.getTime() < endTimeStamp && tempVc2.getTime()>bigTimestamp){
    			accumulatedACCforSE(tempVc2.getGV());
    			//System.out.println("Gv: "+tempVc2.getGV());
    			eventSize++;
    		}
    		else if (tempVc2.getTime() > endTimeStamp)
    			break;
    	}
    	
    	C2Buffer.clear();
    		
    }

    public void removeElementsWithSpeed() {
    	int from = sr.get(0).getFrom();
    	int to = sr.get(0).getTo();

    	for (int i=from; i<=to; i++) {
    		//Things would be removed: g0CumulatedAcc, cumulatedUAcc, cumulatedUSquaredAcc
    		//							cumulatedSAcc, cumulatedSSquaredAcc.
    		VC vc  = vcs.get(0);

    		if (sr.get(0).getStableStatus()) {
    			g0CumulatedAcc[0] -= vc.getGX(); g0CumulatedAcc[1] -= vc.getGY(); g0CumulatedAcc[2] -= vc.getGZ();
    			g0count--;
    		}
    		vcs.remove(0);
    		gBuffer.remove(0);
    		offset++;
    	}    	
    	sr.remove(0);
    }
    

}
