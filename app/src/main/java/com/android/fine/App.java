package com.android.fine;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.fine.library.common.manager.LibraryInitManager;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by tiansj on 2018/4/17.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (isMainProcess()) {
            boolean isDebug = BuildConfig.isDebug;
            String[] modules = getResources().getStringArray(R.array.modules);
            // 初始化Library，务必放到最前面
            LibraryInitManager.init(this, isDebug, modules);
        }
    }

    public boolean isMainProcess() {
        try {
            String packageName = getPackageName();
            String processName = getProcessName(android.os.Process.myPid());
            return packageName != null && processName != null && packageName.equals(processName);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }
    }

    public String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Logger.i("onTerminate");
    }
}