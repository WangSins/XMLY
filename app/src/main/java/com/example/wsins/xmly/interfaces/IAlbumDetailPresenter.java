package com.example.wsins.xmly.interfaces;

import com.example.wsins.xmly.base.IBasePresenter;

public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailViewCallBack> {

    /**
     * 下拉刷新内容
     */
    void pull2RefreshMore();

    /**
     * 上拉加载更多
     */
    void loadMore();

    /**
     * 获取专辑详情
     *
     * @param albumId
     * @param page
     */
    void getAlbumDatail(int albumId, int page);
}
