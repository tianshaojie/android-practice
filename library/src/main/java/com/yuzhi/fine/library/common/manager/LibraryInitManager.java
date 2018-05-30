package com.yuzhi.fine.library.common.manager;

import android.app.Application;

import com.chenenyu.router.Configuration;
import com.chenenyu.router.Router;
import com.facebook.stetho.Stetho;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.yuzhi.fine.library.data.model.User;
import com.yuzhi.fine.library.utils.ProcessUtils;
import com.yuzhi.fine.library.utils.Utils;

/**
 * Created by tiansj on 2018/4/4.
 */

public class LibraryInitManager {

    public static void init(Application application, boolean isDebug, String... modules) {
        initUtils(application, isDebug);
        initLogger();
        initUser();
        initRouter(isDebug, modules);
        initCrashReporter(application);
        initLeakCanary(application);
        initStetho(application);
    }


    private static void initUtils(Application application, boolean isDebug) {
        Utils.init(application, isDebug);
    }

    private static void initLogger() {
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    private static void initUser() {
        User.getInstance().init();
    }

    private static void initRouter(boolean debuggable, String... modules) {
        Router.initialize(new Configuration.Builder()
                // 调试模式，开启后会打印log
                .setDebuggable(debuggable)
                // 模块名，每个使用Router的module都要在这里注册，getResources().getStringArray(R.array.modules)
                .registerModules(modules)
                .build());
    }

    private static void initLeakCanary(Application application) {
        LeakCanary.install(application);
    }

    private static void initCrashReporter(Application application) {
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(application);
        strategy.setUploadProcess(ProcessUtils.isMainProcess());
        // 初始化Bugly
        // CrashReport.initCrashReport(this, "c57feab2c2", AppUtils.isAppDebug(), strategy);
        // 初始化Bugly & 应用升级
        Bugly.init(application, "c57feab2c2", false, strategy);
    }

    /**
     * Facebook调试工具，Chrome输入chrome://inspect
     */
    private static void initStetho(Application application) {
        Stetho.initializeWithDefaults(application);
    }
}
