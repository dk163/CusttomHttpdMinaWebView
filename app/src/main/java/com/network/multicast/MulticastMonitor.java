package com.network.multicast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import com.network.utils.NetWorkConfig;
import com.network.wifi.WifiConnectManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MulticastMonitor{
	private final static String TAG = "MultiSocketMonitor";
	
	private final static int MONITOR_START = 1;
	private final static int MONITOR_STOP = 2;
	private final static int MONITOR_DEAL_MSG = 3;
	private final static int MONITOR_SEND_MSG = 4;
	private final static int MONITOR_TIMEOUT = 5;
	private final static int NEW_RECEIVERTHREAD = 6;
	
	private final int SWITCH_AP = 0;
	
	private MonitorHandler mMonitorHandler;
	private static MulticastMonitor mMultiSocketMonitor;
	private Context mContext;
	private MulticastMessage mMulticastMessage;
	public static MulticastMonitor getInstance(Context context){
		
		if(mMultiSocketMonitor == null){
			mMultiSocketMonitor = new MulticastMonitor(context);
		}

		return mMultiSocketMonitor;
	}
	
	public MulticastMonitor(Context context){
		mContext = context;
		mMulticastMessage = new MulticastMessage(context);
		HandlerThread handlerThread = new HandlerThread("MultiSocketMonitor");
        handlerThread.start();  
        mMonitorHandler = new MonitorHandler(handlerThread.getLooper());
        Log.d(TAG, "MulticastMonitor enter");
	}
	
	public void startMonitor(){
		Log.d(TAG, "startMonitor enter ");
//		mMonitorHandler.sendEmptyMessage(MONITOR_STOP);
        mMonitorHandler.obtainMessage(MONITOR_START).sendToTarget();
		mMonitorHandler.sendEmptyMessageDelayed(MONITOR_TIMEOUT, 2*60*1000);//timeout enable ap,kang
	}
	
	public void stopMonitor(){
		Log.d(TAG, "stopMonitor enter ");
		mMonitorHandler.obtainMessage(MONITOR_STOP).sendToTarget();
	}
		
	/**
	*判断wifi是否连接
	* @param context
	* @return
	*/
	private boolean isWiFiConnected(){
		ConnectivityManager connectManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(networkInfo.isConnected()){
			return true;
		}else{
			Log.d(TAG, "isWiFiConnected false ");
			return false;
		}
	}
	
	private class MonitorHandler extends Handler{
		private DatagramPacket mDp;   
		private MulticastSocket mSocket;
		private InetAddress mInetAddress ;
		private volatile  boolean mMutilcastMoniting = false;
		private Thread mReceiverThread = null;
		
        public MonitorHandler(Looper looper){
        	super(looper);
        }
        
		@Override
		public void handleMessage(Message msg) {
			int cmd = msg.what;
			Log.d(TAG, "handleMessage cmd = "+cmd);
			
			switch (cmd) {
			case MONITOR_START:
				if(mMutilcastMoniting){
					Log.d(TAG, "handleMessage monitor has started");
					return;
				}
				mMutilcastMoniting = true;
				
				try {
					if(!isWiFiConnected()){
						Log.d(TAG, "handleMessage wifi not connected");
						mMonitorHandler.sendEmptyMessageDelayed(MONITOR_START, 10000);	
						mMutilcastMoniting = false;
						return;
					}
					Log.d(TAG, "handleMessage start monitor!");
					mInetAddress = InetAddress.getByName(NetWorkConfig.MULTICAST_IP);
					if(null == mSocket){
						Log.d(TAG, "handleMessage monitor new socket");
						mSocket = new MulticastSocket(
								NetWorkConfig.MULTICAST_PORT);
						mSocket.setTimeToLive(1); // 指定数据报发送到本地局域网
						mSocket.joinGroup(mInetAddress);
						mSocket.setLoopbackMode(true); // 设置本MulticastSocket发送的数据报不被回送到自身
						Log.d(TAG, "handleMessage monitor end");
					}
				} catch (Exception e) {
					mMutilcastMoniting = false;
					Log.e(TAG, "handleMessage Exception : ", e);
					return;
				}
				
				mMonitorHandler.sendEmptyMessage(NEW_RECEIVERTHREAD);
				Log.d(TAG, "handleMessage monitor NEW_RECEIVERTHREAD end");
//				if((null != mReceiverThread)){
//					Log.i(TAG, "mReceiverThread.start()");
//					try {
//						mReceiverThread.start();
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
				break;
				
			case MONITOR_STOP:
				Log.d(TAG, "handleMessage MONITOR_STOP");
				try {
					if(!mMutilcastMoniting){
						Log.e(TAG, "handleMessage MONITOR_STOP monitor has stoped ");
						break;
					}
					
					//stop monitor
					if((mReceiverThread != null) && (!mReceiverThread.isInterrupted())){
						mReceiverThread.interrupt();
						mReceiverThread = null;
					}
					
					if (mSocket != null){
						mInetAddress = InetAddress.getByName(NetWorkConfig.MULTICAST_IP);
						mSocket.leaveGroup(mInetAddress);  
						mSocket.close();	
						mSocket = null;
					}
					 
				} catch (Exception e) {
					Log.e(TAG, "handleMessage MONITOR_STOP Exception : ", e);
				}
				mMutilcastMoniting = false;
				break;
				
			case MONITOR_DEAL_MSG:
				Log.d(TAG, "handleMessage MONITOR_DEAL_MSG");
				String receiver = (String)msg.obj;
				boolean bResult = mMulticastMessage.parseMultiSocketMsg(receiver);
				if(!bResult){
					break;
				}
				mMonitorHandler.removeMessages(MONITOR_TIMEOUT);//timeout enable ap,kang
				mMonitorHandler.obtainMessage(MONITOR_SEND_MSG).sendToTarget();
				break;
				
			case MONITOR_SEND_MSG:
				Log.d(TAG, "handleMessage MONITOR_SEND_MSG");
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							String sendMsg = mMulticastMessage.packageMultiSocketMsg();
							Log.d(TAG, "handleMessage sendMsg = "+sendMsg);
							byte[] sendBuf = sendMsg.getBytes();  
							DatagramPacket sendDP = new DatagramPacket(sendBuf, sendBuf.length, mInetAddress, NetWorkConfig.MULTICAST_PORT);  
							mSocket.send(sendDP);
							
//							mMonitorHandler.sendEmptyMessageDelayed(MONITOR_STOP, 2*1000);
						} catch (Exception e) {
							Log.d(TAG, "handleMessage MONITOR_SEND_MSG Exception e", e);
//			                mMonitorHandler.sendEmptyMessageDelayed(MONITOR_SEND_MSG, 3*1000);
						}
					}
				}).start();
				break;
			
			case MONITOR_TIMEOUT:
				Log.d(TAG, "handleMessage MONITOR_TIMEOUT");
				mMutilcastMoniting = false;
				
				mMonitorHandler.removeMessages(MONITOR_START);
				//mMonitorHandler.obtainMessage(MONITOR_STOP).sendToTarget();
				
				WifiConnectManager.getInstance(mContext).openWifi(SWITCH_AP);
				break;
				
			case NEW_RECEIVERTHREAD:
				Log.d(TAG, "handleMessage NEW_RECEIVERTHREAD");
				getReceiverThread();
				Log.d(TAG, "handleMessage NEW_RECEIVERTHREAD end");
				break;
				
			default:
				break;
			}
		}
		
		private void getReceiverThread(){
			mReceiverThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Log.d(TAG, "handleMessage MONITOR_START receive ");
						byte buf[] = new byte[1024];
						mDp = new DatagramPacket(buf, buf.length);
						while (!mReceiverThread.isInterrupted()) {
							try {
								mSocket.receive(mDp);
								String receiver = new String(mDp.getData()).trim();
								Log.d(TAG,
										"handleMessage MONITOR_START receive receiver = "
												+ receiver);
								mMonitorHandler.obtainMessage(MONITOR_DEAL_MSG,
										receiver).sendToTarget();
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								Log.e(TAG, "handleMessage InterruptedException e ",
										e);
								break;
							}
						}
					} catch (Exception e) {
						Log.e(TAG, "handleMessage MONITOR_START Exception = ", e);
					}
				}
			});
			mReceiverThread.start();
		}
	}	
}
