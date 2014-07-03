package com.weight;

import java.io.File;

import com.weight.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class Splash extends Activity {

	private final String myPictureName = "weight.png";
	
	private final class HandlerExtension extends Handler {
		public void handleMessage(android.os.Message message){
			
			Common common = new Common(Splash.this);
			
			//Check Already Share Picture
			//네트워크 요청 후 Path정보를 가져온뒤, File이 존재하는지 체크한다.
		    if ( common.getID() != null ) {
		    	Log.e("Splash", "before reading");
		    	File file = getFileStreamPath(myPictureName);
		    	boolean isFileExists = false;
		    	
		    	Log.e("Splash", "file : " + file);
		    	if (file != null) {
		    		isFileExists= file.exists();
		    	}
		    
				Log.e("Splash", "filePath : " + file);
		    	
	    		Log.e("Splash", "isFileExists : " + isFileExists);
	    		
	    		//만약 공유한 내용이 존재하면
	    		if ( isFileExists == true) {
	    			startActivity(new Intent(Splash.this, Realize.class));
	    			finish();
	    			return;
	    		}
		    }
		    startActivity(new Intent(Splash.this, InitialSet.class));
		    finish();
			//startActivity(new Intent(Splash.this, Calculate.class));
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		Handler myTask=new HandlerExtension();
    	myTask.sendEmptyMessageDelayed(0, 1500);
	}
}
