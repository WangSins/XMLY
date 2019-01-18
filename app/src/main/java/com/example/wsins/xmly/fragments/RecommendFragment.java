package com.example.wsins.xmly.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wsins.xmly.DetailActivity;
import com.example.wsins.xmly.R;
import com.example.wsins.xmly.adapters.RecommendListAdapter;
import com.example.wsins.xmly.base.BaseFragment;
import com.example.wsins.xmly.interfaces.IRecommendViewCallback;
import com.example.wsins.xmly.presenters.RecommendPersenter;
import com.example.wsins.xmly.utils.LogUtil;
import com.example.wsins.xmly.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, RecommendListAdapter.OnRecommendItemClickListener {

    private static final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView recommendRv;
    private RecommendListAdapter recommendListAdapter;
    private RecommendPersenter recommendPersenter;
    private UILoader uiLoader;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {

        uiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater, container);
            }
        };

        //获取到逻辑层对象
        recommendPersenter = RecommendPersenter.getsInstance();
        //先要注册通知接口的注册
        recommendPersenter.registerViewCallback(this);
        //获取推荐列表
        recommendPersenter.getRecommendList();

        if (uiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) uiLoader.getParent()).removeView(uiLoader);
        }

        uiLoader.setOnRetryClickListener(this);

        //返iew，给界面显示
        return uiLoader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        //view加载完成
        rootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);

        //Recyclerview的使用
        //1.找出控件
        recommendRv = rootView.findViewById(R.id.recommend_list);
        //2.设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recommendRv.setLayoutManager(linearLayoutManager);
        recommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        //3.设置适配器
        recommendListAdapter = new RecommendListAdapter();
        recommendRv.setAdapter(recommendListAdapter);
        recommendListAdapter.setOnRecommendItemClickListener(this);
        return rootView;
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        LogUtil.d(TAG, "onRecommendListLoaded");
        //当我们获取到推荐内容的时候，这个方法就会被调用（成功了）
        //数据回来以后，就是更新UI
        //把数据设置给适配器并更新UI
        recommendListAdapter.setDate(result);
        uiLoader.updateStatus(UILoader.UIStatus.SUCCESS);

    }

    @Override
    public void onNetworkError() {
        LogUtil.d(TAG, "onNetworkError");
        uiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);

    }

    @Override
    public void onEmpty() {
        LogUtil.d(TAG, "onEmpty");
        uiLoader.updateStatus(UILoader.UIStatus.EMPTY);

    }

    @Override
    public void onLoading() {
        LogUtil.d(TAG, "onLoading");
        uiLoader.updateStatus(UILoader.UIStatus.LOADING);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册
        if (recommendPersenter != null) {
            recommendPersenter.unRegisterViewCallback(this);
        }

    }

    @Override
    public void onRetryClick() {
        //表示网络不佳的时候用户点击了重试
        //重新获取数据即可
        if (recommendPersenter != null) {
            recommendPersenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int position) {
        //item被点击了，跳转到详情页面
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);

    }
}
