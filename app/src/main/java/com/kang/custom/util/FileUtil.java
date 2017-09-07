package com.kang.custom.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by kang on 2017/8/18.
 */

public class FileUtil {
    /**
     * 递归删除文件和文件夹
     * @param file 要删除的根目录
     */
    public static boolean RecursionDeleteFile(File file){
        if(!file.exists()){
            LogUtils.e("file is not exist");
            return true;
        }
        if(file.isFile()){
            file.delete();
            return false;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return true;
            }
            for(File f : childFile){
                RecursionDeleteFile(f);
                LogUtils.d("delete files");
            }
        }
        file.delete();
        return true;
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

    /**
     * 压缩整个文件夹中的所有文件，生成指定名称的zip压缩包
     * @param filepath 文件所在目录
     * @param zippath 压缩后zip文件名称
     * @param dirFlag zip文件中第一层是否包含一级目录，true包含；false没有
     */
    public static boolean zipMultiFile(String filepath ,String zippath, boolean dirFlag) {
        try {
            File file = new File(filepath);// 要被压缩的文件夹
            if(!file.exists()){
                LogUtils.e("zip dir no exists");
                return false;
            }
            File zipFile = new File(zippath);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            if(file.isDirectory()){
                File[] files = file.listFiles();
                for(File fileSec:files){
                    if(dirFlag){
                        recursionZip(zipOut, fileSec, file.getName() + File.separator);
                    }else{
                        recursionZip(zipOut, fileSec, "");
                    }
                }
            }
            zipOut.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 压缩为zip包
     * @param zipOut
     * @param file
     * @param baseDir
     * @throws Exception
     */
    private static void recursionZip(ZipOutputStream zipOut, File file, String baseDir) throws Exception{
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File fileSec:files){
                recursionZip(zipOut, fileSec, baseDir + file.getName() + File.separator);
            }
        }else{
            byte[] buf = new byte[1024];
            InputStream input = new FileInputStream(file);
            zipOut.putNextEntry(new ZipEntry(baseDir + file.getName()));
            int len;
            while((len = input.read(buf)) != -1){
                zipOut.write(buf, 0, len);
            }
            input.close();
        }
    }
}
