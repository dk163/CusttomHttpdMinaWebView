package com.network.FileUpload;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.kang.custom.util.LogUtils;
import com.network.exception.ErrorCode;
import com.network.exception.NetworkException;
import com.network.exception.ServerInternalException;

import android.util.Log;
//import com.bird.IntelligentVehicle.BirdIntelligentVehicle;


public class HttpClient {

    private static final String TAG = "HttpClient";
    private static final int TIME_OUT = 15 * 1000;
//    private static BirdIntelligentVehicle mBirdIntelligentVehicle = new BirdIntelligentVehicle();

    private HttpClient(){

    }

    private static class SingletonHolder{
        static final HttpClient client = new HttpClient();
    }

    public static HttpClient getClient(){
        return SingletonHolder.client;
    }

    public String post(String url, final byte[] data) throws ServerInternalException, NetworkException {
        ByteArrayOutputStream outputStream = null;
        BufferedInputStream in = null;
        HttpURLConnection conn = null;
        DataOutputStream out = null;

        try {
            URL postUrl = new URL(url);

            Log.d(TAG, "post url=" + url);

            conn = (HttpURLConnection) postUrl.openConnection();
//            mBirdIntelligentVehicle.BirdSetIsinkLightOn(0);
            conn.setRequestMethod("POST");
//            conn.setRequestProperty("User-Agent", getUserAgentString());
            conn.setRequestProperty("Content-Type", "application/octet-stream");

            conn.setRequestProperty("Accept-Encoding", "");
            conn.setRequestProperty("Content-Length", data.length + "");
            conn.setUseCaches(false);
            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(TIME_OUT);
            conn.setChunkedStreamingMode(0);

            conn.setDoInput(true);
            conn.setDoOutput(true);

            out = new DataOutputStream(conn.getOutputStream());
            out.write(data);
            out.flush();

            int responseCode = conn.getResponseCode();
            Log.i(TAG, "response code=" + responseCode);
            if(responseCode!=200){
                throw new ServerInternalException(ErrorCode.getServerCode(responseCode),
                        "Server getResponseCode"+responseCode);
            }
            in = new BufferedInputStream(conn.getInputStream());
            outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = in.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();

            byte[] result = outputStream.toByteArray();
            String resultStr = new String(result);
            Log.i(TAG, "response resultStr="+resultStr);
            return resultStr;

        } catch (IOException e) {
            Log.e(TAG, "post e:", e);
//            mBirdIntelligentVehicle.BirdSetIsinkLightOff(0);
            throw new NetworkException("IOException",e);
        } finally {
//            mBirdIntelligentVehicle.BirdSetIsinkLightOff(0);
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ignored) {}

            try {
                if (in != null)
                    in.close();
            } catch (IOException ignored) {}

            try {
                if (out != null)
                    out.close();
            } catch (IOException ignored) {}

            if (conn != null)
                conn.disconnect();
        }
    }


    public String post(String url) throws NetworkException,ServerInternalException{
        ByteArrayOutputStream outputStream = null;
        BufferedInputStream in = null;
        HttpURLConnection conn = null;
        DataOutputStream out = null;

        try {
            URL postUrl = new URL(url);

            LogUtils.d(TAG, "url=" + url);

            conn = (HttpURLConnection) postUrl.openConnection();
//            mBirdIntelligentVehicle.BirdSetIsinkLightOn(0);
            conn.setRequestMethod("POST");
//            conn.setRequestProperty("User-Agent", getUserAgentString());
            conn.setRequestProperty("Content-Type", "application/octet-stream");

            conn.setRequestProperty("Accept-Encoding", "");
            conn.setRequestProperty("Content-Length", "0");
            conn.setUseCaches(false);
            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(TIME_OUT);
            conn.setChunkedStreamingMode(0);

            conn.setDoInput(true);

            int responseCode = conn.getResponseCode();
            LogUtils.d(TAG, "response code=" + responseCode);
            if(responseCode!=200){
                throw new ServerInternalException(ErrorCode.getServerCode(responseCode),
                        "Server getResponseCode="+responseCode);
            }
            in = new BufferedInputStream(conn.getInputStream());
            outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = in.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();

            byte[] result = outputStream.toByteArray();
            String resultStr = new String(result);
            LogUtils.i(TAG, "response resultStr="+resultStr);
            return resultStr;

        } catch (IOException e) {
        	LogUtils.e(TAG, "post e:", e);
//            mBirdIntelligentVehicle.BirdSetIsinkLightOff(0);
            throw new NetworkException("post url="+url,e);
        } finally {
//            mBirdIntelligentVehicle.BirdSetIsinkLightOff(0);
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ignored) {}

            try {
                if (in != null)
                    in.close();
            } catch (IOException ignored) {}

            if (conn != null)
                conn.disconnect();
        }
    }

}
