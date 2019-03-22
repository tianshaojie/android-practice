package cn.skyui.practice.activity;

import cn.skyui.library.base.fragment.BaseFragment;

public abstract class BaseLazyLoadFragment extends BaseFragment {

    protected boolean isVisible = false;

    public void show() {
        isVisible = true;
        onShow();
    }

    public void hide() {
        isVisible = false;
        onHide();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isVisible) {
            onShow();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isVisible) {
            onHide();
        }
    }

    /**
     * 开始刷新数据
     */
    abstract void onShow();

    /**
     * 停止刷新数据
     */
    abstract void onHide();
}
