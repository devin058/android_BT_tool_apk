package com.example.keylistenerx;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PTS_TOOL {
    private Context context;
    private BluetoothGatt bluetoothGatt;

    // HOGP Service
    public static final UUID HOGP_SERVICE_UUID = UUID.fromString("00001812-0000-1000-8000-00805f9b34fb");
    // HOGP Characteristic
    public static final UUID REPORT_CHARACTERISTICS_UUID = UUID.fromString("00002a4d-0000-1000-8000-00805f9b34fb");
    // HOGP Descriptor
    public static final UUID HOGP_REPORT_REFERENCE_UUID = UUID.fromString("00002908-0000-1000-8000-00805f9b34fb");

    public PTS_TOOL(Context context) {
        this.context = context;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bluetoothDevice = null;

        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this.context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this.context, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION}, 111);
            Toast.makeText(context, "Cannot get Bluetooth permissions", Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "Cannot get Bluetooth permissions");
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().contains("PTS") || device.getName().contains("ByHan")) {
                bluetoothDevice = device;
                break;
            }
        }

        if (bluetoothDevice == null) {
            Toast.makeText(context, "Cannot find connected device", Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "Cannot find connected device");
            return;
        }

        // 创建 BluetoothGattCallback 对象
        BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // 连接成功
                    bluetoothGatt = gatt; // 将 gatt 对象赋值给全局变量 bluetoothGatt
                    Log.e("xiaohan", "get BluetoothProfile.STATE_CONNECTED event" +
                            bluetoothGatt + ",bluetoothGatt.getServices() = " + bluetoothGatt.getServices());
                    bluetoothGatt.discoverServices(); // 发现设备上的服务
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // 连接断开
                    bluetoothGatt = null; // 将全局变量 bluetoothGatt 置为 null
                    Log.e("xiaohan", "get BluetoothProfile.STATE_DISCONNECTED event" +
                            bluetoothGatt + ",bluetoothGatt.getServices() = " + bluetoothGatt.getServices());
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    // 服务发现成功
                    List<BluetoothGattService> services = gatt.getServices();
                    Log.e("xiaohan", "Services discovered: " + services);
                    PTS_HOGP("11111111");
                } else {
                    // 服务发现失败
                    Log.e("xiaohan", "Service discovery failed with status: " + status);
                }
            }
        };

        // 使用 BluetoothDevice 对象连接到远程设备
        bluetoothGatt = bluetoothDevice.connectGatt(context, true, gattCallback);
    }

    public void PTS_HOGP(String msg) {
        BluetoothGattService hogpService = null;
        BluetoothGattCharacteristic reportCharacteristic = null;
        BluetoothGattDescriptor descriptor = null;
        if(bluetoothGatt.getServices().isEmpty()){
            Log.e("xiaohan", "bluetoothGatt.getServices() is empty , connecting may do not completed ");
            return;
        }
        Log.e("xiaohan", "bluetoothGatt:"+bluetoothGatt + ",bluetoothGatt.getServices() = "+bluetoothGatt.getServices());
        hogpService = bluetoothGatt.getService(HOGP_SERVICE_UUID);
        if (hogpService == null) {
            Toast.makeText(context, "cannot find HOGP service", Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "cannot find HOGP Service");
            return;
        }
        reportCharacteristic = hogpService.getCharacteristic(REPORT_CHARACTERISTICS_UUID);
        if (reportCharacteristic == null) {
            Toast.makeText(context, "cannot find reportCharacteristics", Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "cannot find reportCharacteristics");
            return;
        }
        descriptor = reportCharacteristic.getDescriptor(HOGP_REPORT_REFERENCE_UUID);
        if (descriptor == null) {
            Toast.makeText(context, "cannot find descriptor", Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "cannot find descriptor");
            return;
        }

        if (ActivityCompat.checkSelfPermission(this.context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "cannot get BLUETOOTH_CONNECT", Toast.LENGTH_SHORT).show();
            Log.e("xiaohan", "cannot get BLUETOOTH_CONNECT");
            //return;
        }
        /*
        // 设置读取请求的句柄
        int handle = 0x008A;
        //reportCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        byte[] handleBytes = new byte[2];
        handleBytes[0] = (byte) (handle & 0xFF);
        handleBytes[1] = (byte) ((handle >> 8) & 0xFF);
        reportCharacteristic.setValue(handleBytes);*/
        bluetoothGatt.readCharacteristic(reportCharacteristic);
        bluetoothGatt.readDescriptor(descriptor);
    }
    public void PTS_HID(String msg) {
        Log.e("xiaohan", "PTS_HID do not impl now!");
        return;
    }
}
