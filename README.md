# CustomLog

launch activity 
https://raw.githubusercontent.com/dk163/customHttpdMina/master/images-folder/mainActivity.png

1.httpd port 8080 Button startHttpd Button stopHttpd

2.MINA  
mina server port 12345

mainActivity Test   
Button startServer  
Button stopServer

mina client ip 192.168.43.1 port 12345  
Button startClient  
Button stopClient

CustomLog tools     
launch activity 
https://raw.githubusercontent.com/dk163/customHttpdMina/master/images-folder/CustomLog_mainActivity

activity 20170907   
https://raw.githubusercontent.com/dk163/customHttpdMina/master/images-folder/zip_simple

目的
ROM管理设备端自定义的log

应用程序安装之后的名字是CustomHttpdMina，基于mina和httpd开发的

说明  
APK既是服务端也是客户端   
作为服务端：  
apk会接收android.intent.action.BOOT_COMPLETED 广播自启动监听12345端口

作为客户端：
手机端直接安装apk使用    
1、点击startClient按钮连接服务端,发送启动httpd    
2、startMtkLog,stopMtkLog 分别是打开和关闭mtklog     
3、clearCustomLog是清除mtklog和自定义log    
4、startWebView是打开httpd服务器地址,浏览下载log文件   
