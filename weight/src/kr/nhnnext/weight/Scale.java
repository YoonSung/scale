package kr.nhnnext.weight;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Scale extends Activity implements OnClickListener{

	private long interval = 10000L;
	private LinearLayout strelka;
	private RotateAnimation rotateanimation5; 
	private Button btnUp, btnDown, btnStart;
	private Handler handler = new Handler();
	private SharedPreferences sp;
	private boolean isman = true;
	private int height = 150;
	private float average_weight;
	private TextView txtNotice;
	private int from;
	private int to;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    setContentView(R.layout.scale);

        strelka = (LinearLayout)findViewById(R.id.strelka);
        btnUp = (Button)findViewById(R.id.btnUp);
        btnDown = (Button)findViewById(R.id.btnDown);
        btnStart = (Button)findViewById(R.id.btnStart);
        txtNotice = (TextView)findViewById(R.id.txtNotice);
        
        sp = getSharedPreferences("info", MODE_PRIVATE);
        isman = sp.getBoolean("isman", true);
        height = sp.getInt("height", 170); 
        
        if (height < 150) {
        	average_weight = height - 100;
        } else if ( 150 < height && height <160) {
        	average_weight = (float) (( height - 150 ) * 0.5) + 50; 
        } else {
        	average_weight =  (float) (( height - 100 ) * 0.9);
        }
        
        from = 0;
        to = (int) (360*5 + average_weight);
        
        Log.e("Scale", "height : "+height+"      /      isman : "+isman);
        
        btnUp.setOnClickListener(this);
        btnDown.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        
    	
	}
	Handler myTask=new Handler(){
		public void handleMessage(android.os.Message message){
	        switch(message.what){
	        case 0:
	        	txtNotice.setText("측정중....");
	        	mUpdateUi();
	        	break;
	        case 1:
	        	txtNotice.setText("귀하의 몸무게는 '"+average_weight+"' 입니다.");
	        	break;
	        case 2:
	        	startActivity(new Intent(Scale.this, Calculate.class));
	        	Scale.this.finish();
	        	break;
	        }
	        
		}
	};
	

	private void mUpdateUi()
    {   
		Log.e("Scale", "from : "+from + "   /   to : "+to);
		//rotateanimation5 = new RotateAnimation(from, to / 1.0F*/, 1, 0.5F, 1, 1.0F);
		rotateanimation5 = new RotateAnimation(from, to, 1, 0.5F, 1, 1.0F);
        rotateanimation5.setDuration(interval);
        
        //rotateanimation5.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.accelerate_interpolator));  
        //rotateanimation5.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.bounce_interpolator)); 
        rotateanimation5.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.overshoot_interpolator));
        
        rotateanimation5.setFillEnabled(true);
        rotateanimation5.setFillAfter(true);
        strelka.startAnimation(rotateanimation5);   
	}
	/*
	private final Runnable mUpdate = new Runnable(){
		public void run(){
			mUpdateUi();
		}
	};
*/
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btnUp){
			from = to;
			to = to + 30;
		}else if(v.getId()==R.id.btnDown){
			from = to;
			to = to - 30;
		}else if(v.getId()==R.id.btnStart){
			btnStart.setVisibility(View.INVISIBLE);
			btnStart.setClickable(false);

			myTask.sendEmptyMessageDelayed(0, 1000);
			myTask.sendEmptyMessageDelayed(1, 11000);
			myTask.sendEmptyMessageDelayed(2, 12000);
/*
			preview.Capture(new Camera.PictureCallback() {
				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					ByteArrayInputStream in = new ByteArrayInputStream(data);
					//in.read(data);
					Bitmap bitmap = BitmapFactory.decodeStream(in);
					img.setImageBitmap(bitmap);
				}
			});*/
			//myTask.sendEmptyMessageDelayed(0, 4000);
	    	//myTask.sendEmptyMessageDelayed(0, 6000);
		}
		//handler.post(mUpdate);
	}
}
