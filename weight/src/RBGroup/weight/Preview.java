package RBGroup.weight;

import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class Preview extends SurfaceView implements Callback, Camera.PictureCallback {

    private SurfaceHolder mHolder;
    private Camera mCamera = null;
    
    
    public Preview(Context context) {
        super(context);
        
        try {
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        } catch (Exception e) {
            Log.d("ErrorLog ", "PreView : Preview");
        }
    }

    private Camera getCameraInstance() {
    	int cameraCount = 0;
    	  Camera cam = null;
    	  Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
    	  cameraCount = Camera.getNumberOfCameras();
    	  for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
    		  Camera.getCameraInfo(camIdx, cameraInfo);
    	    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
    	    	try {
    	    	  cam = Camera.open(camIdx);
    	    	  cam.setDisplayOrientation(0);
    	      } catch (RuntimeException e) {
    	         Log.e("1", "error");
    	      }
    	    }
    	  }
    	  return cam;  
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        // TODO Auto-generated method stub
    	Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> cSize = parameters.getSupportedPreviewSizes();
        Camera.Size tempSize = cSize.get(1);
        //parameters.setPreviewSize(tempSize.height, tempSize.width);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        //mCamera = Camera.open();
    	mCamera = getCameraInstance();
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (Exception e) {
            // TODO: handle exception
            Log.d("ErrorLog", "Preview : surfaceCreated");
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public boolean Capture (Camera.PictureCallback jpegHandler) {
    	if(mCamera != null) {
    		mCamera.takePicture(null, null, jpegHandler);
    		return true;
    	} else {
    		return false;
    	}
    }

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		
	}
}
