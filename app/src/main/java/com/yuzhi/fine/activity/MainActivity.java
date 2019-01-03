package com.yuzhi.fine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TimeUtils;

import com.chenenyu.router.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.orhanobut.logger.Logger;
import com.yuzhi.fine.R;
import com.yuzhi.fine.data.ApiService;
import com.yuzhi.fine.data.FounderHttpObserver;
import com.yuzhi.fine.library.base.activity.BaseActivity;
import com.yuzhi.fine.library.http.RetrofitFactory;
import com.yuzhi.fine.library.utils.ToastUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        findViewById(R.id.btn_browser).setOnClickListener(v -> {
            String url = "http://tianshaojie.com/interest/js.html";
            Router.build("webview").with("url", url).go(this);
        });

        Observable observable = RetrofitFactory.createService(ApiService.class).search("600", 1);
        findViewById(R.id.btn_httpdns).setOnClickListener(v -> {
            observable.throttleFirst(1500, TimeUnit.MILLISECONDS)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new FounderHttpObserver<String>() {
                        @Override
                        protected void onSuccess(String response) {
                            ToastUtils.showShort(response);
                            Logger.i(response);
                        }
                    });
        });


        findViewById(R.id.btn_rxjava).setOnClickListener(v -> {

        });

        findViewById(R.id.btn_viewpager).setOnClickListener(v -> {
            startActivity(new Intent(this, LazyLoadFragmentActivity.class));
        });
    }


    private void log() {

    }
}
