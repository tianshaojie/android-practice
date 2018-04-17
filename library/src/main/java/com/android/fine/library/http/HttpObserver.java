package com.android.fine.library.http;

import android.app.Activity;
import android.content.Context;

import com.chenenyu.router.Router;
import com.orhanobut.logger.Logger;
import com.android.fine.library.http.exception.ApiException;
import com.android.fine.library.utils.ActivityUtils;
import com.android.fine.library.utils.StringUtils;
import com.android.fine.library.utils.ToastUtils;
import com.android.fine.library.utils.Utils;
import com.android.fine.library.widget.progress.ProgressDialog;

import java.lang.ref.WeakReference;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class HttpObserver<T> implements Observer<HttpResponse<T>> {

    public static final int TOKEN_INVALID_CODE = 501;

    private WeakReference<Activity> mActivity;
    private ProgressDialog dialog;

    protected HttpObserver() {
    }

    protected HttpObserver(Activity activity) {
        if(activity != null) {
            mActivity = new WeakReference<>(activity);
            dialog = new ProgressDialog(mActivity.get());
        }
    }

    protected HttpObserver(Activity activity, String loadingMessage) {
        if(activity != null) {
            mActivity = new WeakReference<>(activity);
            dialog = new ProgressDialog(mActivity.get());
            if(!StringUtils.isEmpty(loadingMessage)) {
                dialog.setLoadingMessage(loadingMessage);
            }
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        if(dialog != null) {
            dialog.show();
        }
    }

    @Override
    public void onNext(HttpResponse<T> response) {
        if (response.isSuccess()) {
            onSuccess(response.getBody());
        } else {
            onFailure(new ApiException(response.getCode(), response.getMsg()));
        }
    }

    @Override
    public void onError(Throwable e) {
        Logger.e("onError: " + e.getMessage());
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        onFailure(ApiException.handleException(e));
    }

    @Override
    public void onComplete() {
        Logger.d("onComplete");
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    protected abstract void onSuccess(T response);

    protected void onFailure(ApiException e) {
        Logger.e("onFailure：code={}, msg={}", e.getCode(), e.getMsg());
        if(e.getCode() == TOKEN_INVALID_CODE) {
            Context context = null;
            if(mActivity != null) {
                context = mActivity.get();
            }
            if(context == null) {
                context = ActivityUtils.getTopActivity();
                if(context == null) {
                    context = Utils.getApp();
                }
            }
            Router.build("login").go(context);
        }
        if(e.getMsg() != null && e.getMsg().length() > 0) {
            ToastUtils.showShort(e.getMsg());
        }
    }
}
