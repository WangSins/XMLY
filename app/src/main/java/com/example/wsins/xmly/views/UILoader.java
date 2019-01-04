package com.example.wsins.xmly.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.wsins.xmly.R;
import com.example.wsins.xmly.base.BaseApplication;

public abstract class UILoader extends FrameLayout {

    private View loadingView;
    private View successView;
    private View networkErrorView;
    private View emptyView;

    public enum UIStatus {
        LOADING, SUCCESS, NETWORK_ERROR, EMPTY, NONE
    }

    public UIStatus currentStatus = UIStatus.NONE;

    public UILoader(@NonNull Context context) {
        this(context, null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //
        init();
    }

    public void updateStatus(UIStatus status) {
        currentStatus = status;
        //更新UI一定要在主线程上
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });

    }

    /**
     * 初始化UI
     */
    private void init() {
        switchUIByCurrentStatus();

    }

    private void switchUIByCurrentStatus() {
        //加载中
        if (loadingView == null) {
            loadingView = getLoadingView();
            addView(loadingView);
        }
        //根据状态设置是否可见
        loadingView.setVisibility(currentStatus == UIStatus.LOADING ? VISIBLE : GONE);

        //成功
        if (successView == null) {
            successView = getSuccessView(this);
            addView(successView);
        }
        //根据状态设置是否可见
        successView.setVisibility(currentStatus == UIStatus.SUCCESS ? VISIBLE : GONE);

        //网络错误页面
        if (networkErrorView == null) {
            networkErrorView = getNetworkErrorView();
            addView(networkErrorView);
        }
        //根据状态设置是否可见
        networkErrorView.setVisibility(currentStatus == UIStatus.NETWORK_ERROR ? VISIBLE : GONE);

        //数据为空的界面
        if (emptyView == null) {
            emptyView = getEmptyView();
            addView(emptyView);
        }
        //根据状态设置是否可见
        emptyView.setVisibility(currentStatus == UIStatus.EMPTY ? VISIBLE : GONE);

    }

    private View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
    }

    private View getNetworkErrorView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_error_view, this, false);
    }

    protected abstract View getSuccessView(ViewGroup container);

    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view, this, false);
    }
}
