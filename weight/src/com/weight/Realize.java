package com.weight;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Realize extends BannerActivity implements OnClickListener {

	//default layout
	ImageView imageView;
	//TODO Constants리팩토링
	private final String myPictureName = "weight.png";
	Common common;
	File file;
	ProgressDialog progressDialog;
	Button btnShare, myPictureShareBtn, myPictureReset;
	int toastWidth;
	int toastHeight;
	
	Toast toast;
	ImageView toastImageView;
	
	// shareApplication
	FrameLayout layoutShare;
	ImageButton btnShareFacebook, btnShareTwitter, btnShareKakao, btnShareKakaoStory, btnExit;
	EditText edtShareComment;
	
	// sharedList
	private ArrayList<ListData> listDataArrayList = new ArrayList<ListData>();
	private ListView listView; 
	private FrameLayout layoutList;
	private Button btnCloseSharedList;
	
	//flag
	private boolean isAlreadyShared = false;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.realize);

		//최초에 생성되어야 하기 때문에 먼저 초기화
		myPictureReset = (Button)findViewById(R.id.myPictureReset);
		myPictureReset.setOnClickListener(this);
		
		common = new Common(this);
		file = getFileStreamPath(myPictureName);
		//Log.e("Realize", "filePath : " + file);
		
		//Check Already Share Picture
		//네트워크 요청 후 Path정보를 가져온뒤, File이 존재하는지 체크한다.
	    if ( common.getID() != null ) {
	    	
	    	boolean isFileExists = file.exists();
	    	//TODO check is valid process
    		this.isAlreadyShared = ( isFileExists == true && common.isAlreadyShared() == true);
	    	//this.isAlreadyShared = true;
	    	
    		//Log.e("Realize", "isAlreadyShared : " + isAlreadyShared);
    		//Log.e("Realize", "isFileExists : " + isFileExists);
    		
    		//만약 공유한 내용이 존재하지 않으면
    		if ( isFileExists == false ) {
    			startActivity(new Intent(Realize.this, InitialSet.class));
    			finish();
    		}
    		
    		resetButtonExposeBySharedStatus();
	    }
	    
		imageView = (ImageView) findViewById(R.id.myPicture);
		btnShare = (Button) findViewById(R.id.btnShare);
		myPictureShareBtn = (Button) findViewById(R.id.myPictureShareBtn);
		
		
		// share layout
		layoutShare = (FrameLayout) findViewById(R.id.layoutShare);
		btnExit = (ImageButton) findViewById(R.id.btnExit);
		btnShareFacebook = (ImageButton) findViewById(R.id.btnShareFacebook);
		btnShareTwitter = (ImageButton) findViewById(R.id.btnShareTwitter);
		btnShareKakao = (ImageButton) findViewById(R.id.btnShareKakao);
		btnShareKakaoStory = (ImageButton) findViewById(R.id.btnShareKakaoStory);
		edtShareComment = (EditText)findViewById(R.id.edtShareComment);
		
		btnShare.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		myPictureShareBtn.setOnClickListener(this);
		
		btnShareFacebook.setOnClickListener(this);
		btnShareTwitter.setOnClickListener(this);
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
			this.toastWidth = deviceHeight;
			this.toastHeight = deviceHeight;//deviceHeight*deviceHeight/deviceWidth;
			//너비 : 높이 = 높이 = x
			imageView.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));
			imageView.getLayoutParams().width = deviceWidth;
			imageView.getLayoutParams().height = deviceHeight*deviceHeight/ deviceWidth;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,      
				0
		);
		
		params.weight = 1;
		params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
		createSmartBanner(this, findViewById(R.id.banner));
		params.setMargins(0, 0, 0, getBannerHeight(this)+10);
		findViewById(R.id.layoutContent).setLayoutParams(params);
	}

	private void resetButtonExposeBySharedStatus() {
		if (isAlreadyShared == true) {
			myPictureReset.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btnShare) {
			layoutShare.setVisibility(View.VISIBLE);
		} else if (id == R.id.btnExit) {
			layoutShare.setVisibility(View.GONE);
		} else if (id == R.id.myPictureShareBtn) {
			if ( isAlreadyShared == false ) {
				myPictureShare();
			} else {
				getSharedListFromSameLanguage();
			}
		} else if (id == R.id.myPictureReset) {
			new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.realize_mypicture_reset_btn))
			.setMessage(getResources().getString(R.string.realize_mypicture_reset_text))
			.setPositiveButton(getResources().getString(R.string.realize_alert_progress),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						common.resetPreference();
						startActivity(new Intent(Realize.this, InitialSet.class));
						finish();
					}
				})
			.setNegativeButton(getResources().getString(R.string.realize_share_mypicture_alert_cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
			}).create().show();
		} else if (id == R.id.btnCloseSharedList) {
			layoutList.setVisibility(View.GONE);
			resetButtonExposeBySharedStatus();
		} else {
			shareApplication(v.getId());
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
			this.id = common.getID();
			this.weight = common.getWeight();
			this.isMan = common.getSexInfoIsMan();
			this.filePath = Realize.this.file.getAbsolutePath();
			progressDialog = ProgressDialog.show(Realize.this, "", getResources().getString(R.string.realize_wait));
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
			progressDialog.cancel();
			if ( !isUploadRequestSuccess ) {
				common.centerToast(Realize.this, getResources().getString(R.string.realize_share_mypicture_upload_fail));
			} else {
				//list화면으로
				//preference로 저장
				//loadSharedPicturesList();
				//Network on MainThread Exception
				isAlreadyShared = true;
				common.saveIsShared();
				getSharedListFromSameLanguage();
			}
		}
	}
	
	//shareMyPicture End
	/************************************************************************************/
	
	private void shareApplication(int resourceID) {
		String message = edtShareComment.getText().toString();
		//getResources().getString(R.string.realize_share_app_text);
		String referenceURLString = "https://market.android.com/details?id=com.weight";
		String appVersion = "1.0";
		String recommendURL = null;

		if (resourceID == R.id.btnShareKakao) {
			try {
				ArrayList<Map<String, String>> arrMetaInfo = new ArrayList<Map<String, String>>();
				Map<String, String> metaInfoAndroid = new Hashtable<String, String>(1);
				metaInfoAndroid.put("os", "android");
				metaInfoAndroid.put("devicetype", "phone");
				metaInfoAndroid.put("installurl",
						"market://details?id=com.weight");
				metaInfoAndroid.put("executeurl",
						"market://details?id=com.weight");
				arrMetaInfo.add(metaInfoAndroid);
				KakaoLink link = new KakaoLink(this, "https://play.google.com/store/apps/details?id=com.weight",
						referenceURLString, appVersion, message, "Digital Weight Scale",
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
				requestShareKakaoLink(message, referenceURLString);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			if (resourceID == R.id.btnShareTwitter) {
				recommendURL = "https://twitter.com/intent/tweet?source=webclient&text="
											+message
											+"&url="+referenceURLString;
			} else if (resourceID == R.id.btnShareFacebook) {
				recommendURL = "https://www.facebook.com/sharer/sharer.php?u="+referenceURLString;
			}
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse(recommendURL)));
		}
	}

	private void requestShareKakaoLink(String message, String referenceURLString) throws NameNotFoundException {
		Map<String, Object> urlInfoAndroid = new Hashtable<String, Object>(1);
		urlInfoAndroid.put("title", "Digital Weight Scale");
		urlInfoAndroid.put("desc", message);
		//urlInfoAndroid.put("url", referenceURL);
		//urlInfoAndroid.put("imageurl", new String[] {"http://m1.daumcdn.net/photo-media/201209/27/ohmynews/R_430x0_20120927141307222.jpg"});
		
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
				//"내용 http://m.media.daum.net/entertain/enews/view?newsid=20120927110708426",
				message+" "+referenceURLString,//+" http://m.media.daum.net/entertain/enews/view?newsid=20120927110708426",
				//message,
				getPackageName(), 
				getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
				"Scale",
				//"한글입니다",
				"UTF-8", 
				urlInfoAndroid);
	}
	/************************************************************************************/
	class GetSharedPictures extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(Realize.this, "", "Wait For Seconds");
		}
		
		@Override
		protected String doInBackground(Void... params) {
			String jsonString = common.loadSharedPicturesList( common.getID() );
			//Log.e("Realize.java", "JSONSTRING : "+jsonString);
			return jsonString;
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			common.centerToast(Realize.this, "UnExpected Error Occur, Please Try Again");
			progressDialog.cancel();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			progressDialog.cancel();
			if ( result == null || result.length() == 0 || result.equalsIgnoreCase("") == true) {
				common.centerToast(Realize.this, "UnExpected Error Occur, Please Try Again");
			} else if (result.contains("ER_SUBQUERY_NO_")) {
				final String id = common.getID();
				final float weight = common.getWeight();
				final boolean isMan = common.getSexInfoIsMan();
				
				//파일주소
				File file = getFileStreamPath(myPictureName);
				final String path = file.getAbsolutePath();
				
				//언어
				Locale systemLocale = getResources().getConfiguration().locale;
				final String language = systemLocale.getLanguage();

				if (id!=null && weight !=0 && path !=null && language != null) {
					final Common tempCommon = Realize.this.common;
					new Thread() {
						public void run() {
							tempCommon.uploadDataToServer(id, weight, isMan, path, language);
							tempCommon.saveIsShared();
						}
					}.start();
				} else {
					//common.resetPreference();
					//file.delete();
				}
			}  else {
				updateView( common.getMapFromJsonString(result) );
				layoutList.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void updateView ( ArrayList<Map<String, Object>> list) {
		listDataArrayList.clear();
		
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
		private ListData listData;
		//BookListData k;

		public ListTabAdapter(Context context, int textViewResourceId,
				ArrayList<ListData> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.list_tab_cell, null);
			}
			view.setTag(position);
			listData = items.get(position);
			if (listData != null) {
		
				TextView txtIsMan, txtWeight, txtLanguage;
				ImageView image;
				LinearLayout row;
		
				row = (LinearLayout) view.findViewById(R.id.listtab_row);
				image = (ImageView) view.findViewById(R.id.listtab_custom_image);
				//image.buildDrawingCache(true);
				//image.setDrawingCacheEnabled(true);
				txtIsMan = (TextView) view.findViewById(R.id.listtab_custom_isMan);
				txtWeight = (TextView) view.findViewById(R.id.listtab_custom_weight);
				txtLanguage = (TextView) view.findViewById(R.id.listtab_custom_language);
				
				row.setTag(position);
				row.setOnClickListener(this);
				
				txtIsMan.setText(getResources().getString(R.string.realize_shared_list_sex ) + ((listData.isMan() == true)? "M" : "W") );
				txtWeight.setText(getResources().getString(R.string.realize_shared_list_weight )+ " " +listData.getWeight());
				txtLanguage.setText(getResources().getString(R.string.realize_shared_list_alert_language ) + listData.getLanguage());
				
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
		
		@SuppressLint("InflateParams")
		@Override
		public void onClick(View v) {
			
			ImageView imageView = null;
			
			LinearLayout layout = (LinearLayout) v;
			int childcount = layout.getChildCount();
			for (int i=0; i < childcount; i++){
			      View view = layout.getChildAt(i);
			      if (view instanceof ImageView) {
			    	    imageView = (ImageView) view;
			    	}
			}
			
			if (Realize.this.toast == null) {
				toast = new Toast(Realize.this);
				LayoutInflater inflater;
		        View inflaterView;
		        inflater = (LayoutInflater)Realize.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        inflaterView = inflater.inflate(R.layout.custom_toast, null);
		        Realize.this.toastImageView = (ImageView) inflaterView.findViewById(R.id.custom_image);
		        toast.setView(inflaterView);
		        toast.setDuration(Toast.LENGTH_LONG);
			}
			
			if (imageView != null) {
				Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
				Bitmap copiedBitmap = bitmap.copy(bitmap.getConfig(), true);
				//ImageView targetImageView = (ImageView) toast.getView();
				//Log.e("Realize", ""+bitmap);
				copiedBitmap = Bitmap.createScaledBitmap(copiedBitmap, toastWidth, toastHeight, true);
				//if (bitmap != null)
				//	Log.e("Realize", bitmap.toString());
				
				Realize.this.toastImageView.setImageBitmap(copiedBitmap);
				
				//Realize.this.toastImageView.setImageBitmap(bitmap);
				//targetImageView.getLayoutParams().width = deviceWidth;
				//targetImageView.getLayoutParams().height = deviceHeight;
				//targetImageView.setImageDrawable(drawable);
				//Toast toast = new Toast(Realize.this);
				toast.show();
			}
//			int position = (Integer) v.getTag();
//			ListData listData = items.get(position)
		}
	}
}
