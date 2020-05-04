package com.example.wsins.xmly.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by Sin on 2019/6/30
 */
public interface ISubDaoCallBack {

    /**
     * 添加的结果回调方法
     *
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除的结果回调方法
     *
     * @param isSuccess
     */
    void onDelResult(boolean isSuccess);

    /**
     * 加载的结果
     *
     * @param result
     */
    void onSubListLoaded(List<Album> result);
}
