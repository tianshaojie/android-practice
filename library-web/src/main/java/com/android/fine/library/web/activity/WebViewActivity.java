package com.android.fine.library.web.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.fine.library.web.IWebAidlInterface;
import com.android.fine.library.web.R;
import com.android.fine.library.web.jsmethod.JavaScriptMethod;
import com.android.fine.library.web.jsbridge.DefaultHandler;
import com.android.fine.library.web.widget.CustomWebChromeClient;
import com.android.fine.library.web.widget.CustomWebView;
import com.android.fine.library.web.widget.CustomWebViewClient;
import com.chenenyu.router.annotation.Route;

import java.lang.reflect.Method;

/**
 * 1. 独立进程：AIDL通信，获取App用户态，调用主App分享界面等
 * 2. 使用JsBridge作为js和java通信：https://github.com/lzyzsd/JsBridge
 * 3. 进度条
 * 4. Title自动更新
 *
 * @author tianshaojie
 * @date 2018/1/15
 */
@Route("webview")
public class WebViewActivity extends BaseWebViewActivity {

    private static final String TAG = "WebViewActivity";
    private static final String ACTION = "com.android.fine.library.web.IWebAidlInterface";

    public static final String URL = "url";
    public static final String TITLE = "title";

    private CustomWebView mWebView;
    private JavaScriptMethod javaScriptMethod;
    private String sourceUrl;   // 打开页面时的URL
    private String currentUrl;  // 点击链接后的URL

    private IWebAidlInterface aidlInterface;
    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            aidlInterface = IWebAidlInterface.Stub.asInterface(service);
            javaScriptMethod.setWebViewInterface(aidlInterface);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            aidlInterface = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initIntentData();
        if(sourceUrl == null || sourceUrl.length() == 0) {
            finish();
            return;
        }
        javaScriptMethod = new JavaScriptMethod(this);
        bindService();
        initView();
    }

    private void initIntentData() {
        Intent intent = getIntent();
        sourceUrl = intent.getStringExtra(URL);
        if(sourceUrl == null || sourceUrl.length() == 0) {
            Toast.makeText(this, "Url不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        currentUrl = sourceUrl;
        String title = intent.getStringExtra(TITLE);
        if(title != null) {
            setTitle(title);
        }
    }

    private void bindService() {
        Intent service = new Intent(ACTION);
        service.setPackage(getPackageName());
        bindService(service, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void initView() {
        // 代码添加WebView
        FrameLayout mWebContainer = (FrameLayout) findViewById(R.id.layoutWebViewParent);
        mWebView = new CustomWebView(this);
        mWebContainer.addView(mWebView);

        mWebView.setLongClickable(true);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setDrawingCacheEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        //WebView远程代码执行漏洞修复
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.removeJavascriptInterface("accessibility");
        mWebView.removeJavascriptInterface("accessibilityTraversal");

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        mWebView.setDefaultHandler(new DefaultHandler());
        mWebView.loadUrl(sourceUrl);
        mWebView.setWebViewClient(new CustomWebViewClient(this, mWebView));
        mWebView.setWebChromeClient(new CustomWebChromeClient(this));

        // 初始化JS调用方法
        for(String key : javaScriptMethod.handlers.keySet()) {
            mWebView.registerHandler(key, javaScriptMethod.handlers.get(key));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aidlInterface != null) {
            unbindService(mServiceConnection);
        }
        if(mWebView != null) {
            ((FrameLayout)mWebView.getParent()).removeView(mWebView);
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView.mProgressBar = null;
        }
        mWebView = null;
//        System.exit(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if(keyCode == KeyEvent.KEYCODE_BACK) {
                onBackClick();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onBackClick() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_webview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            onBackClick();
            return true;
        } else if (id == R.id.action_refresh) {
            mWebView.loadUrl(currentUrl);
        } /*else if(id == R.id.action_share) {
            try {
                aidlInterface.invokeShare();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }*/ else if(id == R.id.action_copy) {
            ClipboardManager clipboard = (ClipboardManager) getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newUri(getApplication().getContentResolver(), "uri", Uri.parse(currentUrl)));
        } else if(id == R.id.action_browser) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl)));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 使隐藏菜单项显示icon
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try{
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }
}
