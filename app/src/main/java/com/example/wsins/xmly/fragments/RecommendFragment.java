package com.example.wsins.xmly.fragments;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wsins.xmly.R;
import com.example.wsins.xmly.base.BaseFragment;
import com.example.wsins.xmly.utils.Constants;
import com.example.wsins.xmly.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment {

    private static final String TAG = "RecommendFragment";

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        //view加载完成
        View rootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);

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
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    if (albumList != null) {
                        LogUtil.d(TAG, "size --> " + albumList.size());
                    }
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
}
