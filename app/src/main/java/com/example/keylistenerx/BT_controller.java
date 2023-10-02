package com.example.keylistenerx;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class BT_controller {
    private static volatile BT_controller instance;
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private BroadcastReceiver scanReceiver = null;

    private BT_controller(Context con) {
        // 获取BluetoothAdapter实例
        context = con;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.e("xiaohan", "bluetoothAdapter is " + bluetoothAdapter);
    }

    public static BT_controller getInstance(Context context) {
        if (instance == null) {
            synchronized (BT_controller.class) {
                if (instance == null) {
                    instance = new BT_controller(context);
                }
            }
        }
        return instance;
    }

    public void enableBluetooth() {
        Log.e("xiaohan", "disableBluetooth");
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            //Toast.makeText(context,"get bt_status -> on",Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "get bt_status -> on");
            Log.e("xiaohan", bluetoothAdapter+" now is "+bluetoothAdapter.isEnabled());
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()==false) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "1cannot get BLUETOOTH_CONNECT", Toast.LENGTH_SHORT).show();
                    Log.e("xiaohan", "1cannot get BLUETOOTH_CONNECT");
                    return;
                }
                Log.e("xiaohan", "bluetoothAdapter.enable()" );
                bluetoothAdapter.enable();
            }else {
                Log.e("xiaohan", "bluetoothAdapter is " + bluetoothAdapter+" state is "+bluetoothAdapter.isEnabled() + "so do not enable");
            }
            return;
        }
    }

    public void disableBluetooth() {
        Log.e("xiaohan", "disableBluetooth");
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            //Toast.makeText(context,"get bt_status -> off",Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "get bt_status -> off" );
            Log.e("xiaohan", bluetoothAdapter+" "+bluetoothAdapter.isEnabled());
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "1cannot get BLUETOOTH_CONNECT", Toast.LENGTH_SHORT).show();
                    Log.e("xiaohan", "1cannot get BLUETOOTH_CONNECT");
                    return;
                }
                Log.e("xiaohan", "bluetoothAdapter.disable()" );
                bluetoothAdapter.disable();
            }else {
                Log.e("xiaohan", "bluetoothAdapter is " + bluetoothAdapter+" state is "+bluetoothAdapter.isEnabled() + "so do not disable");
            }
            return;
        }
    }


    public void startScan() {
        Log.e("xiaohan", "start scan");
        if(!bluetoothAdapter.isEnabled()){
            Toast.makeText(context, "bluetooth do not enable", Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "bluetooth do not enable so do not start scan,return");
        }
        if(bluetoothAdapter == null){
            Toast.makeText(context, "btAdapter is null", Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "bluetoothAdapter is null so do not start scan,return");
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "1cannot get BLUETOOTH_SCAN", Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "1cannot get BLUETOOTH_SCAN");
            return;
        }
        if(scanReceiver == null) {
            // 注册广播接收器来接收蓝牙扫描结果
            scanReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        // 扫描到蓝牙设备
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        Log.e("xiaohan", "Found device: " + device.getName() + " - " + device.getAddress());

                    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        // 扫描结束
                        Log.e("xiaohan", "Scan finished");
                    }
                }
            };

            // 注册广播接收器
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(scanReceiver, filter);
        }
        Log.e("xiaohan", "Note : default scan timeout is 15ms");
        bluetoothAdapter.startDiscovery();
    }

    public void stopScan() {
        Log.e("xiaohan", "stop scan");
        if(!bluetoothAdapter.isEnabled()){
            Toast.makeText(context, "bluetooth do not enable", Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "bluetooth do not enable so do not stop scan,return");
        }
        if(bluetoothAdapter == null){
            Toast.makeText(context, "btAdapter is null", Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "bluetoothAdapter is null so do not stop scan,return");
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "1cannot get BLUETOOTH_SCAN", Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "1cannot get BLUETOOTH_SCAN");
            return;
        }
        bluetoothAdapter.cancelDiscovery();
        if(scanReceiver != null){
            context.unregisterReceiver(scanReceiver);
            scanReceiver = null;
        }
    }
}
