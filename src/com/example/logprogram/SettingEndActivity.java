package com.example.logprogram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**************  
 * Activity that can set and save the information:
 * 1. Collector
 * 2. Plate Number(VID)
 * 3. Memo
 * *****************/

public class SettingEndActivity extends Activity {
	
	private Button bt_informationOK;
	private TextView tv_information;
	private TextView tv_collector;
	private TextView tv_memo;
	private TextView tv_plateNumber;
	private EditText et_collector;
	private EditText et_memo;
	private EditText et_plateNumber;
	private EditText et_phoneModel;
	private EditText et_vehicleModel;
	
	private Spinner sp_mountingmethod;
	private Spinner sp_mountinglocation;
	private Spinner spinner1;
	
	
	public LogFileWriter ReadmeWriter;
	private FileOutputStream outputStream;
	private boolean isWrite=true;
	
	private final String blank = " ";
	private final String nothing = "";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settingend);
		ReadmeWriter = new LogFileWriter(this,"ReadMe.txt",6);
		
		makeCacheDir();		
			
		findViews();
		try {
			initialize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
	}
	
	private void findViews()
	{
		bt_informationOK = (Button)findViewById(R.id.bt_informationOK);
		tv_collector = (TextView)findViewById(R.id.tv_collector);
		tv_memo = (TextView)findViewById(R.id.tv_memo);
		tv_plateNumber = (TextView)findViewById(R.id.tv_plateNumber);
		et_collector = (EditText)findViewById(R.id.et_collector);
		et_memo = (EditText)findViewById(R.id.et_memo);
		et_plateNumber = (EditText)findViewById(R.id.et_plateNumber);
		et_phoneModel = (EditText)findViewById(R.id.et_phonemodel);
		et_vehicleModel = (EditText)findViewById(R.id.et_vehiclemodel);
		sp_mountingmethod = (Spinner) findViewById(R.id.sp_mountingmethod);
		sp_mountinglocation = (Spinner) findViewById(R.id.sp_mountinglocation);
	}
	
	private void makeCacheDir(){
		String path = Environment.getExternalStorageDirectory().getPath();
	    File dir;
	    File file;	    
	    String fileName = "cache.log";
		dir = new File(path + "/NOL/BumpsOnRoads/cache");
    	if (!dir.exists()){ 
    		
    		dir.mkdirs();
	    	file = new File(path + "/NOL/BumpsOnRoads/cache", fileName);

			String outputString = " / / / / / /5.0/2.5/";

			try {
				outputStream = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				outputStream.write(outputString.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} 	
		
	}
	
	public void initialize() throws IOException{

		if(SystemParameters.Collector.equals(blank))
			SystemParameters.Collector = "";
		if(SystemParameters.VID.equals(blank))
			SystemParameters.VID = "";
		if(SystemParameters.Memo.equals(blank))
			SystemParameters.Memo = "";
		if(SystemParameters.PhoneModel.equals(blank))
			SystemParameters.PhoneModel = "";
		if(SystemParameters.VehicleModel.equals(blank))
			SystemParameters.VehicleModel = "";
			
		et_collector.setText(SystemParameters.Collector);
		et_plateNumber.setText(SystemParameters.VID);
		et_memo.setText(SystemParameters.Memo);
		et_phoneModel.setText(SystemParameters.PhoneModel);
		et_vehicleModel.setText(SystemParameters.VehicleModel);
		
		//spinner mounting method
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this,android.R.layout.simple_spinner_item,new String[]{"Rack","Adhere"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_mountingmethod.setAdapter(adapter);
        sp_mountingmethod.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
		
        	@Override
        	public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
			// TODO Auto-generated method stub
        		SystemParameters.MountingMethod = adapterView.getSelectedItem().toString();		
        		//Toast.makeText(SettingActivity.this, "�z���"+adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView arg0) {
			// TODO Auto-generated method stub
							
			}
        });
        
        //spinner mounting location
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
				this,android.R.layout.simple_spinner_item,new String[]{"Front","Side","Rear"});
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_mountinglocation.setAdapter(adapter2);
        sp_mountinglocation.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
		
        	@Override
        	public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
			// TODO Auto-generated method stub
        		SystemParameters.MountingLocation = adapterView.getSelectedItem().toString();		
        		//Toast.makeText(SettingActivity.this, "�z���"+adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView arg0) {
			// TODO Auto-generated method stub
							
			}
	});
        
        
	}
	
	
	//press save button
	public void gotoSave(View view) throws IOException
	{
		SystemParameters.Collector = et_collector.getText().toString();
		SystemParameters.Memo = et_memo.getText().toString();
		SystemParameters.VID = et_plateNumber.getText().toString();
		SystemParameters.isSetting = true;//record that the user is already set the information
		SystemParameters.PhoneModel = et_phoneModel.getText().toString();
		SystemParameters.VehicleModel = et_vehicleModel.getText().toString();
		
		saveInformation();
		showLogInformationDialog();

	}
	
	private void saveInformation() throws IOException{
		if(isExternalStorageWritable()){
			String path = Environment.getExternalStorageDirectory().getPath();
		    File dir;
		    File file;
			dir = new File(path + "/NOL/BumpsOnRoads/cache");
	    	if (!dir.exists()){
	    		dir.mkdirs();
	    	}
	    	String fileName = "cache.log";
	    	file = new File(path + "/NOL/BumpsOnRoads/cache", fileName);
	    	
			if(SystemParameters.Collector.equals(nothing))
				SystemParameters.Collector = " ";
			if(SystemParameters.VID.equals(nothing))
				SystemParameters.VID = " ";
			if(SystemParameters.Memo.equals(nothing))
				SystemParameters.Memo = " ";
			if(SystemParameters.PhoneModel.equals(nothing))
				SystemParameters.PhoneModel = " ";
			if(SystemParameters.VehicleModel.equals(nothing))
				SystemParameters.VehicleModel = " ";
	    	
			String outputString = 		SystemParameters.Collector +"/"+
										SystemParameters.VID +"/"+SystemParameters.PhoneModel+"/"+
										SystemParameters.VehicleModel+"/"+SystemParameters.MountingMethod+"/"+
										SystemParameters.MountingLocation+"/"+
										SystemParameters.C1+"/"+SystemParameters.C2+"/"+
										SystemParameters.Memo;
	    	
			outputStream = new FileOutputStream(file);
			outputStream.write(outputString.getBytes());
		}

	}
	
	private void loadInformation() throws IOException{
		String path = Environment.getExternalStorageDirectory().getPath();
	    File dir = new File(path + "/NOL/BumpsOnRoads/cache");

		//Get the text file
		File file = new File(dir,"cache.log");
		BufferedReader br = null;
	
		try {
			br = new BufferedReader(new FileReader(file));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String Line;
		Line = br.readLine();
		StringTokenizer token = new StringTokenizer(Line, "/");
		
		SystemParameters.Collector = token.nextToken().toString();
		SystemParameters.VID = token.nextToken().toString();
		SystemParameters.PhoneModel = token.nextToken().toString();
		SystemParameters.VehicleModel = token.nextToken().toString();
		SystemParameters.MountingMethod = token.nextToken().toString();
		SystemParameters.MountingLocation = token.nextToken().toString();
		SystemParameters.C1= Double.valueOf(token.nextToken().toString()).doubleValue();
		SystemParameters.C2= Double.valueOf(token.nextToken().toString()).doubleValue();
		SystemParameters.Memo = token.nextToken().toString();
		
	}
	
	public void showLogInformationDialog(){//and also write readme.txt
		
		try {
			ReadmeWriter.writeReadMeFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingEndActivity.this);
		alertDialogBuilder.setTitle("Log Information")
						.setMessage("Collector: " +SystemParameters.Collector +"\n"
								+"Plate Number: " + SystemParameters.VID +"\n"
								+"Memo: " +SystemParameters.Memo+"\n\n"
								+ "Duration: " + SystemParameters.Duration + "\n"
								+ "Mileage: "+SystemParameters.Mileage+" (KM)\n\n"
								+ "GpsFile: "+SystemParameters.GpsCount+" records\n"
								+"AccFile: "+SystemParameters.AccCount+" records\n"
								+"MagFile: "+SystemParameters.MagCount+" records\n"
								+"AuxFile: "+SystemParameters.AuxCount+" records\n"
								+"AnomFile: "+SystemParameters.AnomCount+" records\n\n"
								+"6 files are created.")
						.setPositiveButton("OK",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, close
								// current activity
								//MainActivity.this.finish();
								SettingEndActivity.this.finish();
								
								//Intent intent = new Intent(RecordActivity.this, SettingActivity.class);
								//startActivity(intent);
							}
						  })

						  
						.show();
		
		
	}
	
	public void gotoCancel(View view)
	{
		finish();
	}
	
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

}