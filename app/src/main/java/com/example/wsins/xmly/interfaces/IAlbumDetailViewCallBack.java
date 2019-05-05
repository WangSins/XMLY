package com.example.wsins.xmly.interfaces;

import android.os.Trace;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IAlbumDetailViewCallBack {

    /**
     * 专辑详情内容加载出来了
     *
     * @param traces
     */
    void onDetailListLoaded(List<Trace> traces);

    /**
     * 把Album传给UI使用
     *
     * @param album
     */
    void onAlbumLoaded(Album album);

}
