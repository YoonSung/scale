package kr.nhnnext.weight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Example extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.example);
		
	    Handler myTask=new Handler(){
    		public void handleMessage(android.os.Message message){
    			startActivity(new Intent(Example.this, Scale.class));
    			finish();
    		}
    	};
    	myTask.sendEmptyMessageDelayed(0, 1000);
	}

}
