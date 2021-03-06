package com.weight;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;

public class SnapShot extends Activity {

	private final class HandlerExtension extends Handler {
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0){
				preview.Capture(new Camera.PictureCallback() {
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						
						ByteArrayInputStream in = new ByteArrayInputStream(data);
						//in.read(data);
						Bitmap bitmap = BitmapFactory.decodeStream(in);
						
						String filePath = getApplicationContext().getFilesDir().getPath();
						//Uri saveFile = FileUtil.getTemporaryFileName();
						//경로 기반으로 파일 인스턴스 생성
						File file = new File(filePath + File.separator + PICTURENAME);
						OutputStream out = null;
						 
						try {
						    //빈 파일 생성 및 스트림 생성
						    file.createNewFile();
						    out = new FileOutputStream(file);
						    //비트맵 이미지를 압축하면서 스트림으로 보냄
						    
						    //bitmap.compress(Bitmap.CompressFormat.PNG, 85, out);
						    if (bitmap.compress(Bitmap.CompressFormat.PNG, 85, out)) {
						    	new Common(SnapShot.this).saveAndReturnID();
						    }
						}
						catch (Exception e) {
						    e.printStackTrace();
						}
						finally {
						    try {
						        out.close();
						        in.close();
						    }
						    catch (IOException e) {
						        e.printStackTrace();
						    }
						}
						
						handler.sendEmptyMessageDelayed(1, 0);
					}
				});		
			}else if(msg.what==1){
				finish();
			}
		}
	}

	private Preview preview;
	private final String PICTURENAME = "weight.png";
	
	Handler handler = new HandlerExtension();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    preview = new Preview(this);
	    
	    setContentView(preview);
	    
	    handler.sendEmptyMessageDelayed(0, 1000);
	}
}
