package RBGroup.weight;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Scale extends Activity implements OnClickListener, SensorEventListener {

	private int CHANGE_INTERVAL = 12;
	private int MAX_MOVEMENT_NUM = 1;//50
	private final int RETURN_RESULT_OKAY = 0;
	private Button btnStart;
	private SharedPreferences sp;
	private boolean isman = true;
	private int height = 150;
	private float average_weight;
	int resultDecimal = 0;
	int resultUnit = 0;
	int resultFloat = 0;
	
	private TextView txtNotice;
	private ImageView imgDecimal, imgUnit, imgFloat;
	private int[] images = new int[] { R.drawable.no_0, R.drawable.no_1,
			R.drawable.no_2, R.drawable.no_3, R.drawable.no_4, R.drawable.no_5,
			R.drawable.no_6, R.drawable.no_7, R.drawable.no_8, R.drawable.no_9, };
	Random randomGenerator = new Random();

	private final int CHANGE_START = 0;
	private final int CHANGE_DECIMAL = 0;
	private final int CHANGE_UNIT = 1;
	private final int CHANGE_FLOAT = 2;
	private final int CHANGE_LAST_DETAIL_MOVE = 3;
	private final int CHANGE_END = 4;
	private final int CHANGE_MOVE_REALIZE_ACTIVITY = 5;

	//Sensor
	private SensorManager sm;
	private SensorEventListener accL;
	private Sensor accSensor;
	private float current=0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.scale);

		//SENSOR
		sm = (SensorManager)getSystemService(SENSOR_SERVICE); 
	    accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		btnStart = (Button) findViewById(R.id.btnStart);
		txtNotice = (TextView) findViewById(R.id.txtNotice);
		imgDecimal = (ImageView) findViewById(R.id.imgDecimal);
		imgUnit = (ImageView) findViewById(R.id.imgUnit);
		imgFloat = (ImageView) findViewById(R.id.imgFloat);

		sp = getSharedPreferences("info", MODE_PRIVATE);
		isman = sp.getBoolean("isman", true);
		height = sp.getInt("height", 170);
		resultFloat = getRandomIndex();
		
		if (height < 150) {
			average_weight = height - 100;
		} else if (150 < height && height < 160) {
			average_weight = (float) ((height - 150) * 0.5) + 50;
		} else {
			average_weight = (float) ((height - 100) * 0.9);
		}
		Log.e("Scale", "height : " + height + "      /      isman : " + isman);
		btnStart.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnStart) {
			btnStart.setVisibility(View.INVISIBLE);
			btnStart.setClickable(false);
			myTask.sendEmptyMessageDelayed(CHANGE_START, 1000);
		}
		// myTask.sendEmptyMessageDelayed(1, 11000);
		// myTask.sendEmptyMessageDelayed(2, 12000);
		
//		  preview.Capture(new Camera.PictureCallback() {
//		  
//		  @Override public void onPictureTaken(byte[] data, Camera camera) {
//		  ByteArrayInputStream in = new ByteArrayInputStream(data);
//		  //in.read(data); Bitmap bitmap = BitmapFactory.decodeStream(in);
//		  img.setImageBitmap(bitmap); } });
		 
		// myTask.sendEmptyMessageDelayed(0, 4000);
		// myTask.sendEmptyMessageDelayed(0, 6000);
	}
	
	Handler myTask = new Handler() {
		public void handleMessage(android.os.Message message) {
			switch (message.what) {
			case 0:
				txtNotice.setText(R.string.scale_calculate_now);
				updateDecimal();
				break;
			case 1:
				updateUnit();
				break;
			case 2:
				updateFloat();
				break;
			case 3:
				updateLastDetailMove();
				break;
			case 4:
				Log.e("Scale", "averageWeight : " + average_weight);
				
				StringBuilder sb = new StringBuilder();
				sb.append(resultDecimal);
				sb.append(resultUnit);
				sb.append(".");
				sb.append(resultFloat);
				
				txtNotice.setText(R.string.scale_surprise_comment);
				Common common = new Common(Scale.this);
			    common.saveWeight( Float.parseFloat(sb.toString()) );
				startActivityForResult(new Intent(Scale.this, Calculate.class), RETURN_RESULT_OKAY);

				break;
			case 5:
				startActivity(new Intent(Scale.this, Realize.class));
				finish();
				break;
			}
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if ( resultCode == RETURN_RESULT_OKAY) {
			txtNotice.setVisibility(View.VISIBLE);
			myTask.sendEmptyMessageDelayed(CHANGE_MOVE_REALIZE_ACTIVITY, 3000);
		}
	};
	
	int detailMovementNum = 5;
	int detailMovementUnitNum = 9;
	private void updateLastDetailMove() {
		if ( detailMovementNum == 0) {
			imgFloat.setImageResource(images[ resultFloat ]);
			myTask.sendEmptyMessageDelayed(CHANGE_END, 1000);
		} else {
			imgUnit.setImageResource(images[ (detailMovementUnitNum)%10]);
			imgFloat.setImageResource(images[ getRandomIndex() ]);
			Log.e("Scale", "detailMovementNum : "+ detailMovementNum);
			Log.e("Scale", "resource num : "+ (detailMovementUnitNum-detailMovementNum)%10);
			
			detailMovementNum--;
			detailMovementUnitNum--;
			myTask.sendEmptyMessageDelayed(CHANGE_LAST_DETAIL_MOVE, 200*(6-detailMovementNum));
		}
	}
	
	private void updateDecimal() {
		if (MAX_MOVEMENT_NUM == 0) {
			resultDecimal = (int) (average_weight / 10);
			resultUnit = (int) (average_weight - (resultDecimal * 10));
			imgDecimal.setImageResource(images[resultDecimal]);
			
			detailMovementUnitNum += resultUnit;
			detailMovementUnitNum += detailMovementNum;
			
			myTask.sendEmptyMessageDelayed(CHANGE_LAST_DETAIL_MOVE, 500 );
			
		} else {
			imgDecimal.setImageResource(images[getRandomIndex()]);
			MAX_MOVEMENT_NUM--;
			myTask.sendEmptyMessageDelayed(CHANGE_UNIT, CHANGE_INTERVAL);
		}
	}

	private void updateUnit() {
		imgUnit.setImageResource(images[getRandomIndex()]);
		myTask.sendEmptyMessageDelayed(CHANGE_FLOAT, CHANGE_INTERVAL);
	}

	private void updateFloat() {
		imgFloat.setImageResource(images[getRandomIndex()]);
		myTask.sendEmptyMessageDelayed(CHANGE_DECIMAL, CHANGE_INTERVAL);
	}

	private int getRandomIndex() {
		return randomGenerator.nextInt(10);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		sm.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float var0 = event.values[0];
		float var1 = event.values[1];
		float var2 = event.values[2];
		
		current = Math.abs(var0*var1*var2);
	}
}
