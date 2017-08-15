package com.network.multicast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 组播协议：
 * 		1.app端在点击搜索设备时在局域网发送组播[224.0.0.1:7451]，udp协议格式[json]，如：
 * 	 		{"clientid":"","devname":"k28"}
 * 		2.rom端接收到udp数据[*:7451]，解析协议，若devname=配置名字，则单向响应格式[json]， 如：
 *	 		{"deviceid":"","svclist":[{"name":"rtsp", "value":""}, {"name":"rtmp", "value":""}, {"name":"deviceIp", "value":""}]}
 * 		3.app收到所有响应报文，根据内容进一步交互
 *
 * 简要流程：
 *	 	1.app端在局域网广播搜索k28指令
 *		2.rom端收到指令，同时响应自身提供的服务列表
 */

public class MulticastMessage {

	private static final String TAG = "MulticastMessage";
	private final String DEVICENAME = "lepai";
	private JSONArray mServiceList = new JSONArray();
	private Context mContext;
	
	public MulticastMessage(Context context){
		mContext = context;
	}
	
	/**
	* 得到wifi连接的IP地址
	* @param
	* @return
	*/
	private  String getWifiIP(){
		WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddr = wifiInfo.getIpAddress();
		String ipStr = int2string(ipAddr);
		return ipStr;
	}
	
	/**
	* 输入int 得到String类型的ip地址
	* @param i
	* @return
	*/
	private  String int2string(int i){
		return (i & 0xFF)+ "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) +"."+((i >> 24 ) & 0xFF );
	}
	
	public boolean parseMultiSocketMsg(String msg){
		try {
			 JSONObject json = new JSONObject(msg);
	         String clientId = json.optString("clientid");
	         String devname = json.optString("devname");
	         Log.d(TAG, "parseMultiSocketMsg clientId = "+clientId+"; devname= "+devname);
	         if((devname == null) || (!devname.equalsIgnoreCase(DEVICENAME))){
	        	 Log.e(TAG, "device name is  Invalid");
	        	 return false;
	         }
		} catch (JSONException e) {
			Log.e(TAG, "json exception: " + e.toString());
			return false;
		}
		return true;
	}
	
/*	public String packageMultiSocketMsg(){
		JSONObject reponse = null;
		try {
			JSONArray svcList = new JSONArray();
			JSONObject rtspParam = new JSONObject();
			rtspParam.put("name", "rtsp");
			rtspParam.put("value", "http://rtsp");
			JSONObject rtmpParam = new JSONObject();
			rtmpParam.put("name", "rtmp");
			rtmpParam.put("value", "http://rtmp");
			
			JSONObject deviceParam = new JSONObject();
			String deviceWifiIp = getWifiIP();
			Log.d(TAG, "parseMultiSocketMsg  deviceWifiIp= "+deviceWifiIp);
			deviceParam.put("name", "deviceIp");
			deviceParam.put("value", deviceWifiIp);
			
			svcList.put(rtspParam);
			svcList.put(rtmpParam);
			svcList.put(deviceParam);
			
			reponse = new JSONObject();
			reponse.put("deviceid", "k28");
			reponse.put("svclist", svcList);
		} catch (JSONException e) {
			Log.d(TAG, "parseMultiSocketMsg  JSONException:", e);
		} 
		return reponse.toString();
	}	*/

	/*
	 * server send to client MultiSocketMsg
	 */
	public String packageMultiSocketMsg(){
		String deviceWifiIp = getWifiIP();
		Log.d(TAG, "parseMultiSocketMsg  deviceWifiIp= "+deviceWifiIp);
		
        TelephonyManager mTm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        Log.d(TAG, "parseMultiSocketMsg  imei= " + imei);
		
		JSONObject deviceParam = new JSONObject();
		try {
			deviceParam.put("name", "imei");
			deviceParam.put("value", imei);
			deviceParam.put("name", "deviceIp");
			deviceParam.put("value", deviceWifiIp);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return deviceParam.toString();
	}
	
/*	public String packageMultiSocketMsg(){
		String deviceWifiIp = getWifiIP();
		Log.d(TAG, "parseMultiSocketMsg  deviceWifiIp= "+deviceWifiIp);
				
		return deviceWifiIp;
	}	*/

}
