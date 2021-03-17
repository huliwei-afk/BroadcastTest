package com.example.broadcasttest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private IntentFilter intentFilter;

    //本地广播,可以减少安全问题
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //本地广播 getInstance单例模式 防止内存浪费
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        //定义按钮作为发送广播的触发点
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送本地广播
                Intent intent = new Intent("com.example.broadcasttest.LOCAL_BROADCAST");
                localBroadcastManager.sendBroadcast(intent);

                /*
                Intent intent = new Intent("com.example.broadcasttest.MY_BROADCAST");
                发送有序广播
                sendOrderedBroadcast(intent, null);

                发送广播
                sendBroadcast(intent);
                 */

            }
        });

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.broadcasttest.LOCAL_BROADCAST");
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
        /*
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
         */

    }

    protected void onDestroy(){
        super.onDestroy();
        //销毁
        localBroadcastManager.unregisterReceiver(localReceiver);
        unregisterReceiver(networkChangeReceiver);
    }

    class LocalReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent){
            Toast.makeText(context,"received local broadcast", Toast.LENGTH_LONG).show();
        }
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            //优化，让人知道网络变化
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                Toast.makeText(context, "network is available!" + networkInfo, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "network is unavailable", Toast.LENGTH_LONG).show();
            }
        }
    }

}
