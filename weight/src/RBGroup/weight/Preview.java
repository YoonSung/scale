package RBGroup.weight;

import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class Preview extends SurfaceView implements Callback, Camera.PictureCallback {

    private SurfaceHolder mHolder;
    private Camera mCamera = null;
    private Context context;
    
    public Preview(Context context) {
        super(context);
        this.context = context;
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
    	         Log.e("error 1", ""+e);
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

    	Camera.Parameters parameters = mCamera.getParameters();
        //List<Camera.Size> cSize = parameters.getSupportedPreviewSizes();
    	List<Camera.Size> cSize = getSupportedPictureSizes();
    	Camera.Size tempSize = cSize.get(cSize.size()-1);
    	
    	Log.e("Preview","result width : "+tempSize.width);
    	Log.e("Preview","result height : "+tempSize.height);
    	
        parameters.setPreviewSize(tempSize.width, tempSize.height);
        parameters.setPictureSize(tempSize.width, tempSize.height);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    //
    public List<Camera.Size> getSupportedPictureSizes() {
        if (mCamera == null) {
            return null;
        }
     
        List<Camera.Size> pictureSizes = mCamera.getParameters().getSupportedPictureSizes();
                 
        checkSupportedPictureSizeAtPreviewSize(pictureSizes);
         
        for (Size size : pictureSizes) {
			Log.e("Preview","size Width : "+size.width);
			Log.e("Preview","size Height : "+size.height);
		}
        
        return pictureSizes;
    }
     
    private void checkSupportedPictureSizeAtPreviewSize(List<Camera.Size> pictureSizes) {
        List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        Camera.Size pictureSize;
        Camera.Size previewSize;
        double  pictureRatio = 0;
        double  previewRatio = 0;
        final double aspectTolerance = 0.05;
        boolean isUsablePicture = false;
         
        for (int indexOfPicture = pictureSizes.size() - 1; indexOfPicture >= 0; --indexOfPicture) {
            pictureSize = pictureSizes.get(indexOfPicture);
            pictureRatio = (double) pictureSize.width / (double) pictureSize.height;
            isUsablePicture = false;
             
            for (int indexOfPreview = previewSizes.size() - 1; indexOfPreview >= 0; --indexOfPreview) {
                previewSize = previewSizes.get(indexOfPreview);
                 
                previewRatio = (double) previewSize.width / (double) previewSize.height;
                 
                if (Math.abs(pictureRatio - previewRatio) < aspectTolerance) {
                    isUsablePicture = true;
                    break;
                }
            }
             
            if (isUsablePicture == false) {
                pictureSizes.remove(indexOfPicture);
            }
        }
    }

    
    //
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
