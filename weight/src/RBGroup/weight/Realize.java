package RBGroup.weight;

import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

public class Realize extends Activity {

	ImageView imageView;
	private final String myPictureName = "weight.png";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.realize);
	    
	    imageView = (ImageView)findViewById(R.id.myPicture);
	    
	    File filePath = getFileStreamPath( myPictureName);
	    imageView.setImageDrawable(Drawable.createFromPath(filePath.toString()));
	}

}
