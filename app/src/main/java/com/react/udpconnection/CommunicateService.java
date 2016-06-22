package com.react.udpconnection;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;


/**
 * DLNA服务
 * Created by 82138 on 2016/6/21.
 */
public class CommunicateService extends Service{

    private MyBinder myBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    class MyBinder extends Binder {

        private UDPServer server;
        private ExecutorService exec;

        public void startServer() {
            // 开启服务器
            server = new UDPServer();
            exec = Executors.newCachedThreadPool();
            exec.execute(server);
        }
    }

    /**
     * Server端实体类
     */
    public class UDPServer implements Runnable {

        private static final int port = 1900;
        private static final String host = "225.228.0.1";
        private byte[] msg = new byte[1024];
        private InetAddress group;
        private MulticastSocket socket;
        private GeneralKnown generalKnown;
        private SharedpTools sharedpTools;
        private Date date;
        private ParameterConstructor parameterConstructor;
        private ParameterAnalysis parameterAnalysis;
        private String[] RequestBody = new String[3];
        private int DELYED = 100000;
        private static final int SSDP_ALL = 1;
        private static final int OTHER_SERVER = 2;
        private static final int UNLEGAL = 0;

        private DatagramPacket[] sendPacket;
        Handler handler = new Handler();
        Runnable runnbale = new Runnable() {
            @Override
            public void run() {
                generalKnown = new GeneralKnown();
                generalKnown.setData(RequestBody);
                generalKnown.execute();
                handler.postDelayed(this, DELYED);
            }
        };

        public UDPServer() {
            parameterConstructor = ParameterConstructor.getParameterConstructor();
            parameterAnalysis = ParameterAnalysis.getParameterAnalysis();
            sharedpTools = SharedpTools.getInstance(getApplicationContext());

            parameterConstructor.setNT("upnp:rootdevice");
            parameterConstructor.setHOST("239.255.255.250:1900");
            parameterConstructor.setNTS("ssdp:alive");
            parameterConstructor.setLOCATION("http://127.0.0.1:2351/1.xml");
            parameterConstructor.setCACHE_CONTROL("1800");
            parameterConstructor.setSERVER("Windows NT/5.0, UPnP/1.0");
            parameterConstructor.setUUID("f7001351-cf4f-4edd-b3df-4b04792d0e8a");
            parameterConstructor.joinInit();
            RequestBody[0] = parameterConstructor.getRequsetBody();
            parameterConstructor.clear();
            parameterConstructor.setNT("urn:schemas-upnp-org:service:AVTransport:1");
            parameterConstructor.setHOST("239.255.255.250:1900");
            parameterConstructor.setNTS("ssdp:alive");
            parameterConstructor.setLOCATION("http://127.0.0.1:2351/1.xml");
            parameterConstructor.setCACHE_CONTROL("1800");
            parameterConstructor.setSERVER("Windows NT/5.0, UPnP/1.0");
            parameterConstructor.setUUID("f7001351-cf4f-4edd-b3df-4b04792d0e8a");
            parameterConstructor.joinInit();
            RequestBody[1] = parameterConstructor.getRequsetBody();
            parameterConstructor.clear();
            parameterConstructor.setNT("urn:baofeng-tv:service:Controller:1");
            parameterConstructor.setHOST("239.255.255.250:1900");
            parameterConstructor.setNTS("ssdp:alive");
            parameterConstructor.setLOCATION("http://127.0.0.1:2351/1.xml");
            parameterConstructor.setCACHE_CONTROL("1800");
            parameterConstructor.setSERVER("Windows NT/5.0, UPnP/1.0");
            parameterConstructor.setUUID("f7001351-cf4f-4edd-b3df-4b04792d0e8a");
            parameterConstructor.joinInit();
            RequestBody[2] = parameterConstructor.getRequsetBody();
            parameterConstructor.clear();

            sharedpTools.addService("upnp:rootdevice");
            sharedpTools.addService("urn:schemas-upnp-org:service:AVTransport:1");
            sharedpTools.addService("urn:baofeng-tv:service:Controller:1");
            sharedpTools.saveDeviceServices();

            sharedpTools.setDeviceID("f7001351-cf4f-4edd-b3df-4b04792d0e8a");
            sharedpTools.setLocation("http://127.0.0.1:2351/1.xml");
            sharedpTools.setServer("Windows NT/5.0, UPnP/1.0");

            try {
                group = InetAddress.getByName(this.host);
                socket = new MulticastSocket(port);
                socket.joinGroup(group);
                handler.postDelayed(runnbale, DELYED);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * 循环监听端口信息
         */
        @Override
        public void run() {
            DatagramPacket dPacket = new DatagramPacket(msg, msg.length);
            while(true) {
                int Command = UNLEGAL;
                try {
                    socket.receive(dPacket);
                    String message = new String(dPacket.getData(),0,dPacket.getLength());
                    String aimAddress = dPacket.getAddress().toString();
                    aimAddress = aimAddress.substring(1);
                    int aimPort = dPacket.getPort();
                    Log.e("-----------------", aimAddress);
                    Log.e("-----------------", aimPort+"");
                    Log.e("-----------------", message);
                    EventBus.getDefault().post(aimAddress+","+aimPort);
                    // 解析请求
                    parameterAnalysis.setRequestBody(message);
                    parameterAnalysis.Parser();
                    String Service = parameterAnalysis.getST();
                    String[] deviceService = sharedpTools.getDeviceService().split(",");

                    if (parameterAnalysis.getST().equals("ssdp:all")) {
                        Command = SSDP_ALL;
                    } else {
                        for (int i = 0; i < deviceService.length; i ++) {
                            if (parameterAnalysis.getST().equals(deviceService[i])) {
                                Command = OTHER_SERVER;
                            }
                        }
                    }

                    switch (Command) {
                        case SSDP_ALL:
                            SendResponse(aimAddress, aimPort, "rootdevice");
                            SendResponse(aimAddress, aimPort, "uuid:"+sharedpTools.getDeviceID());
                            for (int i = 0; i < deviceService.length; i ++) {
                                SendResponse(aimAddress, aimPort, "urn:" + deviceService[i]);
                            }
                            Command = UNLEGAL;
                            break;
                        case OTHER_SERVER:
                            SendResponse(aimAddress, aimPort, Service);
                            Command = UNLEGAL;
                            break;
                        default:
                            Command = UNLEGAL;
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 发送单播响应
         * @param aimAddress
         * @param aimPort
         * @param Service
         */
        private void SendResponse(String aimAddress, int aimPort, String Service) {
            date = new Date();
            UDPClient send = new UDPClient();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss zzz", Locale.US);

            parameterConstructor.setHTTP("200 OK");
            parameterConstructor.setCACHE_CONTROL("1800");
            parameterConstructor.setDATE(dateFormat.format(date));
            parameterConstructor.setLOCATION(sharedpTools.getLocation());
            parameterConstructor.setST(Service);
            parameterConstructor.setSERVER(sharedpTools.getServer());
            parameterConstructor.setUUID(sharedpTools.getDeviceID());
            parameterConstructor.responseInit();
            send.setMsg(parameterConstructor.getRequsetBody());
            parameterConstructor.clear();
            send.setHost(aimAddress);
            send.setPort(aimPort);
            send.SendMsg();
        }

        /**
         * 循环发送server信息实体类
         */
        public class GeneralKnown extends AsyncTask<String, Void, String> {

            private DatagramPacket[] packet;
            private String[] data;
            public void setData(String[] data) {
                this.data = data;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                packet = new DatagramPacket[3];
                packet[0] = new DatagramPacket(data[0].getBytes(), data[0].length(), group, port);
                packet[1] = new DatagramPacket(data[1].getBytes(), data[1].length(), group, port);
                packet[2] = new DatagramPacket(data[2].getBytes(), data[2].length(), group, port);
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    for(int i = 0; i < 3; i++) {
                        socket.send(packet[i]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }
    }

    /**
     * 发送Response实体类
     */
    public class UDPClient {
        private String msg;
        private Task mTask;
        private int port;
        private String host;
        private DatagramSocket dataSocket = null;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public UDPClient() {
            super();
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void SendMsg() {
            mTask = new Task();
            mTask.execute();
        }


        private class Task extends AsyncTask<String, Void, String> {

            StringBuilder sb = null;
            InetAddress group = null;
            InetAddress local = null;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                group = null;
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    group = InetAddress.getByName(host);
                    local = InetAddress.getByName("0.0.0.0");
                    dataSocket = new DatagramSocket(0, local);
                    // dataSocket.connect(group, port);
                    int msg_len = msg == null ? 0 : msg.length();
                    DatagramPacket dPacket = new DatagramPacket(msg.getBytes(), msg_len, group, port);
                    dataSocket.send(dPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

            }
        }

    }
}
