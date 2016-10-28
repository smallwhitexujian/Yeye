
package com.angelatech.yeyelive.activity;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.model.WebTransportModel;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.view.HeaderLayout;

/**
 * Created by jjfly on 15-10-29.
 * 统一调用WEB页面
 */


@SuppressLint("SetJavaScriptEnabled")
public class WebActivity extends HeaderBaseActivity {

    public static final String URL_KEY = WebActivity.class.getName() + "_URL_KEY";
    public static final String PARAM_KEY = WebActivity.class.getName() + "_PARAM_KEY";
    private WebView mWebView;
    private String type;
    private WebTransportModel mWebTransportModel;
    @SuppressLint("AddJavascriptInterface")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mWebTransportModel = StartActivityHelper.getTransactionSerializable_1(WebActivity.this);
        initView();
        setView();
        String mUrl = mWebTransportModel.url;

        String mParamStr = getIntent().getStringExtra(PARAM_KEY);
        if (mParamStr != null && mUrl != null) {
            mUrl = mUrl + "?" + mParamStr;
        }
        mWebView.loadUrl(mUrl+ "&time=" + System.currentTimeMillis());
        //增加接口方法,让html页面调用
        mWebView.addJavascriptInterface(new Object(){
            @JavascriptInterface
            public void clickOnAndroid(){
                finish();
            }
        },"demo");
    }

    private void initView() {
        headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
        headerLayout.showTitle(mWebTransportModel.title);
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        type = mWebTransportModel.title;
        if (type.equals(getString(R.string.gift_center))){
            headerLayout.setVisibility(View.GONE);
        }
        mWebView = (WebView) findViewById(R.id.web_webview);
    }

    private void setView() {
        /***设置webView***/
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        //设置支持js代码
        mWebView.getSettings().setJavaScriptEnabled(true);
        //设置支持插件
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        //设置允许访问文件数据
        mWebView.getSettings().setAllowFileAccess(true);
        //支持缩放
        mWebView.getSettings().setSupportZoom(true);
        //支持缓存
        mWebView.getSettings().setAppCacheEnabled(true);
        //设置缓存模式
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //设置多窗口
        // webSettings.setSupportMultipleWindows(true);
        //设置此属性，可任意比例缩放
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        /**WebViewClient主要帮助WebView处理各种通知、请求事件**/
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            //开始加载页面，可以设置提示语
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            //页面加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //过滤urL
                if (url.contains("tel:")) {// 页面上有数字会导致系统会自动连接电话,屏蔽此功能
                    return true;
                }
                mWebView.loadUrl(url);
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        /*** WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等比如 **/
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//				处理alert操作
//				final AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).setMessage(message).show();
//				alertDialog.setCanceledOnTouchOutside(false);
//				alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//					@Override
//					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//						if ((keyCode == KeyEvent.KEYCODE_BACK)){
//							alertDialog.cancel();
//							mWebView.goBack();//返回上一个历史网页
//							return true;
//						}
//						return false;
//					}
//				});
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void getVisitedHistory(ValueCallback<String[]> callback) {
                super.getVisitedHistory(callback);
            }

            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
            }


            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onRequestFocus(WebView view) {
                super.onRequestFocus(view);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            // 返回键退回
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
            else{
                finish();
                return true;
            }
        }
        return false;
    }
}