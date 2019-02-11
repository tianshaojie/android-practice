package cn.skyui.practice.activity;

import android.view.View;

/**
 * @author tianshaojie
 * @date 2019/1/4
 * description:
 */
public interface ILazyLoadFragment {

    void initData();

    void initView(View rootView);

    void show();

    void hide();

}
