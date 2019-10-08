package com.example.supercolliderble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BLEActivity extends AppCompatActivity {
    // BLE
    private BluetoothLeScanner mBLEScanner;
    private ScanSettings settings;
    private BluetoothAdapter mBluetoothAdapter;
    private List<ScanFilter> filterList;

    private static final int REQUEST_ENABLE_BT = 1;
    protected ArrayList<BLEdata> bleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Need Location Permission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                Utility.PERMISSION_REQUEST_COARSE_LOCATION);

        setupBLE();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            filterList = new ArrayList<ScanFilter>();
        }
    }

    @Override
    public void onPause() { super.onPause(); }

    @Override
    public void onDestroy() { super.onDestroy(); }

    private void setupBLE() {
        // Use this check to determine whether BLE is supported on the device.
        // Then you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "이 디바이스는 BLE를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter. For API level 18 and above,
        // get a reference to BluetoothAdapter through BlueToothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "이 디바이스는 BLE를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // 블루투스 신호 스캔
    protected boolean scanLeDevice(final boolean enable) {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            if(enable) {
                mBLEScanner.startScan(filterList, settings, mScanCallback);
            }
            else {
                mBLEScanner.stopScan(mScanCallback);
            }
        }
        return enable;
    }

    private int is_found(String mac){
        for(int i = 0; i < bleList.size(); i++) {
            if (bleList.get(i).getMac().equals(mac)) {
                return i;
            }
        }

        return -1;
    }

    // BLE signal 수신 시 호출되는 함수
    private ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            final BluetoothDevice device = result.getDevice();
            mBluetoothAdapter.getState();
            device.getBluetoothClass().getMajorDeviceClass();
            String mac;
            int rssi;
            mac = device.getAddress();
            rssi = result.getRssi();
//            battery =
            int index = is_found(mac);

            // MAC주소가 없으면
            if(index == -1){
                bleList.add(new BLEdata(mac, rssi));

            }else{
                bleList.get(index).addRssi(rssi);
            }

            // bleList에서 MAC주소 비교후 동일하며 rssi갱신
            Log.d("bleList", String.valueOf(bleList.size()));

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("SCAN2", "ScanResult - Results: " + sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("SCAN", "Scan Failed:Error Code: " + errorCode);
        }
    };

    // Need Location Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Utility.PERMISSION_REQUEST_COARSE_LOCATION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
            }
            else {
                // Alert the user that this application requires the location permission to perform the scan.
            }
        }
    }
}
