package com.react.udpconnection;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 设备BaseInfo
 * Created by 82138 on 2016/6/21.
 */
public class SharedpTools {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static SharedpTools instance;
    private String service;

    public SharedpTools(Context context) {
        preferences = context.getSharedPreferences("device_info", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();
    }

    public static SharedpTools getInstance(Context context) {
        if (instance == null) {
            instance = new SharedpTools(context);
        }
        return instance;
    }

    /**
     * 设备uuid
     * @param uuid
     */
    public void setDeviceID(String uuid) {
        editor.putString("id", uuid);
        editor.commit();
    }

    public String getDeviceID() {
        return preferences.getString("id", "0");
    }

    /**
     * 设备服务
     * @param service
     */
    private void setDeviceService(String service) {
        editor.putString("service", service);
        editor.commit();
    }

    public String getDeviceService() {
        return preferences.getString("service", "");
    }

    public void addService(String service) {
        this.service = this.service + "," + service;
    }

    public void saveDeviceServices() {
        setDeviceService(this.service);
    }

    public void clearDeviceService() {
        this.service = "";
    }

    /**
     * 跟设备描述的URL
     * @param location
     */
    public void setLocation(String location) {
        editor.putString("location", location);
        editor.commit();
    }

    public String getLocation() {
        return preferences.getString("location", "");
    }

    /**
     * 设备基本信息
     * @param server
     */
    public void setServer(String server) {
        editor.putString("server", server);
        editor.commit();
    }

    public String getServer() {
        return preferences.getString("server", "");
    }



}
