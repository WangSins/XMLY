package com.example.wsins.xmly.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailViewCallBack {

    /**
     * 专辑详情内容加载出来了
     *
     * @param tracks
     */
    void onDetailListLoaded(List<Track> tracks);

    /**
     * 网络错误
     */
    void onNetWorkError(int errorCode, String errorMsg);

    /**
     * 把Album传给UI使用
     *
     * @param album
     */
    void onAlbumLoaded(Album album);

}
