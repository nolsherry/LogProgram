package com.example.logprogram;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class C_Camera extends Activity{
	private Button camera,choose,comfirm,goback; 
	private ImageView preview;
	//private DisplayMetrics mphone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��ܤ���
		setContentView(R.layout.c_camera);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //mphone = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(mphone);
        camera = (Button)findViewById(R.id.c_take_picture);
        choose = (Button)findViewById(R.id.c_choose_picture);
        preview = (ImageView)findViewById(R.id.preview);
        comfirm = (Button)findViewById(R.id.c_comfirm);
        goback = (Button)findViewById(R.id.c_goback);     
    	camera.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View v) {
    		// TODO Auto-generated method stub
    			c_camera();	
    		}
    		
    	});
    	choose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				c_choose();
			}
		});
    	goback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				c_goback();
			}
		});
	}
	@Override
	protected void onActivityResult(int reqCode,int resCode,Intent data){
		super.onActivityResult(reqCode, resCode, data);
		if(resCode ==Activity.RESULT_OK){
			switch(reqCode){
			case 100: //�����Ӥ��ɪ��B�z�A�B�z�Ǧ^�����		
				c_camera();
				break;
				/*Bundle extras = data.getExtras();//�Nintent�����[����ରBundle����
				Bitmap bmp = (Bitmap)extras.get("data");//���o�Ϥ��Y��
				preview.setImageBitmap(bmp)	;//�NBitmap������ܦbImageView��		
				Toast.makeText(this,"��Ӧ��\", Toast.LENGTH_LONG);*/
				
				
			case 101:
				c_choose();
				break;
				
			}
		
		}
	}
    public void c_camera(){
		String dir = Environment.getExternalStoragePublicDirectory(
				     Environment.DIRECTORY_PICTURES).toString();//���o�t�Ϊ����ι��ɸ��|
		String fname = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpg";
		Uri imgUri = Uri.parse("file://" + dir + "/" +fname);//�̫e�������|�إ�Uri����
		Intent it = new Intent("android.media.action.IMAGE CAPTURE");//�秹�ӷ|�I�sonActivityResult
		it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
		startActivityForResult(it,100);//��intent�Ұʵ{��,100���ۭq���ѧO�X
		Bitmap bmp = BitmapFactory.decodeFile(imgUri.getPath());
		preview.setImageBitmap(bmp);
		//startActivityForResult(intent,CAMERA);
		//Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
 
    }
    public void c_choose(){
    	
    	Intent it = new Intent(Intent.ACTION_GET_CONTENT);//������e
    	it.setType("image/*");//�]�w�n������C��������:�Ҧ��������Ϥ�
    	startActivityForResult(it, 101);//�Ұ�Intent,�D�Ǧ^������ɮ�
    	
    	
    }
	public void c_goback(){
		Intent intent = new Intent();
		intent.setClass(C_Camera.this, C_Artificial_Return.class);
		startActivity(intent);
		C_Camera.this.finish();	
	}

}
