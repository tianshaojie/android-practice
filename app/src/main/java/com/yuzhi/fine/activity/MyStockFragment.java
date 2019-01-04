package com.yuzhi.fine.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.yuzhi.fine.R;
import com.yuzhi.fine.library.base.fragment.BaseFragment;

/**
 * @author tianshaojie
 * @date 2018/1/27
 */
public class MyStockFragment extends BaseFragment implements ILazyLoadFragment {

    public static Fragment newInstance(String title) {
        MyStockFragment messageFragment = new MyStockFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        messageFragment.setArguments(bundle);
        return messageFragment;
    }

    boolean isCreate = false;
    String title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        isCreate = true;
    }


    @Override
    public void initData() {
        title = getArguments().getString("title");
    }

    @Override
    public void initView(View rootView) {
        TextView textView = rootView.findViewById(R.id.textView);
        textView.setText(title);
    }

    @Override
    public void onShow() {
        Logger.i("");
    }

    @Override
    public void onHide() {

    }
}

