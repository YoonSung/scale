package RBGroup.weight;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

public class KakaoLink {
	static final Charset kakaoLinkCharset = Charset.forName("UTF-8");
	static final String kakaoLinkEncoding = kakaoLinkCharset.name();
	private Context context;
	private Intent intent;
	private String appId;
	private String version;
	private String url;
	private String msg;
	private String encoding;
	private String type 					= "";
	private String apiVer 					= "2.0";
	private String appName 					= "";
	private ArrayList<Map<String, String>> arrMetaInfo = null;
	private Uri data;
	public KakaoLink(	Context context, 
						String url, 
						String appId, 
						String appVersion, 
						String msg,
						String appName,
						ArrayList<Map<String, String>> arrMetaInfo,
						String encoding ) throws UnsupportedEncodingException {
		this.context = context;
		this.appId = appId;
		this.version = appVersion;
		this.url = url;
		this.msg = msg;
		this.type = "app";
		this.appName = appName;
		this.arrMetaInfo = arrMetaInfo;
		this.encoding = encoding;
		data = createAppLinkData();
		intent = new Intent(Intent.ACTION_SEND, data);
	}
	private Uri createAppLinkData() throws UnsupportedEncodingException {
		if ( isEmptyString(msg) || isEmptyString(appId) || isEmptyString(type) || isEmptyString(appName) ) {
			throw new IllegalArgumentException();
		}
		Charset charset = Charset.forName(encoding);
		if (!kakaoLinkCharset.equals(charset)) {
			if (!isEmptyString(msg)) {
				msg = new String(msg.getBytes(charset.name()), kakaoLinkEncoding);
			}
		}
		StringBuilder sb = new StringBuilder("kakaolink://sendurl?");
		if( !isEmptyString(msg) ) {
			sb.append("&msg=").append(URLEncoder.encode(msg, kakaoLinkEncoding));
		}
		if( !isEmptyString(url) ) {
			sb.append("&url=").append(URLEncoder.encode(url, kakaoLinkEncoding));
		}
		sb.append("&appid=").append(URLEncoder.encode(appId, kakaoLinkEncoding));
		if( !isEmptyString(version) ) {
			sb.append("&appver=").append(URLEncoder.encode(version, kakaoLinkEncoding));
		}
		sb.append("&type=").append(URLEncoder.encode(type, kakaoLinkEncoding));
		sb.append("&apiver=").append(URLEncoder.encode(apiVer, kakaoLinkEncoding));
		sb.append("&appname=").append(URLEncoder.encode(appName, kakaoLinkEncoding));
		if( null != arrMetaInfo && arrMetaInfo.size() > 0 ) {
			String jsonMetaInfo = makeJsonMetaInfo();
			if( !isEmptyString(jsonMetaInfo) ) {
				sb.append("&metainfo=").append(URLEncoder.encode(jsonMetaInfo, kakaoLinkEncoding));
			}
		}
		return Uri.parse(sb.toString());
	}
	private String makeJsonMetaInfo() throws UnsupportedEncodingException
	{
		JSONObject jsonObj 			= new JSONObject();
		JSONArray jsonArrMetaInfo 	= new JSONArray();
		try {
			for (Map<String, String> metaInfo : arrMetaInfo) {
				JSONObject metaObj = new JSONObject();
				for (String key : metaInfo.keySet()) {
					metaObj.put(key, metaInfo.get(key));
				}
				jsonArrMetaInfo.put(metaObj);
			}
			jsonObj.put("metainfo", jsonArrMetaInfo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObj.toString();
	}
	public boolean isAvailable() {
		return isAvailableIntent(context, intent);
	}
	public Intent getIntent() {
		return intent;
	}
	private static boolean isAvailableIntent(Context context, Intent intent) {
		List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,   
	            PackageManager.MATCH_DEFAULT_ONLY);
		if (list == null) return false;
		return list.size() > 0;
	}
	private static boolean isEmptyString(String str) {
		return (str == null || str.trim().length() == 0);
	}
}
