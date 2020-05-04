package com.example.wsins.xmly.presenters;

import android.support.annotation.Nullable;

import com.example.wsins.xmly.data.XimalayaApi;
import com.example.wsins.xmly.interfaces.IRecommendPresenter;
import com.example.wsins.xmly.interfaces.IRecommendViewCallBack;
import com.example.wsins.xmly.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecommendPresenter implements IRecommendPresenter {

    private static final String TAG = "RecommendPresenter";

    private List<IRecommendViewCallBack> callBacks = new ArrayList<>();
    private List<Album> currentRecommend = null;
    private List<Album> recommendList;

    private RecommendPresenter() {
    }

    private static RecommendPresenter sInstance = null;

    /**
     * 获取单例对象
     *
     * @return
     */
    public static RecommendPresenter getsInstance() {
        if (sInstance == null) {
            synchronized (RecommendPresenter.class) {
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取当前的推荐专辑列表
     *
     * @return 推荐专辑，使用之前要判空
     */
    public List<Album> getCurrentRecommend() {
        return currentRecommend;
    }

    /**
     * 获取推荐内容，其实就i是猜你喜欢
     * 这个接口，3.10.6 获取猜你喜欢专辑
     */
    @Override
    public void getRecommendList() {
        //如果内容不为空，直接使用当前内容
        if (recommendList != null && recommendList.size() > 0) {
            LogUtil.d(TAG,"getRecommendList --> from list.");
            handlerRecommendResult(recommendList);
            return;
        }
        //获取推荐内容
        updateLoading();
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG, "thread name --> " + Thread.currentThread().getName());
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    recommendList = gussLikeAlbumList.getAlbumList();
                    //获取数据回来我们要去更新UI
                    LogUtil.d(TAG,"getRecommendList --> from network.");
                    handlerRecommendResult(recommendList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //数据获取出错
                LogUtil.d(TAG, "error --> " + i);
                LogUtil.d(TAG, "errorMsg --> " + s);
                handlerError();
            }
        });
    }

    private void handlerError() {
        if (callBacks != null) {
            for (IRecommendViewCallBack callBack : callBacks) {
                callBack.onNetworkError();
            }
        }
    }

    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI更新
        if (albumList != null) {
            //测试，清空一下界面，让界面显示空
            //albumList.clear();
            if (albumList.size() == 0) {
                for (IRecommendViewCallBack callBack : callBacks) {
                    callBack.onEmpty();
                }
            } else {
                for (IRecommendViewCallBack callBack : callBacks) {
                    callBack.onRecommendListLoaded(albumList);
                }
                this.currentRecommend = albumList;
            }
        }

    }

    private void updateLoading() {
        for (IRecommendViewCallBack callBack : callBacks) {
            callBack.onLoading();
        }
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }


    @Override
    public void registerViewCallBack(IRecommendViewCallBack iRecommendViewCallBack) {
        if (callBacks != null && !callBacks.contains(iRecommendViewCallBack)) {
            callBacks.add(iRecommendViewCallBack);
        }
    }

    @Override
    public void unRegisterViewCallBack(IRecommendViewCallBack iRecommendViewCallBack) {
        if (callBacks != null) {
            callBacks.remove(iRecommendViewCallBack);
        }
    }
}
