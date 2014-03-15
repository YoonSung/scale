package RBGroup.weight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class Example extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.example);
		
		FrameLayout layout = (FrameLayout) findViewById(R.id.example_entire_layout);
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Example.this, Scale.class));
    			finish();
			}
		});
		
//	    Handler myTask=new Handler(){
//    		public void handleMessage(android.os.Message message){
//    			startActivity(new Intent(Example.this, Scale.class));
//    			finish();
//    		}
//    	};
//    	myTask.sendEmptyMessageDelayed(0, 1000);
	}

}
