package com.example.keylistenerx;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class OPP_Sender {
    private Context context;

    public OPP_Sender(Context con) {
        context = con;
    }

    public void send(String sppSendFilePath) {
        String filePath = Environment.getExternalStorageDirectory().toString() + sppSendFilePath;
        Log.e("xiaohan", "try to send :" + filePath);
        File file = new File(filePath);
        Log.e("xiaohan", "exist?" + file.exists() + ",canread?"+file.canRead() + ",canwrite?"+file.canWrite() + ",length=" +file.length());

        // 通过FileProvider获取合法的content:// Uri
        Uri contentUri = FileProvider.getUriForFile(context, "com.example.keylistenerx.fileprovider", file);

        // 授予URI临时的读取权限，以防其他应用程序需要访问该URI
        context.grantUriPermission("com.android.bluetooth", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // 创建发送文件的Intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setClassName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // 启动Intent
        context.startActivity(intent);
    }
}
