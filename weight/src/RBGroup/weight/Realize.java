package RBGroup.weight;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Realize extends Activity implements OnClickListener{

	ImageView imageView;
	private final String myPictureName = "weight.png";
	Common common;
	File filePath;
	ProgressDialog progressDialog;
	String recommendURL;
	ImageButton btnKakao, btnTwitter, btnFacebook ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.realize);
	    
	    imageView = (ImageView)findViewById(R.id.myPicture);
	    btnKakao=(ImageButton)findViewById(R.id.btnKakao);
	    btnTwitter=(ImageButton)findViewById(R.id.btnTwitter);
	    btnFacebook=(ImageButton)findViewById(R.id.btnFacebook);
	    btnKakao.setOnClickListener(this);
	    btnTwitter.setOnClickListener(this);
	    btnFacebook.setOnClickListener(this);
	    
	    common = new Common(this);
	    filePath = getFileStreamPath( myPictureName);
	    Log.e("Realize", "filePath : "+filePath);
	    
	    try {
	    	imageView.setImageDrawable(Drawable.createFromPath(filePath.getAbsolutePath()));
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	@Override
	public void onClick(View v) {
			String message ="";
		    String referenceURLString = "https://market.android.com/details?hl=ko&id=com.DooitLocalResearch";
		    String appVersion = "1.0";
			if(v.getId()==R.id.btnKakao){
				  try { 
				        ArrayList< Map < String, String > > arrMetaInfo = new ArrayList< Map< String, String > >();
				        Map < String, String > metaInfoAndroid = new Hashtable < String, String >(1);
				        metaInfoAndroid.put("os", "android");
				        metaInfoAndroid.put("devicetype", "phone");
				        metaInfoAndroid.put("installurl", "market://details?id=com.DooitLocalResearch");
				        metaInfoAndroid.put("executeurl", "market://details?id=com.DooitLocalResearch");
				        arrMetaInfo.add(metaInfoAndroid);
				        KakaoLink link = new KakaoLink(this, "www.dooit.co.kr", referenceURLString, appVersion, 
				        		message, "test", arrMetaInfo, "UTF-8");
				        if (link.isAvailable()) {
				        	startActivity(link.getIntent());
				        } else {
				        	common.centerToast(Realize.this, "File Upload Error.");
				        }
				  }catch(UnsupportedEncodingException e){
				  e.printStackTrace(); 
				  }		
			}else{
			switch(v.getId()){
			case R.id.btnTwitter :
				recommendURL="http://twitter.com/home?status="+message+"test : \""+referenceURLString;
				break;
			case R.id.btnFacebook :
				recommendURL="http://www.facebook.com/sharer/sharer.php?s=100&p[url]="+referenceURLString
									+"&p[images][0]=http://dooit.co.kr/include/img/dooit/btn_main01.gif&p[title]="+message;
				break;
			}
		
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(recommendURL) ));
		//
		final Handler handler = new Handler();
		new Thread() {
			public void run() {
				
				handler.post(new Runnable() {
					@Override
					public void run() {
						progressDialog = ProgressDialog.show(Realize.this, "", "Wait For Seconds");
					}
				});
				
				String id = common.saveID();
				Float weight = common.getWeight();
				
				if ( id != null ) {
					common.uploadDataToServer(id, weight, filePath.getAbsolutePath());
				} else {
					common.centerToast(Realize.this, "File Upload Error.");
				}
				
				handler.post(new Runnable() {
					@Override
					public void run() {
						progressDialog.cancel();
					}
				});
			};
		}.start();
	}
	}
}
