package com.weight;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class BannerActivity extends Activity {
	// banner
	/** The view to show the ad. */
	private AdView adView;

	/* Your ad unit id. Replace with your actual ad unit id. */
	private static final String AD_UNIT_ID = "ca-app-pub-4433619112052789/2417807150";

	public int getBannerHeight(Context context) {
		return adView.getAdSize().getHeightInPixels(context);
	}

	public void createSmartBanner(Context context, View view) {
		_createBanner(context, view, AdSize.SMART_BANNER);
	}

	public void createBanner(Context context, View view) {
		_createBanner(context, view, AdSize.BANNER);
	}
	
	public void _createBanner(Context context, View view, AdSize bannerSize) {
		// admob
		// Create an ad.
		adView = new AdView(context);
		adView.setAdSize(bannerSize);
		adView.setAdUnitId(AD_UNIT_ID);

		// Add the AdView to the view hierarchy. The view will have no size
		// until the ad is loaded.
		LinearLayout layout = (LinearLayout) view;
		layout.addView(adView);

		// Create an ad request. Check logcat output for the hashed device ID to
		// get test ads on a physical device.
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice("AAC7FF0E7DDE44E4EBFA1CB87486AC6E")
				.addTestDevice("F6013E8E6D5F3F7052A5BB43C1B64A69")
				.addTestDevice("4FD0C68452580875678E2BB356748459")
				//성환형
				.addTestDevice("7E9A23C5EB5482DAF632597648C4CC83")
				//대용이
				.addTestDevice("0A9033C2356882578461835B4AB8B120")
				//진상형
				.addTestDevice("6FAE8435959B57CA04916913E6AB4764")

				.build();

		//종기형
		//AdRequest.Builder.addTestDevice("F6013E8E6D5F3F7052A5BB43C1B64A69")
		
		//윤성
		// 06-03 13:18:28.659: I/Ads(5293): Use
		// AdRequest.Builder.addTestDevice("AAC7FF0E7DDE44E4EBFA1CB87486AC6E")
		// to get test ads on this device.

		// Start loading the ad in the background.
		adView.loadAd(adRequest);
	}

	@Override
	protected void onResume() {
		if (adView != null) {
			adView.resume();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	/** Called before the activity is destroyed. */
	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}
}
