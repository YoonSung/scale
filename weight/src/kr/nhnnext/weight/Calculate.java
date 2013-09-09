package kr.nhnnext.weight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Calculate extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	
	private Preview preview;
	private Button btn_capture, btn;
	private static int wrap_content = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static int match_parent = LinearLayout.LayoutParams.MATCH_PARENT;
	private TextView tv;
	
	
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0){
				preview.Capture(new Camera.PictureCallback() {
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						/*
						ByteArrayInputStream in = new ByteArrayInputStream(data);
						//in.read(data);
						Bitmap bitmap = BitmapFactory.decodeStream(in);
						img.setImageBitmap(bitmap);
						*/
					}
				});		
			}else if(msg.what==1){
				btn.setVisibility(View.VISIBLE);
				tv.setText("설마.. 몸무게 측정이 된다고 생각하세요...?;;;");
				tv.setVisibility(View.VISIBLE);
				tv.setBackgroundColor(Color.WHITE);
				btn_capture.setVisibility(View.VISIBLE);
			}
			
			

		};
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    preview = new Preview(this);
	    
	    
	    tv = new TextView(this);
	    tv.setVisibility(View.INVISIBLE);
	    
	    tv.setTextSize(20);
	    tv.setTextColor(Color.BLACK);
	    tv.setTypeface(null, Typeface.BOLD);
	    
	    FrameLayout.LayoutParams tv_param = new FrameLayout.LayoutParams(wrap_content, wrap_content);
	    
	    
	    FrameLayout top = new FrameLayout(this);
	    FrameLayout.LayoutParams top_param = new FrameLayout.LayoutParams(match_parent, match_parent);
	    
	    btn = new Button(this);
	    btn.setTag(0);
	    btn.setVisibility(View.INVISIBLE);
	    FrameLayout.LayoutParams btn_param = new FrameLayout.LayoutParams(wrap_content, wrap_content);
	    btn_param.gravity = Gravity.BOTTOM;
	    //btn.setGravity(Gravity.BOTTOM);
	    btn_param.setMargins(20, 50, 20, 20);
	    
	    btn.setText("다시측정");
	    btn.setOnClickListener(this);
	    
	    btn_capture = new Button(this);
	    btn_capture.setTag(1);
	    btn_capture.setVisibility(View.INVISIBLE);
	    FrameLayout.LayoutParams btn_capture_param = new FrameLayout.LayoutParams(wrap_content, wrap_content);
	    btn_capture_param.gravity = Gravity.CENTER;
	    btn_capture.setText("된다고 믿은 사람들 보기");
	    
	    btn_capture.setTextSize(20);
	    btn_capture.setTextColor(Color.BLACK);
	    btn_capture.setTypeface(null, Typeface.BOLD);
	    
	    btn_capture.setOnClickListener(this);
	    
	    top.addView(preview);
	    top.addView(tv, tv_param);
	    top.addView(btn,btn_param);
	    top.addView(btn_capture, btn_capture_param);
	    //setContentView(preview);
	    setContentView(top, top_param);
	    
	    handler.sendEmptyMessageDelayed(0, 1000);
	    handler.sendEmptyMessageDelayed(1, 2000);
	}


	@Override
	public void onClick(View v) {
		
		if(v.getTag().equals(0)){
			startActivity(new Intent(Calculate.this, Scale.class));
			finish();
		} else if (v.getTag().equals(1)) {
			new AlertDialog.Builder(this)
			//.setIcon(R.drawable.ic_launcher) 
			//.setTitle("")
			.setMessage("다른 사람들을 보시기 위해서는\n귀하의 사진도 공개를 하셔야 합니다. ")
			.setPositiveButton("함께하기", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(Calculate.this, "준비중", 2000).show();
				}
			})
			.setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create().show();
		}
	}
}
