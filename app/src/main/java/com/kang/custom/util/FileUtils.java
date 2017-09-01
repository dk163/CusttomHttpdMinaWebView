package com.kang.custom.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by minstrel on 7/20/16.
 */
public class FileUtils {
    private static final String TAG = "FileUtils";
    public static final int FILE_PART_LEN = 256 * 1024;
    private static final int BUFFER_SIZE = 8192;

    public static boolean checkMD5(String md5, File updateFile) {
        if (TextUtils.isEmpty(md5) || updateFile == null) {
            Log.e(TAG, "MD5 string empty or updateFile null");
            return false;
        }

        String calculatedDigest = calculateMD5(updateFile);
        if (calculatedDigest == null) {
            Log.e(TAG, "calculatedDigest null");
            return false;
        }

        Log.v(TAG, "Calculated digest: " + calculatedDigest);
        Log.v(TAG, "Provided digest: " + md5);

        return calculatedDigest.equalsIgnoreCase(md5);
    }
    public static String calculateMD5(InputStream in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = in.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            return DataUtil.bytesToHexString(md5sum);
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }

    public static String checksum(File file,boolean withHexPrefix,boolean upperCase) {
        try {
            InputStream fin = new FileInputStream(file);
            MessageDigest md5er =
                    MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int read;
            do {
                read = fin.read(buffer);
                if (read > 0)
                    md5er.update(buffer, 0, read);
            } while (read != -1);
            fin.close();
            byte[] digest = md5er.digest();
            if (digest == null)
                return null;

            String strDigest = "";
            if(withHexPrefix){
                strDigest = "0x";
            }
            for (int i = 0; i < digest.length; i++) {
                if(upperCase){
                    strDigest += Integer.toString((digest[i] & 0xff)
                            + 0x100, 16).substring(1).toUpperCase();
                }else{
                    strDigest += Integer.toString((digest[i] & 0xff)
                            + 0x100, 16).substring(1).toLowerCase();
                }
            }

            return strDigest;
        } catch (Exception e) {
            return null;
        }
    }

    public static String calculateMD5(File file) {
//        return checksum(file,false,false);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < md5sum.length; i++) {
                builder.append(Integer.toString((md5sum[i] & 0xff)
                            + 0x100, 16).substring(1).toLowerCase());
            }

            return builder.toString();

//            return DataUtil.bytesToHexString(md5sum);
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }

    public static String getFileExtName(String name) {

        if(!name.contains(".")){
            return "";
        }
        String[] names = name.split("\\.");
        return names[names.length-1];
    }

    public static String getFileExtName(File file) {
        String name = file.getName();
        return getFileExtName(name);
    }

    public static int computeFilePartCount(File file) {

        long size = file.length();
        int n = (int) (size / FILE_PART_LEN);
        if(size % FILE_PART_LEN > 0){
            n += 1;
        }
        return n;
    }

    public static String calculateMD5(File file, int offset, int len) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(file);
            is.skip(offset);
        } catch (IOException e) {
            Log.e(TAG, "calculateMD5 IOException", e);
            return null;
        }
        int size = len > BUFFER_SIZE ? len: BUFFER_SIZE;
        int remain = len;

        byte[] buffer = new byte[size];
        int readLen;
        try {
            while ((readLen = is.read(buffer)) > 0) {
                digest.update(buffer, 0, readLen);
                remain -= readLen;

                if(remain<=0){
                    Log.d(TAG, "break when remain = "+remain);
                    break;
                }

                if(remain > BUFFER_SIZE){
                    buffer = new byte[remain];
                    Log.d(TAG, "read last buffer len="+remain);
                }

            }
            byte[] md5sum = digest.digest();
//            return DataUtil.bytesToHexString(md5sum);
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < md5sum.length; i++) {
                builder.append(Integer.toString((md5sum[i] & 0xff)
                        + 0x100, 16).substring(1).toLowerCase());
            }

            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }
}
