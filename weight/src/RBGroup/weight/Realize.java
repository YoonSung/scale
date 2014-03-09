package RBGroup.weight;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class Realize extends Activity implements OnClickListener{

	ImageView imageView;
	private final String myPictureName = "weight.png";
	Button btnShare;
	Common common;
	File filePath;
	ProgressDialog progressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.realize);
	    
	    imageView = (ImageView)findViewById(R.id.myPicture);
	    btnShare = (Button)findViewById(R.id.realize_btn_share);
	    btnShare.setOnClickListener(this);
	    
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
