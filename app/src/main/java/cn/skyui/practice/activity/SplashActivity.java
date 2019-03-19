package cn.skyui.practice.activity;

import android.app.Activity;
import android.os.Bundle;

import com.chenenyu.router.Router;
import com.gyf.barlibrary.ImmersionBar;

import cn.skyui.library.base.activity.BaseActivity;
import cn.skyui.library.web.service.WebViewPreLoadService;

/**
 * @author tianshaojie
 * @date 2018/1/15
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BaseActivity.APP_STATUS = BaseActivity.APP_STATUS_NORMAL;
//        setTheme(R.style.SplashThemeHuawei);
        ImmersionBar.with(this).transparentBar().init();
        super.onCreate(savedInstanceState);
        WebViewPreLoadService.startHideService(this);
        getWindow().getDecorView().postDelayed(this::enter, 100);


    }

    private void enter() {
        Router.build("main").go(this);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().getDecorView().getHandler().removeCallbacksAndMessages(null);
        ImmersionBar.with(this).destroy();
        WebViewPreLoadService.stopHideService(this);
    }
}