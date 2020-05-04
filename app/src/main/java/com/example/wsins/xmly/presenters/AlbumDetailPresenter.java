package com.example.wsins.xmly.presenters;

import android.support.annotation.Nullable;

import com.example.wsins.xmly.data.XimalayaApi;
import com.example.wsins.xmly.interfaces.IAlbumDetailPresenter;
import com.example.wsins.xmly.interfaces.IAlbumDetailViewCallBack;
import com.example.wsins.xmly.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sin on 2019/1/25
 */
public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private static final String TAG = "AlbumDetailPresenter";
    private List<IAlbumDetailViewCallBack> callBacks = new ArrayList<>();
    private List<Track> mTracks = new ArrayList<>();

    private Album mTargetAlbum = null;
    //当前专辑ID
    private int currentAlbumId = -1;
    //当前页
    private int currentPageIndex = 0;

    private AlbumDetailPresenter() {
    }

    private static AlbumDetailPresenter sInstance = null;

    public static AlbumDetailPresenter getInstance() {
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class) {
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {
        //去加载更多内容
        currentPageIndex++;
        //传入true，表示结果会追加到列表的后方
        doLoaded(true);

    }

    private void doLoaded(final boolean isLoaderMore) {
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG, "tracks size --> " + tracks.size());
                    if (isLoaderMore) {
                        //上拉加载，结果放到前面去
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        handlerLoaderMoreResult(size);
                    } else {
                        //下拉加载更多，结果放到前面去
                        mTracks.addAll(0, tracks);
                    }
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode ->" + errorCode + ", errorMsg ->" + errorMsg);
                if (isLoaderMore) {
                    currentPageIndex--;
                }
                handleError(errorCode, errorMsg);
            }
        }, currentAlbumId, currentPageIndex);
    }

    /**
     * 处理加载更多的结果
     *
     * @param size
     */
    private void handlerLoaderMoreResult(int size) {
        for (IAlbumDetailViewCallBack callBack : callBacks) {
            callBack.onLoaderMoreFinished(size);
        }

    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        mTracks.clear();
        this.currentAlbumId = albumId;
        this.currentPageIndex = page;
        //根据页码和专辑id获取列表
        doLoaded(false);

    }

    /**
     * 如果发生错误，通知UI
     *
     * @param errorCode
     * @param errorMsg
     */
    private void handleError(int errorCode, String errorMsg) {
        for (IAlbumDetailViewCallBack callBack : callBacks) {
            callBack.onNetWorkError(errorCode, errorMsg);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallBack callBack : callBacks) {
            callBack.onDetailListLoaded(tracks);
        }
    }

    @Override
    public void registerViewCallBack(IAlbumDetailViewCallBack detailViewCallBack) {
        if (!callBacks.contains(detailViewCallBack)) {
            callBacks.add(detailViewCallBack);
            if (mTargetAlbum != null) {
                detailViewCallBack.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unRegisterViewCallBack(IAlbumDetailViewCallBack detailViewCallBack) {
        callBacks.remove(detailViewCallBack);
    }

    public void setTargetAlbum(Album targetAlbum) {
        mTargetAlbum = targetAlbum;
    }

}
