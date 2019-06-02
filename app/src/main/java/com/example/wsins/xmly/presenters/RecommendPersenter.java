package com.example.wsins.xmly.presenters;

import android.support.annotation.Nullable;

import com.example.wsins.xmly.interfaces.IRecommendPersenter;
import com.example.wsins.xmly.interfaces.IRecommendViewCallback;
import com.example.wsins.xmly.utils.Constants;
import com.example.wsins.xmly.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPersenter implements IRecommendPersenter {

    private static final String TAG = "RecommendPersenter";

    private List<IRecommendViewCallback> callBacks = new ArrayList<>();

    private RecommendPersenter() {
    }

    private static RecommendPersenter sInstance = null;

    /**
     * 获取单例对象
     *
     * @return
     */
    public static RecommendPersenter getsInstance() {
        if (sInstance == null) {
            synchronized (RecommendPersenter.class) {
                if (sInstance == null) {
                    sInstance = new RecommendPersenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取推荐内容，其实就i是猜你喜欢
     * 这个接口，3.10.6 获取猜你喜欢专辑
     */
    @Override
    public void getRecommendList() {
        //获取推荐内容
        //封装参数
        updateLoading();
        Map<String, String> map = new HashMap<String, String>();
        //这个参数表示一页返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND + "");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG, "thread name --> " + Thread.currentThread().getName());
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    //获取数据回来我们要去更新UI
                    handlerRecommendResult(albumList);
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
            for (IRecommendViewCallback callBack : callBacks) {
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
                for (IRecommendViewCallback callBack : callBacks) {
                    callBack.onEmpty();
                }
            } else {
                for (IRecommendViewCallback callBack : callBacks) {
                    callBack.onRecommendListLoaded(albumList);
                }
            }
        }

    }

    private void updateLoading() {
        for (IRecommendViewCallback callBack : callBacks) {
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
    public void registerViewCallBack(IRecommendViewCallback iRecommendViewCallback) {
        if (callBacks != null && !callBacks.contains(iRecommendViewCallback)) {
            callBacks.add(iRecommendViewCallback);
        }
    }

    @Override
    public void unRegisterViewCallBack(IRecommendViewCallback iRecommendViewCallback) {
        if (callBacks != null) {
            callBacks.remove(iRecommendViewCallback);
        }
    }
}
