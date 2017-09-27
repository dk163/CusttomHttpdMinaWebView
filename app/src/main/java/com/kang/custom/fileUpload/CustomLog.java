package com.kang.custom.fileUpload;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class CustomLog extends BmobObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private String imei;
    private BmobFile file;

    public CustomLog(){

    }

    public CustomLog(String name, String imei, BmobFile file){
        this.name =name;
        this.imei = imei;
        this.file = file;
    }

}
