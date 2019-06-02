package com.example.wsins.xmly.interfaces;

import com.example.wsins.xmly.base.IBasePresenter;

public interface IRecommendPersenter extends IBasePresenter<IRecommendViewCallback> {

    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 下拉刷新内容
     */
    void pull2RefreshMore();

    /**
     * 上拉加载更多
     */
    void loadMore();

}
