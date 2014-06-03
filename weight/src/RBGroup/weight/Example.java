package RBGroup.weight;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Example extends BannerActivity {

	Handler myTask;
	boolean isClicked = false;
	TextView txtNext;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.example);
		
		txtNext = (TextView)findViewById(R.id.txtNext);
		FrameLayout layout = (FrameLayout) findViewById(R.id.example_entire_layout);
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isClicked = true;
			}
		});
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,      
				FrameLayout.LayoutParams.WRAP_CONTENT
		);

		params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
		
		createSmartBanner(this, findViewById(R.id.example_banner));
		params.setMargins(0, 0, 0, getBannerHeight(this)+10);
		txtNext.setLayoutParams(params);
		
	    myTask=new Handler(){
    		public void handleMessage(android.os.Message message){
    			switch(message.what) {
    			case 0:
    				if (isClicked) {
    					myTask.sendEmptyMessageDelayed(2, 0);
    				} else {
    					txtNext.setVisibility(View.VISIBLE);
    					myTask.sendEmptyMessageDelayed(1, 500);
    				}
    				break;
    			case 1:
    				if (isClicked) {
    					myTask.sendEmptyMessageDelayed(2, 0);
    				} else {
    					txtNext.setVisibility(View.INVISIBLE);
    					myTask.sendEmptyMessageDelayed(0, 500);
    				}
    				break;
    			case 2:
    				startActivity(new Intent(Example.this, Scale.class));
        			finish();
    				break;
    			}
    		}
    	};
    	myTask.sendEmptyMessageDelayed(0, 0);
	}

}
