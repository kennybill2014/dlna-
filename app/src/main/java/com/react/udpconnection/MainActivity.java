package com.react.udpconnection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button sendBtn;
    private EditText msg_et;
    private TextView info_tv;
    private Context context;
    private ServiceConnection connection;
    private CommunicateService.MyBinder myBinder;

    // 通知栏信息
    private NotificationManager nm;
    // BASE Notification ID
    private int Notification_ID_BASE = 110;
    private Notification baseNF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendBtn = (Button) findViewById(R.id.send);
        msg_et = (EditText) findViewById(R.id.msg_et);
        info_tv = (TextView) findViewById(R.id.info_tv);
        context = MainActivity.this;
        // 绑定监听事件
        EventBus.getDefault().register(this);

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myBinder = (CommunicateService.MyBinder) service;
                myBinder.startServer();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        // 开启服务器service
        Intent intent = new Intent(this, CommunicateService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        // 发送消息
        sendBtn.setOnClickListener(this);
    }

    // 订阅事件
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void EventBus(String message) {
        String[] deviceInfo = message.split(",");
        String Info = "IP:"+deviceInfo[0] +"\nPort:" + deviceInfo[1];
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent contentIntent = null;
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent).
                setContentTitle("Device Info").
                setTicker("device send message!").
                setSmallIcon(R.mipmap.ic_launcher).
                setAutoCancel(true).
                setContentInfo(Info);
        baseNF = builder.getNotification();
        nm.notify(Notification_ID_BASE, baseNF);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解除绑定
        EventBus.getDefault().unregister(this);
        unbindService(connection);
    }


}
