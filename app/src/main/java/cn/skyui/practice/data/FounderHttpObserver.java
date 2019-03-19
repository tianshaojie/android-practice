package cn.skyui.practice.data;

import android.app.Activity;
import android.content.Context;

import com.chenenyu.router.Router;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

import cn.skyui.library.http.exception.ApiException;
import cn.skyui.library.utils.ActivityUtils;
import cn.skyui.library.utils.StringUtils;
import cn.skyui.library.utils.ToastUtils;
import cn.skyui.library.utils.Utils;
import cn.skyui.library.widget.progress.ProgressDialog;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class FounderHttpObserver<T> implements Observer<FounderHttpResponse<T>> {

    public static final int TOKEN_INVALID_CODE = 501;

    private WeakReference<Activity> mActivity;
    private ProgressDialog dialog;

    protected FounderHttpObserver() {
    }

    protected FounderHttpObserver(Activity activity) {
        if(activity != null) {
            mActivity = new WeakReference<>(activity);
            dialog = new ProgressDialog(mActivity.get());
        }
    }

    protected FounderHttpObserver(Activity activity, String loadingMessage) {
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
    public void onNext(FounderHttpResponse<T> response) {
        if (response.isSuccess()) {
            onSuccess(response.getInfo());
        } else {
            onFailure(new ApiException(response.getCode(), response.getMessage()));
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
