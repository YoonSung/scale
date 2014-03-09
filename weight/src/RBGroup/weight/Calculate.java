package RBGroup.weight;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.net.Uri;
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

public class Calculate extends Activity {

	private Preview preview;
	private final String myPictureName = "weight.png";
	
	Handler handler = new Handler(){
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
						File file = new File(filePath + File.separator + myPictureName);
						OutputStream out = null;
						 
						try {
						    //빈 파일 생성 및 스트림 생성
						    file.createNewFile();
						    out = new FileOutputStream(file);
						    //비트맵 이미지를 압축하면서 스트림으로 보냄
						    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)) {
						 
						    }
						    //저장된 이미지를 화면에 표시(메소드를 만들어야 함)
						    //onSetImage(saveFile.toString());
						}
						catch (Exception e) {
						    e.printStackTrace();
						}
						finally {
						    try {
						        out.close();
						 
						    }
						    catch (IOException e) {
						        e.printStackTrace();
						    }
						}
						
						handler.sendEmptyMessageDelayed(1, 0);
					}
				});		
			}else if(msg.what==1){
//				Intent intent = new Intent(Calculate.this, Realize.class);
//				startActivity(intent);
				finish();
			}
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    preview = new Preview(this);
	    
	    setContentView(preview);
	    
	    handler.sendEmptyMessageDelayed(0, 1000);
	}
}
