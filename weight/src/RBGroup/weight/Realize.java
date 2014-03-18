package RBGroup.weight;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class Realize extends Activity implements OnClickListener {

	ImageView imageView;
	private final String myPictureName = "weight.png";
	Common common;
	File filePath;
	ProgressDialog progressDialog;
	Button btnShare, myPictureShareBtn;

	// share layout
	FrameLayout layoutShare;
	ImageButton btnShareFacebook, btnShareKakao, btnShareKakaoStory, btnExit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.realize);

		imageView = (ImageView) findViewById(R.id.myPicture);
		btnShare = (Button) findViewById(R.id.btnShare);
		myPictureShareBtn = (Button) findViewById(R.id.myPictureShareBtn);
		
		// share layout
		layoutShare = (FrameLayout) findViewById(R.id.layoutShare);
		btnExit = (ImageButton) findViewById(R.id.btnExit);
		btnShareFacebook = (ImageButton) findViewById(R.id.btnShareFacebook);
		btnShareKakao = (ImageButton) findViewById(R.id.btnShareKakao);
		btnShareKakaoStory = (ImageButton) findViewById(R.id.btnShareKakaoStory);

		btnShare.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		myPictureShareBtn.setOnClickListener(this);
		btnShareFacebook.setOnClickListener(this);
		btnShareKakao.setOnClickListener(this);
		btnShareKakaoStory.setOnClickListener(this);

		common = new Common(this);
		filePath = getFileStreamPath(myPictureName);
		Log.e("Realize", "filePath : " + filePath);

		try {
			imageView.setImageDrawable(Drawable.createFromPath(filePath
					.getAbsolutePath()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	// ImageButton btnKakao, btnTwitter, btnFacebook ;
	// btnKakao=(ImageButton)findViewById(R.id.btnKakao);
	// btnTwitter=(ImageButton)findViewById(R.id.btnTwitter);
	// btnFacebook=(ImageButton)findViewById(R.id.btnFacebook);
	// btnKakao.setOnClickListener(this);
	// btnTwitter.setOnClickListener(this);
	// btnFacebook.setOnClickListener(this);
	//
	// String message ="";
	// String referenceURLString =
	// "https://market.android.com/details?hl=ko&id=com.DooitLocalResearch";
	// String appVersion = "1.0";
	//
	//
	//
	// if(v.getId()==R.id.btnKakao){
	// try {
	// ArrayList< Map < String, String > > arrMetaInfo = new ArrayList< Map<
	// String, String > >();
	// Map < String, String > metaInfoAndroid = new Hashtable < String, String
	// >(1);
	// metaInfoAndroid.put("os", "android");
	// metaInfoAndroid.put("devicetype", "phone");
	// metaInfoAndroid.put("installurl",
	// "market://details?id=com.DooitLocalResearch");
	// metaInfoAndroid.put("executeurl",
	// "market://details?id=com.DooitLocalResearch");
	// arrMetaInfo.add(metaInfoAndroid);
	// KakaoLink link = new KakaoLink(this, "www.dooit.co.kr",
	// referenceURLString, appVersion,
	// message, "test", arrMetaInfo, "UTF-8");
	// if (link.isAvailable()) {
	// startActivity(link.getIntent());
	// } else {
	// common.centerToast(Realize.this, "File Upload Error.");
	// }
	// }catch(UnsupportedEncodingException e){
	// e.printStackTrace();
	// }
	// }else{
	// switch(v.getId()){
	// case R.id.btnTwitter :
	// recommendURL="http://twitter.com/home?status="+message+"test : \""+referenceURLString;
	// break;
	// case R.id.btnFacebook :
	// recommendURL="http://www.facebook.com/sharer/sharer.php?s=100&p[url]="+referenceURLString
	// +"&p[images][0]=http://dooit.co.kr/include/img/dooit/btn_main01.gif&p[title]="+message;
	// break;
	// }
	// startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(recommendURL) ));
	//

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnShare:
			layoutShare.setVisibility(View.VISIBLE);
			break;
		case R.id.btnExit:
			layoutShare.setVisibility(View.GONE);
			break;
		case R.id.myPictureShareBtn:
			myPictureShare();
			break;
		default:
			shareApplication(v.getId());
			break;
		}
	}

	private void myPictureShare() {
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.icon)
		.setMessage("다른사람의 사진을 보기위해서는 자신의 사진도 공유해야 합니다.")
		.setPositiveButton("공유", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sharePicture();
				return;
			}
		}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		}).create().show();
	}

	private void shareApplication(int resourceID) {
		String message = getResources().getString(R.string.realize_share_app_text);
		String referenceURLString = "https://market.android.com/details?hl=ko&id=com.DooitLocalResearch";
		String appVersion = "1.0";
		String recommendURL = null;

		if (resourceID == R.id.btnShareKakao) {
			try {
				ArrayList<Map<String, String>> arrMetaInfo = new ArrayList<Map<String, String>>();
				Map<String, String> metaInfoAndroid = new Hashtable<String, String>(1);
				metaInfoAndroid.put("os", "android");
				metaInfoAndroid.put("devicetype", "phone");
				metaInfoAndroid.put("installurl",
						"market://details?id=com.DooitLocalResearch");
				metaInfoAndroid.put("executeurl",
						"market://details?id=com.DooitLocalResearch");
				arrMetaInfo.add(metaInfoAndroid);
				KakaoLink link = new KakaoLink(this, "www.dooit.co.kr",
						referenceURLString, appVersion, message, "test",
						arrMetaInfo, "UTF-8");
				
				if (link.isAvailable()) {
					startActivity(link.getIntent());
				} else {
					common.centerToast(Realize.this, getResources().getString(R.string.realize_share_kakaotalk_not_install));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else if ( resourceID == R.id.btnShareKakaoStory) {
			try {
				requestShareKakaoLink();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			switch (resourceID) {
			case R.id.btnShareTwitter:
				recommendURL = "http://twitter.com/home?status=" + message
						+ "test : \"" + referenceURLString;
				break;
			case R.id.btnShareFacebook:
				recommendURL = "http://www.facebook.com/sharer/sharer.php?s=100&p[url]="
						+ referenceURLString
						+ "&p[images][0]=http://dooit.co.kr/include/img/dooit/btn_main01.gif&p[title]="
						+ message;
				break;
			}
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse(recommendURL)));
		}
	}

	private void requestShareKakaoLink() throws NameNotFoundException {
		Map<String, Object> urlInfoAndroid = new Hashtable<String, Object>(1);
		urlInfoAndroid.put("title", "(광해) 실제 역사적 진실은?");
		urlInfoAndroid.put("desc", "(광해 왕이 된 남자)의 역사성 부족을 논하다.");
		urlInfoAndroid.put("imageurl", new String[] {"http://m1.daumcdn.net/photo-media/201209/27/ohmynews/R_430x0_20120927141307222.jpg"});
		urlInfoAndroid.put("type", "article");

		// Recommended: Use application context for parameter.
		StoryLink storyLink = StoryLink.getLink(getApplicationContext());

		// check, intent is available.
		if (!storyLink.isAvailableIntent()) {
			common.centerToast(this, getResources().getString(R.string.realize_share_kakaoStory_not_install));			
			return;
		}

		/**
		 * @param activity
		 * @param post (message or url)
		 * @param appId
		 * @param appVer
		 * @param appName
		 * @param encoding
		 * @param urlInfoArray
		 */
		storyLink.openKakaoLink(this, 
				"http://m.media.daum.net/entertain/enews/view?newsid=20120927110708426",
				getPackageName(), 
				getPackageManager().getPackageInfo(getPackageName(), 0).versionName, 
				"미디어디음",
				"UTF-8", 
				urlInfoAndroid);
	}

	class ShareMyPicture extends AsyncTask<Void, Void, Boolean> {

		String id; 		
		Float weight;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			id = common.saveID();
			weight = common.getWeight();
			
			progressDialog = ProgressDialog.show(Realize.this, "", "Wait For Seconds");
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			return common.uploadDataToServer(id, weight,
					filePath.getAbsolutePath());
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			progressDialog.cancel();
		}
		
		@Override
		protected void onPostExecute(Boolean isUploadRequestSuccess) {
			super.onPostExecute(isUploadRequestSuccess);
			if ( !isUploadRequestSuccess ) {
				common.centerToast(Realize.this, getResources().getString(R.string.realize_share_mypicture_upload_fail));
			} else {
				//list화면으로
				//preference로 저장
			}
			progressDialog.cancel();
		}
	}
	
	private void sharePicture() {
		new ShareMyPicture().execute();
	}
}
