package com.example.keylistenerx;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    Button off_on_bluetooth, PTS_HOGP_btn;
    boolean bt_status=true;
    private MyBroadcast broadcast_receiver = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter("com.example.keylistenerx.broadcast");
        broadcast_receiver = new MyBroadcast();
        registerReceiver(broadcast_receiver, filter);

        off_on_bluetooth = (Button) findViewById(R.id.off_on_bluetooth);
        PTS_HOGP_btn = (Button) findViewById(R.id.btnPTS_HOGP);
        //am broadcast -a com.example.keylistenerx.broadcast --es bt_off_on off
        off_on_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent broadcast=new Intent();
                broadcast.setAction("com.example.keylistenerx.broadcast");
                Log.e("xiaohan", "turn bt " + (bt_status?"off":"on"));
                if(bt_status==true) {broadcast.putExtra("bt_off_on", "off");bt_status=false;}
                else {broadcast.putExtra("bt_off_on", "on");bt_status=true;}
                sendBroadcast(broadcast);
            }
        });

        //adb shell "am broadcast -a com.example.keylistenerx.broadcast --es test_case \"PTS_HOGP_test111\""
        PTS_HOGP_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent broadcast=new Intent();
                broadcast.setAction("com.example.keylistenerx.broadcast");
                broadcast.putExtra("test_case", "PTS_HOGP_test111");
                Log.e("xiaohan", "PTS_HOGP_btn is pushed");
                sendOrderedBroadcast(broadcast, null);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        /*
        IntentFilter filter = new IntentFilter("com.example.keylistenerx.broadcast");
        broadcast_receiver = new MyBroadcast();
        registerReceiver(broadcast_receiver, filter);
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
        if(broadcast_receiver != null){
            unregisterReceiver(broadcast_receiver);
        }
        */
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcast_receiver != null){
            unregisterReceiver(broadcast_receiver);
        }
    }
}