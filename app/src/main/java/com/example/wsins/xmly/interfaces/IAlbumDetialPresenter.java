package com.example.wsins.xmly.interfaces;

public interface IAlbumDetialPresenter {

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

    /**
     * 注册UI通知接口
     *
     * @param detailViewCallBack
     */
    void registerViewCallBack(IAlbumDetailViewCallBack detailViewCallBack);

    /**
     * 取消注册UI通知得接口
     *
     * @param detailViewCallBack
     */
    void unregisterViewCallBack(IAlbumDetailViewCallBack detailViewCallBack);
}
