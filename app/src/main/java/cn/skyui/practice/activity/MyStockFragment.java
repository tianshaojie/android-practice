package cn.skyui.practice.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import cn.skyui.R;
import cn.skyui.library.base.fragment.BaseFragment;

/**
 * @author tianshaojie
 * @date 2018/1/27
 */
public class MyStockFragment extends BaseLazyLoadFragment {

    public static MyStockFragment newInstance(String title) {
        MyStockFragment messageFragment = new MyStockFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        messageFragment.setArguments(bundle);
        return messageFragment;
    }

    int i = 1;
    String title;
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView(view);
    }

    public void initData() {
        title = getArguments().getString("title");
    }

    public void initView(View rootView) {
        textView = rootView.findViewById(R.id.textView);
    }

    @Override
    void onShow() {
        Logger.i("show - %s", title);
        textView.postDelayed(() -> textView.setText(title + (i++)), 500);
    }

    @Override
    void onHide() {
        Logger.i("hide - %s", title);
        textView.postDelayed(() -> textView.setText("text"), 500);
    }
}

