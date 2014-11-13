package com.woohaeng.btrcar;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * 블루투스 연결 관련 모듈
 */
public class BluetoothClient {
	BluetoothAdapter mBluetoothAdapter = null;
	BluetoothSocket mSocket = null;
	OutputStream mOutStream = null;
	
	String mBluetoothAddress = "";

	public boolean check() {
		if (mBluetoothAdapter == null) {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		
		if (mBluetoothAdapter == null) {
			return false;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			return false;
		}
		
		return true;
	}
	
	public boolean connect(String bluetoothAddress) {
		if (!mBluetoothAddress.equals(bluetoothAddress) && !mBluetoothAddress.equals("")) {
			close();
		}
		
		mBluetoothAddress = bluetoothAddress;

		if (mBluetoothAdapter == null) {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		
		if (mBluetoothAdapter == null) {
			return false;
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
			return false;
		}

		try {
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mBluetoothAddress);

			if (mSocket == null) {
				mSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

				mSocket.connect();
			}
		} catch (IOException e) {
			mSocket = null;

			e.printStackTrace();
			return false;
		}
			
		try {
			mOutStream = mSocket.getOutputStream();
		} catch (IOException e) {
			mOutStream = null;
			
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
//	public boolean reconnect() {
//	return connect(mBluetoothAddress);
//	}

	public boolean close() {
		try {
			if (mOutStream != null) {
				mOutStream.close();
			}

			if (mSocket != null) {
				mSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		mOutStream = null;
		mSocket = null;
		
		return true;
	}
	
	public boolean isConnected() {
		if (mOutStream == null) {
			return false;
		}
		
		return true;
	}
		
    public boolean write(String strData) {
    	byte[] buffer = strData.getBytes();
    	
		try {
			if (mOutStream != null && mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
				mOutStream.write(buffer);
			}
			else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
