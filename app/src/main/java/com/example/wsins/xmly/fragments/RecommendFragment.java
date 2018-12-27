package com.example.wsins.xmly.fragments;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wsins.xmly.R;
import com.example.wsins.xmly.adapters.RecommendListAdapter;
import com.example.wsins.xmly.base.BaseFragment;
import com.example.wsins.xmly.utils.Constants;
import com.example.wsins.xmly.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment {

    private static final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView recommendRv;
    private RecommendListAdapter recommendListAdapter;

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
        //去拿数据回来
        getRecommendData();


        //返回view，给界面显示
        return rootView;
    }

    /**
     * 获取推荐内容，其实就i是猜你喜欢
     * 这个接口，3.10.6 获取猜你喜欢专辑
     */
    private void getRecommendData() {
        //封装参数
        Map<String, String> map = new HashMap<String, String>();
        //这个参数表示一页返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG, "thread name --> " + Thread.currentThread().getName());
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
//                    if (albumList != null) {
//                        LogUtil.d(TAG, "size --> " + albumList.size());
//                    }
                    upRecommendUI(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //数据获取出错
                LogUtil.d(TAG, "error --> " + i);
                LogUtil.d(TAG, "errorMsg --> " + s);
            }
        });

    }

    private void upRecommendUI(List<Album> albumList) {
        //把数据设置给适配器，并更新UI
        recommendListAdapter.setDate(albumList);
    }
}
