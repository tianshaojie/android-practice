package com.android.fine;

import android.os.Bundle;

import com.android.fine.library.base.activity.BaseActivity;
import com.chenenyu.router.Router;

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

//            Intent intent = new Intent();
//            intent.putExtra("url", "http://tianshaojie.com/interest/h5.html");
//            intent.setClass(this, WebViewActivity.class);
//            startActivity(intent);

            String url = "http://tianshaojie.com/interest/h5.html";
            Router.build("webview").with("url", url).go(this);
        });
    }
}
