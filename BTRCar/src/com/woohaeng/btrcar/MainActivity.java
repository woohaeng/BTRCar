package com.woohaeng.btrcar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	BluetoothClient mBluetooth = new BluetoothClient();
	String mBluetoothName = "";
	String mBluetoothAddress = "";
	SharedPreferences defaultSharedPref = null;
	
	ImageView ivControl = null;
	ImageView ivPos = null;
	int mStatusBarSize = 0;

	private SensorManager mSensorManager;
	private SensorEventListener gyroListener;
	private Sensor gyroSensor;
    private Sensor accSensor;
    private boolean mSensorOn = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ivControl = (ImageView) findViewById(R.id.imageViewControl);
		ivPos = (ImageView) findViewById(R.id.imageViewPos);
		
		defaultSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		mBluetoothName = defaultSharedPref.getString("BluetoothName", "");
		mBluetoothAddress = defaultSharedPref.getString("BluetoothAddress", "");

		Log.d("MainActivity", "bluetooth_name=" + mBluetoothName);
		Log.d("MainActivity", "bluetooth_address=" + mBluetoothAddress);

		setBluetoothInfo();
		
//		if (!mBluetoothAddress.equals("")) {
//			mBluetooth.connect(mBluetoothAddress);
//			
//			if (!mBluetooth.connect(mBluetoothAddress)) {
//				TextView textView = (TextView) findViewById(R.id.textBtAddr);
//				textView.setText("Connection failed.");
//			} 
//		}
		
		RelativeLayout layout = new RelativeLayout(this);
	    layout.post(new Runnable() {
	        @Override
	        public void run() {
	            getStatusBarSize();
	        }
	    });
	    
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		gyroListener = new SensorEventListener() {
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				Sensor sensor = event.sensor;
				
			    int accelXValue = 0;
			    int accelYValue = 0;
			    int accelZValue = 0;
			    
			    float posX = 0;
			    float posY = 0;

		        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
		            accelXValue = (int) (event.values[0] * 10);
		            accelYValue = (int) (event.values[1] * 10);
		            accelZValue = (int) (event.values[2] * 10);
		            
					TextView textViewGyro = (TextView) findViewById(R.id.textViewGyro);

					textViewGyro.setText("X=" + String.format("%03d", accelXValue) + ", Y=" + String.format("%03d", accelYValue) + ", Z=" + String.format("%03d", accelZValue));

		            if (accelXValue < -30) accelXValue = -30;
		            if (accelXValue > 30) accelXValue = 30;
		            if (accelXValue < 10 && accelXValue > -10) accelXValue = 0;

		            accelYValue = (accelYValue - 60);
		            if (accelYValue < -25) accelYValue = -25;
		            if (accelYValue > 25) accelYValue = 25;
		            if (accelYValue < 10 && accelYValue > -10) accelYValue = 0;
		            
		            if (mSensorOn) {
		            	checkPosition2(accelXValue * -1, accelYValue);
		            }

//					textViewGyro.setText("X=" + String.format("%03d", accelXValue) + ", Y=" + String.format("%03d", accelYValue) + ", Z=" + String.format("%03d", accelZValue));
		            
		            posX = (accelXValue * (ivControl.getWidth() / 2)) / 40;
		            posX = ivControl.getX() + (ivControl.getRight() / 2) - posX;

		            posY = (accelYValue * (ivControl.getHeight() / 2)) / 30;
		            posY = (ivControl.getY() + ivControl.getBottom() / 2) + posY;

		            ivPos.setX(posX - (ivPos.getWidth() / 2) - 60);
		            ivPos.setY(posY - (ivPos.getHeight() / 2) - mStatusBarSize - 110);
		            
		            
//		            textViewGyro.setText("X=" + String.format("%03d", accelXValue) + 
//		            		", X2=" + String.format("%03d", (int)posX) +
//		            		", LEFT=" + String.format("%03d", (int)ivControl.getX()) + 
//		            		", WIDTH=" + String.format("%03d", ivControl.getWidth()));
		            
//		            textViewGyro.setText("Y=" + String.format("%03d", accelYValue) + 
//		            		", Y2=" + String.format("%03d", (int)posY) +
//		            		", TOP=" + String.format("%03d", (int)ivControl.getY()) + 
//		            		", HEIGHT=" + String.format("%03d", ivControl.getHeight()));

		        }

			}
		};
	}
	
	@Override
	protected void onDestroy() {
		mBluetooth.close();
		
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		mSensorManager.unregisterListener(gyroListener);

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mSensorManager.registerListener(gyroListener, gyroSensor,SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(gyroListener, accSensor,SensorManager.SENSOR_DELAY_FASTEST);
        
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onAccelerometer(View v) {
		Log.d("MainActivity", "onAccelerometer()");
		
		mSensorOn = ((ToggleButton) v).isChecked();
		if (mSensorOn) {
			ivPos.setVisibility(View.VISIBLE);
		}
		else {
			ivPos.setVisibility(View.INVISIBLE);
		}
	}
	
	public void onBluetoothScan(View v) {
		Log.d("MainActivity", "onBluetoothScan()");
		
		Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
		startActivityForResult(serverIntent, CommonUtil.REQUEST_CONNECT_DEVICE);
	}
	
	private void getStatusBarSize() {
		Rect rectgle = new Rect();
		Window window = getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		int StatusBarHeight = rectgle.top;
		int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		int TitleBarHeight = contentViewTop - StatusBarHeight;

		Log.i("getHeight", "StatusBar Height= " + StatusBarHeight + " TitleBar Height = " + TitleBarHeight);
		
		mStatusBarSize = StatusBarHeight + TitleBarHeight;
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		int act = event.getAction();

		switch (act & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			Log.d("zoom", ""+event.getPointerCount());
			
			for (int i = 0; i < event.getPointerCount(); i++) {
				if (checkPosition(event.getX(i), event.getY(i)))
					break;
			}
		}
		return super.onTouchEvent(event);
	}

	private boolean checkPosition(float posX, float posY) {
		if (ivControl.getX() < posX && ivControl.getRight() > posX &&
			ivControl.getY() + mStatusBarSize < posY && ivControl.getBottom() + mStatusBarSize > posY) {
			
//            ivPos.setX(posX - (ivPos.getWidth() / 2));
//            ivPos.setY(posY - (ivPos.getHeight() / 2) - mStatusBarSize);
            
			posX = posX - ivControl.getX();
			posY = posY - (ivControl.getY() + mStatusBarSize);
			
			posX = posX - (ivControl.getWidth() / 2);
			posY = posY - (ivControl.getHeight() / 2);
			
			posX = (posX * 265) / (ivControl.getWidth() / 2);
			posY = (posY * 265) / (ivControl.getHeight() / 2);
			
			if (posX < -255) posX = -255;
			if (posX > 255) posX = 255;
			if (posX < 50 && posX > -50) posX = 0;
			
			if (posY < -255) posY = -255;
			if (posY > 255) posY = 255;
			if (posY < 50 && posY > -50) posY = 0;
			
			TextView textView = (TextView) findViewById(R.id.textView1);
			textView.setText("X=" + (int)posX + ", Y=" + (int)posY);
			
			sendDate(posX, posY);
			
			return true;
		}
		
		return false;
	}
	
	private void checkPosition2(float posX, float posY) {
		posX = (posX * 265) / 30;
		posY = (posY * 265) / 25;
		
		if (posX < -255) posX = -255;
		if (posX > 255) posX = 255;
		if (posX < 50 && posX > -50) posX = 0;
		
		if (posY < -255) posY = -255;
		if (posY > 255) posY = 255;
		if (posY < 50 && posY > -50) posY = 0;
		
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setText("X=" + (int)posX + ", Y=" + (int)posY);
		
		sendDate(posX, posY);
	}
	
	private void sendDate(float posX, float posY) {
		String direction1 = "";
		if (posX < 10) {
			direction1 = String.format("$L%03d,", (int)Math.abs(posX));
		}
		else if (posX > 10) {
			direction1 = String.format("$R%03d,", (int)posX);
		}
		else {
			direction1 = String.format("$N%03d,", (int)posX);
		}

		String direction2 = "";
		if (posY < 10) {
			direction2 = String.format("F%03d;", (int)Math.abs(posY));
		}
		else if (posY > 10) {
			direction2 = String.format("B%03d;", (int)posY);
		}
		else {
			direction2 = String.format("N%03d;", (int)posY);
		}
		
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setText(direction1+direction2);

		mBluetooth.write(direction1+direction2);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CommonUtil.REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				mBluetoothName = data.getExtras().getString(CommonUtil.EXTRA_BLUETOOTH_NAME);
				mBluetoothAddress = data.getExtras().getString(CommonUtil.EXTRA_BLUETOOTH_ADDRESS);
				
				setBluetoothInfo();

				Log.d("MainActivity", "bluetooth_name=" + mBluetoothName);
				Log.d("MainActivity", "bluetooth_address=" + mBluetoothAddress);
				
				if (!mBluetoothAddress.equals("")) {
					SharedPreferences.Editor editor = defaultSharedPref.edit();
					editor.putString("BluetoothName", mBluetoothName);
					editor.putString("BluetoothAddress", mBluetoothAddress);
					editor.commit();

					if (!mBluetooth.connect(mBluetoothAddress)) {
						TextView textView = (TextView) findViewById(R.id.textBtAddr);
						textView.setText("Connection failed.");
					}
				}
			}
			
			break;
		}
	}
	
	private void setBluetoothInfo() {
		TextView textView = (TextView) findViewById(R.id.textBtName);
		textView.setText("Bluetooth Name: " + mBluetoothName);

		textView = (TextView) findViewById(R.id.textBtAddr);
		textView.setText("Bluetooth Address: " + mBluetoothAddress);
	}
}
