package com.example.wsins.xmly.presenters;

import com.example.wsins.xmly.interfaces.IAlbumDetailViewCallBack;
import com.example.wsins.xmly.interfaces.IAlbumDetialPresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sin on 2019/1/25
 */
public class AlbumDetailPresenter implements IAlbumDetialPresenter {

    private List<IAlbumDetailViewCallBack> callBacks = new ArrayList<>();

    private Album mTargetAlbum = null;

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

    }

    @Override
    public void getAlbumDatail(int albumId, int page) {

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
    public void unregisterViewCallBack(IAlbumDetailViewCallBack detailViewCallBack) {
        callBacks.remove(detailViewCallBack);

    }

    public void setTargetAlbum(Album targetAlbum) {
        mTargetAlbum = targetAlbum;
    }

}
