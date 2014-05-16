package RBGroup.weight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
				
		Handler myTask=new Handler(){
    		public void handleMessage(android.os.Message message){
    			startActivity(new Intent(Splash.this, InitialSet.class));
    			//startActivity(new Intent(Splash.this, Calculate.class));
    			finish();
    		}
    	};
    	myTask.sendEmptyMessageDelayed(0, 800);
	}
}
