package com.example.keylistenerx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class MyBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //am broadcast -a com.example.keylistenerx.broadcast --es bt_off_on off
        String bt_off_on = intent.getStringExtra("bt_off_on");
        //am broadcast -a com.example.keylistenerx.broadcast --es scan_off_on off
        String scan_off_on = intent.getStringExtra("scan_off_on");
        //adb shell "am broadcast -a com.example.keylistenerx.broadcast --es test_case \"PTS_HOGP_test111\""
        String test_case = intent.getStringExtra("test_case");
        //adb shell "am broadcast -a com.example.keylistenerx.broadcast --es test_case \"SPP\" --es file_path /Download/aa.png"  #Please note that do not add /sdcard/ like /sdcard/download/aaa.png
        String spp_file_path = intent.getStringExtra("file_path");
        Log.e("xiaohan", "recieve broadcast message:" + test_case + ",bt_off_on:"+ bt_off_on+",file_path:"+spp_file_path);
        Toast.makeText(context,"test_case:" + test_case + ",bt_off_on?"+ bt_off_on,Toast.LENGTH_SHORT).show();

        if (bt_off_on!=null && bt_off_on.equals("on")) {
            BT_controller.getInstance(context).enableBluetooth();
        } else if (bt_off_on!=null && bt_off_on.equals("off")) {
            BT_controller.getInstance(context).disableBluetooth();
        }

        if (scan_off_on!=null && scan_off_on.equals("on")) {
            BT_controller.getInstance(context).startScan();
        } else if (scan_off_on!=null && scan_off_on.equals("off")) {
            BT_controller.getInstance(context).stopScan();
        }

        if(test_case == null || test_case.isEmpty()){
            Log.e("xiaohan", "test_case is empty , return");
            return;
        }
        if(test_case.startsWith("PTS_HOGP")){
            // 创建 MyBluetooth 对象并执行 traverseHOGPService()
            PTS_TOOL ptstool = new PTS_TOOL(context);
            ptstool.PTS_HOGP(test_case);
        }else if(test_case.startsWith("PTS_HID")){
            // 创建 MyBluetooth 对象并执行 traverseHOGPService()
            PTS_TOOL ptstool = new PTS_TOOL(context);
            ptstool.PTS_HID(test_case);
        }else if(test_case.startsWith("SPP")){
            if(spp_file_path == null || spp_file_path.isEmpty()){
                Log.e("xiaohan", "spp_send_file is empty,unknow send which file");
            }
            OPP_Sender opp_sender = new OPP_Sender(context);
            opp_sender.send(spp_file_path);
        }else{
            Log.e("xiaohan", "test_case is unknow , return");
        }
    }
}