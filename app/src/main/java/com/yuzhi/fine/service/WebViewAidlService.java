package com.yuzhi.fine.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.yuzhi.fine.library.data.model.User;
import com.yuzhi.fine.library.utils.ToastUtils;
import com.yuzhi.fine.library.web.IWebAidlInterface;

/**
 * Created by tiansj on 2018/4/17.
 */

public class WebViewAidlService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (checkCallingOrSelfPermission("com.yuzhi.fine.aidl.permission.SERVICE_PERMISSION") == PackageManager.PERMISSION_DENIED) {
            return null;
        }
        return new WebViewAidlBinder();
    }

    private class WebViewAidlBinder extends IWebAidlInterface.Stub {
        @Override
        public String getToken() throws RemoteException {
            return User.getInstance().token;
        }

        @Override
        public boolean isLogin() throws RemoteException {
            return User.getInstance().isLogin;
        }

        @Override
        public long getUserId() throws RemoteException {
            return User.getInstance().userId;
        }

        @Override
        public void invokeShare() throws RemoteException {
            ToastUtils.showShort("share in main process");
        }
    }

}
