package com.weight;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Common {

	//public static final String ROOT_PATH = "http://192.168.1.130:8080";
	public static final String ROOT_PATH = "http://54.178.137.153:8080";
	static String SYSTEM_FILE_PATH;
	//public static final String ROOT_PATH = "http://10.73.43.226:8080";
	private final String lineEnd = "\r\n";
	private final String twoHyphens = "--";
	private final String boundary = "*****";
	
	
	public Common(Context context) {
		spf = PreferenceManager.getDefaultSharedPreferences(context);
		editor = spf.edit();
	}

	public void centerToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
				0, 0);
		toast.show();
	}

	public Drawable LoadImageFromWebOperations(String strPhotoUrl) {
		try {
			InputStream is = (InputStream) new URL(strPhotoUrl).getContent();
			Drawable drawable = Drawable.createFromStream(is, "src name");
			return drawable;
		} catch (Exception e) {
			Log.e("BookList", "LoadImageFromWebOperations Error");
			e.printStackTrace();
		}
		return null;
	}

	public boolean checkNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean isWifiConn = ni.isConnected();
		ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		if ( ni == null) {
			ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		}
		boolean isMobileConn = ni.isConnected();
		if (isWifiConn == true || isMobileConn == true)
			return true;
		centerToast(context, "Network connect Fail.\nTry again");
		return false;
	}

	public ArrayList<Map<String, Object>> getMapFromJsonString(String json) {
		Gson gson = new Gson();

		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Type listType = new TypeToken<List<Map<String, Object>>>() {
		}.getType();
		list = gson.fromJson(json, listType);

		System.out.println(list);

		return list;
	}

	private String getPostData(String key, String value) {
		String result = twoHyphens + boundary + lineEnd;
		result +="Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd;
		result += lineEnd;
		
		result += value;
		
		result += lineEnd;
		
		return result;
	}
	
	public boolean uploadDataToServer(String id, Float weight, boolean isMan, String path, String language) {

		String tempURL = ROOT_PATH + "/upload";

		boolean requestResult= false;
		
		URL connectUrl = null;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;

		FileInputStream mFileInputStream = null;
		try {

			mFileInputStream = new FileInputStream(path);
			connectUrl = new URL(tempURL);
			//Log.d("Common", "mFileInputStream  is " + mFileInputStream);

			// open connection
			conn = (HttpURLConnection) connectUrl.openConnection();
			conn.setConnectTimeout(10 * 1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("Connection", "Keep-Alive");
			
			conn.setRequestProperty("Transfer-Encoding", "chunked");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			dos = new DataOutputStream(conn.getOutputStream());
			
			dos.write( getPostData("id", ""+id).getBytes("UTF-8"));
			dos.write( getPostData("isMan", ""+isMan).getBytes("UTF-8"));
			dos.write( getPostData("weight", ""+weight).getBytes("UTF-8"));
			dos.write( getPostData("path", ""+path).getBytes("UTF-8"));
			dos.write( getPostData("language", ""+language).getBytes("UTF-8"));
			
			
			// write image data
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\""
					+ id + ".png\"" + lineEnd);
			dos.writeBytes(lineEnd);

			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);

			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

			//Log.d("Common", "image byte is " + bytesRead);

			// read image
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = mFileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			//write weight value
			dos.writeBytes("Content-Disposition: form-data; name=\"weight\";name=\""
					+ weight + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			
			// close streams
			//Log.e("Test", "File is written");
			mFileInputStream.close();
			dos.flush(); // finish upload...

			// get response
			int ch;
			InputStream is = conn.getInputStream();
			StringBuffer sb = new StringBuffer();
			while ((ch = is.read()) != -1) {
				sb.append((char) ch);
			}
			String s = sb.toString();
			
			if ( s.equalsIgnoreCase("true")) {
				requestResult = true;
			}
			
			//Log.e("Common", "result = " + s);

		} catch (Exception e) {
			e.printStackTrace();
			//Log.d("Common", "exception " + e.getMessage());
		} finally {
			try {
				if (dos != null)
					dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return requestResult;
	}

	public String requestServerDataById(String url, String id) {
		String  requestResult= null;
		
		URL connectUrl = null;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;

		try {

			connectUrl = new URL(url);
			
			// open connection
			conn = (HttpURLConnection) connectUrl.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			dos = new DataOutputStream(conn.getOutputStream());
			
			dos.write( getPostData("id", ""+id).getBytes("UTF-8"));
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			dos.flush(); // finish

			// get response
			int ch;
			InputStream is = conn.getInputStream();
			StringBuffer sb = new StringBuffer();
			while ((ch = is.read()) != -1) {
				sb.append((char) ch);
			}
			requestResult = sb.toString();
			
			//Log.e("Common", "load Shared Picture List = " + requestResult);
			dos.close();

		} catch (Exception e) {
			Log.e("Common", "exception " + e.getMessage());
		}

		return requestResult;
	}
	
	public String loadSharedPicturesList(String id) {
		String url = ROOT_PATH + "/getList";

		return requestServerDataById(url, id);
	}

	private SharedPreferences spf;
	private SharedPreferences.Editor editor;

	String saveAndReturnID() {
		UUID uuid = UUID.randomUUID();
		String uuidString = uuid.toString();

		savePreference("id", uuidString);
		
		return uuidString;
	}

	String getID() {
		return readStringPreference("id");
	}

	void saveWeight(float weight) {
		savePreference("weight", weight);
	}

	void saveSexInfoIsMan(boolean isMan) {
		savePreference("isMan", true);
	}
	
	boolean getSexInfoIsMan() {
		return readBooleanPreference("isMan");
	}
	
	float getWeight() {
		return readFloatPreference("weight");
	}

	boolean isAlreadyShared() {
		return readBooleanPreference("isAlreadyShared");
	}
	
	void saveIsShared() {
		savePreference("isAlreadyShared", true);
	}
	
	private void savePreference(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	private void savePreference(String key, float value) {
		editor.putFloat(key, value);
		editor.commit();
	}

	private void savePreference(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	private boolean readBooleanPreference(String key) {
		return spf.getBoolean(key, false);
	}
	
	private String readStringPreference(String key) {
		return spf.getString(key, null);
	}

	private float readFloatPreference(String key) {
		return spf.getFloat(key, 0);
	}

	public void resetPreference() {
		savePreference("id", null);
		savePreference("isAlreadyShared", false);
	}
}
