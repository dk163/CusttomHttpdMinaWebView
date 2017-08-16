package com.network.wifi;

import java.util.List;

import com.network.multicast.MulticastMonitor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class WifiConnectManager {
	private static final String TAG = "WifiConnectManager";
	private Context mContext;
	private static WifiConnectManager mManager;
	private static Handler mHander;
	private final static int CONNECT_START = 1;
	public  final static int CONNECT_TIMEOUT = 2;
	private final static int CONNECT_STARTSCAN = 3;
	private final int SWITCH_AP = 0;
	//private final int SWITCH_WIFI = 1;
	public String ssId = "";
	public String password = "";
	public int cipherSetType = -1;
	private boolean mTimeOut = false;
	private MulticastMonitor monitor;
	private boolean mInitNetwork = false;
	
	public WifiConnectManager(Context context){
		mContext = context;
		HandlerThread handlerThread = new HandlerThread("WifiConnectManager");
		handlerThread.start();
		Looper mLooper = handlerThread.getLooper();
		mHander = new ConnectWifiHandler(mLooper, context);
		monitor = MulticastMonitor.getInstance(mContext.getApplicationContext());
	}
	
    public static WifiConnectManager getInstance(Context context){
    	if(mManager == null){
    		mManager = new WifiConnectManager(context);
    	}
    	return mManager;
    }
    
    public void startWifiConnect(){
    	mHander.sendEmptyMessage(CONNECT_STARTSCAN);
    	mHander.sendEmptyMessageDelayed(CONNECT_START, 3*1000);
    	mTimeOut = false;
    	mInitNetwork = false;
    	mHander.sendEmptyMessageDelayed(CONNECT_TIMEOUT, (1*60*1000));//1min  open AP again
    }
    
    private class ConnectWifiHandler extends Handler{
    	private Context mContext;
    	private WifiManager mWifiManager;
        
		public ConnectWifiHandler(Looper mLooper, Context context) {
			super(mLooper);
			mContext = context;
			mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		}

		@Override
		public void handleMessage(Message msg) {
			
			Log.i(TAG, "handleMessage what: " + msg.what);
			switch(msg.what){
				case CONNECT_STARTSCAN:
					mWifiManager.startScan(); 
					break;
				case CONNECT_START:				
				    boolean ret = connect();
				    if(!ret && !mTimeOut){
						mHander.removeMessages(CONNECT_START);
						mHander.sendEmptyMessageDelayed(CONNECT_START, 3000); 
				    }else{
				    	Log.i(TAG, "connect wifi ssid: " + ssId + " success");
						if(monitor == null){
							Log.e(TAG, "monitor == null");
							return;
						}
						monitor.startMonitor();
						
				    	mHander.removeMessages(CONNECT_TIMEOUT);
				    	mHander.removeMessages(CONNECT_START);
				    }
				    break;
				    
				case CONNECT_TIMEOUT:
					Log.i(TAG, "handleMessage CONNECT_TIMEOUT ");
					mHander.removeMessages(CONNECT_START);
					mTimeOut = true;
					
					openWifi(SWITCH_AP);
					break;
					
				default:
					break;
			}
			
		}
		
		private  boolean isWifiConnected(){
			boolean isConnected = false;
			String ssid = "";
	        ConnectivityManager connectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	        if(wifiNetworkInfo.isConnected()){
	        	ssid = getWifiInfo().getSSID();
	        	if(("\"".concat(ssId).concat("\"")).equals(ssid)){
		        	isConnected = true;
	        	}
	        }
	        Log.d(TAG,"isWifiConnected isConnected = "+isConnected+" ,ssid: " + ssid);  
	        return isConnected ;
		}
		 
		private WifiInfo getWifiInfo(){
	        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();   
	        return wifiInfo;
		}
		 
		private WifiConfiguration isExsits(String ssId){    
	        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
	        if(configs != null){
		        Log.d(TAG, "isExsits configs size = "+configs.size());
		        for (WifiConfiguration config : configs){
		        	Log.d(TAG, "isExsits config.SSID = "+config.SSID);
		        	mWifiManager.removeNetwork(config.networkId);
		        	mWifiManager.saveConfiguration();
		        }
	        }
    
	        return null;     
		} 
		
		private boolean connect() {
			int id = 0;
	    	int cipherType = WifiConfiguration.KeyMgmt.WPA_PSK;
	        WifiConfiguration wifiConfig = null;
	        
	    	Log.d(TAG, "connect ssId= "+ssId+"; password= "+password);  
	    	if(ssId == null || password == null){
	    		Log.e(TAG,"ssId or password is null");  
	    		return false;
	    	}
	    	
	    	cipherType = getCipherType(ssId);
	    	if((cipherType == -1) || (cipherSetType != cipherType)){
	    		Log.e(TAG, "connect not find wifi ssid= "+ssId+" ,or cipherSetType != cipherType");
	    		return false;
	    	}
	    	
	    	if(!mInitNetwork){
		    	wifiConfig = isExsits(ssId);
		    	if(wifiConfig != null){
		    		mWifiManager.removeNetwork(wifiConfig.networkId);
		    		wifiConfig = null;
		    	}
	    		
	    		if(null == wifiConfig){
					wifiConfig = new WifiConfiguration();
	    			wifiConfig.SSID = "\"".concat(ssId).concat("\""); 
		    		wifiConfig.preSharedKey = "\"".concat(password).concat("\"");
		    		wifiConfig.hiddenSSID = true;
		    		wifiConfig.status = WifiConfiguration.Status.ENABLED;
		    		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		    		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		    		wifiConfig.allowedKeyManagement.set(cipherType);
		    		wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		    		wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		    		wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
	    			id = mWifiManager.addNetwork(wifiConfig);
	    		}
//	    		else{
//	    			wifiConfig.preSharedKey = "\"".concat(password).concat("\"");
//		    		wifiConfig.allowedKeyManagement.set(cipherType);
//	    			id = mWifiManager.updateNetwork(wifiConfig);
//	    			if(id == -1){
//	    				Log.e(TAG, "updateNetWork is not exists");
//	    				return false;
//	    			}
//	    		}
	    		
	    		mInitNetwork = true;
	    		Log.i(TAG, "wifiConfig  new config");
	    	}
	    	
	    	boolean ret = mWifiManager.enableNetwork(id, true);
	    	if(!ret){
	    		Log.e(TAG, "connected failed ssId= "+ssId+"; password= "+password+"; id= "+id);
	    		openWifi(SWITCH_AP);//AP
	    	}
	    	
	    	if(isWifiConnected()){
	    		Log.i(TAG, "wifi connect ");
	    		return true;
	    	}
	    		    	
	    	return false;
	    }
	    
	    private int getCipherType(String ssid) { 
	    	int type = -1;
	        List<ScanResult> list = mWifiManager.getScanResults();  
	   
	        for (ScanResult scResult : list) {  
	            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {  
	                String capabilities = scResult.capabilities;  
	                Log.d(TAG, "getCipherType capabilities=" + capabilities);  
	                if (!TextUtils.isEmpty(capabilities)) {  
	                    if((capabilities.contains("WPA") || capabilities.contains("wpa")) 
	                    		&& (capabilities.contains("PSK")||capabilities.contains("psk"))){
	                    	type =  WifiConfiguration.KeyMgmt.WPA_PSK;  
	                    }else if((capabilities.contains("WPA") || capabilities.contains("wpa")) 
	                    		&& (capabilities.contains("EAP")||capabilities.contains("eap"))){
	                    	type =  WifiConfiguration.KeyMgmt.WPA_EAP;  
	                    } else if(capabilities.contains("IEEE8021X") || capabilities.contains("ieee8021x")){
	                    	type =  WifiConfiguration.KeyMgmt.IEEE8021X; 
	                    }else{
	                    	type = 0;
	                    }
	                }  
	            }  
	        } 
	        
	        Log.d(TAG, "getCipherType type=" + type);  
	        return type;
	    } 
	}

	public String getSsId() {
		return ssId;
	}

	public void setSsId(String ssId) {
		this.ssId = ssId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getCipherSetType() {
		return cipherSetType;
	}

	public void setCipherSetType(int cipherSetType) {
		this.cipherSetType = cipherSetType;
	}

	public boolean openWifi(int id){
//		Log.i(TAG, "openWifi id: " + id);
//
//		WifiManager wifiManager =  (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//		WifiConfiguration wificonfig = wifiManager.getWifiApConfiguration();
//		if((id == 0) && (wifiManager.isWifiEnabled())){
//			monitor.stopMonitor();
//
//			wifiManager.setWifiEnabled(false);
//			wifiManager.setWifiApEnabled(wificonfig, true);
//			Log.d(TAG, "enable wifi ap, wificonfig.SSID:"+wificonfig.SSID);
//			return true;
//		}else if ((id == 1) && (!wifiManager.isWifiEnabled())) {
//			wifiManager.setWifiApEnabled(wificonfig, false);
//            Log.d(TAG, "disable wifi ap, wificonfig.SSID:"+wificonfig.SSID);
//			wifiManager.setWifiEnabled(true);
//			Log.i(TAG,  "open wifi connect wifi");
//			return true;
//        }

		return false;
	}
    
	public Handler getHandler(){
		return mHander;
	}
}
