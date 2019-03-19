package cn.skyui.practice.activity;

import android.content.Intent;
import android.os.Bundle;

import com.chenenyu.router.annotation.Route;

import cn.skyui.R;
import cn.skyui.library.base.activity.BaseActivity;
import cn.skyui.library.web.activity.WebViewActivity;

@Route("main")
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
            String url = "http://skyui.cn/interest/js.html";
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        });

        findViewById(R.id.btn_viewpager).setOnClickListener(v -> {
            startActivity(new Intent(this, LazyLoadFragmentActivity.class));
        });
    }

}
