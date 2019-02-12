package cn.skyui.practice.activity;

import android.content.Intent;
import android.os.Bundle;

import com.chenenyu.router.Router;
import com.orhanobut.logger.Logger;
import cn.skyui.R;
import cn.skyui.practice.data.ApiService;
import cn.skyui.practice.data.FounderHttpObserver;
import cn.skyui.library.base.activity.BaseActivity;
import cn.skyui.library.http.RetrofitFactory;
import cn.skyui.library.utils.ToastUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BaseActivity.APP_STATUS = BaseActivity.APP_STATUS_NORMAL;
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
