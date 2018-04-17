package com.android.fine.library.web.jsmethod;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.android.fine.library.web.IWebAidlInterface;
import com.android.fine.library.web.jsbridge.BridgeHandler;
import com.android.fine.library.web.jsbridge.CallBackFunction;

import java.util.HashMap;

/**
 * @author tianshaojie
 * @date 2018/1/15
 */
public class JavaScriptMethod {

    private static final String TAG = "WebViewActivity";

    public HashMap<String, BridgeHandler> handlers = new HashMap<>();

    private Context mContext;
    private IWebAidlInterface appInterface;

    public JavaScriptMethod(Context context) {
        this.mContext = context;
        handlers.put("isLogin", isLoginHandler);
        handlers.put("toast", toastHandler);
    }

    public void setWebViewInterface(IWebAidlInterface appInterface) {
        this.appInterface = appInterface;
    }

    private BridgeHandler isLoginHandler = new BridgeHandler() {
        @Override
        public void handler(String data, CallBackFunction function) {
            Log.i(TAG, "handler = isLogin, data from web = " + data);
            if(appInterface == null) {
                return;
            }
            try {
                function.onCallBack(appInterface.isLogin() + "");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private BridgeHandler toastHandler = new BridgeHandler() {
        @Override
        public void handler(String data, CallBackFunction function) {
            Log.i(TAG, "handler = toast, data from web = " + data);
            if(data != null) {
                Toast.makeText(mContext, data, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
