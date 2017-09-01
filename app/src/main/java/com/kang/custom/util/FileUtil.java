package com.kang.custom.util;

import java.io.File;

/**
 * Created by kang on 2017/8/18.
 */

public class FileUtil {
    /**
     * 递归删除文件和文件夹
     * @param file 要删除的根目录
     */
    public static void RecursionDeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                RecursionDeleteFile(f);
                LogUtils.d("delete files");
            }
        }
        file.delete();
    }

    /**
     * find file
     * @param file
     * @param name
     * @return
     */
    public static String RecursionFindFile(File file, final String name ){
        if(file.isFile()){
            return null;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                return null;
            }
            for(File f : childFile){
                if((f.isDirectory()) && (f.getName().equals(name))){
                    LogUtils.i("RecursionFindFile dir path: ", f.getAbsolutePath());
                    return f.getAbsolutePath();
                }
            }
        }
        return null;
    }
}
