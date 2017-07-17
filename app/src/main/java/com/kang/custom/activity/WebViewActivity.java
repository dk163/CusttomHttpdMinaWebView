package com.kang.custom.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.communication.server.constant.Constant;
import com.kang.customhttpdmina.R;

/**
 * Created by rd0551 on 2017/7/12.
 */

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = WebViewActivity.class.getSimpleName();
    private Button btn_back;
    private TextView txt_title;
    private Button btn_top;
    private Button btn_refresh;
    private WebView wView;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        bindViews();
    }


    private void bindViews() {
        btn_back = findViewById(R.id.btn_back);
        txt_title = findViewById(R.id.txt_title);
        btn_top = findViewById(R.id.btn_top);
        btn_refresh = findViewById(R.id.btn_refresh);
        wView = findViewById(R.id.wView);

        btn_back.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_top.setOnClickListener(this);

        WebSettings settings = wView.getSettings();
        settings.setUseWideViewPort(true);//设定支持viewport
        settings.setLoadWithOverviewMode(true);   //自适应屏幕
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);//设定支持缩放
        settings.setDisplayZoomControls(false);
        wView.setInitialScale(25);//为25%，最小缩放等级
        settings.setTextSize(WebSettings.TextSize.LARGER);


        wView.loadUrl(Constant.HTTPDIPPORT);//httpd ip
        wView.setWebChromeClient(new WebChromeClient() {
            //这里设置获取到的网站title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                txt_title.setText(title);
            }
        });


        wView.setWebViewClient(new WebViewClient() {
            //在webview里打开新链接
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //view.loadUrl(url);
                //return true;
                return false;
            }
        });
        wView.setDownloadListener(new MyWebViewDownLoadListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                //finish();          //关闭当前Activity
                wView.goBack();
                break;
            case R.id.btn_refresh:
                wView.reload();    //刷新当前页面
                break;
            case R.id.btn_top:
                wView.setScrollY(0);   //滚动到顶部
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (wView.canGoBack()) {
            wView.goBack();
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }

        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Log.i(TAG, "url=" + url);
            Log.i(TAG, "userAgent=" + userAgent);
            Log.i(TAG, "contentDisposition=" + contentDisposition);
            Log.i(TAG, "mimetype=" + mimetype);
            Log.i(TAG, "contentLength=" + contentLength);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }
}