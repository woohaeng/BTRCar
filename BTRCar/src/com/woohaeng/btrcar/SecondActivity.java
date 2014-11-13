package com.woohaeng.btrcar;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class SecondActivity extends Activity {
	BluetoothClient mBluetooth = new BluetoothClient();
	String mBluetoothName = "";
	String mBluetoothAddress = "";
	SharedPreferences defaultSharedPref = null;
	
	private SensorManager mSensorManager;
	private SensorEventListener gyroListener;
	private Sensor gyroSensor;
    private Sensor accSensor;
//    private boolean mSensorOn = false;
    
    private TimerTask mTask;
    private Timer mTimer;
    
    int[] sensorValue;
    int valueX = 0;
    int valueY = 0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		
		sensorValue = new int[11];
		
		defaultSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		mBluetoothName = defaultSharedPref.getString("BluetoothName", "");
		mBluetoothAddress = defaultSharedPref.getString("BluetoothAddress", "");

		Log.d("SecondActivity", "bluetooth_name=" + mBluetoothName);
		Log.d("SecondActivity", "bluetooth_address=" + mBluetoothAddress);

		if (!mBluetoothAddress.equals("")) {
			mBluetooth.connect(mBluetoothAddress);
			
			TextView textView = (TextView) findViewById(R.id.textBtAddr);
			if (!mBluetooth.connect(mBluetoothAddress)) {
				textView.setText("Connection failed.");
			}
			else {
				textView.setText(mBluetoothName + " connected.");
			}
		}
		
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
        SeekBar seekBarLR = (SeekBar) findViewById(R.id.seekBarLR);
        seekBarLR.setEnabled(false);

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
			    
		        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
		            accelXValue = (int) (event.values[0] * 10);
		            accelYValue = (int) (event.values[1] * 10);
		            accelZValue = (int) (event.values[2] * 10);
		            
		            for (int i = 0; i < 10; i++) {
		            	valueX = valueX + sensorValue[i]; 
		            	sensorValue[i] = sensorValue[i+1];
		            }
		            
		            sensorValue[10] = accelYValue;		            
		            valueX = valueX / 10;

		            TextView textView = (TextView) findViewById(R.id.textView1);
		            textView.setText("X=" + String.format("%03d", accelXValue) + 
		            		", Y=" + String.format("%03d", accelYValue) + 
		            		", Z=" + String.format("%03d", accelZValue));

		            if (valueX < -40) valueX = -40;
		            if (valueX > 40) valueX = 40;
		            if (valueX < 5 && valueX > -5) valueX = 0;

		            valueX = (valueX * 255) / 40;
	
		            SeekBar seekBarLR = (SeekBar) findViewById(R.id.seekBarLR);
		            seekBarLR.setProgress(valueX + 255);
		            
//					textView = (TextView) findViewById(R.id.textView2);
//		            textView.setText("valueX=" + String.format("%03d", valueX) + 
//		            		", valueY=" + String.format("%03d", valueY));

//		            if (mSensorOn) {
//		            	sendDate();
//		            }
		        }
			}
		};
		
		SeekBar seekBar = (SeekBar) findViewById(R.id.seekBarFB);

		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				valueY = progress - 255;
	            if (valueY < 50 && valueY > -50) valueY = 0;

//				TextView textView = (TextView) findViewById(R.id.textView2);
//	            textView.setText("valueX=" + String.format("%03d", valueX) + 
//	            		", valueY=" + String.format("%03d", valueY));
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		
		mTask = new TimerTask() {
			@Override
			public void run() {
				sendDate();
			}
		};

		mTimer = new Timer();

		mTimer.schedule(mTask, 1000, 100);
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
		//getMenuInflater().inflate(R.menu.second, menu);
		menu.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, "블루투스 찾기");
		menu.add(Menu.NONE, Menu.FIRST+2, Menu.NONE, "블루투스 연결");
		menu.add(Menu.NONE, Menu.FIRST+3, Menu.NONE, "블루투스 끊기");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
		
		if (id == Menu.FIRST+1) {
			Log.d("SecondActivity", "BluetoothScan()");
			
			Intent serverIntent = new Intent(SecondActivity.this, DeviceListActivity.class);
			startActivityForResult(serverIntent, CommonUtil.REQUEST_CONNECT_DEVICE);
			
			return true;
		}
		else if (id == Menu.FIRST+2) {
			return true;
		}
		else if (id == Menu.FIRST+3) {
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
//	public boolean onTouchEvent(MotionEvent event) {
//		int act = event.getAction();
//
//		switch (act & MotionEvent.ACTION_MASK) {
//		case MotionEvent.ACTION_DOWN:
//		case MotionEvent.ACTION_MOVE:
//			Log.d("zoom", ""+event.getPointerCount());
//			
//			for (int i = 0; i < event.getPointerCount(); i++) {
////				if (checkPosition(event.getX(i), event.getY(i)))
////					break;
//			}
//		}
//		return super.onTouchEvent(event);
//	}

	private final Handler handler = new Handler();
	
	private void sendDate() {
		Runnable updater = new Runnable() {
			public void run() {
				int x = valueX;
				int y = valueY;
				String direction1 = "";
				
				x = (x * 80) / 255;
				y = (y * 120) / 255;
				
				if (x < 10) {
					direction1 = String.format("$L%03d,", Math.abs(x));
				}
				else if (x > 10) {
					direction1 = String.format("$R%03d,", x);
				}
				else {
					direction1 = String.format("$N%03d,", x);
				}

				String direction2 = "";
				if (y < 10) {
					direction2 = String.format("F%03d;", Math.abs(y));
				}
				else if (y > 10) {
					direction2 = String.format("B%03d;", y);
				}
				else {
					direction2 = String.format("N%03d;", y);
				}
				
				TextView textView = (TextView) findViewById(R.id.textView2);
				textView.setText(direction1+direction2);

				mBluetooth.write(direction1+direction2);
			}
		};

		handler.post(updater);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CommonUtil.REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				mBluetoothName = data.getExtras().getString(CommonUtil.EXTRA_BLUETOOTH_NAME);
				mBluetoothAddress = data.getExtras().getString(CommonUtil.EXTRA_BLUETOOTH_ADDRESS);
				
				Log.d("SecondActivity", "bluetooth_name=" + mBluetoothName);
				Log.d("SecondActivity", "bluetooth_address=" + mBluetoothAddress);
				
				if (!mBluetoothAddress.equals("")) {
					SharedPreferences.Editor editor = defaultSharedPref.edit();
					editor.putString("BluetoothName", mBluetoothName);
					editor.putString("BluetoothAddress", mBluetoothAddress);
					editor.commit();

					TextView textView = (TextView) findViewById(R.id.textBtAddr);
					if (!mBluetooth.connect(mBluetoothAddress)) {
						textView.setText("Connection failed.");
					}
					else {
						textView.setText(mBluetoothName + " connected.");
					}
				}
			}
			
			break;
		}
	}
}