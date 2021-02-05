package cn.skyui.practice.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenenyu.router.annotation.Route;
import com.gyf.barlibrary.ImmersionBar;
import com.gyf.barlibrary.OSUtils;
import com.netease.LDNetDiagnoService.LDNetDiagnoListener;
import com.netease.LDNetDiagnoService.LDNetDiagnoService;

import java.lang.reflect.Method;

import cn.skyui.R;
import cn.skyui.library.base.activity.BaseActivity;
import cn.skyui.library.utils.BarUtils;
import cn.skyui.library.utils.ScreenUtils;
import cn.skyui.library.utils.StringUtils;
import cn.skyui.library.utils.ToastUtils;
import cn.skyui.library.web.activity.WebViewActivity;

import static com.gyf.barlibrary.ImmersionBar.getStatusBarHeight;

@Route("main")
public class MainActivity extends BaseActivity {

    private Button btn;
    private EditText editText;
    private TextView text;
    private ProgressBar progress;

    private String showInfo = "";
    private boolean isRunning = false;
    private LDNetDiagnoService _netDiagnoService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }



    private void initView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        findViewById(R.id.btn_browser).setOnClickListener(v -> {
            String url = "http://skyui.cn/interest/js.html";
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        });

        findViewById(R.id.btn_viewpager).setOnClickListener(v -> {
            startActivity(new Intent(this, LazyLoadFragmentActivity.class));
        });

//        ToastUtils.showShort("getNavBarHeight：" + getNavBarHeight(this));
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                getNavBarHeight(MainActivity.this);
//            }
//        });

        btn = findViewById(R.id.btn_net_checker);
        editText = findViewById(R.id.text_domain);
        text = findViewById(R.id.text_info);
        text.setMovementMethod(new ScrollingMovementMethod());
        progress = findViewById(R.id.progressBar);

        btn.setOnClickListener(view -> {
            String domainName = editText.getText().toString();
            if(StringUtils.isEmpty(domainName)) {
                ToastUtils.showShort("请求输入域名");
                return;
            }
            if (!isRunning) {
                showInfo = "";
                _netDiagnoService = new LDNetDiagnoService(getApplicationContext(),
                        "testDemo", "网络诊断应用", "1.0.0", "huipang@corp.netease.com",
                        "deviceID(option)", domainName, "carriname", "ISOCountyCode",
                        "MobilCountryCode", "MobileNetCode", new LDNetDiagnoListener() {
                    @Override
                    public void OnNetDiagnoFinished(String log) {
                        text.setText(log);
                        progress.setVisibility(View.GONE);
                        btn.setText("开始诊断");
                        btn.setEnabled(true);
                        isRunning = false;
                    }

                    @Override
                    public void OnNetDiagnoUpdated(String log) {
                        showInfo += log;
                        text.setText(showInfo);
                    }
                });
                // 设置是否使用JNIC 完成traceroute
                _netDiagnoService.setIfUseJNICTrace(true);
//        _netDiagnoService.setIfUseJNICConn(true);
                _netDiagnoService.execute();
                progress.setVisibility(View.VISIBLE);
                text.setText("Traceroute with max 30 hops...");
                btn.setText("停止诊断");
                btn.setEnabled(false);
            } else {
                progress.setVisibility(View.GONE);
                btn.setText("开始诊断");
                _netDiagnoService.cancel(true);
                _netDiagnoService = null;
                btn.setEnabled(true);
            }

            isRunning = !isRunning;
        });

        addLayoutListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static int getNavBarHeight(Activity activity) {
        if(activity != null) {
            View rootContentView = activity.findViewById(android.R.id.content);
            if(rootContentView != null) {
                ToastUtils.showShort(ScreenUtils.getScreenHeight() + "," + rootContentView.getMeasuredHeight());
                return ScreenUtils.getScreenHeight() - rootContentView.getMeasuredHeight();
            } else if(checkHasNavigationBar(activity)) {
                return ImmersionBar.getNavigationBarHeight(activity);
            }
        }
        return 0;
    }

    /**
     * 获取是否存在NavigationBar，是否有虚拟按钮
     */
    public static boolean checkHasNavigationBar(Context context) {
        // 小米系统判断虚拟键是否显示方法
        if (OSUtils.isMIUI()) {
            if(Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) != 0){
                //开启手势，不显示虚拟键
                return false;
            }
        }

        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }


    private static boolean isNavBarShow = false;

    /**
     * 通过监听判断是否有导航键
     * @param activity HsMainActivity
     */
    public static void addLayoutListener(Activity activity) {
        if(activity == null){
            return;
        }
        final View rootView = activity.findViewById(android.R.id.content);
        if(rootView == null) {
            return;
        }
        final int screenHeight = ScreenUtils.getScreenHeight();
        final int statusBarHeight = getStatusBarHeight(activity);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int offsetHeight = screenHeight - rootView.getMeasuredHeight();
                isNavBarShow = offsetHeight > 0;
                Log.i("Utils", "isNavBarShow = " + isNavBarShow + ",BarHeight=" + ImmersionBar.getNavigationBarHeight(activity) + ", offsetHeight=" + offsetHeight);
            }
        });
    }
    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        try {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            }
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }
}