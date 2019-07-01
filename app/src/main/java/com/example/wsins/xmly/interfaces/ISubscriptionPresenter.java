package com.example.wsins.xmly.interfaces;

import com.example.wsins.xmly.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * Created by Sin on 2019/6/30
 * 订阅存在上限，不超过100个
 */
public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionCallback> {

    /**
     * 添加订阅
     *
     * @param album
     */
    void addSubscription(Album album);

    /**
     * 删除订阅
     *
     * @param album
     */
    void deleteSubscription(Album album);

    /**
     * 获取订阅列表
     */
    void getSubscriptionList();

    /**
     * 判断当前专辑是否已经收藏
     *
     * @param album
     */
    boolean isSubscription(Album album);
}
