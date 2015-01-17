package com.example.logprogram;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class E_ProgramDescription extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e_program_description);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
}
