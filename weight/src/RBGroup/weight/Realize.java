package RBGroup.weight;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Realize extends BannerActivity implements OnClickListener {

	//default layout
	ImageView imageView;
	private final String myPictureName = "weight.png";
	Common common;
	File filePath;
	ProgressDialog progressDialog;
	Button btnShare, myPictureShareBtn;

	// shareApplication
	FrameLayout layoutShare;
	ImageButton btnShareFacebook, btnShareKakao, btnShareKakaoStory, btnExit;

	// sharedList
	private ArrayList<ListData> listDataArrayList = new ArrayList<ListData>();
	private ListView listView; 
	private FrameLayout layoutList;
	private Button btnCloseSharedList;
	
	//flag
	private boolean isAlreadyShared = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.realize);

		common = new Common(this);
		filePath = getFileStreamPath(myPictureName);
		Log.e("Realize", "filePath : " + filePath);
		
		//Check Already Share Picture
		//네트워크 요청 후 Path정보를 가져온뒤, File이 존재하는지 체크한다.
	    if ( common.getID() != null ) {
	    	
	    	//TODO check is valid process
    		//this.isAlreadyShared = filePath.exists();
	    	this.isAlreadyShared = true;
	    	
    		Log.e("Realize", "isAlreadyShared : " + isAlreadyShared);
    		
    		//
    		if ( isAlreadyShared == false ) {
    			startActivity(new Intent(Realize.this, InitialSet.class));
    			finish();
    		}
	    }
		
		
	    
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

		listView = (ListView)findViewById(R.id.listtab_list);
		layoutList = (FrameLayout)findViewById(R.id.layoutList);
		btnCloseSharedList = (Button)findViewById(R.id.btnCloseSharedList);
		btnCloseSharedList.setOnClickListener(this);
		
		try {
			Display display=((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		    int deviceWidth=(int)display.getWidth();
		    int deviceHeight=(int)display.getHeight();
			
			
			imageView.setImageDrawable(Drawable.createFromPath(filePath
					.getAbsolutePath()));
			imageView.getLayoutParams().width = deviceWidth;
			imageView.getLayoutParams().height = deviceWidth*deviceWidth / deviceHeight;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,      
				FrameLayout.LayoutParams.WRAP_CONTENT
		);

		params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
		
		createBanner(this, findViewById(R.id.realize_banner));
		params.setMargins(0, 0, 0, getBannerHeight(this)+10);
		myPictureShareBtn.setLayoutParams(params);
	}

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
			if ( isAlreadyShared == false ) {
				myPictureShare();
			} else {
				getSharedListFromSameLanguage();
			}
			break;
		case R.id.btnCloseSharedList:
			layoutList.setVisibility(View.GONE);
			break;
		default:
			shareApplication(v.getId());
			break;
		}
	}

	
	/************************************************************************************/
	//shareMyPicture Start
	
	private void myPictureShare() {
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.icon)
		.setMessage(R.string.realize_share_mypicture_alert_title)
		.setPositiveButton(R.string.realize_share_mypicture_alert_confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sharePicture();
				return;
			}
		}).setNegativeButton(R.string.realize_share_mypicture_alert_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		}).create().show();
	}

	private void sharePicture() {
		if ( common.checkNetwork(Realize.this) );
			new ShareMyPicture().execute();
	}
	
	private void getSharedListFromSameLanguage() {
		if ( common.checkNetwork(Realize.this) );
			new GetSharedPictures().execute();
	}
	
	class ShareMyPicture extends AsyncTask<Void, Void, Boolean> {

		String id; 		
		Float weight;
		String filePath;
		boolean isMan;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.id = common.saveAndReturnID();
			this.weight = common.getWeight();
			this.isMan = common.getSexInfoIsMan();
			this.filePath = Realize.this.filePath.getAbsolutePath();
			progressDialog = ProgressDialog.show(Realize.this, "", "Wait For Seconds");
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			if (id.equalsIgnoreCase(null) || weight == null || filePath.equalsIgnoreCase(null) )
				return false;
			
			Locale systemLocale = getResources().getConfiguration().locale;
			String language = systemLocale.getLanguage();
			
			return common.uploadDataToServer(this.id, this.weight, this.isMan, this.filePath, language);
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			common.centerToast(Realize.this, getResources().getString(R.string.realize_share_mypicture_upload_fail));
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
				//loadSharedPicturesList();
				//Network on MainThread Exception
				getSharedListFromSameLanguage();
			}
			progressDialog.cancel();
		}
	}
	
	//shareMyPicture End
	/************************************************************************************/
	
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
				"내용 http://m.media.daum.net/entertain/enews/view?newsid=20120927110708426",
				getPackageName(), 
				getPackageManager().getPackageInfo(getPackageName(), 0).versionName, 
				"미디어디음",
				"UTF-8", 
				urlInfoAndroid);
	}
	/************************************************************************************/
	class GetSharedPictures extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(Void... params) {
			String jsonString = common.loadSharedPicturesList( common.getID() );
			Log.e("Realize.java", "JSONSTRING : "+jsonString);
			return jsonString;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (result != "error" ) {
				updateView( common.getMapFromJsonString(result) );
				layoutList.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void updateView ( ArrayList<Map<String, Object>> list) {
		if (list != null ) {
			for ( int i = 0 ; i < list.size() ; i++ ) {
				Map<String, Object> map = list.get(i);
				
				listDataArrayList.add(
						new ListData(
								(String)map.get("id"),
								( ((Double) map.get("isMan")).intValue() == 0 ) ? false: true,
								Float.parseFloat(""+map.get("weight")),
								(String)map.get("language")
								));
			}
		}
		
		ListTabAdapter ListTabAdapter = new ListTabAdapter(this, R.layout.list_tab_cell, listDataArrayList);
		listView.setAdapter(ListTabAdapter);
	}
	/************************************************************************************/
	
	private class ListTabAdapter extends ArrayAdapter<ListData> implements OnClickListener {

		private ArrayList<ListData> items;
		private Common common;
		private ListData listData;
		//BookListData k;

		public ListTabAdapter(Context context, int textViewResourceId,
				ArrayList<ListData> items) {
			super(context, textViewResourceId, items);
			this.items = items;
			this.common = Realize.this.common;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.list_tab_cell, null);
			}

			listData = items.get(position);
			if (listData != null) {
		
				TextView txtIsMan, txtWeight, txtLanguage;
				ImageView image;
				LinearLayout row;
		
				row = (LinearLayout) view.findViewById(R.id.listtab_row);
				image = (ImageView) view.findViewById(R.id.listtab_custom_image);
				txtIsMan = (TextView) view.findViewById(R.id.listtab_custom_isMan);
				txtWeight = (TextView) view.findViewById(R.id.listtab_custom_weight);
				txtLanguage = (TextView) view.findViewById(R.id.listtab_custom_language);
				
				row.setTag(position);
				row.setOnClickListener(this);
		
				txtIsMan.setText(R.string.realize_shared_list_sex + ((listData.isMan() == true)? "M" : "W") );
				txtWeight.setText(R.string.realize_shared_list_weight+ " " +listData.getWeight());
				txtLanguage.setText(R.string.realize_shared_list_alert_language + listData.getLanguage());
				
				new DownloadImageTask(image).execute(listData.getImageUrl());
				//image.setImageDrawable(common.LoadImageFromWebOperations(listData.getImgURL()));
			}
		
			return view;
		}
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
			  ImageView bmImage;
		
			  public DownloadImageTask(ImageView bmImage) {
			      this.bmImage = bmImage;
			  }
		
			  protected Bitmap doInBackground(String... urls) {
			      String urldisplay = urls[0];
			      Bitmap mIcon11 = null;
			      try {
			        InputStream in = new java.net.URL(urldisplay).openStream();
			        mIcon11 = BitmapFactory.decodeStream(in);
			      } catch (Exception e) {
			          Log.e("Error", e.getMessage());
			          e.printStackTrace();
			      }
			      return mIcon11;
			  }
		
			  protected void onPostExecute(Bitmap result) {
			      bmImage.setImageBitmap(result);
			  }
			}
		
		@Override
		public void onClick(View v) {
			
		}
	}
}
