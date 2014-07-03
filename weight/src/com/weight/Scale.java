package com.weight;

import java.util.Random;

import com.weight.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Scale extends BannerActivity implements OnClickListener,
		SensorEventListener {

	private final int CHANGE_START = 0;
	private final int CHANGE_DECIMAL = 0;
	private final int CHANGE_UNIT = 1;
	private final int CHANGE_FLOAT = 2;
	private final int CHANGE_LAST_DETAIL_MOVE = 3;
	private final int CHANGE_END = 4;
	private final int CHANGE_MOVE_REALIZE_ACTIVITY = 5;
	
	private final class HandlerExtension extends Handler {
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
				common.saveWeight(Float.parseFloat(sb.toString()));
				startActivityForResult(new Intent(Scale.this, SnapShot.class),
						RETURN_RESULT_OKAY);

				break;
			case 5:
				startActivity(new Intent(Scale.this, Realize.class));
				finish();
				break;
			}
		}
	}

	private final int _MAX_MOVEMENT_NUM = 1;
	private final int _detailMovementNum = 5;
	private final int _detailMovementUnitNum = 9;
	
	
	private int CHANGE_INTERVAL = 12;
	private int MAX_MOVEMENT_NUM = _MAX_MOVEMENT_NUM;// 50
	private boolean SCALE_START = false;
	private final float MAX_HORIZONTAL_SENSER = 1.2f;
	private final int RETURN_RESULT_OKAY = 0;
	private Button btnStart;
	private SharedPreferences sp;
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

	// Sensor
	private SensorManager sm;
	private float accSensitivity = 0;
	
	//엑셀로미터 센서(가속)
	private Sensor accSensor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.scale);

		// SENSOR
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		btnStart = (Button) findViewById(R.id.btnStart);
		txtNotice = (TextView) findViewById(R.id.txtNotice);
		imgDecimal = (ImageView) findViewById(R.id.imgDecimal);
		imgUnit = (ImageView) findViewById(R.id.imgUnit);
		imgFloat = (ImageView) findViewById(R.id.imgFloat);

		sp = getSharedPreferences("info", MODE_PRIVATE);
		height = sp.getInt("height", 170);
		resultFloat = getRandomIndex();

		if (height < 150) {
			average_weight = height - 100;
		} else if (150 < height && height < 160) {
			average_weight = (float) ((height - 150) * 0.5) + 50;
		} else {
			average_weight = (float) ((height - 100) * 0.9);
		}
		btnStart.setOnClickListener(this);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,      
				FrameLayout.LayoutParams.WRAP_CONTENT
		);

		params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
		
		createSmartBanner(this, findViewById(R.id.scale_banner));
		params.setMargins(0, 0, 0, getBannerHeight(this)+10);
		btnStart.setLayoutParams(params);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnStart) {
			
			if (accSensitivity >= MAX_HORIZONTAL_SENSER) {
				Toast.makeText(this, getResources().getString(R.string.scale_notice_warn ), Toast.LENGTH_SHORT).show();
			} else {
				SCALE_START = true;
				btnStart.setVisibility(View.INVISIBLE);
				btnStart.setClickable(false);
				myTask.sendEmptyMessageDelayed(CHANGE_START, 1000);
			}
			
		}
	}

	Handler myTask = new HandlerExtension();

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RETURN_RESULT_OKAY) {
			txtNotice.setVisibility(View.VISIBLE);
			myTask.sendEmptyMessageDelayed(CHANGE_MOVE_REALIZE_ACTIVITY, 3000);
		}
	};

	int detailMovementNum = _detailMovementNum;
	int detailMovementUnitNum = _detailMovementUnitNum;
	
	private void updateLastDetailMove() {
		if (detailMovementNum == 0) {
			SCALE_START = false;
			imgFloat.setImageResource(images[resultFloat]);
			myTask.sendEmptyMessageDelayed(CHANGE_END, 1000);
		} else {
			imgUnit.setImageResource(images[(detailMovementUnitNum) % 10]);
			imgFloat.setImageResource(images[getRandomIndex()]);
			Log.e("Scale", "detailMovementNum : " + detailMovementNum);
			Log.e("Scale", "resource num : "
					+ (detailMovementUnitNum - detailMovementNum) % 10);

			detailMovementNum--;
			detailMovementUnitNum--;
			myTask.sendEmptyMessageDelayed(CHANGE_LAST_DETAIL_MOVE,
					200 * (6 - detailMovementNum));
		}
	}

	private void updateDecimal() {
		if (MAX_MOVEMENT_NUM == 0) {
			resultDecimal = (int) (average_weight / 10);
			resultUnit = (int) (average_weight - (resultDecimal * 10));
			imgDecimal.setImageResource(images[resultDecimal]);

			detailMovementUnitNum += resultUnit;
			detailMovementUnitNum += detailMovementNum;

			myTask.sendEmptyMessageDelayed(CHANGE_LAST_DETAIL_MOVE, 500);

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
		sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		sm.unregisterListener(this);
		super.onPause();
	}

	/** Called before the activity is destroyed. */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		if (!SCALE_START)
			return;
		
		float accSensitivity = Math.abs(event.values[0] * event.values[1] * event.values[2]);

		if (this.accSensitivity - accSensitivity > 1) {
			Toast.makeText(this, getResources().getString(R.string.scale_notice_restart), Toast.LENGTH_LONG).show();
			
			myTask.removeCallbacksAndMessages(null);
			//myTask.removeMessages(0);
//			myTask.removeMessages(1);
//			myTask.removeMessages(2);
//			myTask.removeMessages(3);
//			myTask.removeMessages(4);
//			myTask.removeMessages(5);
			MAX_MOVEMENT_NUM = _MAX_MOVEMENT_NUM;
			detailMovementNum = _detailMovementNum;
			detailMovementUnitNum = _detailMovementUnitNum;
			
			imgDecimal.setImageResource(images[0]);
			imgUnit.setImageResource(images[0]);
			imgFloat.setImageResource(images[0]);
			SCALE_START = false;
			btnStart.setVisibility(View.VISIBLE);
			btnStart.setClickable(true);
			this.accSensitivity = 0;
		} else {
			this.accSensitivity = accSensitivity;
			Log.e("Scale", "accSensitivity : " + accSensitivity);
		}
	}
}
