package com.example.wsins.xmly.fragments;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wsins.xmly.R;
import com.example.wsins.xmly.adapters.RecommendListAdapter;
import com.example.wsins.xmly.base.BaseFragment;
import com.example.wsins.xmly.interfaces.IRecommendViewCallback;
import com.example.wsins.xmly.presenters.RecommendPersenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback {

    private static final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView recommendRv;
    private RecommendListAdapter recommendListAdapter;
    private RecommendPersenter recommendPersenter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
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

        //获取到逻辑层对象
        recommendPersenter = RecommendPersenter.getsInstance();
        //获取推荐列表
        recommendPersenter.getRecommendList();
        //先要注册通知接口的注册
        recommendPersenter.registerViewCallback(this);

        //返iew，给界面显示
        return rootView;
    }

    private void upRecommendUI(List<Album> albumList) {
        //把数据设置给适配器，并更新UI
        recommendListAdapter.setDate(albumList);
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当我们获取到推荐内容的时候，这个方法就会被调用（成功了）
        //数据回来以后，就是更新UI
        //把数据设置给适配器并更新UI
        recommendListAdapter.setDate(result);

    }

    @Override
    public void onLoaderMore(List<Album> result) {

    }

    @Override
    public void onRefreshMore(List<Album> result) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册
        if (recommendPersenter != null) {
            recommendPersenter.unRegisterViewCallback(this);
        }

    }
}
